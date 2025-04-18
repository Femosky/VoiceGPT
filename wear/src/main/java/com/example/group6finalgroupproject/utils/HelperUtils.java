package com.example.group6finalgroupproject.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.group6finalgroupproject.R;
import com.example.group6finalgroupproject.activity.MainActivity;
import com.example.group6finalgroupproject.model.ChatRoom;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HelperUtils {
    /**
     * Format a Unix timestamp (seconds) into a short date string dd/MM/yy.
     */
    public static String formatShortDate(long timestampSeconds) {
        long millis = timestampSeconds * 1000L;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        return simpleDateFormat.format(new Date(millis));
    }

    /**
     * Convert a byte[] containing a JSON array of ChatRoom JSON strings
     * into a List<ChatRoom> by deserializing each element.
     */
    public static List<ChatRoom> convertJSONDataToChatroomList(byte[] jsonArrayChatRooms) {
        String jsonString = new String(jsonArrayChatRooms);

        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            List<ChatRoom> chatRooms = new ArrayList<>();
            Gson gson = new Gson();

            for (int i = 0; i < jsonArray.length(); i++) {
                String chatRoomJson = jsonArray.getString(i);
                ChatRoom chatRoom = gson.fromJson(chatRoomJson, ChatRoom.class);
                chatRooms.add(chatRoom);
            }

            return chatRooms;
        } catch (Exception e) {
            Log.e("MainActivity", "Error parsing JSON: " + e.getMessage());
            return null;
        }
    }

    /**
     * Serialize a single ChatRoom into its JSON string form.
     */
    public static String convertChatRoomToJSON(ChatRoom chatRoom) {
        Gson gson = new Gson();
        return gson.toJson(chatRoom);
    }

    /**
     * Deserialize a JSON string back into a ChatRoom object.
     */
    public static ChatRoom convertJSONToChatRoom(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, ChatRoom.class);
    }

    /**
     * Serialize a List<ChatRoom> into a JSON array string.
     */
    public static String convertChatRoomListToJSON(List<ChatRoom> chatRoom) {
        Gson gson = new Gson();
        return gson.toJson(chatRoom);
    }

    /**
     * Display a short Toast message.
     */
    public static void showToast(String text, Context context) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
