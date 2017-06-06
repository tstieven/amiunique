package models;

import com.fasterxml.jackson.databind.JsonNode;

public class GraphValues {
    private String name;
    private JsonNode json;
    private String graphName;
    private String title;


    public GraphValues(String name, JsonNode json, String graphName, String title) {
        this.name = name;
        this.json = json;
        this.graphName = graphName;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }

    public String getGraphName() {
        return graphName;
    }

    public JsonNode getJson() {
        return json;
    }

}