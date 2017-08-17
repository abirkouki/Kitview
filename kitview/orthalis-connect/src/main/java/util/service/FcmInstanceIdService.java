package util.service;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.orthalis.connect.R;

/**
 * Created by orthalis on 23/05/2017.
 */

public class FcmInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String recent_token = FirebaseInstanceId.getInstance().getToken();
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(String.valueOf(R.string.FCM_PREF), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(String.valueOf(R.string.FCM_TOKEN),recent_token);
        editor.commit();

    }

}
