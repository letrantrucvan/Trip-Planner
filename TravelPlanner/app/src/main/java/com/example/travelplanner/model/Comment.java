package com.example.travelplanner.model;

public class Comment {
    private String user_id;
    private String tour_id;
    private String comment;
    private Integer rate;

    public String getUser_id() {
        return user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public String getTour_id() {
        return tour_id;
    }
    public void setTour_id(String tour_id) {
        this.tour_id = tour_id;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public Integer getRate() {
        return rate;
    }
    public void setRate(Integer rate) {
        this.rate = rate;
    }


}
