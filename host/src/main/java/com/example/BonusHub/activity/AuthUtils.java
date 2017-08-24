package com.example.BonusHub.activity;

import android.content.Context;

public class AuthUtils {
    private static final String PREFERENCES_AUTHORIZED_KEY = "isAuthorized";
    private static final String PREFERENCES_ROLE_KEY = "";
    private static final String PREFERENCES_HOSTED_KEY = "isHosted";
    private static final String PREFERENCES_COOKIE_KEY = "cookie";
    private static final String LOGIN_PREFERENCES = "LoginData";

    public static void setCookie(Context context, String cookie)
    {
        context.getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putString(PREFERENCES_COOKIE_KEY, cookie)
                .apply();
    }


    public static void setAuthorized(Context context)
    {
        context.getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(PREFERENCES_AUTHORIZED_KEY, true)
                .apply();
    }

    public static void setHosted(Context context, boolean isHosted)
    {
        context.getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(PREFERENCES_HOSTED_KEY, isHosted)
                .apply();
    }

    public static void logout(Context context)
    {
        context.getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(PREFERENCES_AUTHORIZED_KEY, false)
                .putString(PREFERENCES_ROLE_KEY, "")
                .apply();

    }

    public static boolean isAuthorized(Context context)
    {
        return context.getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE)
                .getBoolean(PREFERENCES_AUTHORIZED_KEY, false);
    }

    public static boolean isHosted(Context context)
    {
        return context.getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE)
                .getBoolean(PREFERENCES_HOSTED_KEY, false);
    }

    public static String getCookie(Context context)
    {
        return context.getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE)
                .getString(PREFERENCES_COOKIE_KEY, "");
    }

    public static void setHostRole(Context context){
        context.getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putString(PREFERENCES_ROLE_KEY, "Host")
                .apply();
    }


    public static void setStaffRole(Context context){
        context.getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putString(PREFERENCES_ROLE_KEY, "Staff")
                .apply();
    }

    public static void setClientRole(Context context){
        context.getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putString(PREFERENCES_ROLE_KEY, "Client")
                .apply();
    }

    public static String getRole(Context context) {
        return context.getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE)
                .getString(PREFERENCES_ROLE_KEY, "");
    }

}
