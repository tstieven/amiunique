package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.*;
import models.*;
import org.apache.commons.codec.digest.DigestUtils;
import play.Configuration;
import play.Play;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.fp2;
import views.html.fpNoJs;
import views.html.results2;
import views.html.viewFP;

import java.io.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;


public class FPController extends Controller {

    //All this global variables are kept, so we don't need to refresh them everytime a new webpage is open
    //we can just set these when the server is turning on
    private static DBCollection collection;
    private static DBCollection combinationStats;
    private static DBCollection nbTotalDB;
    private static Integer nbTotal;
    //contains configuration value from application.conf.json
    private static HashMap<String, HashMap<String, String>> configHashMap = new HashMap<>();
    private static List<String> configList = new ArrayList<>();
    private static final String configEnable = "enable";
    private static final String configComparison = "use in comparison";
    private static final String configOverview = "overview";
    private static final String configDetails = "details";
    private static final String configGraph = "graph";
    private static final String configSentence1 = "sentence1";
    private static final String configSentence2 = "sentence2";
    private static final String configTitle = "display";
    private static final String configParse = "parse";
    private static final String configHashed = "hash";


    private static String getHeader(Http.Request request, String header) {
        if (request.getHeader(header) == null) {
            return "Not specified";
        } else {
            return request.getHeader(header);
        }
    }

    public static String getId() {
        Http.Cookie cookie = request().cookies().get("amiunique");
        String id;
        if (cookie == null) {
            id = "Not supported";
        } else {
            id = cookie.value();
        }
        return id;
    }

    public static String getTime() {
        LocalDateTime time = LocalDateTime.now();
        time = time.truncatedTo(ChronoUnit.HOURS);
        return Timestamp.valueOf(time).toString();
    }

    public static String getIp() {
        String ip;
        if (Play.isProd()) {
            ip = getHeader(request(), "X-Real-IP");
        } else {
            ip = request().remoteAddress();
        }
        return DigestUtils.sha1Hex(ip);
    }

    public static String getAttribute(JsonNode json, String attribute) {
        if (json.get(attribute) == null) {
            return "Not specified";
        } else {
            return json.get(attribute).asText();
        }
    }

    private static void searchInConfig(String path) {

        JsonNode myJson = null;

        try {
            InputStream is = new FileInputStream(path);
            myJson = Json.parse(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String name = getAttribute(myJson, "name");
        HashMap<String, String> val = new HashMap<>();
        val.put(configEnable, getAttribute(myJson, configEnable));
        val.put(configComparison, getAttribute(myJson, configComparison));
        val.put(configDetails, getAttribute(myJson, configDetails));
        val.put(configGraph, getAttribute(myJson, configGraph));
        val.put(configOverview, getAttribute(myJson, configOverview));
        val.put(configParse, getAttribute(myJson, configParse));
        val.put(configSentence1, getAttribute(myJson, configSentence1));
        val.put(configSentence2, getAttribute(myJson, configSentence2));
        val.put(configHashed, getAttribute(myJson, configHashed));
        val.put(configTitle, getAttribute(myJson, configTitle));
        configHashMap.put(name, val);
    }

    private static void setConfigHashMap(File path) {
        File files[];
        int indentLevel = 0;
        files = path.listFiles();
        Arrays.sort(files);
        for (int i = 0, n = files.length; i < n; i++) {
            for (int indent = 0; indent < indentLevel; indent++) {
                System.out.print("  ");
            }
            searchInConfig(files[i].toString());

        }
    }

    //set MongoDB's collections and configHashMap
    //everytime the server is turning on, this function has to be called in fpNoJs() or addFingerprint()
    public static void connection() {
        Map<String, Object> lu = Configuration.root().asMap();
        setConfigHashMap(new File("conf/json"));
        setConfigList();
        try {
            HashMap<String, Object> mongoConfig = (HashMap<String, Object>) lu.get("mongo");
            String s = (String) mongoConfig.get("password");
            char[] configPassword = s.toCharArray();

            Integer configPort = (int) mongoConfig.get("port");

            String configUsername = (String) mongoConfig.get("username");
            String configDatabase = (String) mongoConfig.get("database");
            String configHost = (String) mongoConfig.get("host");
            String configMainCollection = (String) mongoConfig.get("mainCollection");
            String configCombinationCollection = (String) mongoConfig.get("combinationCollection");
            String configTotalCollection = (String) mongoConfig.get("totalCollection");

            MongoCredential credential = MongoCredential.createCredential(configUsername, configDatabase, configPassword);

            // To connect to mongodb server
            MongoClient mongoClient = new MongoClient(new ServerAddress(configHost, configPort), Arrays.asList(credential));

            // Now connect to your databases
            DB db = mongoClient.getDB(configDatabase);
            System.out.println("Connect to database successfully");

            collection = db.getCollection(configMainCollection);
            combinationStats = db.getCollection(configCombinationCollection);
            nbTotalDB = db.getCollection(configTotalCollection);

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }


    //return the minimal counter's value that the query could fine in the database nbTotal
    public static int getNbTotal() {
        int cpt = 0;
        int enable = 1;
        BasicDBObject query = new BasicDBObject();
        DBCursor cursor = nbTotalDB.find(query);

        while (cursor.hasNext()) {
            DBObject doc = cursor.next();
            if (configHashMap.get(doc.get("indicator")).get(configEnable).equals("True")) {

                int a = Integer.parseInt((String) doc.get("counter"));
                if (enable == 1) {
                    enable = 0;
                    cpt = a;
                } else if (cpt > a) {
                    cpt = a;
                }
            }
        }
        cursor.close();
        return cpt;
    }

    //return the number of identical fingerprint
    private static int getNbIdent(BasicDBObject query) {
        DBCursor cursor = collection.find(query);
        int cpt = cursor.length();
        cursor.close();
        return cpt;
    }

    // return a query for searching identical fingerprint
    private static BasicDBObject buildComparisonQuery(HashMap<String, Object> hashMap) {
        BasicDBObject query = new BasicDBObject();
        Iterator it = hashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String name = (String) pair.getKey();
            if ((name != "id") && (name != "ip") && (name != "time")) {
                HashMap<String, String> config = configHashMap.get(name);
                if ((config.get(configEnable).equals("True")) && (config.get(configComparison).equals("True"))) {
                    query.put(name, pair.getValue());
                }
            }
        }
        return query;
    }

    private static void setConfigList() {
        Iterator it = configHashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String name = (String) pair.getKey();
            HashMap<String, String> config = (HashMap<String, String>) pair.getValue();
            if (config.get(configEnable).equals("True")) {
                configList.add(name);
            }
        }

    }

    public static Result fp() {
        if ((collection == null) || (configHashMap.isEmpty())) {
            connection();
        }

        if (request().cookies().get("amiunique") == null) {
            response().setCookie("amiunique", UUID.randomUUID().toString(), 60 * 60 * 24 * 120, "/", "amiunique.org", true, true);
            response().setCookie("tempReturningVis", "temp", 60 * 60 * 12);
        }
        System.out.println("fp");
        return ok(fp2.render(request()));

    }

    private static void setValueHashMap(HashMap<String, Double> percentages, HashMap<String, Percentages> overview, HashMap<String, SuperGraphValues> supergraph, HashMap<String, GraphValues> graph, HashMap<String, Detail> details, FpData data) {
        Parsed parser = new Parsed();
        FpStats stat = new FpStats();

        Iterator it = percentages.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String name = (String) pair.getKey();
            Double value = ((Double) pair.getValue());
            HashMap<String, String> config = configHashMap.get(name);

            if (config.get(configParse).equals("True")) {
               /* if (name=="userAgentHttp"){//(config[11].equals((String)"True")){
                    //long a créer
                    parser.parseOsBrowsers(name,(String)data.fpHashMap.get(name));
                    String osName= parser.getOs();
                    String osVersion= parser.getOsVersion();
                    String browserName = parser.getBrowser();
                    Str
                    if (config[8].equals((String)"True")){
                        overview.put(newName,new Percentages(newName,value,config[12],config[13]));
                        overview.put(versionName,new Percentages(versionName,value,config[12],config[13]));
                    }

                    if (config[9].equals((String)"True")){
                        details.put(newName,newPerc(newName));
                        details.put(versionName,versionPercent(versionName));
                    }
                     if (config[10].equals((String)"True")){
                        supergraph.put(newName, new GraphValues(newName,Json.toJson(stat.getSuperParseAttributStats(collection,name)),newName,versionName,config[7]));
                    }

                }else*/

                String newName = parser.parseAttribute(name, (String) data.fpHashMap.get(name));

                if (!(config.get(configOverview).equals("0"))) {
                    overview.put(config.get(configOverview), new Percentages(newName, value, config.get(configSentence1), config.get(configSentence2)));
                }
                if (!(config.get(configDetails).equals("0"))) {
                    details.put(config.get(configDetails), new Detail(name, newName, value));
                }
                if (!(config.get(configGraph).equals("0"))) {
                    graph.put(config.get(configGraph), new GraphValues(newName, Json.toJson(stat.getParseAttributeStats(combinationStats, name, nbTotal)), name, config.get(configTitle)));
                }
            } else {

                if (!(config.get(configOverview).equals("0"))) {
                    overview.put(config.get(configOverview), new Percentages((String) data.fpHashMap.get(name), value, config.get(configSentence1), config.get(configSentence2)));
                }
                if (!(config.get(configDetails).equals("0"))) {
                    details.put(config.get(configDetails), new Detail(name, (String) data.fpHashMap.get(name), value));
                }
                if (!(config.get(configGraph).equals("0"))) {
                    graph.put(config.get(configGraph), new GraphValues((String) data.fpHashMap.get(name), Json.toJson(stat.getAttributeStats(combinationStats, name, nbTotal)), name, config.get(configTitle)));
                }
            }
        }
    }


    public static Result fpNoJs() {
        System.out.println("fpNoJs");
        //Get FP attributes (body content)

        if ((collection == null) || (configHashMap.isEmpty())) {
            connection();
        }

        FpData data = new FpData(configHashMap, getHeader(request(), "User-Agent"),
                getHeader(request(), "Accept"), getHeader(request(), "Accept-Encoding"),
                getHeader(request(), "Accept-Language"));
        data.saveInDataBases(collection, combinationStats, nbTotalDB);
        BasicDBObject comparisonQuery = buildComparisonQuery(data.fpHashMap);
        int nbIdent = getNbIdent(comparisonQuery);
        if (nbTotal == null) {
            nbTotal = getNbTotal();
        } else {
            nbTotal++;
        }
        HashMap<String, Double> percentages = data.getEachPercentage(combinationStats, nbTotalDB);
        HashMap<String, Percentages> overview = new HashMap<>();
        HashMap<String, SuperGraphValues> supergraph = new HashMap<>();
        HashMap<String, GraphValues> graph = new HashMap<>();
        HashMap<String, Detail> details = new HashMap<>();

        setValueHashMap(percentages, overview, supergraph, graph, details, data);

        return ok(fpNoJs.render((double) nbTotal, nbIdent, details, overview, graph, supergraph));
    }

    public static Result addFingerprint() {

        System.out.println("addFingerprint");

        //Get FP attributes (body content)
        JsonNode json = (request().body().asJson()).get("jsonVal");

        if ((collection == null) || (configHashMap.isEmpty())) {
            connection();
        }


        FpData data = new FpData(json, configHashMap);
        data.saveInDataBases(collection, combinationStats, nbTotalDB);
        BasicDBObject comparisonQuery = buildComparisonQuery(data.fpHashMap);
        int nbIdent = getNbIdent(comparisonQuery);
        if (nbTotal == null) {
            nbTotal = getNbTotal();
        } else {
            nbTotal++;
        }

        HashMap<String, Double> percentages = data.getEachPercentage(combinationStats, nbTotalDB);
        HashMap<String, Percentages> overview = new HashMap<>();
        HashMap<String, SuperGraphValues> supergraph = new HashMap<>();
        HashMap<String, GraphValues> graph = new HashMap<>();
        HashMap<String, Detail> details = new HashMap<>();

        setValueHashMap(percentages, overview, supergraph, graph, details, data);

        HashMap<String, Double> plugins = data.saveAndGetPluginsStatsInHashMap(combinationStats);
        return ok(results2.render((double) nbTotal, nbIdent, details, overview, graph, supergraph, plugins));
    }

    public static Result viewFP() {
        return ok(viewFP.render(request()));
    }
}
