package com.example.pantrypal;

public class Comment {
    private String id;
    private String title;
    private String message;

    Comment() {
        this.id = "";
        this.title = "Title";
        this.message = "Message";
    }

    Comment(String title, String message) {
        this.id = "";
        this.title = title;
        this.message = message;
    }

    Comment(String id, String title, String message) {
        this.id = id;
        this.title = title;
        this.message = message;
    }

    void setTitle(String title) {
        this.title = title;
    }

    String getTitle() {
        return title;
    }

    void setMessage(String message) {
        this.message = message;
    }

    String getMessage() {
        return message;
    }


    void setId(String id) {
        this.id = id;
    }

    String getId() {
        return id;
    }

}
