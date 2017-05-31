package models;

import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import play.db.jpa.JPA;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class FpStats { 


   public HashMap<String,Double> getAttributStats(DBCollection coll,String attr,int nbtotal){

        HashMap<String,Double> fpAttributStats = new HashMap<String,Double>();
        BasicDBObject query = new BasicDBObject("indicator",attr);

        // A changer
                       
        DBCursor cursor = coll.find(query);        
            try {
                while(cursor.hasNext()) { 
                    DBObject getDoc = cursor.next();
                    int cpt = Integer.parseInt((String)getDoc.get("counter"));
                    String value= (String)getDoc.get("combination");
                    fpAttributStats.put(value,(double)cpt/(double)nbtotal);
                                     
                }  
            } finally {
                cursor.close(); 
            }
            
        return fpAttributStats;
    }


    public HashMap<String,Double> getParseAttributStats(DBCollection coll,String attr,int nbtotal){
        

        HashMap<String,Double> fpAttributStats = new HashMap<String,Double>();
        BasicDBObject query = new BasicDBObject("indicator",attr);

        // A changer
                       
        DBCursor cursor = coll.find(query);        
            try {
                while(cursor.hasNext()) { 
                    DBObject getDoc = cursor.next();
                    int cpt = Integer.parseInt((String)getDoc.get("counter"));
                    String value= (String)getDoc.get("combination");
                    Parsed parser = new Parsed();
                    String newName = parser.parseAttribut(attr,value);
                    fpAttributStats.put(newName,(double)cpt/(double)nbtotal);
                                     
                }  
            } finally {
                cursor.close(); 
            }
            
        return fpAttributStats;
    }
}
        
