package in.myfootprint.myfootprint.push;

/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;

import java.util.ArrayList;

import in.myfootprint.myfootprint.MyFootprintApplication;
import in.myfootprint.myfootprint.R;
import in.myfootprint.myfootprint.activities.MainActivity;
import in.myfootprint.myfootprint.activities.ProfileActivity;
import in.myfootprint.myfootprint.utils.PrefUtilsNew;

public class MyGCMListenerService extends GcmListenerService {

    private static final String TAG = "MyGCMListenerService";

    ArrayList<String> messageList = new ArrayList<String>();
    int numMessages;
    int totalMessages = 0;

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.e(TAG, "From: " + from);
        Log.e(TAG, "Message: " + message);
        Log.e(TAG, "Data: " + data.toString());

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        totalMessages++;
        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        updateMyActivity(getApplicationContext(), totalMessages);
        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(message);
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        MyFootprintApplication.getInstance().trackEvent("Open directly", "Push Message", "Notification");
        numMessages = 0;
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("FragmentNumber", "3");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.favicon_marker)
                .setContentTitle("myFootprint")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        notificationBuilder.setNumber(++numMessages);
        //Log.e("NotifWaala", String.valueOf(numMessages));
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

    }

    static void updateMyActivity(Context context, int messageNumber) {

        Intent intent = new Intent("unique_name");

        /*Gson gson = new Gson();
        // This can be any object. Does not have to be an arraylist.
        String json = gson.toJson(messageList);*/
        int prevValue = PrefUtilsNew.getNotifNumber();
        int newValue = prevValue + messageNumber;
        PrefUtilsNew.setNotifNumber(newValue);
        Log.e("Khudka", String.valueOf(newValue));
        //put whatever data you want to send, if any
        //intent.putExtra("message", messageNo);
        //send broadcast
        context.sendBroadcast(intent);
    }
}