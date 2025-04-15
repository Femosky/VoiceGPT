package com.example.group6finalgroupproject.utils;

import android.content.Context;
import android.widget.Toast;

import com.example.group6finalgroupproject.R;
import com.example.group6finalgroupproject.activity.MainActivity;

public class HelperUtils {
    public static void showToast(String text, Context context) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
