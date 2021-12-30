package com.example.nevermore.webscraper;

/**
 * Created by Never More on 12/31/2018.
 */

public class Crew {
    private String name;
    private String cm;
    private String no;
    private String damage;
    private String gap;

    public Crew(String name, String cm, String no, String damage, String gap) {
        this.name = name;
        this.cm = cm;
        this.no = no;
        this.damage = damage;
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

    public String getDamage() {
        return damage;
    }

    public void setDamage(String damage) {
        this.damage = damage;
    }

    public String getGap() {
        return gap;
    }

    public void setGap(String gap) {
        this.gap = gap;
    }
}
