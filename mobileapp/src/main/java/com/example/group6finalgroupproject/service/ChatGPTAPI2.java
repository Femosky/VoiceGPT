package com.example.group6finalgroupproject.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.group6finalgroupproject.BuildConfig;
import com.example.group6finalgroupproject.R;
import com.example.group6finalgroupproject.activity.ChatHistoryActivity2;
import com.example.group6finalgroupproject.activity.MainActivity2;
import com.example.group6finalgroupproject.helpers.ChatRoomManager2;
import com.example.group6finalgroupproject.helpers.ChatSyncManager2;
import com.example.group6finalgroupproject.model.ChatResponse2;
import com.example.group6finalgroupproject.model.ChatRoom2;
import com.example.group6finalgroupproject.model.Choice2;
import com.example.group6finalgroupproject.model.ErrorData2;
import com.example.group6finalgroupproject.model.ErrorResponse2;
import com.example.group6finalgroupproject.model.Message2;
import com.example.group6finalgroupproject.model.MessageItem2;
import com.example.group6finalgroupproject.utils.ChatResponseUtils2;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Handles communication with the ChatGPT API for version2 of the app.
 * Builds request payloads, sends HTTP calls, processes responses,
 * updates local chat storage, syncs to Wear OS, and refreshes UI.
 */
public class ChatGPTAPI2 {
    /**
     * Constructs the JSON payload for the ChatGPT completion endpoint.
     * Includes model selection, store flag, past conversation context, and new user prompt.
     *
     * @param context          app Context for string resources
     * @param contextChatRoom  current ChatRoom2 containing message history
     * @param userPrompt       the new prompt text from the user
     * @return JSONObject representing the request body, or null on error
     */
    private static JSONObject parseStringToJSONObject(Context context, ChatRoom2 contextChatRoom, String userPrompt) {
        // Build the chat completion payload
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(context.getString(R.string.model_string), context.getString(R.string.chatGPT_payload_model));
            jsonObject.put(context.getString(R.string.store_string), true);

            JSONArray jsonArrayMessage = new JSONArray();

            // Insert all past prompt to keep the chat room context going if chatroom is not empty
            if (!contextChatRoom.getChatList().isEmpty()) {
                for (MessageItem2 messageItem : contextChatRoom.getChatList()) {
                    JSONObject jsonObjectMessage = new JSONObject();
                    String from = messageItem.getFrom();
                    jsonObjectMessage.put(context.getString(R.string.role_string), from);
                    jsonObjectMessage.put(context.getString(R.string.content_string), from.equals(context.getString(R.string.assistant_string)) ? messageItem.getMessage() : context.getString(R.string.pre_prompt_for_length) + messageItem.getMessage());
                    jsonArrayMessage.put(jsonObjectMessage);

                    jsonObject.put(context.getString(R.string.messages_string), jsonArrayMessage);
                }
            }

            JSONObject jsonObjectMessage = new JSONObject();
            jsonObjectMessage.put(context.getString(R.string.role_string), context.getString(R.string.user_string));
            jsonObjectMessage.put(context.getString(R.string.content_string), context.getString(R.string.pre_prompt_for_length) + userPrompt);
            jsonArrayMessage.put(jsonObjectMessage);

            jsonObject.put(context.getString(R.string.messages_string), jsonArrayMessage);

            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sends the user prompt and conversation context to ChatGPT API.
     * On success, updates the ChatRoom2, persists it, syncs to watch, and refreshes UI.
     *
     * @param context         app Context for networking and UI callbacks
     * @param contextChatRoom current ChatRoom2 holding the conversation
     * @param userPrompt      new prompt text
     * @param timestamp       Unix timestamp when user sent the prompt
     */
    public static void postPrompt(Context context, ChatRoom2 contextChatRoom, String userPrompt, long timestamp) {
        if (userPrompt.isEmpty()) {
            // Don't send a prompt query if the user prompt is empty
            stopLoading(context);
            return;
        }

        JSONObject payloadObject = parseStringToJSONObject(context, contextChatRoom, userPrompt);

        if (payloadObject == null) {
            // Break out of post function
            stopLoading(context);
            return;
        }

        try {
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            String urlString = context.getString(R.string.post_prompt_url);

            RequestBody postBody = RequestBody.create(JSON, payloadObject.toString());
            Request post = new Request.Builder()
                    .url(urlString)
                    .addHeader(context.getString(R.string.authorization_pre_header_string), context.getString(R.string.bearer_pre_header_string) + BuildConfig.CHATGPT_API_KEY)
                    .post(postBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.newCall(post).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                    stopLoading(context);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        ResponseBody responseBody = response.body();
                        Gson gson = new Gson();

                        // IF THE API RETURNS A SINGLE NODE 'error'
                        if (!response.isSuccessful()) {
                            ErrorResponse2 errorResponse = gson.fromJson(responseBody.string(), ErrorResponse2.class);
                            ErrorData2 error = errorResponse.getError();

                            throw new IOException(context.getString(R.string.unexpected_code) + error.getMessage());
                        }

                        // PARSE AND RETURN THE CHAT COMPLETION RESPONSE TO a ChatResponse object

                        ChatRoom2 chatRoom = new ChatRoom2();
                        if (contextChatRoom.getChatList().isEmpty()) {
                            chatRoom = ChatRoomManager2.getChatRoom();
                        } else {
                            chatRoom = contextChatRoom;
                        }

                        ChatResponse2 chatResponse = gson.fromJson(responseBody.string(), ChatResponse2.class);

                        // the User's prompt
                        MessageItem2 userMessageItem = new MessageItem2();
                        userMessageItem.setCreated(timestamp);
                        userMessageItem.setFrom(context.getString(R.string.user_string));
                        userMessageItem.setModel(chatResponse.getModel());
                        userMessageItem.setMessage(userPrompt);

                        // The Assistant's response
                        MessageItem2 messageItem = new MessageItem2();
                        messageItem.setCreated(chatResponse.getCreated());
                        messageItem.setFrom(context.getString(R.string.assistant_string));
                        messageItem.setModel(chatResponse.getModel());

                        List<Choice2> choices = chatResponse.getChoices();
                        Message2 botResponse = choices.get(0).getMessage();
                        String result = botResponse.getContent();
                        messageItem.setMessage(result);

                        if (chatRoom.getChatList().isEmpty()) {
                            // SET NEW TITLE AND DATE
                            chatRoom.setTitle(result); // FOR NOW, CHANGE LATER IF POSSIBLE!!!

                            long timestampSeconds = System.currentTimeMillis() / 1000;
                            chatRoom.setCreated(timestampSeconds);
                        } else {
                            // GRAB FIRST MESSAGE'S TITLE AND DATE
                            chatRoom.setTitle(chatRoom.getTitle());
                            chatRoom.setCreated(chatRoom.getCreated());
                        }

                        chatRoom.appendChatList(userMessageItem);
                        chatRoom.appendChatList(messageItem);

                        // Save chatroom to shared preferences
                        ChatResponseUtils2.saveMessage(chatRoom, context);
                        ChatSyncManager2.getInstance(context).sendChatRooms();

                        // Refresh the chat room UI
                        refreshPage(context);

                        Log.i("CHAT RESPONSE", result);
                    } catch (IOException e) {
                        stopLoading(context);
                        e.printStackTrace();
                    } finally {
                        stopLoading(context);
                    }
                }
            });

        } catch (Exception e) {
            stopLoading(context);
            e.printStackTrace();
        } finally {
            stopLoading(context);
        }
    }

    /**
     * Refreshes the MainActivity2 UI by invoking its refreshChatRoom method.
     * Must be called on the UI thread.
     *
     * @param context Activity context to cast and refresh
     */
    public static void refreshPage(Context context) {
        // Runs on the UI thread
        if (context instanceof MainActivity2) {
            MainActivity2 activity = (MainActivity2)context;
            activity.runOnUiThread(() -> {
                activity.refreshChatRoom();
            });
        }
    }

    /**
     * Stops the loading indicator in MainActivity2 by setting isLoading = false.
     * Must be called on the UI thread.
     *
     * @param context Activity context to cast and update
     */
    public static void stopLoading(Context context) {
        // Runs on the UI thread
        if (context instanceof MainActivity2) {
            MainActivity2 activity = (MainActivity2)context;
            activity.runOnUiThread(() -> {
                activity.setIsLoading(false);
            });
        }
    }
}
