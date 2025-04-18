package com.example.group6finalgroupproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.example.group6finalgroupproject.R;
import com.example.group6finalgroupproject.adapter.ChatHistoryAdapter;
import com.example.group6finalgroupproject.databinding.ActivityChatHistoryBinding;
import com.example.group6finalgroupproject.helper.ChatSyncManager;
import com.example.group6finalgroupproject.model.ChatRoom;
import com.example.group6finalgroupproject.utils.ChatResponseUtils;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;

public class ChatHistoryActivity extends AppCompatActivity {

    ActivityChatHistoryBinding binding;
    private List<ChatRoom> chatRooms = new ArrayList<>();
    private ChatSyncManager chatSyncManager;
    private ChatHistoryAdapter adapter;

    /**
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.
     *                           <b><i>Note: Otherwise it is null.</i></b>
     * Inflate layout and obtain root view
     * Initialize RecyclerView, load data, and set up click handlers
     * Obtain singleton instance to listen for sync events
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatHistoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        init();

        chatSyncManager = ChatSyncManager.getInstance(this);
    }

    /**
     * Load startup code for the screen
     * Initialize the history list: configure RecyclerView,
     * load existing chat rooms, and attach adapter with click listener.
     * Center items at edges when scrolling
     * Load saved chat rooms from local storage
     * Use a WearableLinearLayoutManager for Wear OS styling
     * When a history item is tapped, open its ChatRoomActivity
     */
    public void init() {
        WearableRecyclerView recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setEdgeItemsCenteringEnabled(true);

        chatRooms = ChatResponseUtils.getChatRooms(this);

        recyclerView.setLayoutManager(new WearableLinearLayoutManager(this));
        adapter = new ChatHistoryAdapter(chatRooms);
        adapter.setOnItemClickListener(new ChatHistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ChatRoom chatRoom) {
                Intent intent = new Intent(ChatHistoryActivity.this, ChatRoomActivity.class);
                intent.putExtra(getString(R.string.chat_room_id), chatRoom.getId());
                startActivity(intent); // Takes us to the Chat Room screen
            }
        });
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * Refresh the displayed list of chat rooms—e.g., after a sync event.
     */
    public void refreshHistoryList() {
        chatRooms = ChatResponseUtils.getChatRooms(ChatHistoryActivity.this);

        adapter.setChatRooms(chatRooms);
        adapter.notifyDataSetChanged();
        Log.i("MainActivity2", "Chat room refreshed!");
    }

    /**
     * Begin listening for data layer events when visible
     * Register the listener when the activity comes to the foreground.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Register the listener when the activity comes to the foreground.
        Wearable.getDataClient(this).addListener(chatSyncManager);
    }

    /**
     * Stop listening when going into background
     * Unregister the listener when the activity goes to the background.
     */
    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener when the activity goes to the background.
        Wearable.getDataClient(this).removeListener(chatSyncManager);
    }
}