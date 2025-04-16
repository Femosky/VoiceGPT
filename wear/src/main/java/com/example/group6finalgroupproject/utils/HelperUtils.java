package com.example.group6finalgroupproject.utils;

import android.content.Context;
import android.widget.Toast;

import com.example.group6finalgroupproject.R;
import com.example.group6finalgroupproject.activity.MainActivity;
import com.example.group6finalgroupproject.model.ChatRoom;
import com.google.gson.Gson;

import java.util.List;

public class HelperUtils {

    public static String convertChatRoomToJSON(ChatRoom chatRoom) {
        Gson gson = new Gson();
        return gson.toJson(chatRoom);
    }

    public static ChatRoom convertJSONToChatRoom(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, ChatRoom.class);
    }

    public static String convertChatRoomListToJSON(List<ChatRoom> chatRoom) {
        Gson gson = new Gson();
        return gson.toJson(chatRoom);
    }

    public static void showToast(String text, Context context) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
