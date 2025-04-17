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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatHistoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        init();

        chatSyncManager = ChatSyncManager.getInstance(this);
    }

    // Load startup code for the screen
    public void init() {
        WearableRecyclerView recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setEdgeItemsCenteringEnabled(true);

        chatRooms = ChatResponseUtils.getChatRooms(this);

        recyclerView.setLayoutManager(new WearableLinearLayoutManager(this));
        ChatHistoryAdapter adapter = new ChatHistoryAdapter(chatRooms);
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

    @Override
    protected void onResume() {
        super.onResume();
        // Register the listener when the activity comes to the foreground.
        Wearable.getDataClient(this).addListener(chatSyncManager);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener when the activity goes to the background.
        Wearable.getDataClient(this).removeListener(chatSyncManager);
    }
}