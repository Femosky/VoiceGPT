package com.example.group6finalgroupproject.helper;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.group6finalgroupproject.R;
import com.example.group6finalgroupproject.activity.ChatHistoryActivity;
import com.example.group6finalgroupproject.activity.ChatRoomActivity;
import com.example.group6finalgroupproject.model.ChatRoom;
import com.example.group6finalgroupproject.model.MessageItem;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Manages synchronization of ChatRoom data between Wear OS and phone
 * Implements both MessageClient.OnMessageReceivedListener and DataClient.OnDataChangedListener
 */
public class ChatSyncManager implements MessageClient.OnMessageReceivedListener, DataClient.OnDataChangedListener {
    private Context context;
    private static final String LISTENER_STATE_PATH = "/listenerState";
    private static final String TAG = "ChatSyncManager";
    private static final String CAPABILITY_NAME = "chatroom_sync";
    private static final String CHATROOM_DATA_PATH = "/chatroom_data";

    /**
     * Private constructor to enforce singleton pattern.
     * @param context current Activity context
     */
    private ChatSyncManager(Context context) {
        this.context = context;
    }

    // Make it a singleton for global use
    private static ChatSyncManager instance;

    /**
     * Get or create the ChatSyncManager singleton.
     * Updates internal context when called again.
     * @param context calling Activity context
     * @return singleton ChatSyncManager
     */
    public static synchronized ChatSyncManager getInstance(Context context) {
        if (instance == null) {
            instance = new ChatSyncManager(context);
        } else {
            // update to whichever Activity is calling
            instance.context = context;
        }

        instance.init();
        return instance;
    }

    /**
     * RECEIVE DATA FROM PHONE
     * **/

    /**
     * Initialize Data Layer listeners for messages and data changes.
     */
    private void init() {
        Wearable.getMessageClient(context).addListener(this);
        readCurrentListenerState();
        Wearable.getDataClient(context).addListener(this);
    }

    /**
     * Fetch the current listener state from DataItems for loader UI feedback.
     */
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
            }
        });
    }

    /**
     * Decode a single byte from DataItem to show syncing state.
     * @param item DataItem containing listener state byte
     */
    private void displayListenerState(DataItem item) {
        byte data = item.getData()[0];
        if (data == 0) {
            // SYNCING...
        } else {
            // NOT SYNCING...
        }
    }

    /**
     * Handle incoming DataEvents (e.g., listener state updates).
     */
    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_DELETED) {
            } else if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().equals(LISTENER_STATE_PATH)) {
                    displayListenerState(item);
                }
            }
        }
    }

    /**
     * Handle incoming MessageEvent from Wear or phone.
     * Expects JSON-encoded list of ChatRoom objects.
     */
    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        if (messageEvent.getPath().equals(CHATROOM_DATA_PATH)) {
            HelperUtils.showToast(context.getString(R.string.chatroom_data_received), context);
            byte[] data = messageEvent.getData();

            // CONVERT RECEIVED DATA FROM WATCH TO A CHATROOM LIST
            List<ChatRoom> chatRooms = HelperUtils.convertJSONDataToChatroomList(data);

            if (chatRooms == null) {
                return;
            }

            for (ChatRoom incoming : chatRooms) {
                String id = incoming.getId();
                ChatRoom stored = ChatResponseUtils.getChatRoom(context, id);

                List<MessageItem> incList = incoming.getChatList();
                List<MessageItem> oldList = new ArrayList<>();

                // skip if incoming has no messages
                if (stored == null) {
                    ChatResponseUtils.saveMessage(incoming, context);
                    continue;
                } else {
                    oldList = stored.getChatList();
                }

                long incTs = incList.get(incList.size() - 1).getCreated();
                long oldTs = oldList.isEmpty()
                        ? 0L
                        : oldList.get(oldList.size() - 1).getCreated();

                if (incTs > oldTs) {
                    ChatResponseUtils.saveMessage(incoming, context);
                }
            }

            // Refresh the UI to reflect changes
            if (context instanceof ChatRoomActivity) {
                ChatRoomActivity activity = (ChatRoomActivity)context;
                activity.runOnUiThread(() -> {
                    activity.refreshChatRoom();
                });
            }
            else if (context instanceof ChatHistoryActivity) {
                ChatHistoryActivity activity = (ChatHistoryActivity)context;
                activity.runOnUiThread(activity::refreshHistoryList);
            }
        }
    }

    /**
     * SEND TO DATA PHONE
     **/

    /**
     * Send all locally saved ChatRooms to the connected node (phone or watch).
     */
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

    /**
     * Pick the best node (prefer nearby) and initiate message send.
     */
    private void sendToNodeForChatRooms(CapabilityInfo capabilityInfo, byte[] data) {
        Set<Node> connectedNodes = capabilityInfo.getNodes();
        String bestNodeId = pickBestNodeId(connectedNodes);

        if (bestNodeId == null) {
            HelperUtils.showToast(context.getString(R.string.no_node_error), context);
            return;
        }
        sendChatRoomData(data, bestNodeId);
    }

    /**
     * Choose a nearby node if available, otherwise any node.
     */
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

    /**
     * Actually send the byte[] payload over MessageClient to the given nodeId.
     */
    private void sendChatRoomData(byte[] dataToSend, String nodeId) {
        MessageClient messageClient = Wearable.getMessageClient(context);
        Task<Integer> sendTask = messageClient.sendMessage(nodeId, CHATROOM_DATA_PATH, dataToSend);
        sendTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                 HelperUtils.showToast(context.getString(R.string.chat_rooms_sync_successful), context);
                Log.d(TAG, context.getString(R.string.successfully_sent_chatrooms_to_node) + nodeId);
            }
        });
        sendTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                 HelperUtils.showToast(context.getString(R.string.chat_rooms_sync_failed), context);
                Log.e(TAG, context.getString(R.string.failed_to_send_chatrooms_to_node) + nodeId, e);
            }
        });
    }
}
