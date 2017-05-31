package models;


import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Percentages {
	private String name;
	private Double percent;
	private String sentence1;
	private String sentence2;

	public Percentages(String name, Double percent, String sentence1, String sentence2){
		this.name=name;
		this.percent=percent;
		this.sentence1= sentence1;
		this.sentence2 = sentence2;
	}

	public String getName(){
		return name;
	}

	public Double getPercentages(){
		return percent;
	}

	public String getSentence1(){
		return sentence1;
	}
	public String getSentence2(){
		return sentence2;
	}
}