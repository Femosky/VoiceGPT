package com.example.group6finalgroupproject.model;

import java.util.List;

/**
 * Represents the full API response from ChatGPT for a single request.
 * Contains metadata (id, model, timestamps), a list of Choice2 objects,
 * usage statistics, and service tier information.
 */
public class ChatResponse2 {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice2> choices;
    private Usage2 usage;
    private String service_tier;

    // Getters and Setters

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getObject() {
        return object;
    }
    public void setObject(String object) {
        this.object = object;
    }
    public long getCreated() {
        return created;
    }
    public void setCreated(long created) {
        this.created = created;
    }
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public List<Choice2> getChoices() {
        return choices;
    }
    public void setChoices(List<Choice2> choices) {
        this.choices = choices;
    }
    public Usage2 getUsage() {
        return usage;
    }
    public void setUsage(Usage2 usage) {
        this.usage = usage;
    }
    public String getService_tier() {
        return service_tier;
    }
    public void setService_tier(String service_tier) {
        this.service_tier = service_tier;
    }
}
