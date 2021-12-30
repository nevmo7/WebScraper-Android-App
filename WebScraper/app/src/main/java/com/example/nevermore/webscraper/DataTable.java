package com.example.nevermore.webscraper;

/**
 * Created by mstha on 2/8/2019.
 */

public class DataTable {

    private String tableName,dateCreated,clcr,clcrName;
    private int size;

    public DataTable(String tableName, String dateCreated, int size, String clcr, String clcrName) {
        this.tableName = tableName;
        this.dateCreated = dateCreated;
        this.size = size;
        this.clcr = clcr;
        this.clcrName = clcrName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getClcr() {
        return clcr;
    }

    public void setClcr(String clcr) {
        this.clcr = clcr;
    }

    public String getClcrName() {
        return clcrName;
    }

    public void setClcrName(String clcrName) {
        this.clcrName = clcrName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "DataTable{" +
                "tableName='" + tableName + '\'' +
                ", dateCreated='" + dateCreated + '\'' +
                ", clcr='" + clcr + '\'' +
                ", clcrName='" + clcrName + '\'' +
                ", size=" + size +
                '}';
    }
}
