package com.example.share.Data;


import java.io.Serializable;
import java.util.Date;

public class Item implements Comparable<Item>,Serializable {

    private String item_id;
    private String item_name;
    private String item_price_per_day;
    private double latitude;
    private double longitude;
    private float[] distanceToUser;
    private Date availableFrom;
    private Date availableTo;
    private String file_path;
    private String category;
    private String content;
    private String owner_email;

    public Item(String item_id,String item_name, String item_price_per_day, double latitude, double longitude, Date availableFrom, Date availableTo, String FilePath,String category, String content, String owner_email){
        this.item_id = item_id;
        this.item_name = item_name;
        this.item_price_per_day = item_price_per_day;
        this.latitude = latitude;
        this.longitude = longitude;
        this.availableFrom = availableFrom;
        this.availableTo = availableTo;
        this.file_path = FilePath;
        this.category = category;
        this.content = content;
        this.owner_email= owner_email;
    }

    public String getItem_id() {
        return item_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public String getItem_price_per_day() {
        return item_price_per_day;
    }

    public double getLatitude() { return latitude;  }

    public double getLongitude() { return longitude; }

    public void setDistanceToUser(float[] distanceToUser) {
        this.distanceToUser = distanceToUser;
    }

    public float[] getDistanceToUser() {  return distanceToUser;  }

    public Date getAvailableFrom() {  return availableFrom;  }

    public Date getAvailableTo() {  return availableTo;  }

    public String getFilepath() {  return file_path;  }

    public String getCategory() {  return category;  }

    public void setCategory(String category) {  this.category = category;  }

    public String getContent() {        return content;    }

    public void setContent(String content) {        this.content = content;    }

    public String getOwner_email(){ return this.owner_email; }

    @Override
    public int compareTo(Item i) {
        return  Double.compare(this.distanceToUser[0],i.getDistanceToUser()[0]);
    }
}