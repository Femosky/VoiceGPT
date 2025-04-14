package com.example.group6finalgroupproject.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatRoom {
    private String id;
    private String title;
    private long created;
    final private List<MessageItem> chatList = new ArrayList<>();

    // Assign an ID to each ChatRoom Object
    public ChatRoom() {
        // Generate a unique identifier using UUID.
        this.id = UUID.randomUUID().toString();
    }

    // Getters and setters

    public String getId() {
        return id;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) {
        this.title = title;
    }

    public long getCreated() { return created; }
    public void setCreated(long created) {
        this.created = created;
    }

    public List<MessageItem> getChatList() {
        return chatList;
    }
    public void appendChatList(MessageItem messageItem) {
        this.chatList.add(messageItem);
    }

    public void resetChatRoom() {
        this.id = UUID.randomUUID().toString();
        this.title = null;
        this.created = System.currentTimeMillis() / 1000;
        this.chatList.clear();
    }
}
