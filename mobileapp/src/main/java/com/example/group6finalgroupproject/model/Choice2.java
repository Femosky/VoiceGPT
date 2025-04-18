package com.example.group6finalgroupproject.model;

/**
 * Represents a single completion choice returned by ChatGPT API.
 * Contains the message payload and metadata about logging and finish reason.
 */
public class Choice2 {
    private int index;
    private Message2 message;
    private Object logprobs;
    private String finish_reason;

    // Getters and Setters

    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public Message2 getMessage() {
        return message;
    }
    public void setMessage(Message2 message) {
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
