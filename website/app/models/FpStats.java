package models;


import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import java.util.HashMap;


public class FpStats {


    public HashMap<String, Double> getAttributeStats(DBCollection coll, String attr, int nbtotal) {

        HashMap<String, Double> fpAttributeStats = new HashMap<String, Double>();
        BasicDBObject query = new BasicDBObject("indicator", attr);

        // A changer

        DBCursor cursor = coll.find(query);
        try {
            while (cursor.hasNext()) {
                DBObject getDoc = cursor.next();
                int cpt = Integer.parseInt((String) getDoc.get("counter"));
                String value = (String) getDoc.get("combination");
                fpAttributeStats.put(value, (double) cpt / (double) nbtotal);

            }
        } finally {
            cursor.close();
        }

        return fpAttributeStats;
    }


    public HashMap<String, Double> getParseAttributeStats(DBCollection coll, String attr, int nbtotal) {


        HashMap<String, Double> fpAttributeStats = new HashMap<String, Double>();
        BasicDBObject query = new BasicDBObject("indicator", attr);

        // A changer

        DBCursor cursor = coll.find(query);
        try {
            while (cursor.hasNext()) {
                DBObject getDoc = cursor.next();
                int cpt = Integer.parseInt((String) getDoc.get("counter"));
                String value = (String) getDoc.get("combination");
                Parsed parser = new Parsed();
                String newName = parser.parseAttribute(attr, value);
                fpAttributeStats.put(newName, (double) cpt / (double) nbtotal);

            }
        } finally {
            cursor.close();
        }

        return fpAttributeStats;
    }
}
        
