package com.example.pantrypal;

public class Comment {
    private String title;
    private String message;

    public Comment(String title, String message){
        this.title = title;
        this.message = message;
    }

    public String getTitle(){
        return title;
    }

    public String getMessage(){
        return message;
    }

}
