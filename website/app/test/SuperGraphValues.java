package models;


import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class SuperGraphValues {
	private String name;
	private String nameVersion;
	private JsonNode json;
	private String graphName;
	private String title;
	

	public SuperGraphValues(String name, JsonNode json, String graphName, String nameVersion, String title){
		this.name=name;
		this.json=json;
		this.graphName=graphName;
		this.nameVersion=nameVersion;
		this.title= title;
		
	}

	public String getTitle(){
		return title;
	}
	public String getVersion(){
		return nameVersion;
	}
	public String getName(){
		return name;
	}
	public String getGraphName(){
		return graphName;
	}

	public JsonNode getJson(){
		return json;
	}

}