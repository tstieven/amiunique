/**
 * Created by thomas on 02/06/17.
 */


package settings;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.*;
import play.Application;
import play.Configuration;
import play.GlobalSettings;
import play.Logger;
import play.libs.Json;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public final class Global extends GlobalSettings {
    public static DBCollection collection;
    public static DBCollection combinationStats;
    public static DBCollection nbTotalDB;
    public static HashMap<String, String[]> configHashMap = new HashMap<String, String[]>();

    private static String getAttribute(JsonNode json, String attribute) {
        if (json.get(attribute) == null) {
            return "Not specified";
        } else {
            return json.get(attribute).asText();
        }
    }

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

    public static void print() {
        System.out.println(configHashMap.get("fontsFlash")[1]);
    }


    //use SearchInConfig in every file of a repertory
    private static void listConfig(File path) {
        File files[];
        files = path.listFiles();
        Arrays.sort(files);
        for (int i = 0, n = files.length; i < n; i++) {

            searchInConfig(files[i].toString());

        }
    }

    //get value in a .json
    private static void searchInConfig(String path) {

        JsonNode myJson = null;

        try {
            InputStream is = new FileInputStream(path);
            myJson = Json.parse(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String name = getAttribute(myJson, "name");
        String[] val = new String[]{getAttribute(myJson, "script"), getAttribute(myJson, "enable"), getAttribute(myJson, "hash"),
                getAttribute(myJson, "use in comparison"), getAttribute(myJson, "async"), getAttribute(myJson, "jsrequired"),
                getAttribute(myJson, "flashrequired"), getAttribute(myJson, "display"), getAttribute(myJson, "overview"),
                getAttribute(myJson, "details"), getAttribute(myJson, "graph"), getAttribute(myJson, "supergraph"),
                getAttribute(myJson, "sentence1"), getAttribute(myJson, "sentence2"), getAttribute(myJson, "parse")};
        configHashMap.put(name, val);
    }

    @Override
    public void onStart(Application app) {
        System.out.println("Debut du programme");
        connection();
        Map<String, Object> lu = Configuration.root().asMap();


    }

    @Override
    public void onStop(Application app) {
        Logger.info("Application shutdown...");
    }
}