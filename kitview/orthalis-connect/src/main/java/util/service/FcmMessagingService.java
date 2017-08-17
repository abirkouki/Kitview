package util.service;


import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.orthalis.connect.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import out.activity.NotificationsActivity;
import util.session.SQLiteHandler;
import util.session.SessionManager;

/**
 * Created by orthalis on 23/05/2017.
 */

public class FcmMessagingService extends FirebaseMessagingService {
    private SessionManager session;
    private SQLiteHandler db;

    @TargetApi(Build.VERSION_CODES.N)
    //@RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMessageReceived (RemoteMessage remoteMessage) {
        //String title = remoteMessage.getNotification().getTitle();
        //String message = remoteMessage.getNotification().getBody();
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String message = data.get("body");
        DateFormat df = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        }//TODO tester sur version précedentes pour gérer la date
        // Get the date today using Calendar object.
        Date today = Calendar.getInstance().getTime();
        // Using DateFormat format method we can create a string
        // representation of a date with the defined format.
        String dateToday = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            dateToday = df.format(today);
        }//TODO tester sur version précedentes pour gérer la date
        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // Session manager
        session = new SessionManager(getApplicationContext());


        db.addNotif(message, dateToday);
        db.close();

        Intent intent = new Intent(this, NotificationsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(message);
        notificationBuilder.setSmallIcon(R.drawable.logo98);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notificationBuilder.build());


    }


}
