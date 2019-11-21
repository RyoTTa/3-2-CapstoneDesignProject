package com.example.share.Chatting;

import android.graphics.drawable.Drawable;

public class ChatroomlistItem {

    private Drawable icon;
    private String roomName;
    private String other_email;
    private String other_name;
    private String message;

    public ChatroomlistItem() {}

    public ChatroomlistItem(String roomName, String message) {
        this.roomName = roomName;
        this.message = message;

    }

    public void setUserName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getOther_email() {
        return other_email;
    }

    public void setOther_email(String other_email) {
        this.other_email = other_email;
    }

    public String getOther_name() {
        return other_name;
    }

    public void setOther_name(String other_name) {
        this.other_name = other_name;
    }

    public void setMessage(String message) {
        this.message = message;
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