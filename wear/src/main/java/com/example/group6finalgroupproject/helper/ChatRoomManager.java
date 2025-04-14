package com.example.group6finalgroupproject.helper;

import com.example.group6finalgroupproject.model.ChatRoom;

public class ChatRoomManager {
    private static ChatRoom instance = new ChatRoom();

    // Singleton creation
    private ChatRoomManager() {}

    // Getters and setters

    public static ChatRoom getChatRoom() {
        return instance;
    }

    public static void setChatRoom(ChatRoom chatRoom) {
        instance = chatRoom;
    }
}
