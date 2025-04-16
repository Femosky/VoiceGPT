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

import com.example.group6finalgroupproject.R;
import com.example.group6finalgroupproject.databinding.ActivityMain2Binding;
import com.example.group6finalgroupproject.helpers.ChatSyncManager2;
import com.example.group6finalgroupproject.model.ChatRoom2;
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

public class MainActivity2 extends AppCompatActivity implements MessageClient.OnMessageReceivedListener, DataClient.OnDataChangedListener {

    ActivityMain2Binding binding;
//    private ChatSyncManager2 chatSyncManager2;
    private static final int SPEECH_REQUEST_CODE = 101;

    private static final String LISTENER_STATE_PATH = "/listenerState";
    private static final String TAG = "ChatSyncManager";
    private static final String GRAPH_CAPABILITY_NAME = "chatroom_sync";
    private static final String CHATROOM_DATA_PATH = "/chatroom_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main2);

        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        init();
//        chatSyncManager2 = ChatSyncManager2.getInstance(this);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        // Register the listener when the activity comes to the foreground.
//        Wearable.getDataClient(this).addListener(chatSyncManager2);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        // Unregister the listener when the activity goes to the background.
//        Wearable.getDataClient(this).removeListener(chatSyncManager2);
//    }

    public void loadListeners() {
        // Listeners
        binding.voiceInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVoiceInputCapture();
            }
        });

        binding.syncButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity2.this, getString(R.string.syncing_data), Toast.LENGTH_SHORT).show();
            }
        }));
    }

    // To run start up code
    private void init() {
        Wearable.getMessageClient(this).addListener(this);
        readCurrentListenerState();
        Wearable.getDataClient(this).addListener(this);

        loadListeners();
    }

    private void readCurrentListenerState() {
        Task<DataItemBuffer> task = Wearable.getDataClient(this).getDataItems();
        task.addOnSuccessListener(new OnSuccessListener<DataItemBuffer>() {
            @Override
            public void onSuccess(DataItemBuffer dataItems) {
                for (DataItem item : dataItems) {
                    if (item.getUri().getPath().equals(MainActivity2.LISTENER_STATE_PATH)) {
                        displayListenerState(item);
                        return;
                    }
                }
                MainActivity2.this.binding.testText.setText("No data");
            }
        });
    }

    private void displayListenerState(DataItem item) {
        byte data = item.getData()[0];
        if (data == 0) {
            this.binding.testText.setText("recording");
        } else {
            this.binding.testText.setText("not recording");
        }
    }

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_DELETED) {
                this.binding.testText.setText(getString(R.string.test_string_2));
            } else if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().equals(MainActivity2.LISTENER_STATE_PATH)) {
                    displayListenerState(item);
                }
            }
        }
    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        if (messageEvent.getPath().equals(CHATROOM_DATA_PATH)) {
            HelperUtils2.showToast("Chatroom Data received", this);
            byte[] data = messageEvent.getData();

            // COME BACK HERE!!!!! CHECK IF NULL
            List<ChatRoom2> chatRooms = HelperUtils2.convertJSONDataToChatroomList(data);

            String testResult = chatRooms == null ? "null" : chatRooms.toString();
            binding.testText.setText(testResult);
            Log.i("MainActivity", "Received Chatroom list from watch: " + testResult);
        }
    }

    public void startVoiceInputCapture() {
        Intent voiceIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(voiceIntent, SPEECH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                String spokenText = results.get(0);
                binding.chatInput.setText(spokenText);
            }
        }

    }
}