package com.example.group6finalgroupproject.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatRoom2 {
    private String id;
    private String title;
    private long created;
    final private List<MessageItem2> chatList = new ArrayList<>();

    // Assign an ID to each ChatRoom Object
    public ChatRoom2() {
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

    public List<MessageItem2> getChatList() {
        return chatList;
    }
    public void appendChatList(MessageItem2 messageItem) {
        this.chatList.add(messageItem);
    }

    public void resetChatRoom() {
        this.id = UUID.randomUUID().toString();
        this.title = null;
        this.created = System.currentTimeMillis() / 1000;
        this.chatList.clear();
    }
}
