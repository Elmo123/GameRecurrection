package com.example.gamerecurrection;

import android.content.Context;

import java.io.InputStream;

public class Helpers {

    public Helpers () {};

    public static String loadJSON(Context context, String filename) {
        try (InputStream is = context.getAssets().open(filename)) {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            return new String(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
