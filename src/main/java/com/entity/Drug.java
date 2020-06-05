package com.entity;

public class Drug {
    private String id;
    private String name;

    public Drug() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drug(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Drug{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
