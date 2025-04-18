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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.example.group6finalgroupproject.R;
import com.example.group6finalgroupproject.adapter.ChatHistoryAdapter;
import com.example.group6finalgroupproject.adapter.ChatRoomAdapter;
import com.example.group6finalgroupproject.databinding.ActivityChatRoomBinding;
import com.example.group6finalgroupproject.helper.ChatRoomManager;
import com.example.group6finalgroupproject.helper.ChatSyncManager;
import com.example.group6finalgroupproject.model.ChatRoom;
import com.example.group6finalgroupproject.model.MessageItem;
import com.example.group6finalgroupproject.service.ChatGPTAPI;
import com.example.group6finalgroupproject.utils.ChatResponseUtils;
import com.example.group6finalgroupproject.utils.HelperUtils;
import com.google.android.gms.wearable.Wearable;
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

    private ChatSyncManager chatSyncManager;

    /**
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     * Initialize TextToSpeech before loading the UI
     * Set up RecyclerView, listeners, and handle any incoming voice-trigger
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatRoomBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Load TextToSpeech First to ensure it reads our responses back.
        initializedTextToSpeech();

        init();

        chatSyncManager = ChatSyncManager.getInstance(this);
    }

    /**
     * Initialize the TextToSpeech engine for reading messages.
     * Queues up any pending text until initialization completes.
     */
    private void initializedTextToSpeech() {
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
    }

    /**
     * Configure RecyclerView for messages, scroll to latest,
     * retrieve the ChatRoom object, and set up UI listeners.
     *
     * Set up chat list UI
     * Retrieve chatRoomId passed from history or MainActivity
     * Show newest messages at bottom
     * Scroll immediately to the latest message
     * If coming from history, read out the last AI response
     */
    public void init() {
        WearableRecyclerView recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setEdgeItemsCenteringEnabled(true);

        chatRoomId = getIntent().getStringExtra(getString(R.string.chat_room_id));

        if (chatRoomId == null) {
            chatRoomId = chatRoom.getId();
        }

        chatRoom = ChatResponseUtils.getChatRoom(this, chatRoomId);

        WearableLinearLayoutManager manager = new WearableLinearLayoutManager(this);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);
        adapter = new ChatRoomAdapter(chatRoom);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // Immediately scroll to the bottom (latest messages)
        recyclerView.post(() -> {
            int last = adapter.getItemCount() - 1;
            if (last >= 0) {
                recyclerView.scrollToPosition(last);
            }
        });

        // LISTENERS
        loadListeners();

        // If cameFromMainActivity, run the voice to text
        boolean cameFromMainActivity = getIntent().getBooleanExtra(getString(R.string.came_from_main_activity), false);
        Log.i("CAMEFROMMAINACTIVITY", String.valueOf(cameFromMainActivity));

        if (cameFromMainActivity && !chatRoom.getChatList().isEmpty()) {
            String response = chatRoom.getChatList().get(chatRoom.getChatList().size() - 1).getMessage();
            Log.i("RESPONSE IS: ", response);
            speakText(response);
        }
    }

    /**
     * Wire up button clicks: replay last message, start new chat, and record speech input
     * Reset conversation and return to main
     * Trigger voice input capture
     */
    private void loadListeners() {
        binding.reListenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<MessageItem> chatRoomList = chatRoom.getChatList();
                if (!chatRoomList.isEmpty()) {
                    MessageItem lastMessageItem = chatRoom.getChatList().get(chatRoomList.size() - 1);
                    String textToBeSpoken = lastMessageItem.getMessage();
                    speakText(textToBeSpoken);
                } else {
                    HelperUtils.showToast("No message to read aloud.", ChatRoomActivity.this);
                }
            }
        });

        binding.newChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                chatRoom.resetChatRoom();
                ChatRoomManager.setChatRoom(new ChatRoom());
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

    /**
     * Speak the given text via TTS, or queue it if TTS isn’t ready.
     */
    public void speakText(String text) {
        if (textToSpeech != null) {
            if (!ttsInitialized) {
                // TTS is still initializing, so store text in a queue
                queuedText = text;
                Log.i("ChatRoomActivity", "TTS not initialized, queuing text.");
            } else {
                Log.i("THE TEXT IS", text);
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utteranceId");
            }
        }
    }

    /**
     * Reload messages from storage, update the adapter, and scroll to the bottom
     */
    public void refreshChatRoom() {
        // always show the singleton
        chatRoom = ChatResponseUtils.getChatRoom(this, chatRoomId);

        adapter.setChatRoom(chatRoom);
        adapter.notifyDataSetChanged();

        int last = adapter.getItemCount() - 1;
        if (last >= 0) {
            binding.recyclerView.smoothScrollToPosition(last);
        }
        Log.i("MainActivity2", "Chat room refreshed!");
    }

    /**
     * Launch Android’s built-in speech recognizer
     */
    public void startVoiceInputCapture() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    /**
     * Handle the result of speech input and send prompt to ChatGPT API
     * Post prompt and expect a TTS callback on response
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                String userPrompt = results.get(0);

                // Send the prompt to through the ChatGPT API for a response
                HelperUtils.showToast(getString(R.string.loading_prompt_response), this);
                long timestamp = System.currentTimeMillis() / 1000;
                ChatGPTAPI.postPrompt(this, chatRoom, userPrompt, timestamp, true);
            }
        }
    }

    /**
     * Clean up TTS resources
     */
    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    /**
     * Register the listener when the activity comes to the foreground.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Register the listener when the activity comes to the foreground.
        Wearable.getDataClient(this).addListener(chatSyncManager);
    }

    /**
     * Unregister the listener when the activity goes to the background.
     */
    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener when the activity goes to the background.
        Wearable.getDataClient(this).removeListener(chatSyncManager);
    }
}