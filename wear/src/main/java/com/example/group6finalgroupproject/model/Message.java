package com.example.group6finalgroupproject.model;

import java.util.List;

public class Message {
    private String role;
    private String content;
    private Object refusal;
    private List<Object> annotations;

    // Getters and Setters

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public Object getRefusal() {
        return refusal;
    }
    public void setRefusal(Object refusal) {
        this.refusal = refusal;
    }
    public List<Object> getAnnotations() {
        return annotations;
    }
    public void setAnnotations(List<Object> annotations) {
        this.annotations = annotations;
    }
}
