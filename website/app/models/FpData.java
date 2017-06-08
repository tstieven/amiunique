package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import controllers.FPController;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FpData {

    public static HashMap<String, Object> fpHashMap = new HashMap<String, Object>();
    private static HashMap<String, Object> hashedHashMap = new HashMap<String, Object>();
    private static HashMap<String, HashMap<String, String>> configHashMap = new HashMap<String, HashMap<String, String>>();

    private static final String configEnable = "enable";
    private static final String configComparison = "use in comparison";
    private static final String configOverview = "overview";
    private static final String configDetails = "details";
    private static final String configGraph = "graph";
    private static final String configHashed = "hash";



    //Call in FPController.addFingerprint() in order to set fpHashMap
    public FpData(JsonNode json, HashMap<String, HashMap<String, String>> confHashMap) {
        FPController fpControl = new FPController();
        fpHashMap.put("id", fpControl.getId());
        fpHashMap.put("ip", fpControl.getIp());
        fpHashMap.put("time", fpControl.getTime());
        configHashMap = confHashMap;

        Iterator it = configHashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String name = (String) pair.getKey();
            HashMap<String, String> config = ((HashMap<String, String>) pair.getValue());
            setFingerprintInHashMap(json, name, config);
        }
    }

    //Call in FPController.fpNoJs() in order to set fpHashMap
    public FpData(HashMap<String, HashMap<String, String>> confHashMap, String user, String accept, String encoding, String language) {
        FPController fpControl = new FPController();
        fpHashMap.put("id", fpControl.getId());
        fpHashMap.put("ip", fpControl.getIp());
        fpHashMap.put("time", fpControl.getTime());
        configHashMap = confHashMap;
        Iterator it = configHashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String name = (String) pair.getKey();
            HashMap<String, String> config = ((HashMap<String, String>) pair.getValue());
            String value;
            switch (name) {
                case "userAgentHttp":
                    value = user;
                    break;
                case "acceptHttp":
                    value = accept;
                    break;
                case "encodingHttp":
                    value = encoding;
                    break;
                case "languageHttp":
                    value = language;
                    break;
                default:
                    value = "NoJs";
                    break;
            }
            setFpNoJsInHashMap(name, config, value);
        }
    }

    //Call in FpData() if FpData is called in fpNoJs
    // in order to put value in fpHashMap in the configuration file allow it
    private static void setFpNoJsInHashMap(String name, HashMap<String, String> config, String value) {

        if (config.get(configEnable).equals("True")) {
            fpHashMap.put(name, value);
            if (config.get(configHashed).equals("True")) {
                hashedHashMap.put(name, DigestUtils.sha1Hex(value));
            }
        }
    }

    //Call in FpData() if FpData is called in addFingerprint
    // in order to put value in fpHashMap in the configuration file allow it
    private static void setFingerprintInHashMap(JsonNode json, String name, HashMap<String, String> config) {
        if (config.get(configEnable).equals("True")) {
            fpHashMap.put(name, getAttribute(json, name));
            if (config.get(configHashed).equals("True")) {
                hashedHashMap.put(name, DigestUtils.sha1Hex(getAttribute(json, name)));

            }
        }
    }

    //return the value of an attribute
    private static String getAttribute(JsonNode json, String attribute) {
        if (json.get(attribute) == null) {
            return "Not specified";
        } else {
            return json.get(attribute).asText();
        }
    }

    //call in saveAndGetPluginsStatsInHashMap()
    // in order to get a list of plugins
    private static List<String> getPluginsInList() {

        List<String> listPlugins = new ArrayList<>();
        if (configHashMap.get("pluginsJs").get(configEnable).equals("True")) {

            String patternStringPlugin = "Plugin [0-9]+: ([a-zA-Z -.]+)";
            Pattern pattern = Pattern.compile(patternStringPlugin);
            Matcher matcher = pattern.matcher((String) fpHashMap.get("pluginsJs"));
            while (matcher.find()) {
                listPlugins.add(matcher.group(1));
            }
        }
        return listPlugins;
    }

    //Call in FPController.addFingerprint() in order to return
    //an HashMap with the name of the plugin as key and its percentages of presence as value

    //It also update the database "combinationStats"
    //It create a new table if the plugin is not in the database
    //Or it increment by 1 the counter of this table
    public HashMap<String, Double> saveAndGetPluginsStatsInHashMap(DBCollection combination) {
        FPController fpControl = new FPController();
        int total = fpControl.getNbTotal();
        List<String> listPlugins = getPluginsInList();
        HashMap<String, Double> pluginsHashMap = new HashMap<>();
        int cpt = 0;
        for (String plugin : listPlugins) {
            BasicDBObject query = new BasicDBObject("combination", plugin);
            DBCursor cursor = combination.find(query);
            if (cursor.hasNext()) {
                DBObject updateDoc = cursor.next();
                String tmp = (String) updateDoc.get("counter");
                cpt = Integer.parseInt(tmp) + 1;
                updateDoc.put("counter", Integer.toString(cpt));
                combination.update(query, updateDoc);
                pluginsHashMap.put(plugin, ((double) cpt / (double) total) * 100);

            } else {
                BasicDBObject combi = new BasicDBObject();
                combi.put("combination", plugin);
                combi.put("indicator", "plugin");
                combi.put("counter", "1");
                pluginsHashMap.put(plugin, ((double) 1 / (double) total) * 100);
                combination.insert(combi);
            }
            cursor.close();
        }
        return pluginsHashMap;
    }

    //Call in saveInDataBases() in order to update the database "combinationStats"
    //It create a new table if this value is not in the database
    //Or it increment by 1 the counter of this table
    private void saveInCombinationStats(DBCollection collection, BasicDBObject query, String combination, String indicator) {
        DBCursor cursor = collection.find(query);
        int cpt= 0;
        try {
            if (cursor.hasNext()) {
                DBObject updateDoc = cursor.next();
                String tmp = (String) updateDoc.get("counter");
                cpt = Integer.parseInt(tmp) + 1;
                updateDoc.put("counter", Integer.toString(cpt));
                collection.update(query, updateDoc);

            } else {
                BasicDBObject combi = new BasicDBObject();
                combi.put("combination", combination);
                combi.put("indicator", indicator);
                combi.put("counter", "1");
                collection.insert(combi);
            }
        } finally {
            cursor.close();
        }
    }

    //Call in saveInDataBases() in order to update the database "nbTotal"
    //It create a new table if this value is not in the database
    // Or it increment by 1 the counter of this table
    private void saveInNbTotal(DBCollection collection, String indicator) {
        BasicDBObject nbTotalQuery = new BasicDBObject("indicator", indicator);
        DBCursor cursor = collection.find(nbTotalQuery);
        int cpt=0;
        try {
            if (cursor.hasNext()) {
                DBObject updateDoc = cursor.next();
                String tmp = (String) updateDoc.get("counter");
                cpt = Integer.parseInt(tmp) + 1;
                updateDoc.put("counter", Integer.toString(cpt));
                collection.update(nbTotalQuery, updateDoc);

            } else {
                BasicDBObject nb = new BasicDBObject("indicator", indicator);
                nb.put("counter", "1");
                collection.insert(nb);
            }
        } finally {
            cursor.close();
        }
    }

    //Call in FPController.addFingerprint() and FPController.fpNoJs()
    //Save the fingerprint in testCollection, and update combinationStats and nbTotal
    public void saveInDataBases(DBCollection coll, DBCollection combination, DBCollection nbtotal) {

        if ((!(getIfExist(coll))) || (fpHashMap.get("id").equals("Not supported"))) {
            BasicDBObject doc = new BasicDBObject();
            Iterator it = fpHashMap.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                doc.put((String) pair.getKey(), (String) pair.getValue());

                if ((pair.getKey() != "ip") && (pair.getKey() != "time") && (pair.getKey() != "id")) {

                    if (configHashMap.get(pair.getKey()).get(configHashed).equals("True")) {

                        BasicDBObject combinationQuery = new BasicDBObject("combination", hashedHashMap.get(pair.getKey()));
                        combinationQuery.put("indicator", pair.getKey());
                        saveInCombinationStats(combination, combinationQuery, (String) hashedHashMap.get(pair.getKey()), (String) pair.getKey());

                        saveInNbTotal(nbtotal, (String) pair.getKey());

                        doc.put(pair.getKey() + "Hashed", hashedHashMap.get(pair.getKey()));

                    } else {

                        BasicDBObject combinationQuery = new BasicDBObject("combination", pair.getValue());
                        combinationQuery.put("indicator", pair.getKey());
                        saveInCombinationStats(combination, combinationQuery, (String) pair.getValue(), (String) pair.getKey());

                        saveInNbTotal(nbtotal, (String) pair.getKey());
                    }
                }
            }
            System.out.println("save");
            coll.insert(doc);
        }
    }

    //Call in saveInDataBases
    //return true if the same fingerprint exist (with the same id)
    private static boolean getIfExist(DBCollection coll) {
        BasicDBObject query = getQueryForIfExist();
        DBCursor cursor = coll.find(query);
        return (cursor.hasNext());
    }

    //Call in getIfExist()
    //return the query for searching the same fingerprint.
    private static BasicDBObject getQueryForIfExist() {

        BasicDBObject query = new BasicDBObject();
        query.put("id", fpHashMap.get("id"));
        Iterator it = configHashMap.entrySet().iterator();

        while (it.hasNext()) {

            Map.Entry pair = (Map.Entry) it.next();
            String name = (String) pair.getKey();

            if ((name != "id") && (name != "ip") && (name != "time")) {

                HashMap<String, String> config = ((HashMap<String, String>) pair.getValue());
                putInQuery(query, name, config);
            }
        }
        return query;
    }

    //Call in getQueryForIfExist()
    //Put value in the query on the basis of the configuration file
    private static void putInQuery(BasicDBObject query, String name, HashMap<String, String> config) {

        if ((config.get(configEnable).equals("True")) && (config.get(configComparison).equals("True"))) {

            if (config.get(configHashed).equals("True")) {
                query.put(name + "Hashed", hashedHashMap.get(name));
            } else {
                query.put(name, fpHashMap.get(name));
            }

        } else if (config.get(configEnable).equals("False")) {

            query.put(name, null);
            if (config.get(configHashed).equals("True")) {
                query.put(name + "Hashed", null);
            }
        }
    }

    //Call in FPController.addFingerprint() and FPController.fpNoJs
    //Return a HashMap with the value of an attribute as key
    // and the percentage of the presence of this value as value
    public static HashMap<String, Double> getEachPercentage(DBCollection combi, DBCollection nbtotal) {

        HashMap<String, Double> eachPercentageMap = new HashMap<String, Double>();
        Iterator it = configHashMap.entrySet().iterator();
        int counter = 0;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String name = (String) pair.getKey();
            HashMap<String, String> config = ((HashMap<String, String>) pair.getValue());
            if ((config.get(configEnable).equals("True")) && ((!(config.get(configOverview).equals("0"))) || (!(config.get(configDetails).equals("0"))) || (!(config.get(configGraph).equals("0"))))) {

                BasicDBObject query = new BasicDBObject("indicator", pair.getKey());
                DBCursor cursor = nbtotal.find(query);
                if (cursor.hasNext()) {
                    DBObject updateDoc = cursor.next();
                    counter = Integer.parseInt((String) updateDoc.get("counter"));
                    cursor.close();
                }
                eachPercentageMap.put(name, ((double) getNbAttribute(combi, name) * (double) 100 / (double) counter));
            }
        }
        return eachPercentageMap;
    }

    //Call in getEachPercentage
    //return the number of presence for a value of an attribute
    private static int getNbAttribute(DBCollection combi, String name) {
        int cpt = 0;
        BasicDBObject query = new BasicDBObject("indicator", name);
        if (configHashMap.get(name).get(configHashed).equals("True")) {
            query.put("combination", hashedHashMap.get(name));
        } else {
            query.put("combination", fpHashMap.get(name));
        }
        DBCursor cursor = combi.find(query);
        if (cursor.hasNext()) {
            DBObject doc = cursor.next();
            cpt = Integer.parseInt((String) doc.get("counter"));
        }
        cursor.close();
        return cpt;
    }
}