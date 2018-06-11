package com.google.scheduler.view;

import com.google.gson.annotations.Expose;

import org.joda.time.DateTime;

/**
 * Created by kfugaban on 6/6/18.
 */

public class DataModel {

    @Expose
    String name;

    @Expose
    String tierGroup;

    @Expose
    String role;

    DateTime time;

    @Expose
    String columnLetter;

    public DataModel(String name, String tierGroup, String role, DateTime time, String columnLetter) {
        this.name=name;
        this.tierGroup=tierGroup;
        this.role=role;
        this.time=time;
        this.columnLetter = columnLetter;
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

    public DateTime getTime() {
        return this.time;
    }

    public String getColumnLetter() {
        return columnLetter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataModel dataModel = (DataModel) o;
        return (this.name == null ? dataModel.name == null : this.name.equals(dataModel.name)) &&
                (this.tierGroup == null ? dataModel.tierGroup == null : this.tierGroup.equals(dataModel.tierGroup)) &&
                (this.role == null ? dataModel.role == null : this.role.equals(dataModel.role) &&
                (this.columnLetter == null ? dataModel.columnLetter == null : this.columnLetter.equals(dataModel.columnLetter)));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (this.name == null ? 0: this.name.hashCode());
        result = 31 * result + (this.tierGroup == null ? 0: tierGroup.hashCode());
        result = 31 * result + (this.role == null ? 0: this.role.hashCode());
        result = 31 * result + (this.columnLetter == null ? 0: this.columnLetter.hashCode());
        return result;
    }
}
