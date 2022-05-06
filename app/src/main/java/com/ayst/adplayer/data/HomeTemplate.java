package com.ayst.adplayer.data;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HomeTemplate implements Serializable {

    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private int type;
    @SerializedName("items")
    @Expose
    private List<BoxInfo> items = null;
    private final static long serialVersionUID = 5067580569871660306L;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<BoxInfo> getItems() {
        return items;
    }

    public void setItems(List<BoxInfo> items) {
        this.items = items;
    }

    public HomeTemplate withItems(List<BoxInfo> items) {
        this.items = items;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HomeTemplate that = (HomeTemplate) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "HomeTemplate{" +
                "id=" + id +
                "name=" + name +
                "type=" + type +
                ", items=" + items +
                '}';
    }
}