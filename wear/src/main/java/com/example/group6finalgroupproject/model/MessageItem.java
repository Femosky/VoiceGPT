package com.example.group6finalgroupproject.model;

import java.util.UUID;

public class MessageItem {
    private String id;
    private long created;
    private String model;
    private String from; // Must be either "assistant" or "user"
    private String message;

    // Assign an ID to each ChatRoom Object
    public MessageItem() {
        this.id = UUID.randomUUID().toString();
    }

    // Getters and setters

    public String getId() {
        return id;
    }

    public long getCreated() {
        return created;
    }
    public void setCreated(long created) {
        this.created = created;
    }

    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }

    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
