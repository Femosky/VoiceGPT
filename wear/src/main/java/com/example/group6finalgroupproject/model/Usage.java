package com.example.group6finalgroupproject.model;

public class Usage {
    private int prompt_tokens;
    private int completion_tokens;
    private int total_tokens;
    private PromptTokensDetails prompt_tokens_details;
    private CompletionTokensDetails completion_tokens_details;

    // Getters and Setters

    public int getPrompt_tokens() {
        return prompt_tokens;
    }
    public void setPrompt_tokens(int prompt_tokens) {
        this.prompt_tokens = prompt_tokens;
    }
    public int getCompletion_tokens() {
        return completion_tokens;
    }
    public void setCompletion_tokens(int completion_tokens) {
        this.completion_tokens = completion_tokens;
    }
    public int getTotal_tokens() {
        return total_tokens;
    }
    public void setTotal_tokens(int total_tokens) {
        this.total_tokens = total_tokens;
    }
    public PromptTokensDetails getPrompt_tokens_details() {
        return prompt_tokens_details;
    }
    public void setPrompt_tokens_details(PromptTokensDetails prompt_tokens_details) {
        this.prompt_tokens_details = prompt_tokens_details;
    }
    public CompletionTokensDetails getCompletion_tokens_details() {
        return completion_tokens_details;
    }
    public void setCompletion_tokens_details(CompletionTokensDetails completion_tokens_details) {
        this.completion_tokens_details = completion_tokens_details;
    }
}
