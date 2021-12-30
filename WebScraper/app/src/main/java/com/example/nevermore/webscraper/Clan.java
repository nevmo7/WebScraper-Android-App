package com.example.nevermore.webscraper;

/**
 * Created by Never More on 12/31/2018.
 */

public class Clan {
    private String name;
    private String cm;
    private String no;
    private String reps;
    private String gap;

    public Clan(String name, String cm, String no, String reps, String gap) {
        this.name = name;
        this.cm = cm;
        this.no = no;
        this.reps = reps;
        this.gap = gap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCm() {
        return cm;
    }

    public void setCm(String cm) {
        this.cm = cm;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getReps() {
        return reps;
    }

    public void setReps(String reps) {
        this.reps = reps;
    }

    public String getGap() {
        return gap;
    }

    public void setGap(String gap) {
        this.gap = gap;
    }
}
