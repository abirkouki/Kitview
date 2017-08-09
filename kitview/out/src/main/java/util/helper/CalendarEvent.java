package util.helper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.CalendarContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class CalendarEvent {

    private Context context;

    public CalendarEvent(Context appContext){
        context = appContext;
    }

    public long createEvent(String title, String description, String location, String dateStart, int duration){
        Uri uri;
        long calID = 1;//1 -> telephone, 2 -> compte 1, 3 -> compte 2, etc
        Date d = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            d = sdf.parse(dateStart);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar beginTime = Calendar.getInstance();
        beginTime.setTime(d);
        Calendar endTime = Calendar.getInstance();
        endTime.setTime(d);
        endTime.add(Calendar.MINUTE,duration);

        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();

        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, description);
        values.put(CalendarContract.Events.AVAILABILITY,CalendarContract.Events.AVAILABILITY_BUSY);
        values.put(CalendarContract.Events.DTSTART, beginTime.getTimeInMillis());
        values.put(CalendarContract.Events.EVENT_LOCATION, location);
        values.put(CalendarContract.Events.DTEND, endTime.getTimeInMillis());
        TimeZone tz = TimeZone.getDefault();
        values.put(CalendarContract.Events.EVENT_TIMEZONE, tz.getID());
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        // get the event ID that is the last element in the Uri
        return Long.parseLong(uri.getLastPathSegment());//eventID
    }

    //Invités
    public void addAttendee(long eventID, String name){
        //Uri uri;
        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Attendees.ATTENDEE_NAME, name);
        //values.put(CalendarContract.Attendees.ATTENDEE_EMAIL, "mail@example.com");
        values.put(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP, CalendarContract.Attendees.RELATIONSHIP_ATTENDEE);
        values.put(CalendarContract.Attendees.ATTENDEE_TYPE, CalendarContract.Attendees.TYPE_REQUIRED);
        values.put(CalendarContract.Attendees.ATTENDEE_STATUS, CalendarContract.Attendees.ATTENDEE_STATUS_ACCEPTED);
        values.put(CalendarContract.Attendees.EVENT_ID, eventID);
        //uri = cr.insert(CalendarContract.Attendees.CONTENT_URI, values);
        cr.insert(CalendarContract.Attendees.CONTENT_URI, values);
    }

    //Rappels
    public void addReminder(long eventID, int daysBefore){
        //Uri uri;
        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.MINUTES, 60*24*daysBefore);
        values.put(CalendarContract.Reminders.EVENT_ID, eventID);
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        //uri = cr.insert(CalendarContract.Reminders.CONTENT_URI, values);
        cr.insert(CalendarContract.Reminders.CONTENT_URI, values);
    }

}
