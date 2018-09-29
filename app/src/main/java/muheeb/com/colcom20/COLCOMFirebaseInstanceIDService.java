package muheeb.com.colcom20;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class COLCOMFirebaseInstanceIDService extends FirebaseInstanceIdService {




    private static final String TAG = COLCOMFirebaseInstanceIDService.class.getSimpleName();
    @Override
    public void onTokenRefresh() {
        //super.onTokenRefresh();
        Log.e("Test", "onTokenRefresh: " );
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("RT in IIdService", refreshedToken);
        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server

        Log.e(TAG, "sendRegistrationToServer: " + token);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        Log.e(TAG, "storeRegIdInPref: "+token );
        Log.e(TAG, "storeRegIdInPref: "+pref.getString("regId","") );
        editor.commit();
    }
}