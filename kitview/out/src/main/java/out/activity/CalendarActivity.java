package out.activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.orthalis.connect.R;

import java.util.Calendar;
import java.util.TimeZone;


public class CalendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_calendar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.logo98);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        Button button = (Button) findViewById(R.id.calendrier);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            //TODO dans un thread pas dans l'UI

            Uri uri;
            long calID = 1;//1 -> telephone, 2 -> compte 1, 3 -> compte 2, etc
            long startMillis = 0;
            long endMillis = 0;
            Calendar beginTime = Calendar.getInstance();
            beginTime.set(2017, 7, 2, 16, 56);
            startMillis = beginTime.getTimeInMillis();
            Calendar endTime = Calendar.getInstance();
            endTime.set(2017, 7, 2, 17, 15);//TODO duration
            endMillis = endTime.getTimeInMillis();


            ContentResolver cr = getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, startMillis);
            values.put(CalendarContract.Events.DURATION, "P120M");//TODO marche pas
            //values.put(CalendarContract.Events.DTEND, endMillis);
            values.put(CalendarContract.Events.TITLE, "RDV Ortho");
            values.put(CalendarContract.Events.DESCRIPTION, "libellé du rdv, type, opération");
            values.put(CalendarContract.Events.CALENDAR_ID, calID);
            TimeZone tz = TimeZone.getDefault();
            values.put(CalendarContract.Events.EVENT_TIMEZONE, tz.getID());
            uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

            // get the event ID that is the last element in the Uri
            long eventID = Long.parseLong(uri.getLastPathSegment());

            values = new ContentValues();
            values.put(CalendarContract.Attendees.ATTENDEE_NAME, "namePatient");
//            values.put(CalendarContract.Attendees.ATTENDEE_EMAIL, "mail@example.com");
            values.put(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP, CalendarContract.Attendees.RELATIONSHIP_ATTENDEE);
            values.put(CalendarContract.Attendees.ATTENDEE_TYPE, CalendarContract.Attendees.TYPE_REQUIRED);
            values.put(CalendarContract.Attendees.ATTENDEE_STATUS, CalendarContract.Attendees.ATTENDEE_STATUS_ACCEPTED);
            values.put(CalendarContract.Attendees.EVENT_ID, eventID);
            uri = cr.insert(CalendarContract.Attendees.CONTENT_URI, values);

            values = new ContentValues();//TODO trouver comment mettre plusieurs reminders
            values.put(CalendarContract.Reminders.MINUTES, 60*24);
            values.put(CalendarContract.Reminders.EVENT_ID, eventID);
            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            uri = cr.insert(CalendarContract.Reminders.CONTENT_URI, values);

            }
        });










    }
}
