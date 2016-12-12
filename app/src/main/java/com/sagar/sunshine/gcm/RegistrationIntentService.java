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

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.sagar.sunshine.MainActivity;
import com.sagar.sunshine.R;


public class RegistrationIntentService extends IntentService {
    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            synchronized (TAG) {
                InstanceID instanceID = InstanceID.getInstance(this);

                // TODO: gcm_default sender ID comes from the API console
                String senderId = getString(R.string.gcm_defaultSenderId);
                if ( senderId.length() != 0 ) {
                    String token = instanceID.getToken(senderId,
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    sendRegistrationToServer(token);
                }
                sharedPreferences.edit().putBoolean(MainActivity.SENT_TOKEN_TO_SERVER, true).apply();
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean(MainActivity.SENT_TOKEN_TO_SERVER, false).apply();
        }
    }

    private void sendRegistrationToServer(String token) {
        Log.i(TAG, "GCM Registration Token: " + token);
    }
}
