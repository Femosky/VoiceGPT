package com.example.group6finalgroupproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
import com.example.group6finalgroupproject.model.MessageItem;
import com.example.group6finalgroupproject.service.ChatGPTAPI;
import com.example.group6finalgroupproject.utils.ChatResponseUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatRoomActivity extends AppCompatActivity {

    ActivityChatRoomBinding binding;
    private static final int SPEECH_REQUEST_CODE = 100;
    private ChatRoom chatRoom;
    private ChatRoomAdapter adapter;
    private String chatRoomId;
    private TextToSpeech textToSpeech;
    private boolean ttsInitialized = false;
    private String queuedText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatRoomBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.getDefault());
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("ChatRoomActivity", "Language is not supported");
                    } else {
                        ttsInitialized = true;
                        // If any text was queued, speak it now.
                        if (queuedText != null) {
                            speakText(queuedText);
                            queuedText = null;
                        }
                    }
                } else {
                    Toast.makeText(ChatRoomActivity.this, getString(R.string.tts_init_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });
        init();
    }

    // Load start up code
    public void init() {
        WearableRecyclerView recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setEdgeItemsCenteringEnabled(true);

        chatRoomId = getIntent().getStringExtra(getString(R.string.chat_room_id));
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

        // If cameFromMainActivity, run the voice to text
        boolean cameFromMainActivity = getIntent().getBooleanExtra(getString(R.string.came_from_main_activity), false);
        Log.i("CAMEFROMMAINACTIVITY", String.valueOf(cameFromMainActivity));

        if (cameFromMainActivity && !chatRoom.getChatList().isEmpty()) {
            String response = chatRoom.getChatList().get(chatRoom.getChatList().size() - 1).getMessage();
            Log.i("RESPONSE IS: ", response);
            speakText(response);
        }
    }

    public void speakText(String text) {
        if (textToSpeech != null) {
            if (!ttsInitialized) {
                // TTS is still initializing, so store text in a queue
                queuedText = text;
                Log.i("ChatRoomActivity", "TTS not initialized, queuing text.");
            } else {
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utteranceId");
            }
        }
    }

    // The refresh method to re-load the chatroom data
    public void refreshChatRoom() {
        chatRoom = ChatResponseUtils.getChatRoom(this, chatRoomId);
        // Update the adapter's data if necessary.
        if (adapter != null) {
            adapter.setChatRoom(chatRoom);
            adapter.notifyDataSetChanged();

            // Scroll to the bottom - last item
            binding.recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
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