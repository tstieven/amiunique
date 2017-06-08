package models;

/**
 * Created by thomas on 06/06/17.
 */
public class Detail {
    private String indicator;
    private String name;
    private Double value;

    public Detail(String indicator, String name, Double value) {
        this.name = name;
        this.indicator = indicator;
        this.value = value;
    }

    public String getIndicator() {
        return indicator;
    }

    public String getName() {
        return name;
    }

    public Double getValue() {
        return value;
    }
}
