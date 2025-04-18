package com.example.group6finalgroupproject.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.group6finalgroupproject.model.ChatRoom2;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * General-purpose helper methods for JSON conversion, date formatting,
 * and lightweight UI feedback (toasts).
 */
public class HelperUtils2 {
    /**
     * Convert Unix timestamp (seconds) into a short date string (dd/MM/yy).
     */
    public static String formatShortDate(long timestampSeconds) {
        long millis = timestampSeconds * 1000L;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        return simpleDateFormat.format(new Date(millis));
    }

    /**
     * Deserialize a byte[] containing a JSON array of ChatRoom2 objects
     * into a List<ChatRoom2>.
     * Returns null if parsing fails.
     */
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

    /**
     * Serialize a single ChatRoom2 to its JSON string representation.
     */
    public static String convertChatRoomToJSON(ChatRoom2 chatRoom) {
        Gson gson = new Gson();
        return gson.toJson(chatRoom);
    }

    /**
     * Serialize a List<ChatRoom2> into a JSON array string.
     * Useful for sending multiple rooms over the data layer.
     */
    public static String convertChatRoomListToJSON(List<ChatRoom2> chatRoom) {
        Gson gson = new Gson();
        return gson.toJson(chatRoom);
    }

    /**
     * Deserialize a JSON string back into a ChatRoom2 object.
     */
    public static ChatRoom2 convertJSONToChatRoom(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, ChatRoom2.class);
    }

    /**
     * Show a short Toast message on screen.
     */
    public static void showToast(String text, Context context) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
