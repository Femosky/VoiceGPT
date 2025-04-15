package com.example.group6finalgroupproject.utils;

import android.content.Context;
import android.widget.Toast;

public class HelperUtils2 {
    public static void showToast(String text, Context context) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
