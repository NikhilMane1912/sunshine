/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sagar.sunshine.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.gcm.GcmListenerService;
import com.sagar.sunshine.MainActivity;
import com.sagar.sunshine.R;

import org.json.JSONException;
import org.json.JSONObject;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    private static final String EXTRA_DATA = "data";
    private static final String EXTRA_WEATHER = "weather";
    private static final String EXTRA_LOCATION = "location";

    public static final int NOTIFICATION_ID = 1;

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        if (!data.isEmpty()) {
            // TODO: gcm_default sender ID comes from the API console
            String senderId = getString(R.string.gcm_defaultSenderId);
            if (senderId.length() == 0) {
                Toast.makeText(this, "SenderID string needs to be set", Toast.LENGTH_LONG).show();
            }
            if ((senderId).equals(from)) {
                try {
                    JSONObject jsonObject = new JSONObject(data.getString(EXTRA_DATA));
                    String weather = jsonObject.getString(EXTRA_WEATHER);
                    String location = jsonObject.getString(EXTRA_LOCATION);
                    String alert =
                            String.format(getString(R.string.gcm_weather_alert), weather, location);
                    sendNotification(alert);
                } catch (JSONException e) {
                   e.printStackTrace();
                }
            }
            Log.i(TAG, "Received: " + data.toString());
        }
    }

    private void sendNotification(String message) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent =
                PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        Bitmap largeIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.art_storm);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.art_clear)
                        .setLargeIcon(largeIcon)
                        .setContentTitle("Weather Alert!")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}