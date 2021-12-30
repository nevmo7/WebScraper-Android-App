package com.example.nevermore.webscraper;

import android.support.annotation.NonNull;

/**
 * Created by Never More on 1/13/2019.
 */

public class Member implements Comparable<Member>{
    String name, level;
    int reputation;

    public Member(String name, String level, int reputation) {
        this.name = name;
        this.level = level;
        this.reputation = reputation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getReputation() {
        return reputation;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
    }

    @Override
    public int compareTo(@NonNull Member o) {
        int compare = Integer.compare(o.reputation, reputation);
        return compare;
    }
}