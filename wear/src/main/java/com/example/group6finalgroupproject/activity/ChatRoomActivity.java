package com.example.group6finalgroupproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
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
import com.example.group6finalgroupproject.service.ChatGPTAPI;
import com.example.group6finalgroupproject.utils.ChatResponseUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity {

    ActivityChatRoomBinding binding;
    private static final int SPEECH_REQUEST_CODE = 100;
    private ChatRoom chatRoom;
    private ChatRoomAdapter adapter;
    private String chatRoomId;

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

        chatRoomId = getIntent().getStringExtra("chat_room_id");
        chatRoom = ChatResponseUtils.getChatRoom(this, chatRoomId);

        recyclerView.setLayoutManager(new WearableLinearLayoutManager(this));
        adapter = new ChatRoomAdapter(chatRoom);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // LISTENERS

        binding.newChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatRoom.resetChatRoom();
                startActivity(new Intent(ChatRoomActivity.this, MainActivity.class));
            }
        });

        binding.micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVoiceInputCapture();
            }
        });
    }

    // The refresh method to re-load the chatroom data
    public void refreshChatRoom() {
        chatRoom = ChatResponseUtils.getChatRoom(this, chatRoomId);
        // Update the adapter's data if necessary.
        if (adapter != null) {
            adapter.setChatRoom(chatRoom);
            adapter.notifyDataSetChanged();
        }
        Log.i("ChatRoomActivity", "Chat room refreshed!");
    }

    public void startVoiceInputCapture() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                String userPrompt = results.get(0);

                // Send the prompt to through the ChatGPT API for a response
                long timestamp = System.currentTimeMillis() / 1000;
                ChatGPTAPI.postPrompt(this, chatRoom, userPrompt, timestamp, true);
            }
        }
    }
}