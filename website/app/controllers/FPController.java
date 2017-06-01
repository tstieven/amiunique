package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import org.apache.commons.codec.digest.DigestUtils;
import play.Play;
import play.libs.Crypto;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.fp;
import views.html.fpNoJs;
import views.html.results;
import views.html.results2;
import views.html.viewFP;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.Set;
import java.util.Arrays;

import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ParallelScanOptions;
import com.mongodb.ServerAddress;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.MongoCredential;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Iterator;

import javax.persistence.*;

import java.util.Properties;
import java.util.Map.Entry;

import java.io.*;

import org.apache.commons.lang3.ArrayUtils;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FPController extends Controller{

    public static String getHeader(Http.Request request, String header){
        if(request.getHeader(header) == null){
            return "Not specified";
        } else {
            return request.getHeader(header);
        }
    }

    public static String getAttribute(JsonNode json, String attribute){
        if(json.get(attribute) == null){
            return "Not specified";
        } else {
            return json.get(attribute).asText();
        }
    }

    public static Result fp() {

        
        if(request().cookies().get("amiunique") == null){            
            response().setCookie("amiunique", UUID.randomUUID().toString(),60*60*24*120,"/","amiunique.org",true,true);
            response().setCookie("tempReturningVis","temp",60*60*12);
        }

        return ok(fp.render(request()));
    }

    public static Result fpNoJs() {
        Http.Cookie cookie = request().cookies().get("amiunique");
        String id;
        if(cookie == null){
            id = "Not supported";
        } else {
            id = cookie.value();
        }

        //MongoDb's part
        boolean newFpm;
        FingerprintManager fpc = new FingerprintManager();
        DBCollection collection = fpc.connection();
        Fingerprint fpmongo;

        FpDataEntityManager em = new FpDataEntityManager();
        CombinationStatsEntityManager emc = new CombinationStatsEntityManager();
        FpDataEntity fp;
        boolean newFp;

        String noJS = "no JS";

        if(!id.equals("Not supported") && em.checkIfFPWithNoJsExists(id, getHeader(request(), "User-Agent"),
                getHeader(request(), "Accept"),getHeader(request(), "Accept-Encoding"),
                getHeader(request(), "Accept-Language"))){
            fp = em.getExistingFPById(id);
            newFp = false;
        } else {
            LocalDateTime time = LocalDateTime.now();
            time = time.truncatedTo(ChronoUnit.HOURS);

            String ip;
            if(Play.isProd()) {
                ip = getHeader(request(), "X-Real-IP");
            } else {
                ip = request().remoteAddress();
            }
            fp = em.createWithoutJavaScript(id,
                    DigestUtils.sha1Hex(ip), Timestamp.valueOf(time), getHeader(request(),"User-Agent"),
                    getHeader(request(),"Accept"), getHeader(request(),"Host"), getHeader(request(),"Connection"),
                    getHeader(request(),"Accept-Encoding"), getHeader(request(),"Accept-Language"),
                    request().headers().keySet().toString().replaceAll("[,\\[\\]]", ""));

            emc.updateCombinationStats(getHeader(request(),"User-Agent"),
                    getHeader(request(),"Accept"), getHeader(request(),"Connection"),
                    getHeader(request(),"Accept-Encoding"), getHeader(request(),"Accept-Language"), request().headers().keySet().toString().replaceAll("[,\\[\\]]", ""),
                    noJS, noJS, noJS,
                    noJS, noJS, noJS,
                    noJS, noJS, noJS,
                    noJS, noJS, noJS,
                    noJS, noJS, noJS, noJS);

            newFp = true;
        }

        ObjectNode node = (ObjectNode) Json.toJson(fp);

        //Analyse the user agent
        ParsedFP parsedFP = new ParsedFP(node.get("userAgentHttp").asText());
        //Analyse the language and timezone
        parsedFP.setLanguage(node.get("languageHttp").asText());
        parsedFP.setTimezone(node.get("timezoneJs").asText());
        parsedFP.setNbFonts(node.get("fontsFlash").asText());

        node.remove("counter");
        node.remove("octaneScore");
        node.remove("sunspiderTime");
        node.remove("addressHttp");
        node.remove("time");
        node.remove("hostHttp");
        node.remove("connectionHttp");
        node.remove("orderHttp");
        node.remove("ieDataJs");
        node.remove("id");
        node.remove("vendorWebGljs");
        node.remove("rendererWebGljs");
        node.remove("webGlJs");
        node.remove("canvasJs");
        node.remove("fontsFlash");
        node.remove("webGLJsHashed");
        JsonNode json = (JsonNode) node;

        //Get the stats instance
        //Stats s = Stats.getInstance();
        Stats s = new Stats(collection);
        if(newFp) {
            //Add the newly parsed FP to the stats
            s.addFingerprint(parsedFP);
        }

        //Number of fingerprints
        Double nbTotal = s.getNbTotal().doubleValue();
        //Number of identical fingerprints
        Integer nbIdent = em.getNumberOfIdenticalFingerprints(json);
        //Get the percentages of every attribute
        Map<String,Double> percentages = emc.getPercentages(json);

        percentages.put("pluginsJs", percentages.get("pluginsJsHashed"));
        percentages.put("canvasJs", percentages.get("canvasJsHashed"));
        percentages.put("fontsFlash", percentages.get("fontsFlashHashed"));
        percentages.remove("pluginsJsHashed");
        percentages.remove("canvasJsHashed");
        percentages.remove("fontsFlashHashed");

        node.put("canvasJs","no JS");
        node.put("vendorWebGljs","no JS");
        node.put("rendererWebGljs","no JS");
        node.put("fontsFlash","no JS");

        //Get some general stats
        HashMap<String, VersionMap> osMap = s.getOs();
        HashMap<String, VersionMap> browsersMap = s.getBrowsers();
        VersionMap langMap = s.getLanguages();

        //Adding percentages for OS and browsers
        for (Map.Entry<String, VersionMap> entry : osMap.entrySet()) {
            percentages.put(entry.getKey(),entry.getValue().getCounter()*100/nbTotal);
        }
        for (Map.Entry<String, VersionMap> entry : browsersMap.entrySet()) {
            percentages.put(entry.getKey(),entry.getValue().getCounter()*100/nbTotal);
        }

        //Render the FP + models.Stats
        return ok(fpNoJs.render(json, parsedFP, Json.toJson(percentages), Json.toJson(osMap),
                Json.toJson(browsersMap), Json.toJson(langMap), nbTotal, nbIdent));
    }



    public static HashMap<String,String[]> configHashMap= new HashMap<String,String[]>(); 
    private static DBCollection collection;
    private static DBCollection combinationStats;
    private static DBCollection nbTotalDB;
    public static int nbTotal;
    public static int nbIdent;
    public static HashMap<String,Double> percentages = new HashMap<String,Double>() ;



    //return a collection of MongoDB's database,
    public static void connection(){
        try{

            String s = "spirals";
            char [] pwd = s.toCharArray();
            MongoCredential credential =  MongoCredential.createCredential("root", "test", pwd);

            // To connect to mongodb server
            MongoClient mongoClient = new MongoClient(new ServerAddress("localhost",27017), Arrays.asList(credential));

             // Now connect to your databases
             
             DB db = mongoClient.getDB( "test" );
            System.out.println("Connect to database successfully");

            collection = db.getCollection("testCollection");
            combinationStats=db.getCollection("combinationStats");
            nbTotalDB=db.getCollection("nbTotal");
            
             }catch(Exception e){
         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      }
    }

   
    //use SearchInConfig in every file of a repertory
    public static void listConfig(File path, JsonNode json){
        File files[];
        int indentLevel= 0;
        files = path.listFiles();
        Arrays.sort(files);
        for (int i = 0, n = files.length; i < n; i++) {
          for (int indent = 0; indent < indentLevel; indent++) {
            System.out.print("  ");
          }
          searchInConfig(files[i].toString(),json);
         
        }
    }

    //get value in a .json 
    public static void searchInConfig(String path,JsonNode json){
        
        JsonNode myJson= null;
        
        try {
          InputStream is =new FileInputStream(path);
          myJson = Json.parse(is);
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
        
        String name = getAttribute(myJson,"name");
        String[] val = new String[] {getAttribute(myJson,"script"),getAttribute(myJson,"enable"),getAttribute(myJson,"hash"),
        getAttribute(myJson,"use in comparison"),getAttribute(myJson,"async"),getAttribute(myJson,"jsrequired"),
        getAttribute(myJson,"flashrequired"),getAttribute(myJson,"display"),getAttribute(myJson,"overview"),
        getAttribute(myJson,"details"),getAttribute(myJson,"graph"),getAttribute(myJson,"supergraph"),
        getAttribute(myJson,"sentence1"),getAttribute(myJson,"sentence2"),getAttribute(myJson,"parse")};
        configHashMap.put(name,val);
    }

    public static int getNbTotal(){
        int cpt= 0;
        BasicDBObject query = new BasicDBObject();
        DBCursor cursor = nbTotalDB.find(query);
        try {
                while(cursor.hasNext()) {
                    DBObject doc = cursor.next();
                    cpt=Integer.parseInt((String)doc.get("counter"));
                }
            }finally {
                cursor.close();
            }       
        return cpt;
    }

    public static int getNbIdent(HashMap<String,Object>hashMap){
        int cpt= 0;
        BasicDBObject query = new BasicDBObject();
        Iterator it=hashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String name = (String)pair.getKey();
            if ((name!="id")&&(name!="ip")&&(name!="time")){
                String[] config = configHashMap.get(name);
                //System.out.println(name);
                if ((config[1].equals((String)"True"))&&(config[3].equals((String)"True"))){
                    query.put((String)name, ((String)pair.getValue()));
                }
            }
        }
        DBCursor cursor = collection.find(query);
        try {
                while(cursor.hasNext()) {
                    
                    DBObject doc = cursor.next();
                    cpt++;
                }
            }finally {
                cursor.close();
            }    
        return cpt;
    }

    public static Result addFingerprint() {
        System.out.println("addFingerprint");

        //Get FP attributes (body content)
        JsonNode json = request().body().asJson();
        
        connection();
        listConfig(new File("conf/json"),json);

        FpData data = new FpData(json,configHashMap);
        data.save(collection,combinationStats,nbTotalDB);
        nbTotal=getNbTotal();
        nbIdent = getNbIdent(data.fpHashMap);
        percentages = data.getEachPercentage(combinationStats,nbTotalDB);
        
        HashMap<String,Percentages> overview= new HashMap<String,Percentages>();
        HashMap<String,SuperGraphValues> supergraph= new HashMap<String,SuperGraphValues>();
        HashMap<String,GraphValues> graph= new HashMap<String,GraphValues>();
        HashMap<String,Double> details= new HashMap<String,Double>();
        Parsed parser= new Parsed();
        FpStats stat= new FpStats();
        

       Iterator it=percentages.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String name = (String)pair.getKey();
            Double value = ((Double)pair.getValue());
            String[] config = configHashMap.get(name);
            int version =0;
            
            if (config[14].equals((String)"True")){
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
                    
                   String newName= parser.parseAttribut(name,(String)data.fpHashMap.get(name));
                                       
                     
                    if (config[8].equals((String)"True")){
                        overview.put(newName,new Percentages(newName,value,config[12],config[13]));
                    }
                    if (config[9].equals((String)"True")){
                        details.put(newName,value);
                    }
                    if (config[10].equals((String)"True")){
                        graph.put(newName, new GraphValues(newName,Json.toJson(stat.getParseAttributStats(combinationStats,name,nbTotal)),name,config[7]));
                    }
            }else{                
                if (config[8].equals((String)"True")){
                    overview.put(name,new Percentages((String)data.fpHashMap.get(name),value,config[12],config[13]));
                }
                if (config[9].equals((String)"True")){
                    details.put(name,value);
                }
                if (config[10].equals((String)"True")){
                    //System.out.println(stat.getAttributStats(combinationStats,name,nbTotal));
                    graph.put(name, new GraphValues((String)data.fpHashMap.get(name),Json.toJson(stat.getAttributStats(combinationStats,name,nbTotal)),name,config[7]));
                }       
            }
        }
        return ok(results2.render(json,(double)nbTotal,nbIdent,details,overview,graph,supergraph)); 
    }

    public static Result viewFP() {
        return ok(viewFP.render(request()));
    }
}
