package com.example.group6finalgroupproject.activity;

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
import com.example.group6finalgroupproject.adapter.ChatRoomAdapter;
import com.example.group6finalgroupproject.databinding.ActivityChatRoomBinding;
import com.example.group6finalgroupproject.model.ChatRoom;
import com.example.group6finalgroupproject.utils.ChatResponseUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity {

    ActivityChatRoomBinding binding;

    private ChatRoom chatRoom = new ChatRoom();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatRoomBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        init();
    }

    // Load start up code
    public void init() {
        WearableRecyclerView recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setEdgeItemsCenteringEnabled(true);

        String chatRoomId = getIntent().getStringExtra("chat_room_id");
        chatRoom = ChatResponseUtils.getChatRoom(this, chatRoomId);

        recyclerView.setLayoutManager(new WearableLinearLayoutManager(this));
        ChatRoomAdapter adapter = new ChatRoomAdapter(chatRoom);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}