package com.teamtreehouse.instateam.web;

public enum Status {
    ACTIVE("Active", "active"),
    ARCHIVED("Archived", "archived"),
    NOT_STARTED("Not Started", "not_started");

    private final String name;
    private final String classCode;

    Status(String name, String classCode){
        this.name = name;
        this.classCode = classCode;
    }

    public String getName() { return name; }

    public String getClassCode() { return classCode; }
}
