package com.google.scheduler.view;

/**
 * Created by kfugaban on 6/6/18.
 */

public class DataModel {

    String name;
    String tierGroup;
    String role;
    String time;

    public DataModel(String name, String tierGroup, String role, String time ) {
        this.name=name;
        this.tierGroup=tierGroup;
        this.role=role;
        this.time=time;

    }

    public String getName() {
        return this.name;
    }

    public String getTierGroup() {
        return this.tierGroup;
    }

    public String getRole() {
        return this.role;
    }

    public String getTime() {
        return this.time;
    }
}
