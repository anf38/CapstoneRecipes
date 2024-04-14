package com.example.pantrypal;

public class Comment {
    private String id;
    private String title;
    private String message;

   public Comment() {
        this.id = "";
        this.title = "Title";
        this.message = "Message";
    }

    public Comment(String title, String message) {
        this.id = "";
        this.title = title;
        this.message = message;
    }

    public Comment(String id, String title, String message) {
        this.id = id;
        this.title = title;
        this.message = message;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
