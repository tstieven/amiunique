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
import views.html.fp;
import views.html.results2;
import views.html.viewFP;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;


public class FPController extends Controller {


    public static String getHeader(Http.Request request, String header) {
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


    public static HashMap<String, HashMap<String, String>> configHashMap = new HashMap<String, HashMap<String, String>>();

    private static DBCollection collection;
    private static DBCollection combinationStats;
    private static DBCollection nbTotalDB;
    //TODO a dégager d'ici
    //et essayer de le faire lors du démarrage.
    //Attention probleme de concurrence
    public static int nbTotal;
    public static int nbIdent;
    public static HashMap<String, Double> percentages = new HashMap<String, Double>();


    //TODO
    //Une seule connection lors du demarrage du serveur
    //Voir meme dans le fichier de config
    //return a collection of MongoDB's database,
    public static void connection() {
        try {

            String s = "spirals";
            char[] pwd = s.toCharArray();
            MongoCredential credential = MongoCredential.createCredential("root", "test", pwd);

            // To connect to mongodb server
            MongoClient mongoClient = new MongoClient(new ServerAddress("localhost", 27017), Arrays.asList(credential));

            // Now connect to your databases

            DB db = mongoClient.getDB("test");
            System.out.println("Connect to database successfully");

            collection = db.getCollection("testCollection");
            combinationStats = db.getCollection("combinationStats");
            nbTotalDB = db.getCollection("nbTotal");

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }


    //use SearchInConfig in every file of a repertory
    public static void listConfig() {
        Map<String, Object> lu = Configuration.root().asMap();
        configHashMap = (HashMap<String, HashMap<String, String>>) lu.get("json");
    }

    //Prend le nb le plus petit des attributs de la configuration
    //Mais ne calcul pas le nb de la combinaison d'attributs
    //Ainsi nb ne peut être que plus grand que celui qu'il devrait etre.
    //Il n'y a donc pas de probleme de pourcentage supérieur à 100
    public static int getNbTotal() {
        int cpt = 0;
        int enable = 1;
        BasicDBObject query = new BasicDBObject();
        DBCursor cursor = nbTotalDB.find(query);

        while (cursor.hasNext()) {
            DBObject doc = cursor.next();
            if (configHashMap.get(doc.get("indicator")).get("enable").equals("True")) {

                int a = Integer.parseInt((String) doc.get("counter"));
                if (enable == 1) {
                    enable = 0;
                    cpt = a;
                } else {
                    if (cpt > a) {
                        cpt = a;
                    }
                }
            }
        }
        cursor.close();
        return cpt;
    }

    public static int getNbIdent(BasicDBObject query) {
        int cpt = 0;
        DBCursor cursor = collection.find(query);

        while (cursor.hasNext()) {

            DBObject doc = cursor.next();
            cpt++;
        }
        cursor.close();
        return cpt;
    }

    public static BasicDBObject getQuery(HashMap<String, Object> hashMap) {
        BasicDBObject query = new BasicDBObject();
        Iterator it = hashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String name = (String) pair.getKey();
            if ((name != "id") && (name != "ip") && (name != "time")) {
                HashMap<String, String> config = configHashMap.get(name);
                System.out.println(name);
                if ((config.get("enable").equals("True")) && (config.get("use in comparison").equals("True"))) {
                    query.put(name, pair.getValue());
                }
            }
        }
        return query;
    }

    public static Result fp() {


        if (request().cookies().get("amiunique") == null) {
            response().setCookie("amiunique", UUID.randomUUID().toString(), 60 * 60 * 24 * 120, "/", "amiunique.org", true, true);
            response().setCookie("tempReturningVis", "temp", 60 * 60 * 12);
        }

        return ok(fp.render(request()));
    }

    /*public static Result fpNoJs(){
         System.out.println("addFingerprint");

        //Get FP attributes (body content)
        JsonNode json = request().body().asJson();



        //TODO
        //Faire une fois seulement lors du démarrage du serveur
        connection();
        listConfig();


        FpData data = new FpData(json,configHashMap);
        //TODO
        //verifier si l'empreinte n'existe pas deja
        data.save(collection,combinationStats,nbTotalDB);
        BasicDBObject query=getQuery(data.fpHashMap);
        nbTotal=getNbTotal();
        nbIdent = getNbIdent(query);

        percentages = data.getEachPercentage(combinationStats,nbTotalDB);


        //Global.print();//get("fontsFlash")[8]);

        HashMap<String,Percentages> overview= new HashMap<String,Percentages>();
        HashMap<String,SuperGraphValues> supergraph= new HashMap<String,SuperGraphValues>();
        HashMap<String,GraphValues> graph= new HashMap<String,GraphValues>();
        HashMap<String,Double> details= new HashMap<String,Double>();
        Parsed parser= new Parsed();
        FpStats stat= new FpStats();
System.out.println(configHashMap);

       Iterator it=percentages.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String name = (String)pair.getKey();
            Double value = ((Double)pair.getValue());
            HashMap<String,String> config = configHashMap.get(name);

            if (config.get("parse").equals((String)"True")){
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

                   /*String newName= parser.parseAttribut(name,(String)data.fpHashMap.get(name));


                    if (config.get("overview").equals("True")){
                        overview.put(newName,new Percentages(newName,value,config.get("sentence1"),config.get("sentence2")));
                    }
                    if (config.get("details").equals("True")){
                        details.put(newName,value);
                    }
                    if (config.get("graph").equals("True")){
                        graph.put(newName, new GraphValues(newName,Json.toJson(stat.getParseAttributStats(combinationStats,name,nbTotal)),name,config.get("display")));
                    }
            }else{
                if (config.get("overview").equals("True")){
                    overview.put(name,new Percentages((String)data.fpHashMap.get(name),value,config.get("sentence1"),config.get("sentence2")));
                }
                if (config.get("details").equals("True")){
                    details.put(name,value);
                }
                if (config.get("graph").equals("True")){
                    graph.put(name, new GraphValues((String)data.fpHashMap.get(name),Json.toJson(stat.getAttributStats(combinationStats,name,nbTotal)),name,config.get("display")));
                }
            }
        }
        return ok(results2.render(json,(double)nbTotal,nbIdent,details,overview,graph,supergraph));
    }*/

    public static Result addFingerprint() {
        System.out.println("addFingerprint");

        //Get FP attributes (body content)
        JsonNode json = request().body().asJson();

        //TODO
        //Faire une fois seulement lors du démarrage du serveur


        connection();
        listConfig();

        FpData data = new FpData(json, configHashMap);
        data.save(collection, combinationStats, nbTotalDB);
        BasicDBObject query = getQuery(data.fpHashMap);
        nbTotal = getNbTotal();
        nbIdent = getNbIdent(query);
        percentages = data.getEachPercentage(combinationStats, nbTotalDB);

        HashMap<String, Percentages> overview = new HashMap<String, Percentages>();
        HashMap<String, SuperGraphValues> supergraph = new HashMap<String, SuperGraphValues>();
        HashMap<String, GraphValues> graph = new HashMap<String, GraphValues>();
        HashMap<String, Double> details = new HashMap<String, Double>();
        Parsed parser = new Parsed();
        FpStats stat = new FpStats();

        Iterator it = percentages.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String name = (String) pair.getKey();
            Double value = ((Double) pair.getValue());
            HashMap<String, String> config = configHashMap.get(name);

            if (config.get("parse").equals((String) "True")) {
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


                if (config.get("overview").equals("True")) {
                    overview.put(newName, new Percentages(newName, value, config.get("sentence1"), config.get("sentence2")));
                }
                if (config.get("details").equals("True")) {
                    details.put(newName, value);
                }
                if (config.get("graph").equals("True")) {
                    graph.put(newName, new GraphValues(newName, Json.toJson(stat.getParseAttributeStats(combinationStats, name, nbTotal)), name, config.get("display")));
                }
            } else {
                if (config.get("overview").equals("True")) {
                    overview.put(name, new Percentages((String) data.fpHashMap.get(name), value, config.get("sentence1"), config.get("sentence2")));
                }
                if (config.get("details").equals("True")) {
                    details.put(name, value);
                }
                if (config.get("graph").equals("True")) {
                    graph.put(name, new GraphValues((String) data.fpHashMap.get(name), Json.toJson(stat.getAttributeStats(combinationStats, name, nbTotal)), name, config.get("display")));
                }
            }
        }

        return ok(results2.render(json, (double) nbTotal, nbIdent, details, overview, graph, supergraph));
    }

    public static Result viewFP() {
        return ok(viewFP.render(request()));
    }
}
