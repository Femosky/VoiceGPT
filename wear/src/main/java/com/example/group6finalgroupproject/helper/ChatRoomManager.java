package com.example.group6finalgroupproject.helper;

import com.example.group6finalgroupproject.model.ChatRoom;

/**
 * Singleton manager for a shared ChatRoom instance.
 * Provides global access to the current chat session.
 */
public class ChatRoomManager {
    private static ChatRoom instance = new ChatRoom();

    /**
     * Retrieve the current ChatRoom singleton.
     * @return the ChatRoom instance
     */
    private ChatRoomManager() {}

    public static ChatRoom getChatRoom() {
        return instance;
    }

    /**
     * Replace the current ChatRoom with a different one.
     * @param chatRoom the new ChatRoom to set
     */
    public static void setChatRoom(ChatRoom chatRoom) {
        instance = chatRoom;
    }

    /**
     * Get the number of messages in the current ChatRoom.
     * @return size of the chatList
     */
    public static int getLength() {
        return instance.getChatList().size();
    }
}
