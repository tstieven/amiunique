package models;


import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import controllers.FPController;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class FpData {

    public static HashMap<String, Object> fpHashMap = new HashMap<String, Object>();
    public static HashMap<String, HashMap<String, String>> configHashMap = new HashMap<String, HashMap<String, String>>();
    public static Integer counter;
    private static FPController fpControl = new FPController();

    public FpData(JsonNode json, HashMap<String, HashMap<String, String>> confHashMap) {

        fpHashMap.put("id", fpControl.getId());
        fpHashMap.put("ip", fpControl.getIp());
        fpHashMap.put("time", fpControl.getTime());
        configHashMap = confHashMap;

        Iterator it = configHashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String name = (String) pair.getKey();
            HashMap<String, String> config = ((HashMap<String, String>) pair.getValue());
            addInHashMap(json, name, config);
        }
    }


    public void save(DBCollection coll, DBCollection combination, DBCollection nbtotal) {

        if ((!(getExisting(coll))) || (fpHashMap.get("id").equals("Not supported"))) {
            BasicDBObject doc = new BasicDBObject();

            Iterator it = fpHashMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                doc.put((String) pair.getKey(), (String) pair.getValue());
                if ((pair.getKey() != "ip") && (pair.getKey() != "time") && (pair.getKey() != "id")) {

                    BasicDBObject query = new BasicDBObject("combination", pair.getValue());
                    DBCursor cursor = combination.find(query);
                    try {
                        if (cursor.hasNext()) {
                            DBObject updateDoc = cursor.next();
                            String tmp = (String) updateDoc.get("counter");
                            counter = Integer.parseInt(tmp) + 1;
                            updateDoc.put("counter", Integer.toString(counter));
                            combination.update(query, updateDoc);

                        } else {
                            BasicDBObject combi = new BasicDBObject();
                            combi.put("combination", pair.getValue());
                            combi.put("indicator", pair.getKey());
                            combi.put("counter", "1");
                            combination.insert(combi);
                        }
                    } finally {
                        cursor.close();
                    }

                    BasicDBObject query2 = new BasicDBObject("indicator", pair.getKey());
                    DBCursor cursor2 = nbtotal.find(query2);
                    try {
                        if (cursor2.hasNext()) {
                            DBObject updateDoc = cursor2.next();
                            String tmp = (String) updateDoc.get("counter");
                            counter = Integer.parseInt(tmp) + 1;
                            updateDoc.put("counter", Integer.toString(counter));
                            nbtotal.update(query2, updateDoc);

                        } else {
                            BasicDBObject nb = new BasicDBObject("indicator", pair.getKey());
                            nb.put("counter", "1");
                            nbtotal.insert(nb);
                        }
                    } finally {
                        cursor.close();
                    }
                }
            }
            System.out.println("save");
            coll.insert(doc);
        }
    }

    public static boolean getExisting(DBCollection coll) {
        BasicDBObject query = new BasicDBObject();
        throughDBExisting(query);
        System.out.println(query);
        DBCursor cursor = coll.find(query);
        return (cursor.hasNext());
    }


    public static void throughDBExisting(BasicDBObject query) {

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
    }


    public static HashMap<String, Double> getEachPercentage(DBCollection combi, DBCollection nbtotal) {

        HashMap<String, Double> nbMap = new HashMap<String, Double>();
        Iterator it = configHashMap.entrySet().iterator();
        int counter = 0;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String name = (String) pair.getKey();
            HashMap<String, String> config = ((HashMap<String, String>) pair.getValue());
            if ((config.get("enable").equals("True")) && ((config.get("overview").equals("True")) || (config.get("details").equals("True")) || (config.get("graph").equals("True")))) {

                BasicDBObject query = new BasicDBObject("indicator", pair.getKey());
                DBCursor cursor = nbtotal.find(query);
                if (cursor.hasNext()) {
                    DBObject updateDoc = cursor.next();
                    counter = Integer.parseInt((String) updateDoc.get("counter"));
                    cursor.close();
                }
                nbMap.put(name, ((double) getNbAttribute(combi, name) * (double) 100 / (double) counter));


            }

        }
        return nbMap;
    }

    public static String getAttribute(JsonNode json, String attribute) {
        if (json.get(attribute) == null) {
            return "Not specified";
        } else {
            return json.get(attribute).asText();
        }
    }


    public static void addInHashMap(JsonNode json, String name, HashMap<String, String> config) {
        if (config.get("enable").equals("True")) {
            fpHashMap.put(name, getAttribute(json, name));
            if (config.get("hash").equals("True")) {
                fpHashMap.put(name + "Hashed", DigestUtils.sha1Hex(getAttribute(json, name + "Hashed")));

            }
        }
    }

    public static void putInQuery(BasicDBObject query, String name, HashMap<String, String> config) {
        if ((config.get("enable").equals("True")) && (config.get("use in comparison").equals("True"))) {
            query.put(name, fpHashMap.get(name));
            if (config.get("hash").equals("True")) {
                query.put(name + "Hashed", fpHashMap.get(name + "Hashed"));
            }

        } else {
            if (config.get("enable").equals("False")) {
                query.put(name, null);
                if (config.get("hash").equals("True")) {
                    query.put(name + "Hashed", null);
                }
            }
        }
    }


    public static int getNbAttribute(DBCollection combi, String name) {
        int cpt = 0;
        BasicDBObject query = new BasicDBObject("indicator", name);
        query.put("combination", fpHashMap.get(name));
        DBCursor cursor = combi.find(query);
        if (cursor.hasNext()) {
            DBObject doc = cursor.next();
            cpt = Integer.parseInt((String) doc.get("counter"));
        }
        cursor.close();
        return cpt;
    }
}