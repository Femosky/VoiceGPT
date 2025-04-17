package com.example.group6finalgroupproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group6finalgroupproject.R;
import com.example.group6finalgroupproject.adapter.ChatRoomAdapter2;
import com.example.group6finalgroupproject.databinding.ActivityMain2Binding;
import com.example.group6finalgroupproject.helpers.ChatRoomManager2;
import com.example.group6finalgroupproject.helpers.ChatSyncManager2;
import com.example.group6finalgroupproject.model.ChatRoom2;
import com.example.group6finalgroupproject.service.ChatGPTAPI2;
import com.example.group6finalgroupproject.utils.ChatResponseUtils2;
import com.example.group6finalgroupproject.utils.HelperUtils2;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    ActivityMain2Binding binding;
    private ChatSyncManager2 chatSyncManager;
    private ChatRoomAdapter2 adapter;
    ChatRoom2 chatRoom = ChatRoomManager2.getChatRoom();
    Boolean isLoading = false;
    private String chatRoomId;
    private static final int SPEECH_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        init();

        chatSyncManager = ChatSyncManager2.getInstance(this);
    }

    // To run start up code
    private void init() {
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);

        chatRoomId = getIntent().getStringExtra(getString(R.string.chat_room_id));

        if (chatRoomId != null) {
            chatRoom = ChatResponseUtils2.getChatRoom(this, chatRoomId);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ChatRoomAdapter2(chatRoom);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // Load listerners
        loadListeners();
    }

    public void loadListeners() {
        binding.newChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!chatRoom.getChatList().isEmpty() || chatRoomId != null) {
                    ChatRoom2 newRoom = new ChatRoom2();
                    ChatRoomManager2.setChatRoom(newRoom);

                    Intent fresh = new Intent(MainActivity2.this, MainActivity2.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(fresh);
                }
            }
        });

        binding.historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatRoom.resetChatRoom();
                startActivity(new Intent(MainActivity2.this, ChatHistoryActivity2.class));
            }
        });

        binding.syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Manually sync chat data to watch
                ChatSyncManager2.getInstance(MainActivity2.this).sendChatRooms();
            }
        });

        binding.micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVoiceInputCapture();
            }
        });

        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userPrompt = binding.promptText.getText().toString();

                if (userPrompt.isEmpty()) {
                    HelperUtils2.showToast(getString(R.string.empty_prompt_error_message), MainActivity2.this);
                    return;
                }

                // Clear and send the prompt
                binding.promptText.setText(getString(R.string.empty_string));
                setIsLoading(true);

                long timestamp = System.currentTimeMillis() / 1000;
                ChatGPTAPI2.postPrompt(MainActivity2.this, chatRoom, userPrompt, timestamp);
            }
        });
    }

    public void refreshChatRoom() {
        // always show the singleton
        chatRoom = ChatRoomManager2.getChatRoom();

        adapter.setChatRoom(chatRoom);
        adapter.notifyDataSetChanged();

        int last = adapter.getItemCount() - 1;
        if (last >= 0) {
            binding.recyclerView.smoothScrollToPosition(last);
        }
        Log.i("MainActivity2", "Chat room refreshed!");
    }

    public void setIsLoading(Boolean status) {
        isLoading = status;

        if (status) {
            binding.loadingTextView.setVisibility(View.VISIBLE);
        } else {
            binding.loadingTextView.setVisibility(View.INVISIBLE);
        }
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
                HelperUtils2.showToast(getString(R.string.loading_prompt_response), this);
                long timestamp = System.currentTimeMillis() / 1000;
                ChatGPTAPI2.postPrompt(this, chatRoom, userPrompt, timestamp);
            }
        }
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