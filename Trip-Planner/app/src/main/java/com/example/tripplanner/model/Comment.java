package com.example.tripplanner.model;

import java.util.Date;

public class Comment {
    public String id;
    public String content;
    public Date date;

    public Comment(String ID, String text){
        this.id = ID;
        this.content = text;
    }

}
