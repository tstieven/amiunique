package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.CombinationStatsEntityManager;
import models.FpDataEntity;
import models.FpDataEntityManager;
import models.Stats;
import play.cache.Cache;
import play.libs.Crypto;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.stats;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

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

public class StatsController extends Controller{

    /*
    Method called through a form on "stats" page
    It receives 2 dates (upper and lower)
    It reloads the page with new parameters for the charts
    */
    public static Result statsTime(){
        Map<String, String[]> vals = request().body().asFormUrlEncoded();
        String datelString = vals.get("datel")[0];
        String dateuString = vals.get("dateu")[0];
        String typeReq = vals.get("typereq")[0];

        //We check if the information is already in cache
        if((typeReq.equals("month") && Cache.get("monthStats") != null) || (typeReq.equals("week") && Cache.get("weekStats") != null)){
            Stats s = (Stats) Cache.get(typeReq+"Stats");

            return ok(stats.render(s.getNbTotal(), Json.toJson(s.getTimezone()), Json.toJson(s.getBrowsers()),
                    Json.toJson(s.getOs()), Json.toJson(s.getLanguages()), Json.toJson(s.getNbFonts()), datelString, dateuString, typeReq));
        }

        //Only if custom or if the information is not in cache
        try{
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date datel = dateFormat.parse(datelString);
            Date dateu = dateFormat.parse(dateuString);

            Timestamp datelTs = new Timestamp(datel.getTime());
            Timestamp dateuTs = new Timestamp(dateu.getTime());

            Stats s = new Stats(datelTs, dateuTs);

            //We check if the cache is empty
            if(typeReq.equals("month") && Cache.get("monthStats") == null){
                Cache.set("monthStats", s, 60*60*24);
            }else if(typeReq.equals("week") && Cache.get("weekStats") == null){
                Cache.set("weekStats", s, 60*60*12);
            }
            //The case for "all" is managed by controllers.stats

            return ok(stats.render(s.getNbTotal(),Json.toJson(s.getTimezone()),Json.toJson(s.getBrowsers()),
                    Json.toJson(s.getOs()),Json.toJson(s.getLanguages()),Json.toJson(s.getNbFonts()), datelString, dateuString, typeReq));

        }catch(ParseException e){
            System.out.println("Parse exception : "+e);
        }

        return redirect("/stats");
    }
}
