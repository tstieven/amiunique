package controllers;

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
  
    public static HashMap<String, Object> fpHashMap = new HashMap<String, Object>();
    public static HashMap<String, String[]> configHashMap = new HashMap<String, String[]>();
    public static Integer counter;

    public FpData(JsonNode json,HashMap<String, String[]> confHashMap){

        fpHashMap.put("id",getId());
        fpHashMap.put("ip",getIp());
        fpHashMap.put("time",getTime()); 
        configHashMap= confHashMap;
        throughConfigAddInDB(json);
    }
   

   	public void save(DBCollection coll,DBCollection combination,DBCollection nbtotal){
			
        BasicDBObject doc = new BasicDBObject();
        
        Iterator it=fpHashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
           // System.out.println(pair.getKey()+ " : " +pair.getValue() );
           doc.put((String)pair.getKey(),(String)pair.getValue());
           //System.out.println(pair.getKey());
           if ((pair.getKey()!="ip")&&(pair.getKey()!="time")&&(pair.getKey()!="id")){

               BasicDBObject query = new BasicDBObject("combination",pair.getValue());
               //System.out.println(query);
               DBCursor cursor = combination.find(query);
               try{
                if(cursor.hasNext()) {
                    DBObject updateDoc = cursor.next();
                    String tmp=(String)updateDoc.get("counter");
                    counter=Integer.parseInt(tmp)+1;
                    updateDoc.put("counter",Integer.toString(counter));
                    combination.update(query,updateDoc);
                    
                }else{
                    BasicDBObject combi= new BasicDBObject();
                    combi.put("combination",pair.getValue());
                    combi.put("indicator",pair.getKey());
                    combi.put("counter","1");
                    combination.insert(combi);
                 }
                }finally {
                    cursor.close();
                } 

               BasicDBObject query2 = new BasicDBObject("indicator",pair.getKey());
               DBCursor cursor2 = nbtotal.find(query2);
            try{
                if(cursor2.hasNext()) {
                    DBObject updateDoc = cursor2.next();
                    String tmp=(String)updateDoc.get("counter");
                    counter=Integer.parseInt(tmp)+1;
                    updateDoc.put("counter",Integer.toString(counter));
                    nbtotal.update(query2,updateDoc);
                    
                }else{
                    BasicDBObject nb= new BasicDBObject("indicator",pair.getKey());
                    nb.put("counter","1");
                    nbtotal.insert(nb);
                 }
                }finally {
                    cursor.close();
                }  

            }
        }
           
        coll.insert(doc);
    }

    
    public static String getAttribute(JsonNode json, String attribute){
        if(json.get(attribute) == null){
            return "Not specified";
        } else {
            return json.get(attribute).asText();
        }
    }

    public static String getId(){
        Http.Cookie cookie = request().cookies().get("amiunique");
        String id;
        if(cookie == null){
            id = "Not supported";
        } else {
            id = cookie.value();
        }
        return id ;
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

    public static void throughConfigAddInDB(JsonNode json){
      

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

    public static HashMap<String,Double> getEachPercentage(DBCollection combi,DBCollection nbtotal){
      
        HashMap<String,Double> nbMap= new HashMap<String,Double>();
        Iterator it=configHashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String name = (String)pair.getKey();
            int counter;
            String[] config = ((String[])pair.getValue());
            if ((config[1].equals((String)"True"))&&((config[8].equals((String)"True"))||(config[9].equals((String)"True"))||(config[10].equals((String)"True")))){
                
                BasicDBObject query = new BasicDBObject("indicator",pair.getKey());
                DBCursor cursor = nbtotal.find(query);
                try{
                    if(cursor.hasNext()) {
                        DBObject updateDoc = cursor.next();
                        
                        counter=Integer.parseInt((String)updateDoc.get("counter"));
                        
                        
                    }else{
                        counter=0;
                     }
                    }finally {
                        cursor.close();
                    }            
                nbMap.put(name,(double)((double)getNbAttribut(combi,name)*(double)100/(double)counter));
                //System.out.println("total"+counter); System.out.println("attribute"+getNbAttribut(combi,name));
                   
            }            
        }
        return nbMap;
    }


    public static void addInHashMap(JsonNode json,String name, String[] config){
        if(config[1].equals((String)"True")){
            fpHashMap.put(name,getAttribute(json,name));
            if(config[2].equals((String)"True")){
            fpHashMap.put(name+"Hashed",DigestUtils.sha1Hex(getAttribute(json,name+"Hashed")));

            }
        }
    }

    public static void putInQuery(BasicDBObject query,String name, String[] config){
        if((config[1].equals((String)"True"))&&(config[3].equals((String)"True"))){
            query.put(name,fpHashMap.get(name));
            if (config[2].equals((String)"True")){
                query.put(name+"Hashed",fpHashMap.get(name+"Hashed"));
            }
            
        }else{
            if (config[1].equals((String)"False")){
                query.put(name,null);
                if (config[2].equals((String)"True")){
                    query.put(name+"Hashed",null);
                }
            }
        }     
    }


    public static int getNbAttribut(DBCollection combi,String name){
       int cpt= 0;
        BasicDBObject query = new BasicDBObject("indicator",name);
        query.put("combination",fpHashMap.get(name));
        DBCursor cursor = combi.find(query);
        //System.out.println("query"+query);
        try {
                if(cursor.hasNext()) {
                    DBObject doc = cursor.next();
                    cpt=Integer.parseInt((String)doc.get("counter"));
                }
            }finally {
                cursor.close();
            }     
        return cpt; 
    } 
}