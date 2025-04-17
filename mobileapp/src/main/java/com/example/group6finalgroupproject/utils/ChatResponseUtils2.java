package com.example.group6finalgroupproject.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.group6finalgroupproject.R;
import com.example.group6finalgroupproject.model.ChatRoom2;
import com.example.group6finalgroupproject.model.MessageItem2;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChatResponseUtils2 {
    public static void saveMessage(ChatRoom2 chatRoom, Context context) {
        if (chatRoom != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(context.getString(R.string.chats_sharedPreferencesKey), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPrefs.edit();

            // Parse to JSON for storage
            String json = HelperUtils2.convertChatRoomToJSON(chatRoom);
            editor.putString(context.getString(R.string.chats_sharedPreferencesPrefix) + chatRoom.getId(), json);
            editor.apply();
        }
    }

    public static ChatRoom2 getChatRoom(Context context, String chatRoomId) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(
                context.getString(R.string.chats_sharedPreferencesKey),
                Context.MODE_PRIVATE);
        String key = context.getString(R.string.chats_sharedPreferencesPrefix) + chatRoomId;
        String json = sharedPrefs.getString(key, null);
        if (json != null) {
            return new Gson().fromJson(json, ChatRoom2.class);
        } else {
            return null;
        }
    }

    public static List<ChatRoom2> getChatRooms(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(context.getString(R.string.chats_sharedPreferencesKey), Context.MODE_PRIVATE);
        List<ChatRoom2> chatRooms = new ArrayList<>();
        Map<String, ?> map = sharedPrefs.getAll();

        Set set = map.entrySet();
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            String json = (String) entry.getValue();

            if (json != null) {
                ChatRoom2 chatRoom = HelperUtils2.convertJSONToChatRoom(json);
                Log.i("CHATROOM", chatRoom.getTitle());
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

    public static long getLastTimestamp(ChatRoom2 room) {
        List<MessageItem2> list = room.getChatList();
        if (list != null && !list.isEmpty()) {
            // last message in chronological order
            return list.get(list.size() - 1).getCreated();
        }
        return room.getCreated();
    }
}
