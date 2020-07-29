package com.example.chatbot.Model;

import com.ibm.watson.assistant.v2.model.RuntimeResponseGeneric;

import java.io.Serializable;

public class Message implements Serializable {
    String id, message, url, title, description;
    Type type;

    public enum Type {
        TEXT,
        IMAGE
    }

    public Message() {
        this.type = Type.TEXT;
    }

    public Message(RuntimeResponseGeneric r) {
        this.message = "";
        this.title = "";
        this.description = "";
        this.url = r.source();
        this.id = "2";
        this.type = Type.IMAGE;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
