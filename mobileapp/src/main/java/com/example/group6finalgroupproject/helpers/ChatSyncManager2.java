package com.example.group6finalgroupproject.helpers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.group6finalgroupproject.R;
import com.example.group6finalgroupproject.activity.ChatHistoryActivity2;
import com.example.group6finalgroupproject.activity.MainActivity2;
import com.example.group6finalgroupproject.model.ChatRoom2;
import com.example.group6finalgroupproject.model.MessageItem2;
import com.example.group6finalgroupproject.utils.ChatResponseUtils2;
import com.example.group6finalgroupproject.utils.HelperUtils2;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ChatSyncManager2 implements MessageClient.OnMessageReceivedListener, DataClient.OnDataChangedListener {
    private Context context;
    private static final String LISTENER_STATE_PATH = "/listenerState";
    private static final String CHATROOM_DATA_PATH = "/chatroom_data";
    private static final String CAPABILITY_NAME = "chatroom_sync";
    private static final String TAG = "ChatSyncManager";

    private ChatSyncManager2(Context context) {
//        this.context = context.getApplicationContext();
        this.context = context;
    }

    // Make it a singleton for global use
    private static ChatSyncManager2 instance;
    public static synchronized ChatSyncManager2 getInstance(Context context) {
        if (instance == null) {
            instance = new ChatSyncManager2(context);
        } else {
            // update to whichever Activity is calling
            instance.context = context;
        }
        instance.init();
        return instance;
    }

    /**

     RECEIVE DATA FROM WATCH

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
            HelperUtils2.showToast("Chatroom Data received", context);
            byte[] data = messageEvent.getData();

            // CONVERT RECEIVED DATA FROM WATCH TO A CHATROOM LIST
            List<ChatRoom2> chatRooms = HelperUtils2.convertJSONDataToChatroomList(data);

            if (chatRooms == null) {
                return;
            }

            for (ChatRoom2 incoming : chatRooms) {
                String id = incoming.getId();
                ChatRoom2 stored = ChatResponseUtils2.getChatRoom(context, id);

                List<MessageItem2> incList = incoming.getChatList();
                List<MessageItem2> oldList = new ArrayList<>();

                // skip if incoming has no messages
                if (stored == null) {
                    ChatResponseUtils2.saveMessage(incoming, context);
                    continue;
                } else {
                    oldList = stored.getChatList();
                }

                long incTs = incList.get(incList.size() - 1).getCreated();
                long oldTs = oldList.isEmpty()
                        ? 0L
                        : oldList.get(oldList.size() - 1).getCreated();

                if (incTs > oldTs) {
                    ChatResponseUtils2.saveMessage(incoming, context);
                }
            }

            // Refresh the UI to reflect changes
            if (context instanceof MainActivity2) {
                MainActivity2 activity = (MainActivity2)context;
                activity.runOnUiThread(() -> {
                    activity.refreshChatRoom();
                });
            }
            else if (context instanceof ChatHistoryActivity2) {
                ChatHistoryActivity2 activity = (ChatHistoryActivity2)context;
                activity.runOnUiThread(activity::refreshHistoryList);
            }
        }
    }

    /**

    SEND DATA TO WATCH

     **/
    public void sendChatRooms() {
        // Retrieve the current chatrooms from SharedPreferences (or your chosen persistence)
        List<ChatRoom2> chatRooms = ChatResponseUtils2.getChatRooms(context);

        String chatRoomsJsonArrayString = HelperUtils2.convertChatRoomListToJSON(chatRooms);

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
                HelperUtils2.showToast(context.getString(R.string.failed_string), context);
            }
        });
    }

    private void sendToNodeForChatRooms(CapabilityInfo capabilityInfo, byte[] data) {
        Set<Node> connectedNodes = capabilityInfo.getNodes();
        String bestNodeId = pickBestNodeId(connectedNodes);

        if (bestNodeId == null) {
            HelperUtils2.showToast(context.getString(R.string.no_node_error), context);
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
                HelperUtils2.showToast("Chat rooms sync successful", context);
                Log.d(TAG, "Successfully sent chatrooms to node: " + nodeId);
            }
        });
        sendTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                HelperUtils2.showToast("Chat rooms sync failed", context);
                Log.e(TAG, "Failed to send chatrooms to node: " + nodeId, e);
            }
        });
    }
}
