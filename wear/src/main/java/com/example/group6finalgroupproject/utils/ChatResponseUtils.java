package com.example.group6finalgroupproject.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.group6finalgroupproject.R;
import com.example.group6finalgroupproject.model.ChatRoom;
import com.example.group6finalgroupproject.model.MessageItem;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChatResponseUtils {
    // Save the task in SharedPreferences as a JSON string
    public static void saveMessage(ChatRoom chatRoom, Context context) {
        if (chatRoom != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(context.getString(R.string.chats_sharedPreferencesKey), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPrefs.edit();

            // Parse to JSON for storage
            Gson gson = new Gson();
            String json = gson.toJson(chatRoom);
            editor.putString(context.getString(R.string.chats_sharedPreferencesPrefix) + chatRoom.getId(), json);
            editor.apply();
        }
    }

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

    public static void updateChatRoom(ChatRoom chatRoom, Context context) {
        saveMessage(chatRoom, context);
    }

    public static List<ChatRoom> getChatRooms(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(context.getString(R.string.chats_sharedPreferencesKey), Context.MODE_PRIVATE);
        List<ChatRoom> chatRooms = new ArrayList<>();
        Map<String, ?> map = sharedPrefs.getAll();

        Set set = map.entrySet();
        Iterator iterator = set.iterator();
        Gson gson = new Gson();

        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            String json = (String) entry.getValue();

            if (json != null) {
                ChatRoom chatRoom = gson.fromJson(json, ChatRoom.class);
                chatRooms.add(chatRoom);
            }
        }

        return chatRooms;
    }
}
