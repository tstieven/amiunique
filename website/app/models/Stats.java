package models;

import java.util.HashMap;
import java.sql.Timestamp;

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

public class Stats{

    private Integer nbTotal;
    private CounterMap timezone;
    private HashMap<String, VersionMap> browsers;
    private HashMap<String, VersionMap> os;
    public VersionMap languages;
    private RangeMap nbFonts;

    public Integer getNbTotal() {
        return nbTotal;
    }

    public CounterMap getTimezone() {
        return timezone;
    }

    public HashMap<String, VersionMap> getBrowsers() {
        return browsers;
    }

    public HashMap<String, VersionMap> getOs() {
        return os;
    }

    public VersionMap getLanguages() {
        return languages;
    }

    public RangeMap getNbFonts() {
        return nbFonts;
    }
   

    public Stats(DBCollection coll){
        FingerprintManager fpm = new FingerprintManager();
        FpDataEntityManager em = new FpDataEntityManager();
        this.nbTotal = em.getNumberOfEntries();
        this.timezone = em.getTimezoneStats();
        HashMap<String,HashMap<String, VersionMap>> resMap = em.getOSBrowserStats();
        this.browsers = resMap.get("browsers");
        this.os = resMap.get("os");
        this.nbFonts = em.getFontsStats();
        //Mongo
        this.languages = fpm.getLanguageStats(coll);
       
    }

    public Stats(Timestamp tsl, Timestamp tsu){
        FpDataEntityManager em = new FpDataEntityManager();
        this.nbTotal = em.getNumberOfEntriesSinceDate(tsl, tsu);
        this.timezone = em.getTimezoneStatsSinceDate(tsl, tsu);
        HashMap<String,HashMap<String, VersionMap>> resMap = em.getOSBrowserStatsSinceDate(tsl, tsu);
        this.browsers = resMap.get("browsers");
        this.os = resMap.get("os");
        this.languages = em.getLanguageStatsSinceDate(tsl, tsu);
        this.nbFonts = em.getFontsStatsSinceDate(tsl, tsu);
    }

   /* private static Stats INSTANCE = new Stats(DBCollection coll);

    public static Stats getInstance(){
        return INSTANCE;
    }*/
    
    public void addLanguage(ParsedFP fp){
      languages.add(fp.getLanguage());  
    }

    public void addFingerprint(ParsedFP fp){
        nbTotal += 1;
        timezone.add(fp.getTimezone());
        browsers.get(fp.getBrowser()).add(fp.getBrowserVersion());
        os.get(fp.getOs()).add(fp.getOsVersion());
        languages.add(fp.getLanguage());
        if(!fp.getNbFonts().equals("NC")) nbFonts.add(fp.getNbFonts());
    }


}
