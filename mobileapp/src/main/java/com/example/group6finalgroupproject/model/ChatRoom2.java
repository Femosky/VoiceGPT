package com.example.group6finalgroupproject.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a single chat session (“room”) on the phone.
 * Holds a unique ID, an optional title, creation timestamp,
 * and an ordered list of MessageItem2 instances.
 */
public class ChatRoom2 {
    private String id;
    private String title;
    private long created;
    final private List<MessageItem2> chatList = new ArrayList<>();

    /**
     * Constructor: generates a new UUID and sets up an empty message list.
     */
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

    /** Append a single message to this ChatRoom2. */
    public void appendChatList(MessageItem2 messageItem) {
        this.chatList.add(messageItem);
    }

    /**
     * Reset this chat room for a brand-new conversation:
     * generates a new ID, clears title, resets timestamp, and empties messages.
     */
    public void resetChatRoom() {
        this.id = UUID.randomUUID().toString();
        this.title = null;
        this.created = System.currentTimeMillis() / 1000;
        this.chatList.clear();
    }
}
