package com.example.pantrypal;

public class Comment {
    private String title;
    private String message;

    Comment(){
        this.title = "Title";
        this.message = "Message";
    }

    Comment(String title, String message){
        this.title = title;
        this.message = message;
    }

    void setTitle(String title){
        this.title = title;
    }

    String getTitle(){
        return title;
    }

    void setMessage(String message){
        this.message = message;
    }

    String getMessage(){
        return message;
    }

}
