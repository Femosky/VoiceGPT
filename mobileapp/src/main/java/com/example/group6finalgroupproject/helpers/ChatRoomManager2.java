package com.example.group6finalgroupproject.helpers;

import com.example.group6finalgroupproject.model.ChatRoom2;

/**
 * Singleton manager for the current ChatRoom2 instance.
 * Allows global access and replacement of the active chat room.
 */
public class ChatRoomManager2 {
    private static ChatRoom2 instance = new ChatRoom2();

    // Singleton creation
    private ChatRoomManager2() {}

    // Getters and setters

    public static ChatRoom2 getChatRoom() {
        return instance;
    }

    /**
     * Replace the shared ChatRoom2 with a new one.
     * @param chatRoom the new ChatRoom2 to set
     */
    public static void setChatRoom(ChatRoom2 chatRoom) {
        instance = chatRoom;
    }

    /** @return number of messages currently in the shared ChatRoom2 */
    public static int getLength() {
        return instance.getChatList().size();
    }
}
