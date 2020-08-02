package com.example.hodgepodge;

import com.google.gson.Gson;

public class Utils {
    private Utils() {}

    private final static Gson GSON = new Gson();

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }
}
