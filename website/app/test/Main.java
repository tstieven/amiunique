package models;
import javax.persistence.*;

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
import java.util.Properties;

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

import java.io.*;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Iterator;
import org.apache.commons.lang3.ArrayUtils;


public class Main extends Controller{
	public static HashMap<String,String[]> configHashMap= new HashMap<String,String[]>(); 
	private DBCollection collection;
	//return a collection of MongoDB's database,
	/*public DBCollection connection(){
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
            
             }catch(Exception e){
         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      }
      return collection;

    }*/

	public static String getAttribute(JsonNode json, String attribute){
        if(json.get(attribute) == null){
            return "Not specified";
        } else {
            return json.get(attribute).asText();
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
        String[] val = new String[] {getAttribute(myJson,"hash"),getAttribute(myJson,"use in comparison"),getAttribute(myJson,"async"),
        getAttribute(myJson,"display"),getAttribute(myJson,"enable"),getAttribute(myJson,"jsrequired"),
        getAttribute(myJson,"flashrequired")};
        configHashMap.put(name,val);
    }

    public static int getNbTotal(DBCollection coll){
        int cpt= 0;
        BasicDBObject query = new BasicDBObject();
        DBCursor cursor = coll.find(query);
        try {
                while(cursor.hasNext()) {
                    DBObject doc = cursor.next();
                    cpt=cpt+Integer.parseInt((String)doc.get("counter"));
                }
            }finally {
                cursor.close();
            }       
        return cpt;
    }

    public static int getNbAttribut(DBCollection coll,String name){
       int cpt= 0;
        BasicDBObject query = new BasicDBObject(name,fpHashMap.get(name));
        DBCursor cursor = coll.find(query);
        try {
                while(cursor.hasNext()) {
                    DBObject doc = cursor.next();
                    cpt=cpt+Integer.parseInt((String)doc.get("counter"));
                }
            }finally {
                cursor.close();
            }       
        return cpt; 
    }


    public static void addFingerprint() {

        //Get FP attributes (body content)
        JsonNode json = request().body().asJson();
    
        //MongoDb's part
        FingerprintManager fpc = new FingerprintManager();
        DBCollection collection = fpc.connection();
        //DBCollection collection = connection();
        listConfig(new File("conf/json"),json);
       
        FpData data = new FpData(json,configHashMap);
        data.chooseItself(collection);
        System.out.println(getNbTotal(collection));
    }

        //Mongo's part
        
/*
       
        //Presque compris la suite
       
        ObjectNode node = (ObjectNode) Json.toJson(fpmongo);
        //System.out.println(node);
        int counter = node.get("counter").asInt();
        //System.out.println(counter);
        node.remove("counter");
        node.remove("octaneScore");
        node.remove("sunspiderTime");
        //System.out.println(node.get("addressHttp"));
        node.remove("addressHttp");
        node.remove("time");
        node.remove("hostHttp");
        node.remove("connectionHttp");
        node.remove("orderHttp");
        node.remove("id");
        node.remove("webGlJs");
        json = (JsonNode) node;
        

        //Analyse the user agent
        ParsedFP parsedFP = new ParsedFP(node.get("userAgentHttp").asText());
        //Analyse the language and timezone
        parsedFP.setLanguage(node.get("languageHttp").asText());
        parsedFP.setTimezone(node.get("timezoneJs").asText());
        parsedFP.setNbFonts(node.get("fontsFlash").asText());

        //Get the stats instance
        Stats s = new Stats(collection);
         //Stats.getInstance();
         System.out.println (s.getLanguages());
    

        //Number of fingerprints
        Double nbTotal = s.getNbTotal().doubleValue();
        //Number of identical fingerprints
        Integer nbIdent = em.getNumberOfIdenticalFingerprints(json);
        //Get the percentages of every attribute
        Map<String,Double> percentages = em.getPercentages(json);
        //Map<String,Double> percentages = new HashMap<>();

        //Get some general stats
        HashMap<String, VersionMap> osMap = s.getOs();
        HashMap<String, VersionMap> browsersMap = s.getBrowsers();
        VersionMap langMap = s.getLanguages();
        CounterMap timezoneMap = s.getTimezone();

        //Adding percentages for OS and browsers
        for (Map.Entry<String, VersionMap> entry : osMap.entrySet()) {
            percentages.put(entry.getKey(),entry.getValue().getCounter()*100/nbTotal);
        }
        for (Map.Entry<String, VersionMap> entry : browsersMap.entrySet()) {
            percentages.put(entry.getKey(),entry.getValue().getCounter()*100/nbTotal);
        }

        ObjectNode n = Json.newObject();
        n.put("c",counter);
        n.put("t", Crypto.generateToken());
        String c = Crypto.encryptAES(n.toString());

        //Render the FP + Stats
        return ok(results.render(json, parsedFP, Json.toJson(percentages), Json.toJson(osMap),
                Json.toJson(browsersMap), Json.toJson(langMap), Json.toJson(timezoneMap), nbTotal,
                nbIdent, c));
    }

    public static Result viewFP() {
        return ok(viewFP.render(request()));
    }*/
}