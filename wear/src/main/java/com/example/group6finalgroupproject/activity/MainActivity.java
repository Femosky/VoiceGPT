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

import com.example.group6finalgroupproject.R;
import com.example.group6finalgroupproject.databinding.ActivityMainBinding;
import com.example.group6finalgroupproject.helper.ChatRoomManager;
import com.example.group6finalgroupproject.helper.ChatSyncManager;
import com.example.group6finalgroupproject.model.ChatRoom;
import com.example.group6finalgroupproject.service.ChatGPTAPI;
import com.example.group6finalgroupproject.utils.HelperUtils;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private static final int SPEECH_REQUEST_CODE = 100;
    private ChatSyncManager chatSyncManager;

    ChatRoom chatRoom = ChatRoomManager.getChatRoom();

    /**
     * Configure button listeners
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        init();

        chatSyncManager = ChatSyncManager.getInstance(this);
    }

    /**
     * Set up button clicks for reset, mic input, and viewing history
     * Set up reset button listener
     */
    private void init() {
        // Set up reset button listener
        binding.resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatRoom.resetChatRoom();
                HelperUtils.showToast("Chatroom has been reset", MainActivity.this);
            }
        });

        // Set up mic button listener
        binding.micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVoiceInputCapture();
            }
        });

        // Set up Chat history button listener
        binding.viewChatHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ChatHistoryActivity.class));
            }
        });
    }

    /**
     * Launch speech recognizer
     */
    public void startVoiceInputCapture() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    /**
     * Send recognized speech to ChatGPT API on result
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
                ChatGPTAPI.postPrompt(this, chatRoom, userPrompt, timestamp, false);
            }
        }
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