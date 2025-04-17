package com.example.group6finalgroupproject.helpers;

import com.example.group6finalgroupproject.model.ChatRoom2;

public class ChatRoomManager2 {
    private static ChatRoom2 instance = new ChatRoom2();

    // Singleton creation
    private ChatRoomManager2() {}

    // Getters and setters

    public static ChatRoom2 getChatRoom() {
        return instance;
    }

    public static void setChatRoom(ChatRoom2 chatRoom) {
        instance = chatRoom;
    }

    public static int getLength() {
        return instance.getChatList().size();
    }
}
