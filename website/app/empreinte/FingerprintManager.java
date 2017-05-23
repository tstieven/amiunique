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
import java.lang.Integer;

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

public class FingerprintManager {


    private int counter;
    private String id;
    private String addressHttp;
    private Timestamp time;
    private String userAgentHttp;
    private String acceptHttp;
    private String hostHttp;
    private String connectionHttp;
    private String encodingHttp;
    private String languageHttp;
    private String orderHttp;
    private String pluginsJs;
    private String platformJs;
    private String cookiesJs;
    private String dntJs;
    private String timezoneJs;
    private String resolutionJs;
    private String localJs;
    private String sessionJs;
    private String ieDataJs;
    private String canvasJs;
    private String fontsFlash;
    private String resolutionFlash;
    private String languageFlash;
    private String platformFlash;
    private String adBlock;
    private String octaneScore;
    private String sunspiderTime;
    private String webGlJs;
    private String vendorWebGljs;
    private String rendererWebGljs;

    //Hashed Attributes
    private String pluginsJsHashed;
    private String canvasJsHashed;
    private String webGLJsHashed;
    private String fontsFlashHashed;
    private DBCollection coll;

     private <A> A withTransaction(Function<EntityManager, A> f) {
        try {
            return JPA.withTransaction(() -> f.apply(JPA.em()));
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public DBCollection connection(){
        try{

            String s = "spirals";
            char [] pwd = s.toCharArray();
            MongoCredential credential =  MongoCredential.createCredential("root", "test", pwd);

            // To connect to mongodb server
            MongoClient mongoClient = new MongoClient(new ServerAddress("localhost",27017), Arrays.asList(credential));

             // Now connect to your databases
             
             DB db = mongoClient.getDB( "test" );
           System.out.println("Connect to database successfully");

             coll = db.getCollection("testCollection");
            
             }catch(Exception e){
         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      }
      return coll;

    }


   /*private <A> A withTransaction(Function<EntityManager, A> f) {
        try {
            return JPA.withTransaction(() -> f.apply(JPA.em()));
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }*/
    public void oneUp(String id,String userAgentHttp,
                                   String acceptHttp, String encodingHttp,
                                   String languageHttp, String pluginsJsHashed, String platformJs, String cookiesJs,
                                   String dntJs, String timezoneJs, String resolutionJs, String localJs, String sessionJs,
                                   String ieDataJs, String canvasJsHashed, String webGlJsHashed, String fontsFlashHashed, String resolutionFlash,
                                   String languageFlash, String platformFlash, String adBlock){
            BasicDBObject query = new BasicDBObject("id",id)
            .append("userAgentHttp",userAgentHttp)
            .append("acceptHttp",acceptHttp)
            .append("encodingHttp",encodingHttp)
            .append("languageHttp",languageHttp)
            .append("pluginsJsHashed",pluginsJsHashed)
            .append("platformJs",platformJs)
            .append("cookiesJs",cookiesJs)
            .append("dntJs",dntJs)
            .append("timezoneJs",timezoneJs)
            .append("resolutionJs",resolutionJs)
            .append("localJs",localJs)
            .append("sessionJs",sessionJs)
            .append("ieDataJs",ieDataJs)
            .append("canvasJsHashed",canvasJsHashed)
            .append("webGlJsHashed",webGlJsHashed)
            .append("fontsFlashHashed",fontsFlashHashed)
            .append("resolutionFlash",resolutionFlash)
            .append("languageFlash",languageFlash)
            .append("platformFlash",platformFlash)
            .append("adBlock",adBlock);
            DBCursor cursor = coll.find(query);
            try {
                if(cursor.hasNext()) {
                    //System.out.println(cursor.next());
                    DBObject updateDoc = cursor.next();
                    String tmp=(String)updateDoc.get("counter");
                    updateDoc.put("counter",Integer.toString(Integer.parseInt(tmp)+1));
                    coll.update(query,updateDoc);
                    }
                } finally {
                    cursor.close();
                }
           




    }

    public boolean checkIfFPExists(String id,String userAgentHttp,
                                   String acceptHttp, String encodingHttp,
                                   String languageHttp, String pluginsJsHashed, String platformJs, String cookiesJs,
                                   String dntJs, String timezoneJs, String resolutionJs, String localJs, String sessionJs,
                                   String ieDataJs, String canvasJsHashed, String webGlJsHashed, String fontsFlashHashed, String resolutionFlash,
                                   String languageFlash, String platformFlash, String adBlock){

            BasicDBObject query = new BasicDBObject("id",id)
            .append("userAgentHttp",userAgentHttp)
            .append("acceptHttp",acceptHttp)
            .append("encodingHttp",encodingHttp)
            .append("languageHttp",languageHttp)
            .append("pluginsJsHashed",pluginsJsHashed)
            .append("platformJs",platformJs)
            .append("cookiesJs",cookiesJs)
            .append("dntJs",dntJs)
            .append("timezoneJs",timezoneJs)
            .append("resolutionJs",resolutionJs)
            .append("localJs",localJs)
            .append("sessionJs",sessionJs)
            .append("ieDataJs",ieDataJs)
            .append("canvasJsHashed",canvasJsHashed)
            .append("webGlJsHashed",webGlJsHashed)
            .append("fontsFlashHashed",fontsFlashHashed)
            .append("resolutionFlash",resolutionFlash)
            .append("languageFlash",languageFlash)
            .append("platformFlash",platformFlash)
            .append("adBlock",adBlock);
        
        int bool=0;    
        
            DBCursor cursor = coll.find(query);
            try {
                if(cursor.hasNext()) {
                    //System.out.println(cursor.next());
                    bool=1;
                    }
                } finally {
                    cursor.close();
                }
           
            //return ((Long) q.getResultList().get(0)).intValue();
       
        return bool == 1;
    }

   public boolean checkIfFPWithNoJsExists(String id,String userAgentHttp,
                                   String acceptHttp, String encodingHttp,
                                   String languageHttp){

    BasicDBObject query = new BasicDBObject("id",id)
            .append("userAgentHttp",userAgentHttp)
            .append("acceptHttp",acceptHttp)
            .append("encodingHttp",encodingHttp)
            .append("languageHttp",languageHttp)
            .append("pluginsJs","no JS");
      
        int bool=0;    
        
            DBCursor cursor = coll.find(query);
            try {
                if(cursor.hasNext()) {
                    //System.out.println(cursor.next());
                    bool=1;
                    }
                } finally {
                    cursor.close();
                }
       
        return bool == 1;
    }





    public Fingerprint getExistingFPById(String id){

        BasicDBObject query = new BasicDBObject("id",id);                   
        DBCursor cursor = coll.find(query);
        DBObject obj;
        
        
        obj = cursor.next();
        Fingerprint fp=new Fingerprint(obj);
        cursor.close();
        return fp;                   
                      
    }
/*

    public FpDataEntity getExistingFPByCounter(int counter){
        return withTransaction(em -> em.find(FpDataEntity.class,counter));
    }


    public TreeSet<FpDataEntity> getExistingFPsById(String id){
        String query = "SELECT counter FROM FpDataEntity WHERE id= :id";
        List<Integer> counters= withTransaction(em -> (em.createQuery(query).setParameter("id", id).getResultList()));

        TreeSet<FpDataEntity> fps = new TreeSet<FpDataEntity>();
        for(int c : counters){
            fps.add(getExistingFPByCounter(c));
        }

        return fps;
    }*/

    //Done
    public Fingerprint createFull(String id, String addressHttp, Timestamp time, String userAgentHttp,
                                   String acceptHttp, String hostHttp, String connectionHttp, String encodingHttp,
                                   String languageHttp, String orderHttp, String pluginsJs, String platformJs, String cookiesJs,
                                   String dntJs, String timezoneJs, String resolutionJs, String localJs, String sessionJs,
                                   String ieDataJs, String canvasJs, String webGlJs, String fontsFlash, String resolutionFlash,
                                   String languageFlash, String platformFlash, String adBlock, String vendorJs,
                                   String rendererJs, String octaneScore, String sunspiderTime, String pluginsJsHashed,
                                   String canvasJsHashed, String webGLJsHashed, String fontsFlashHashed) {
        
                 
             //return withTransaction(em -> {
        
            Fingerprint fp = new Fingerprint(id,addressHttp,time,userAgentHttp,acceptHttp,hostHttp,connectionHttp,encodingHttp,languageHttp,
                orderHttp,pluginsJs,platformJs,cookiesJs,dntJs,timezoneJs,resolutionJs,localJs,sessionJs,ieDataJs,canvasJs,webGlJs,fontsFlash,
                resolutionFlash,languageFlash,platformFlash,adBlock,vendorJs,rendererJs,octaneScore,sunspiderTime,pluginsJsHashed,canvasJsHashed,
                webGLJsHashed,fontsFlashHashed);
           
            //em.persist(fp);
            return fp;
        //});       
        
    }

    public Fingerprint createWithoutFlash(String id, String addressHttp, Timestamp time, String userAgentHttp,
                                           String acceptHttp, String hostHttp, String connectionHttp, String encodingHttp,
                                           String languageHttp, String orderHttp, String pluginsJs, String platformJs, String cookiesJs,
                                           String dntJs, String timezoneJs, String resolutionJs, String localJs, String sessionJs,
                                           String ieDataJs, String canvasJs, String webGlJs, String adBlock, String vendorJs, String rendererJs,
                                           String octaneScore, String sunspiderTime, String pluginsJsHashed,
                                           String canvasJsHashed, String webGLJsHashed) {
        return createFull(id, addressHttp, time, userAgentHttp,
                acceptHttp, hostHttp, connectionHttp, encodingHttp,
                languageHttp, orderHttp, pluginsJs, platformJs, cookiesJs,
                dntJs, timezoneJs, resolutionJs, localJs, sessionJs,
                ieDataJs, canvasJs, webGlJs, "", "",
                "", "", adBlock, vendorJs, rendererJs, octaneScore, sunspiderTime,
                pluginsJsHashed, canvasJsHashed, webGLJsHashed, "");

    }


    public Fingerprint createWithoutJavaScript(String id, String addressHttp, Timestamp time, String userAgentHttp,
                                                String acceptHttp, String hostHttp, String connectionHttp, String encodingHttp,
                                                String languageHttp, String orderHttp) {
        String noJs = "no JS";
        return createFull(id, addressHttp, time, userAgentHttp,
                acceptHttp, hostHttp, connectionHttp, encodingHttp,
                languageHttp, orderHttp, noJs, noJs, noJs,
                noJs, noJs, noJs, noJs, noJs,
                noJs, noJs, noJs, noJs, noJs,
                noJs, noJs, noJs, noJs, noJs,
                noJs, noJs, noJs, noJs, noJs, noJs);
    }


    public Map<String,Double> getPercentages(JsonNode values) {

        //Get the total number of entries in the database
        String nbTotalQuery = "SELECT count(*) FROM FpDataEntity";
        double nbTotal = withTransaction(em -> ((Long) em.createQuery(nbTotalQuery).getResultList().get(0)).doubleValue());
        
         //For each attribute
         //-> computation of the percentage of each value
         
        //Query to get the number of entries with the same value
        String nbSameValueBaseQuery = "SELECT count(*) FROM FpDataEntity WHERE ";//Add attribute = value
        HashMap<String,Double> percentage = new HashMap<>();

        Iterator<String> it = values.fieldNames();
        while(it.hasNext()) {
            String column = it.next();

            //Computation of the percentage
            String nbSameValueQuery = nbSameValueBaseQuery+column+" = :"+column;
            double nbSameValue = withTransaction(em -> ((Long) em.createQuery(nbSameValueQuery)
                    .setParameter(column, (values.get(column).asText()).replace("\"", "'"))
                    .getResultList().get(0)).doubleValue());
            if(nbSameValue != 1.0) {
                percentage.put(column, nbSameValue * 100 / nbTotal);
            } else {
                percentage.put(column, -1.0);
            }
        }
        return percentage;
    }
/*
    public int getNumberOfIdenticalFingerprints(JsonNode values){
        String query = "SELECT count(*) FROM FpDataEntity WHERE ";

        Iterator<String> it = values.fieldNames();
        String column = it.next();
        query+=column+" = :"+column;

        //Building query
        while(it.hasNext()) {
            column = it.next();
            query+=" AND "+column+" = :"+column;
        }
        String finalQuery = query;

        return withTransaction(em -> {
            Iterator<String> itQ = values.fieldNames();
            String col = itQ.next();
            Query q = em.createQuery(finalQuery);
            q.setParameter(col,(values.get(col).asText()).replace("\"", "'"));
            while(itQ.hasNext()) {
              col = itQ.next();
              q.setParameter(col,(values.get(col).asText()).replace("\"", "'"));
            }
            return ((Long) q.getResultList().get(0)).intValue();
        });
    }

    public int getNumberOfEntries(){
        String nbTotalQuery = "SELECT count(*) FROM FpDataEntity";
        return withTransaction(em -> ((Long) em.createQuery(nbTotalQuery).getResultList().get(0)).intValue());
    }

    public int getNumberOfEntriesSinceDate(Timestamp tsl, Timestamp tsu){
        String nbTotalQuery = "SELECT count(*) FROM FpDataEntity WHERE time BETWEEN :lower AND :upper";
        return withTransaction(em -> ((Long) em.createQuery(nbTotalQuery)
            .setParameter("lower",tsl)
            .setParameter("upper",tsu)
            .getResultList().get(0)).intValue());
    }

    public CounterMap getTimezoneStats(){
        String timeQuery = "SELECT timezoneJS, count(timezoneJS) FROM fpData GROUP BY timezoneJS";
        ArrayList<Object[]> q = withTransaction(em -> (ArrayList<Object[]>)  (em.createNativeQuery(timeQuery).getResultList()));

        CounterMap res = new CounterMap();
        for(Object[] obj:  q){
            res.add(obj[0].toString(), obj[1].toString());
        }
        return res;
    }

    public CounterMap getTimezoneStatsSinceDate(Timestamp tsl, Timestamp tsu){
        String timeQuery = "SELECT timezoneJS, count(timezoneJS) FROM fpData WHERE time BETWEEN :lower AND :upper GROUP BY timezoneJS";
        ArrayList<Object[]> q = withTransaction(em -> (ArrayList<Object[]>)  (em.createNativeQuery(timeQuery)
            .setParameter("lower",tsl)
            .setParameter("upper",tsu)
            .getResultList()));

        CounterMap res = new CounterMap();
        for(Object[] obj:  q){
            res.add(obj[0].toString(), obj[1].toString());
        }
        return res;
    }*/
    


    public VersionMap getLanguageStats(DBCollection coll){


        BasicDBObject query = new BasicDBObject(); 
        List<String> langList = new ArrayList<String>();                  
        DBCursor cursor = coll.find(query);
        
            try {
                while(cursor.hasNext()) { 
                    DBObject getDoc = cursor.next();
                    int cpt = Integer.parseInt((String)getDoc.get("counter"));
                    System.out.println(cpt);
                    int i;
                    for (i=0;i<cpt;i++){
                        langList.add((String)getDoc.get("languageHttp"));   
                    }                      
                 }  
                } finally {
                    cursor.close(); 
                }
        VersionMap res = parseLanguages(langList);      
        return res;
    }

    
    /*
    public VersionMap getLanguageStatsSinceDate(Timestamp tsl, Timestamp tsu){
        String query = "SELECT languageHttp FROM FpDataEntity WHERE time BETWEEN :lower AND :upper";
        return withTransaction(em -> {
            Session session = (Session) em.getDelegate();
            StatelessSession stateless = session.getSessionFactory().openStatelessSession();
            stateless.beginTransaction();
            ScrollableResults langList = stateless.createQuery(query)
                    .setParameter("lower",tsl)
                    .setParameter("upper",tsu)
                    .scroll(ScrollMode.FORWARD_ONLY);

            VersionMap res = parseLanguages(langList);

            stateless.getTransaction().commit();
            stateless.close();
            return res;
        });
    }*/

    public VersionMap parseLanguages(List<String> langList){
        Iterator it=langList.iterator();
        Pattern langP = Pattern.compile("^(\\S\\S)");
        VersionMap langMap = new VersionMap();
        while (it.hasNext()) {
             Matcher langM = langP.matcher((String) it.next());
             if(langM.find()) {
                 langMap.add(langM.group(1));
             } else {
                 langMap.add("Not communicated");
             }
        }
        return langMap;
    }

/*
    public HashMap<String,HashMap<String, VersionMap>> getOSBrowserStats(){
        String query = "SELECT userAgentHttp FROM FpDataEntity";
        return withTransaction(em -> {
            Session session = (Session) em.getDelegate();
            StatelessSession stateless = session.getSessionFactory().openStatelessSession();
            stateless.beginTransaction();
            ScrollableResults userAgentList = stateless.createQuery(query).scroll(ScrollMode.FORWARD_ONLY);

            HashMap<String,HashMap<String, VersionMap>> res = parseUserAgents(userAgentList);

            stateless.getTransaction().commit();
            stateless.close();
            return res;
        });
    }

    public HashMap<String,HashMap<String, VersionMap>> getOSBrowserStatsSinceDate(Timestamp tsl, Timestamp tsu){
        String query = "SELECT userAgentHttp FROM FpDataEntity WHERE time BETWEEN :lower AND :upper";
        return withTransaction(em -> {
            Session session = (Session) em.getDelegate();
            StatelessSession stateless = session.getSessionFactory().openStatelessSession();
            stateless.beginTransaction();
            ScrollableResults userAgentList = stateless.createQuery(query)
                    .setParameter("lower",tsl)
                    .setParameter("upper",tsu)
                    .scroll(ScrollMode.FORWARD_ONLY);

            HashMap<String,HashMap<String, VersionMap>> res = parseUserAgents(userAgentList);

            stateless.getTransaction().commit();
            stateless.close();
            return res;
        });
    }

    public HashMap<String,HashMap<String, VersionMap>> parseUserAgents(ScrollableResults userAgents){

        /* Browser 
        HashMap<String, VersionMap> browsers = new HashMap<>();
        browsers.put("Firefox", new VersionMap());
        browsers.put("Chrome", new VersionMap());
        browsers.put("Safari", new VersionMap());
        browsers.put("IE", new VersionMap());
        browsers.put("Edge", new VersionMap());
        browsers.put("Opera", new VersionMap());
        browsers.put("Others", new VersionMap());

        /* OS 
        HashMap<String, VersionMap> os  = new HashMap<>();
        os.put("Windows", new VersionMap());
        os.put("Linux", new VersionMap());
        os.put("Mac", new VersionMap());
        os.put("Android", new VersionMap());
        os.put("iOS", new VersionMap());
        os.put("Windows Phone", new VersionMap());
        os.put("Others", new VersionMap());


        /* Parse user agents 
         while (userAgents.next()) {
            ParsedFP ua = new ParsedFP((String) userAgents.get(0));
            browsers.get(ua.getBrowser()).add(ua.getBrowserVersion());
            os.get(ua.getOs()).add(ua.getOsVersion());
        }

        HashMap<String,HashMap<String, VersionMap>> res = new HashMap<>();
        res.put("browsers",browsers);
        res.put("os",os);
        return res;
    }

    public RangeMap getFontsStats(){
        String query = "SELECT fontsFlash FROM FpDataEntity";
        return withTransaction(em -> {
            Session session = (Session) em.getDelegate();
            StatelessSession stateless = session.getSessionFactory().openStatelessSession();
            stateless.beginTransaction();
            ScrollableResults fontsList = stateless.createQuery(query).scroll(ScrollMode.FORWARD_ONLY);

            RangeMap nbFontsMap = parseFonts(fontsList);

            stateless.getTransaction().commit();
            stateless.close();
            return nbFontsMap;
        });
    }

    public RangeMap getFontsStatsSinceDate(Timestamp tsl, Timestamp tsu){
        String query = "SELECT fontsFlash FROM FpDataEntity WHERE time BETWEEN :lower AND :upper";
        return withTransaction(em -> {
            Session session = (Session) em.getDelegate();
            StatelessSession stateless = session.getSessionFactory().openStatelessSession();
            stateless.beginTransaction();
            ScrollableResults fontsList = stateless.createQuery(query)
                    .setParameter("lower",tsl)
                    .setParameter("upper",tsu)
                    .scroll(ScrollMode.FORWARD_ONLY);

            RangeMap nbFontsMap = parseFonts(fontsList);

            stateless.getTransaction().commit();
            stateless.close();
            return nbFontsMap;
        });
    }

    public RangeMap parseFonts(ScrollableResults fontsList){
        RangeMap nbFontsMap = new RangeMap();

        while (fontsList.next()) {
            String fonts = (String) fontsList.get(0);
            Integer nbFonts = fonts.split("_").length;
            if (nbFonts > 2) {
                int step = 50;
                int j = step;
                while (j < nbFonts) {
                    j += step;
                }
                nbFontsMap.add((j - step) + "-" + j);
            }
        }
        return nbFontsMap;
    }


    public ArrayList<String> getAttribute(String attribute){
        String query = "SELECT "+attribute+" FROM FpDataEntity";
        ArrayList<String> listAttribute = withTransaction(em -> ((ArrayList<String>) em.createQuery(query).getResultList()));

        return listAttribute;
    }

    public List<Object[]> getStatsAttribute(String attribute){
        String query = "SELECT "+attribute+", count(*) AS nbAtt FROM FpDataEntity Group By "+attribute;

        List<Object[]> result = withTransaction(em -> ((List<Object[]>) em.createQuery(query).getResultList()));
        return result;

    }*/


}
