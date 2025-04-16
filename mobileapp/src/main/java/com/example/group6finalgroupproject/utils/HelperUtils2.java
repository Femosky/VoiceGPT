package com.example.group6finalgroupproject.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.group6finalgroupproject.model.ChatRoom2;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class HelperUtils2 {

    public static List<ChatRoom2> convertJSONDataToChatroomList(byte[] jsonArrayChatRooms) {
        String jsonString = new String(jsonArrayChatRooms);

        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            List<ChatRoom2> chatRooms = new ArrayList<>();
            Gson gson = new Gson();

            for (int i = 0; i < jsonArray.length(); i++) {
                String chatRoomJson = jsonArray.getString(i);
                ChatRoom2 chatRoom = gson.fromJson(chatRoomJson, ChatRoom2.class);
                chatRooms.add(chatRoom);
            }

            return chatRooms;
        } catch (Exception e) {
            Log.e("MainActivity", "Error parsing JSON: " + e.getMessage());
            return null;
        }
    }

    public static String convertChatRoomToJSON(ChatRoom2 chatRoom) {
        Gson gson = new Gson();
        return gson.toJson(chatRoom);
    }

    public static String convertChatRoomListToJSON(List<ChatRoom2> chatRoom) {
        Gson gson = new Gson();
        return gson.toJson(chatRoom);
    }

    public static ChatRoom2 convertJSONToChatRoom(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, ChatRoom2.class);
    }

    public static void showToast(String text, Context context) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
