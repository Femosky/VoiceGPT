package com.example.group6finalgroupproject.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.group6finalgroupproject.R;
import com.example.group6finalgroupproject.model.MessageItem;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChatResponseUtils {
    // Save the task in SharedPreferences as a JSON string
    public static void saveMessage(MessageItem messageItem, Context context) {
        if (messageItem != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(context.getString(R.string.chats_sharedPreferencesKey), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPrefs.edit();

            // Parse to JSON for storage
            Gson gson = new Gson();
            String json = gson.toJson(messageItem);
            editor.putString(context.getString(R.string.chats_sharedPreferencesPrefix) + messageItem.getId(), json);
            editor.apply();
        }
    }

    public static List<MessageItem> getChats(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(context.getString(R.string.chats_sharedPreferencesKey), Context.MODE_PRIVATE);
        List<MessageItem> chatList = new ArrayList<>();
        Map<String, ?> map = sharedPrefs.getAll();

        Set set = map.entrySet();
        Iterator iterator = set.iterator();
        Gson gson = new Gson();

        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            String json = (String) entry.getValue();

            if (json != null) {
                MessageItem messageItem = gson.fromJson(json, MessageItem.class);
                chatList.add(messageItem);
            }
        }

        return chatList;
    }
}
