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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group6finalgroupproject.R;
import com.example.group6finalgroupproject.adapter.ChatHistoryAdapter2;
import com.example.group6finalgroupproject.adapter.ChatRoomAdapter2;
import com.example.group6finalgroupproject.databinding.ActivityChatHistory2Binding;
import com.example.group6finalgroupproject.helpers.ChatSyncManager2;
import com.example.group6finalgroupproject.model.ChatRoom2;
import com.example.group6finalgroupproject.utils.ChatResponseUtils2;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity displaying a list of past chat rooms.
 * Uses a RecyclerView to show titles and allows tapping to re-enter a conversation.
 */
public class ChatHistoryActivity2 extends AppCompatActivity {

    ActivityChatHistory2Binding binding;
    private List<ChatRoom2> chatRooms = new ArrayList<>();
    private ChatSyncManager2 chatSyncManager;
    private ChatHistoryAdapter2 adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ChatHistoryActivity2", "onCreate called");

        binding = ActivityChatHistory2Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        init();

        chatSyncManager = ChatSyncManager2.getInstance(this);
    }

    /**
     * Set up the RecyclerView with LinearLayoutManager and adapter.
     * Loads chat rooms from storage and handles item clicks.
     */
    private void init() {
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);

        chatRooms = ChatResponseUtils2.getChatRooms(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatHistoryAdapter2(chatRooms);
        adapter.setOnItemClickListener(new ChatHistoryAdapter2.OnItemClickListener() {
            @Override
            public void onItemClick(ChatRoom2 chatRoom) {
                Intent intent = new Intent(ChatHistoryActivity2.this, MainActivity2.class);
                intent.putExtra(getString(R.string.chat_room_id), chatRoom.getId());
                startActivity(intent); // Takes us to the Chat Room screen
            }
        });
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * Refresh the RecyclerView data when new chat rooms arrive via sync.
     */
    public void refreshHistoryList() {
        chatRooms = ChatResponseUtils2.getChatRooms(ChatHistoryActivity2.this);

        adapter.setChatRooms(chatRooms);
        adapter.notifyDataSetChanged();

        if (adapter.getItemCount() > 0) {
            binding.recyclerView.smoothScrollToPosition(0);
        }
        Log.i("MainActivity2", "Chat room refreshed!");
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