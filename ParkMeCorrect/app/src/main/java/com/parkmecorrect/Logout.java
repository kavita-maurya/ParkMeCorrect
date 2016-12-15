package com.parkmecorrect;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

public class Logout {
    public void logout(Context context) {


        SharedPreferences sharedPrefLogin = context.getSharedPreferences(context.getString(R.string.login_shared_preference), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sharedPrefLogin.edit();

        String userType = sharedPrefLogin.getString("type",null);

        editor1.remove("userId");
        editor1.remove("pwd");
        editor1.remove("type");
        editor1.remove("language");
        editor1.commit();

        if(userType != null && userType.equalsIgnoreCase("Student")) {
            SharedPreferences sharedPrefProfile = context.getSharedPreferences(context.getString(R.string.profile_shared_preference), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor2 = sharedPrefProfile.edit();

            editor2.remove("regid");
            editor2.remove("carno");
            editor2.remove("sticker");
            editor2.remove("name");
            editor2.remove("mobile");
            editor2.remove("email");
            editor2.remove("language");
            editor2.commit();
        }



    }
}
