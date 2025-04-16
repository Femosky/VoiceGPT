package com.example.group6finalgroupproject.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.group6finalgroupproject.R;
import com.example.group6finalgroupproject.model.ChatRoom2;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ChatSyncManager2 implements DataClient.OnDataChangedListener {
    private Context context;
    private static final String LISTENER_STATE_PATH = "/listenerState";
    private static final String TAG = "ChatSyncManager";
    private static final String GRAPH_CAPABILITY_NAME = "graph_generation";
    private static final String CHATROOM_DATA_PATH = "/chatroom_data";

    private ChatSyncManager2(Context context) {
        this.context = context.getApplicationContext();
    }

    // Make it a singleton for global use
    private static ChatSyncManager2 instance;
    public static synchronized ChatSyncManager2 getInstance(Context context) {
        if (instance == null) {
            instance = new ChatSyncManager2(context);
        }
        Log.i("INSTANCE MADE", "YEAH");
        return instance;
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.i("CAME TO ONDATA", "OUI");
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED &&
                    event.getDataItem().getUri().getPath().equals(CHATROOM_DATA_PATH)) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                Asset asset = dataMapItem.getDataMap().getAsset(context.getString(R.string.chatrooms_key));
                List<ChatRoom2> chatRooms = loadChatRoomsFromAsset(asset);
                // Do something with the bitmap
            }
        }
    }

    public List<ChatRoom2> loadChatRoomsFromAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }
        try {
            // Convert asset into a file descriptor and block until it's available.
            DataClient dataClient = Wearable.getDataClient(context);
            InputStream assetInputStream =
                    Tasks.await(dataClient.getFdForAsset(asset)).getInputStream();

            if (assetInputStream == null) {
                Log.w(TAG, "Requested an unknown Asset.");
                return null;
            }
            // Read the stream into a ByteArrayOutputStream.
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = assetInputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            assetInputStream.close();
            // Convert the byte array to a String using UTF-8.
            String json = new String(baos.toByteArray(), StandardCharsets.UTF_8);
            Log.d(TAG, "Loaded chatrooms JSON: " + json);
            // Deserialize the JSON string into a List<ChatRoom> using Gson.
            Gson gson = new Gson();
            Type listType = new TypeToken<List<ChatRoom2>>(){}.getType();
            Log.i("RECEIVED CHATROOMS", json);
            List<ChatRoom2> chatRooms = gson.fromJson(json, listType);
            return chatRooms;
        } catch (Exception e) {
            Log.e(TAG, "Error loading chat rooms from asset", e);
            return null;
        }
    }


}
