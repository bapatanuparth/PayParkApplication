package com.atharva.paypark.util;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Preferences  {
    public static boolean notification = false;
    public static SharedPreferences sharedPreferences;

    public static String Low_Battery_Notification_KEY = "LowBatteryNotification";

    public static String get(String key) {
        return sharedPreferences.getString(key,"True");
    }

    public static void put(String key, String value){
        Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.putString(key,value);
        editor.apply();
    }
}
