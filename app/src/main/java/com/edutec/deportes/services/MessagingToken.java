package com.edutec.deportes.services;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

public class MessagingToken {

    public MessagingToken(Activity activity) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM FAILED", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    String token = task.getResult();
                    Log.d("FCM TOKEN", token);
                    Toast.makeText(activity, token, Toast.LENGTH_SHORT).show();
                });
    }

}
