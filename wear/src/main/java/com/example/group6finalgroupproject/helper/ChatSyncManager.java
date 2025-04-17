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
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

public class ChatSyncManager implements MessageClient.OnMessageReceivedListener, DataClient.OnDataChangedListener {
    private Context context;
    private static final String LISTENER_STATE_PATH = "/listenerState";
    private static final String TAG = "ChatSyncManager";
    private static final String CAPABILITY_NAME = "chatroom_sync";
    private static final String CHATROOM_DATA_PATH = "/chatroom_data";
//    private JSONArray jsonArrayChatRooms;

    private ChatSyncManager(Context context) {
        this.context = context.getApplicationContext();
    }

    // Make it a singleton for global use
    private static ChatSyncManager instance;
    public static synchronized ChatSyncManager getInstance(Context context) {
        if (instance == null) {
            instance = new ChatSyncManager(context);
        }

        // LOAD RECEIVER INIT FUNCTION
        instance.init();
        return instance;
    }

    /**

     RECEIVE DATA FROM PHONE

     **/

    private void init() {
        Wearable.getMessageClient(context).addListener(this);
        readCurrentListenerState();
        Wearable.getDataClient(context).addListener(this);
    }

    private void readCurrentListenerState() {
        Task<DataItemBuffer> task = Wearable.getDataClient(context).getDataItems();
        task.addOnSuccessListener(new OnSuccessListener<DataItemBuffer>() {
            @Override
            public void onSuccess(DataItemBuffer dataItems) {
                for (DataItem item : dataItems) {
                    if (item.getUri().getPath().equals(LISTENER_STATE_PATH)) {
                        displayListenerState(item);
                        return;
                    }
                }
//                MainActivity2.this.binding.testText.setText("No data");
            }
        });
    }

    // USE IT AS LOADER STATE FOR SYNCING
    private void displayListenerState(DataItem item) {
        byte data = item.getData()[0];
        if (data == 0) {
            // SYNCING...
        } else {
            // NOT SYNCING...
        }
    }

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_DELETED) {
//                this.binding.testText.setText(context.getString(R.string.test_string_2));
            } else if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().equals(LISTENER_STATE_PATH)) {
                    displayListenerState(item);
                }
            }
        }
    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        if (messageEvent.getPath().equals(CHATROOM_DATA_PATH)) {
            HelperUtils.showToast("Chatroom Data received", context);
            byte[] data = messageEvent.getData();

            // COME BACK HERE!!!!! CHECK IF NULL
            List<ChatRoom> chatRooms = HelperUtils.convertJSONDataToChatroomList(data);

            String testResult = chatRooms == null ? "null" : chatRooms.toString();
//            binding.testText.setText(testResult);
            Log.i("SYNC DATA", "Received Chatroom list from phone: " + testResult);
            if (chatRooms != null) {
                Log.i("SYNC DATA", "Chatroom example title: " + chatRooms.get(0).getTitle());

                for (ChatRoom chatRoom : chatRooms) {
                    ChatResponseUtils.saveMessage(chatRoom, context);
                }
            }
        }
    }

    /**

     SEND TO DATA PHONE

     **/

    public void sendChatRooms() {
        // Retrieve the current chatrooms from SharedPreferences (or your chosen persistence)
        List<ChatRoom> chatRooms = ChatResponseUtils.getChatRooms(context);

        String chatRoomsJsonArrayString = HelperUtils.convertChatRoomListToJSON(chatRooms);

        // Convert the JSON string into a byte array
        byte[] dataToSend = chatRoomsJsonArrayString.getBytes();

        // Determine the best node to send to
        CapabilityClient capabilityClient = Wearable.getCapabilityClient(context);
        Task<CapabilityInfo> task = capabilityClient.getCapability(CAPABILITY_NAME, CapabilityClient.FILTER_ALL);
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
