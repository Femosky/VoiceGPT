package com.example.group6finalgroupproject.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.group6finalgroupproject.BuildConfig;
import com.example.group6finalgroupproject.R;
import com.example.group6finalgroupproject.activity.MainActivity;
import com.example.group6finalgroupproject.helper.ChatRoomManager;
import com.example.group6finalgroupproject.model.ChatResponse;
import com.example.group6finalgroupproject.model.ChatRoom;
import com.example.group6finalgroupproject.model.Choice;
import com.example.group6finalgroupproject.model.ErrorData;
import com.example.group6finalgroupproject.model.ErrorResponse;
import com.example.group6finalgroupproject.model.Message;
import com.example.group6finalgroupproject.model.MessageItem;
import com.example.group6finalgroupproject.utils.ChatResponseUtils;
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

public class ChatGPTAPI {
    private static JSONObject parseStringToJSONObject(Context context, String userPrompt) {
        // Build the chat completion payload
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(context.getString(R.string.model_string), context.getString(R.string.chatGPT_payload_model));
            jsonObject.put(context.getString(R.string.store_string), true);

            JSONArray jsonArrayMessage = new JSONArray();
            JSONObject jsonObjectMessage = new JSONObject();
            jsonObjectMessage.put(context.getString(R.string.role_string), context.getString(R.string.user_string));
            jsonObjectMessage.put(context.getString(R.string.content_string), userPrompt);
            jsonArrayMessage.put(jsonObjectMessage);

            jsonObject.put(context.getString(R.string.messages_string), jsonArrayMessage);

            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void postPrompt(Context context, String userPrompt) {
        if (userPrompt.isEmpty()) {
            // Don't send a prompt query if the user prompt is empty
            return;
        }

        JSONObject payloadObject = parseStringToJSONObject(context, userPrompt);


        if (payloadObject == null) {
            // Break out of post function
            return;
        }

        Log.i("JSON HOBJECT", payloadObject.toString());

        try {
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            String urlString = context.getString(R.string.post_prompt_url);

            RequestBody postBody = RequestBody.create(JSON, payloadObject.toString());
            Request post = new Request.Builder()
                    .url(urlString)
                    .addHeader("Authorization", "Bearer " + BuildConfig.CHATGPT_API_KEY)
                    .post(postBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.newCall(post).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        ResponseBody responseBody = response.body();
                        Gson gson = new Gson();

                        // IF THE API RETURNS A SINGLE NODE 'error'
                        if (!response.isSuccessful()) {
                            ErrorResponse errorResponse = gson.fromJson(responseBody.string(), ErrorResponse.class);
                            ErrorData error = errorResponse.getError();

                            System.out.println(error.getMessage());
                            throw new IOException(context.getString(R.string.unexpected_code) + response);
                        }

                        // PARSE AND RETURN THE CHAT COMPLETION RESPONSE TO a ChatResponse object

                        ChatRoom chatRoom = ChatRoomManager.getChatRoom();

                        ChatResponse chatResponse = gson.fromJson(responseBody.string(), ChatResponse.class);

                        MessageItem messageItem = new MessageItem();
                        messageItem.setCreated(chatResponse.getCreated());
                        messageItem.setModel(chatResponse.getModel());
                        messageItem.setFrom(context.getString(R.string.bot_string));

                        List<Choice> choices = chatResponse.getChoices();
                        Message botResponse = choices.get(0).getMessage();
                        String result = botResponse.getContent();
                        messageItem.setMessage(result);

                        chatRoom.appendChatList(messageItem);
                        chatRoom.setTitle(result); // FOR NOW, CHANGE LATER IF POSSIBLE!!!
                        long timestampSeconds = System.currentTimeMillis() / 1000;
                        chatRoom.setCreated(timestampSeconds);

                        // Save prompt response to shared preferences
                        ChatResponseUtils.saveMessage(chatRoom, context);
                        Log.i("CHAT RESPONSE", result);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
