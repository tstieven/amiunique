/* constructeur jsonnode ou bson en paramètre
crée un hashmap(dictionnaire)
+ fonction save pour mettre dans la base de donnée*/

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


/*
@Entity
@Table(name = "fpData", schema = "", catalog = "testCollection")*/

public class Fingerprint extends Controller {

  public int counter;
    public String id;
    public String addressHttp;
    public Timestamp time;
    public String userAgentHttp;
    public String acceptHttp;
    public String hostHttp;
    private HashMap<String, String> fpHashMap = new HashMap<String, String>();
    public String connectionHttp;
    public String encodingHttp;
    public String languageHttp;
    public String orderHttp;
    public String pluginsJs;
    public String platformJs;
    public String cookiesJs;
    public String dntJs;
    public String timezoneJs;
    public String resolutionJs;
    public String localJs;
    public String sessionJs;
    public String ieDataJs;
    public String canvasJs;
    public String fontsFlash;
    public String resolutionFlash;
    public String languageFlash;
    public String platformFlash;
    public String adBlock;
    public String octaneScore;
    public String sunspiderTime;
    public String webGlJs;
    private String vendorJs;
    private String rendererJs;

    //Hashed Attributes
    public String pluginsJsHashed;
    public String canvasJsHashed;
    public String webGLJsHashed;
    public String fontsFlashHashed;


   	public void save(DBCollection coll){
			//try{

        	/*String s = "spirals";
        	char [] pwd = s.toCharArray();
        	MongoCredential credential =  MongoCredential.createCredential("root", "test", pwd);

         	// To connect to mongodb server
        	MongoClient mongoClient = new MongoClient(new ServerAddress("localhost",27017), Arrays.asList(credential));

	         // Now connect to your databases
    	     
    		 DB db = mongoClient.getDB( "test" );
  	       System.out.println("Connect to database successfully");

        	 DBCollection coll = db.getCollection("testCollection");*/


         	BasicDBObject doc = new BasicDBObject("id",id)
            .append("counter","1")
         	//.append("ip",ip)
         	.append("acceptHttp",acceptHttp)
         	.append("userAgentHttp",userAgentHttp)
         	.append("hostHttp",hostHttp)
         	.append("connectionHttp",connectionHttp)
         	.append("encodingHttp",encodingHttp)
         	.append("languageHttp",languageHttp)
         	.append("orderHttp",orderHttp)
         	.append("pluginsJs",pluginsJs)
         	.append("platformJs",platformJs)
         	.append("cookiesJs",cookiesJs)
         	.append("dntJs",dntJs)
         	.append("timezoneJs",timezoneJs)
         	.append("resolutionJs",resolutionJs)
         	.append("localJs",localJs)
         	.append("sessionJs",sessionJs)
         	.append("ieDataJs",ieDataJs)
         	.append("canvasJs",canvasJs)
         	.append("webGLJs",webGlJs)
         	.append("fontsFlash",fontsFlash)
         	.append("resolutionFlash",resolutionFlash)
         	.append("languageFlash",languageFlash)
         	.append("platformFlash",platformFlash)	
         	.append("adBlock",adBlock)
         	.append("vendorWebGLJs",vendorJs)
         	.append("rendererWebGLJs",rendererJs)
         	.append("pluginsJsHashed",pluginsJsHashed)
         	.append("canvasJsHashed",canvasJsHashed)
         	.append("webGlJsHashed",webGLJsHashed)
         	.append("fontsFlashHashed",fontsFlashHashed);

            coll.insert(doc);
	}
        
	public Fingerprint(String id, String addressHttp, Timestamp time, String userAgentHttp,
                        String acceptHttp, String hostHttp, String connectionHttp, String encodingHttp,
                        String languageHttp, String orderHttp, String pluginsJs, String platformJs, String cookiesJs,
                        String dntJs, String timezoneJs, String resolutionJs, String localJs, String sessionJs,
                        String ieDataJs, String canvasJs, String webGlJs, String fontsFlash, String resolutionFlash,
                        String languageFlash, String platformFlash, String adBlock, String vendorJs,
                        String rendererJs, String octaneScore, String sunspiderTime, String pluginsJsHashed,
                        String canvasJsHashed, String webGLJsHashed, String fontsFlashHashed){		

            this.id=id;
            this.addressHttp=addressHttp;
            this.counter=1;
            //fpHashMap.put("time",time);
            this.userAgentHttp=userAgentHttp;     
            this.acceptHttp=acceptHttp;
            this.hostHttp=hostHttp;
            this.connectionHttp=connectionHttp;
            this.encodingHttp=encodingHttp;
            this.languageHttp=languageHttp;
            this.orderHttp=orderHttp;
            this.pluginsJs=pluginsJs;
            this.platformJs=platformJs;
            this.cookiesJs=cookiesJs;
            this.dntJs=dntJs;
            this.timezoneJs=timezoneJs;
            this.resolutionJs=resolutionJs;
            this.localJs=localJs;
            this.sessionJs=sessionJs;
            this.ieDataJs=ieDataJs;
            this.canvasJs=canvasJs;
            this.webGlJs=webGlJs;
            this.fontsFlash=fontsFlash;
            this.resolutionFlash=resolutionFlash;
            this.languageFlash=languageFlash;
            this.platformFlash=platformFlash;
            this.adBlock=adBlock;
            this.vendorJs=vendorJs;
            this.rendererJs=rendererJs;
            this.octaneScore=octaneScore;
            this.sunspiderTime=sunspiderTime;
            this.pluginsJsHashed=pluginsJsHashed;
            this.canvasJsHashed=canvasJsHashed;
            this.webGLJsHashed=webGLJsHashed;
            this.fontsFlashHashed=fontsFlashHashed;   
	}
    public Fingerprint(DBObject obj){
        //System.out.println(obj);

            this.id=(String)obj.get("id");
            this.addressHttp=(String)obj.get("addressHttp");
            this.counter=Integer.parseInt((String)obj.get("counter"));
            //fpHashMap.put("time",time);
            this.userAgentHttp=(String)obj.get("userAgentHttp");
            this.acceptHttp=(String)obj.get("acceptHttp"); 
            this.hostHttp=(String)obj.get("hostHttp");
            this.connectionHttp=(String)obj.get("connectionHttp");
            this.encodingHttp=(String)obj.get("encodingHttp");
            this.languageHttp=(String)obj.get("languageHttp");
            this.orderHttp=(String)obj.get("orderHttp");
            this.pluginsJs=(String)obj.get("pluginsJs");
            this.platformJs=(String)obj.get("platformJs");
            this.cookiesJs=(String)obj.get("cookiesJs");
            this.dntJs=(String)obj.get("dntJs");
            this.timezoneJs=(String)obj.get("timezoneJs");
            this.resolutionJs=(String)obj.get("resolutionJs");
            this.localJs=(String)obj.get("localJs");
            this.sessionJs=(String)obj.get("sessionJs");
            this.ieDataJs=(String)obj.get("ieDataJs");
            this.canvasJs=(String)obj.get("canvasJs");
            this.webGlJs=(String)obj.get("webGlJs");
            this.fontsFlash=(String)obj.get("fontsFlash");
            this.resolutionFlash=(String)obj.get("resolutionFlash");
            this.languageFlash=(String)obj.get("languageFlash");
            this.platformFlash=(String)obj.get("platformFlash");
            this.adBlock=(String)obj.get("adBlock");
            this.vendorJs=(String)obj.get("vendorJs");
            this.rendererJs=(String)obj.get("rendererJs");
            this.octaneScore=(String)obj.get("octaneScore");
            this.sunspiderTime=(String)obj.get("sunspiderTime");
            this.pluginsJsHashed=(String)obj.get("pluginsJsHashed");
            this.canvasJsHashed=(String)obj.get("canvasJsHashed");
            this.webGLJsHashed=(String)obj.get("webGLJsHashed");
            this.fontsFlashHashed=(String)obj.get("fontsFlashHashed");
 

    }	

     public HashMap<String, String> fpToHashMap(){
        HashMap<String, String> fpHashMap = new HashMap<String, String>();

        
            fpHashMap.put("id",id);            
            fpHashMap.put("addressHttp",addressHttp);            
            fpHashMap.put("counter","0");
            //fpHashMap.put("time",time);
            fpHashMap.put("userAgentHttp",userAgentHttp);
            fpHashMap.put("acceptHttp",acceptHttp);
            fpHashMap.put("hostHttp",hostHttp);
            fpHashMap.put("connectionHttp",connectionHttp);
            fpHashMap.put("encodingHttp",encodingHttp);
            fpHashMap.put("languageHttp",languageHttp);
            fpHashMap.put("orderHttp",orderHttp);
            fpHashMap.put("pluginsJs",pluginsJs);
            fpHashMap.put("platformJs",platformJs);
            fpHashMap.put("cookiesJs",cookiesJs);
            fpHashMap.put("dntJs",dntJs);
            fpHashMap.put("timezoneJs",timezoneJs);
            fpHashMap.put("resolutionJs",resolutionJs);
            fpHashMap.put("localJs",localJs);
            fpHashMap.put("sessionJs",sessionJs);
            fpHashMap.put("ieDataJs",ieDataJs);
            fpHashMap.put("canvasJs",canvasJs);
            fpHashMap.put("webGlJs",webGlJs);
            fpHashMap.put("fontsFlash",fontsFlash);
            fpHashMap.put("resolutionFlash",resolutionFlash);
            fpHashMap.put("languageFlash",languageFlash);
            fpHashMap.put("platformFlash",platformFlash);
            fpHashMap.put("adBlock",adBlock);
            fpHashMap.put("vendorJs",vendorJs);
            fpHashMap.put("rendererJs",rendererJs);
            fpHashMap.put("octaneScore",octaneScore);
            fpHashMap.put("sunspiderTime",sunspiderTime);
            fpHashMap.put("pluginsJsHashed",pluginsJsHashed);
            fpHashMap.put("canvasJsHashed",canvasJsHashed);
            fpHashMap.put("webGlJsHashed",webGLJsHashed);
            fpHashMap.put("fontsFlashHashed",fontsFlashHashed);  

        return fpHashMap;
    } 
}