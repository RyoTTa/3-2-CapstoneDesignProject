package com.example.share.Chatting;

import android.graphics.drawable.Drawable;

public class ChatlistItem {

    private int type;
    private Drawable icon;
    private String userName;
    private String message;

    public ChatlistItem() {}
    public ChatlistItem(int type,String userName, String message) {
        this.userName = userName;
        this.message = message;
        this.type = type;
    }

    public ChatlistItem(String userName, String message){
        this.userName = userName;
        this.message = message;
    }
    public void setType(int type) {
        this.type = type ;
    }

    public int getType() {
        return this.type ;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public String getMessage() {
        return message;
    }
    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }


}