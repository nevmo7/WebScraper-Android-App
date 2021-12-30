package com.example.nevermore.webscraper;

/**
 * Created by mstha on 2/28/2019.
 */

public class Suggestion {

    private String Name, Id;

    public Suggestion(String name, String id) {
        Name = name;
        Id = id;
    }

    public Suggestion() {

    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
