package com.example.group6finalgroupproject.helper;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.group6finalgroupproject.R;
import com.example.group6finalgroupproject.model.ChatRoom;
import com.example.group6finalgroupproject.utils.ChatResponseUtils;
import com.example.group6finalgroupproject.utils.HelperUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.List;
import java.util.Set;

public class ChatSyncManager {
    private Context context;
    private static final String LISTENER_STATE_PATH = "/listenerState";
    private static final String TAG = "ChatSyncManager";
    private static final String GRAPH_CAPABILITY_NAME = "graph_generation";
    private static final String CHATROOM_DATA_PATH = "/chatroom_data";
    private JSONArray jsonArrayChatRooms;

    private ChatSyncManager(Context context) {
        this.context = context.getApplicationContext();
    }

    // Make it a singleton for global use
    private static ChatSyncManager instance;
    public static synchronized ChatSyncManager getInstance(Context context) {
        if (instance == null) {
            instance = new ChatSyncManager(context);
        }
        return instance;
    }

    public void sendChatRooms() {
        // Retrieve the current chatrooms from SharedPreferences (or your chosen persistence)
        List<ChatRoom> chatRooms = ChatResponseUtils.getChatRooms(context);

        if(chatRooms == null || chatRooms.isEmpty()){
            Log.i(TAG, "No chat rooms to sync.");
            return;
        }

        // Convert list of chatRooms to JSON using Gson
        Gson gson = new Gson();
        String chatRoomsJson = gson.toJson(chatRooms);

        Log.d(TAG, "Syncing chat rooms JSON: " + chatRoomsJson);

        // Convert the JSON string into a byte array
        byte[] dataToSend = chatRoomsJson.getBytes();

        // Determine the best node to send to
        CapabilityClient capabilityClient = Wearable.getCapabilityClient(context);
        Task<CapabilityInfo> task = capabilityClient.getCapability(GRAPH_CAPABILITY_NAME, CapabilityClient.FILTER_REACHABLE);
        task.addOnSuccessListener(new OnSuccessListener<CapabilityInfo>() {
            @Override
            public void onSuccess(CapabilityInfo capabilityInfo) {
                sendToNodeForChatRooms(capabilityInfo, dataToSend);
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                HelperUtils.showToast(context.getString(R.string.failed_string), context);
            }
        });
    }

    private void sendToNodeForChatRooms(CapabilityInfo capabilityInfo, byte[] data) {
        Set<Node> connectedNodes = capabilityInfo.getNodes();
        String bestNodeId = pickBestNodeId(connectedNodes);

        if (bestNodeId == null) {
            HelperUtils.showToast(context.getString(R.string.no_node_error), context);
            return;
        }
        sendChatRoomData(data, bestNodeId);
    }

    private String pickBestNodeId(Set<Node> nodes) {
        String bestNodeId = null;

        for (Node node : nodes) {
            if (node.isNearby()) {
                return node.getId();
            }
            bestNodeId = node.getId();
        }

        return bestNodeId;
    }

    private void sendChatRoomData(byte[] dataToSend, String nodeId) {
        MessageClient messageClient = Wearable.getMessageClient(context);
        Task<Integer> sendTask = messageClient.sendMessage(nodeId, CHATROOM_DATA_PATH, dataToSend);
        sendTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                HelperUtils.showToast("Chat rooms sync successful", context);
                Log.d(TAG, "Successfully sent chatrooms to node: " + nodeId);
            }
        });
        sendTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                HelperUtils.showToast("Chat rooms sync failed", context);
                Log.e(TAG, "Failed to send chatrooms to node: " + nodeId, e);
            }
        });
    }
}
