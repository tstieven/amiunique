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


public class FpData extends Controller{
  
    private static HashMap<String, Object> fpHashMap = new HashMap<String, Object>();
    private static HashMap<String, String[]> configHashMap = new HashMap<String, String[]>();
    private static String id;

    public FpData(JsonNode json,HashMap<String, String[]> confHashMap){

        getId();
        fpHashMap.put("id",id);
        fpHashMap.put("ip",getIp());
        fpHashMap.put("time",getTime()); 
        fpHashMap.put("counter","1");
        configHashMap= confHashMap;
        throughConfigAdd(json);
    }


    //Il faudra rajouter quelque chose pour eviter que la meme personne s'enregistre plusieurs fois
    //Comme à l'origine
    //Mais c'etait gênant pour les tests
    public void chooseItself(DBCollection coll){
        BasicDBObject query = new BasicDBObject();
        throughConfigExisting(query); 
        int bool=0;         
        DBCursor cursor = coll.find(query);
        try {
                if(cursor.hasNext()) {
                    addCounter(coll,cursor,query);
                    System.out.println("one up");
                }else{
                    save(coll);
                    System.out.println("save");
                }
            } finally {
                cursor.close();
            }       
        
    }


   	public void save(DBCollection coll){
			
        BasicDBObject doc = new BasicDBObject();
        Iterator it=fpHashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
           // System.out.println(pair.getKey()+ " : " +pair.getValue() );
           doc.put((String)pair.getKey(),(String)pair.getValue());
        }
           
        coll.insert(doc);	    	
    }

    public void addCounter(DBCollection coll,DBCursor cursor,BasicDBObject query){
        DBObject updateDoc = cursor.next();
        String tmp=(String)updateDoc.get("counter");
        updateDoc.put("counter",Integer.toString(Integer.parseInt(tmp)+1));
        coll.update(query,updateDoc);
    }



    public static String getAttribute(JsonNode json, String attribute){
        if(json.get(attribute) == null){
            return "Not specified";
        } else {
            return json.get(attribute).asText();
        }
    }

    public static void getId(){
        Http.Cookie cookie = request().cookies().get("amiunique");
        
        if(cookie == null){
            id = "Not supported";
        } else {
            id = cookie.value();
        }  
    }

    public static String getTime(){
        LocalDateTime time = LocalDateTime.now();
        time = time.truncatedTo(ChronoUnit.HOURS);
        return Timestamp.valueOf(time).toString();
    }

    public static String getIp(){
        String ip;
        if(Play.isProd()) {
            ip = getHeader(request(), "X-Real-IP");
            } else {
                ip = request().remoteAddress();
            }
    return DigestUtils.sha1Hex(ip);
    }
      
    public static String getHeader(Http.Request request, String header){
        if(request.getHeader(header) == null){
            return "Not specified";
        } else {
            return request.getHeader(header);
        }
    }

    public static void throughConfigAdd(JsonNode json){
      

        Iterator it=configHashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String name = (String)pair.getKey();
            String[] config = ((String[])pair.getValue());
            addInHashMap(json,name,config);   
        }
    }


    public static void throughConfigExisting(BasicDBObject query){
      

        Iterator it=configHashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String name = (String)pair.getKey();
            String[] config = ((String[])pair.getValue());
            putInQuery(query,name,config); 
        }
    }


    public static void addInHashMap(JsonNode json,String name, String[] config){
        
        if(config[4].equals((String)"True")){
            fpHashMap.put(name,getAttribute(json,name));
            System.out.println(name);
            if(config[0].equals((String)"True")){
            fpHashMap.put(name+"Hashed",DigestUtils.sha1Hex(getAttribute(json,name+"Hashed")));

            System.out.println(name+"Hashed");
            }
        }
    }

    public static void putInQuery(BasicDBObject query,String name, String[] config){
        if((config[4].equals((String)"True"))&&(config[1].equals((String)"True"))){
            
            System.out.println("Query : "+name);
            query.put(name,fpHashMap.get(name));

            
            /*play.api.Application$$anon$1: Execution exception[[NullPointerException: null]]
    at play.api.Application$class.handleError(Application.scala:296) ~[play_2.11-2.3.10.jar:2.3.10]
    at play.api.DefaultApplication.handleError(Application.scala:402) [play_2.11-2.3.10.jar:2.3.10]
    at play.core.server.netty.PlayDefaultUpstreamHandler$$anonfun$3$$anonfun$applyOrElse$4.apply(PlayDefaultUpstreamHandler.scala:320) [play_2.11-2.3.10.jar:2.3.10]
    at play.core.server.netty.PlayDefaultUpstreamHandler$$anonfun$3$$anonfun$applyOrElse$4.apply(PlayDefaultUpstreamHandler.scala:320) [play_2.11-2.3.10.jar:2.3.10]
    at scala.Option.map(Option.scala:146) [scala-library-2.11.6.jar:na]
Caused by: java.lang.NullPointerException: null
    at views.html.results$.apply(results.template.scala:117) ~[na:na]
    at views.html.results$.render(results.template.scala:539) ~[na:na]
    at views.html.results.render(results.template.scala) ~[na:na]
    at controllers.FPController.addFingerprint(FPController.java:414) ~[na:na]
    at Routes$$anonfun$routes$1$$anonfun$applyOrElse$15$$anonfun$apply$15.apply(routes_routing.scala:305) ~[na:na]
*/
            if (config[0].equals((String)"True")){
                System.out.println("Query : "+name+"Hashed");
                query.put(name+"Hashed",fpHashMap.get(name+"Hashed"));
            }
            
        }     
    } 
}