package com.example.share.Activity;

public class ReviewItem {
    private String reviewer;
    private int star;
    private String content;

    public ReviewItem(){}

    public ReviewItem(String reviewer, int star, String content){
        this.reviewer = reviewer;
        this.star = star;
        this.content = content;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
