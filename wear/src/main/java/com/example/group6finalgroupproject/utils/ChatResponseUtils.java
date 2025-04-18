package com.example.group6finalgroupproject.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.group6finalgroupproject.R;
import com.example.group6finalgroupproject.model.ChatRoom;
import com.example.group6finalgroupproject.model.MessageItem;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for saving, retrieving, and listing ChatRoom objects
 * using Android's SharedPreferences as JSON storage.
 */
public class ChatResponseUtils {
    /**
     * Persist a ChatRoom's JSON representation in SharedPreferences.
     * @param chatRoom the ChatRoom to save
     * @param context  Android Context for accessing preferences
     */
    public static void saveMessage(ChatRoom chatRoom, Context context) {
        if (chatRoom != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(context.getString(R.string.chats_sharedPreferencesKey), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPrefs.edit();

            // Parse to JSON for storage
            String json = HelperUtils.convertChatRoomToJSON(chatRoom);
            editor.putString(context.getString(R.string.chats_sharedPreferencesPrefix) + chatRoom.getId(), json);
            editor.apply();
        }
    }

    /**
     * Retrieve a single ChatRoom by its ID from SharedPreferences.
     * @param context     Android Context to access preferences
     * @param chatRoomId  ID of the ChatRoom to fetch
     * @return the ChatRoom if found, or null otherwise
     */
    public static ChatRoom getChatRoom(Context context, String chatRoomId) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(
                context.getString(R.string.chats_sharedPreferencesKey),
                Context.MODE_PRIVATE);
        String key = context.getString(R.string.chats_sharedPreferencesPrefix) + chatRoomId;
        String json = sharedPrefs.getString(key, null);
        if (json != null) {
            return new Gson().fromJson(json, ChatRoom.class);
        } else {
            return null;
        }
    }

    /**
     * Retrieve all stored ChatRoom objects, sorted by latest message timestamp.
     * @param context Android Context to access preferences
     * @return list of ChatRoom instances, most recently active first
     */
    public static List<ChatRoom> getChatRooms(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(context.getString(R.string.chats_sharedPreferencesKey), Context.MODE_PRIVATE);
        List<ChatRoom> chatRooms = new ArrayList<>();
        Map<String, ?> map = sharedPrefs.getAll();

        Set set = map.entrySet();
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            String json = (String) entry.getValue();

            if (json != null) {
                ChatRoom chatRoom = HelperUtils.convertJSONToChatRoom(json);
                chatRooms.add(chatRoom);
            }
        }

        chatRooms.sort((a, b) -> {
            long lastA = getLastTimestamp(a);
            long lastB = getLastTimestamp(b);

            return Long.compare(lastB, lastA);
        });

        return chatRooms;
    }

    /**
     * Helper to get the last activity timestamp for a chat room.
     * Falls back to room creation time if no messages exist.
     */
    private static long getLastTimestamp(ChatRoom room) {
        List<MessageItem> list = room.getChatList();
        if (list != null && !list.isEmpty()) {
            // last message in chronological order
            return list.get(list.size() - 1).getCreated();
        }
        return room.getCreated();
    }
}
