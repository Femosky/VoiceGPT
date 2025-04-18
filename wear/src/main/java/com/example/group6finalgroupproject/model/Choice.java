package com.example.group6finalgroupproject.model;

/**
 * Represents one of the possible completions (choices)
 * returned by the ChatGPT API.
 */
public class Choice {
    private int index;
    private Message message;
    private Object logprobs;
    private String finish_reason;

    // Getters and Setters

    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public Message getMessage() {
        return message;
    }
    public void setMessage(Message message) {
        this.message = message;
    }
    public Object getLogprobs() {
        return logprobs;
    }
    public void setLogprobs(Object logprobs) {
        this.logprobs = logprobs;
    }
    public String getFinish_reason() {
        return finish_reason;
    }
    public void setFinish_reason(String finish_reason) {
        this.finish_reason = finish_reason;
    }
}
