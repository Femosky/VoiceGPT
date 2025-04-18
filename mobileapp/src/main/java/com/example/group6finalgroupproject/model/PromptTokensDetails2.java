package com.example.group6finalgroupproject.model;

/**
 * Detailed token usage information for the prompt side
 * of the ChatGPT API request.
 */
public class PromptTokensDetails2 {
    private int cached_tokens;
    private int audio_tokens;

    // Getters and Setters

    public int getCached_tokens() {
        return cached_tokens;
    }
    public void setCached_tokens(int cached_tokens) {
        this.cached_tokens = cached_tokens;
    }
    public int getAudio_tokens() {
        return audio_tokens;
    }
    public void setAudio_tokens(int audio_tokens) {
        this.audio_tokens = audio_tokens;
    }
}
