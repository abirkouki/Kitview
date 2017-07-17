package out.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kitview.out.mobile.R;

import java.sql.Array;
import java.util.ArrayList;

import util.app.AppController;
import util.helper.XmlParser;

public class ContactActivity extends AppCompatActivity {

    private TextView name;
    private TextView doctors;
    private TextView openingHours;
    private TextView address;
    private TextView phone;
    private TextView mail;
    private TextView website;
    private Intent intent;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        name = (TextView) findViewById(R.id.practice_name);
        doctors = (TextView) findViewById(R.id.practice_doctors);
        openingHours = (TextView) findViewById(R.id.practice_opening_hours);
        address = (TextView) findViewById(R.id.practice_address);
        phone = (TextView) findViewById(R.id.practice_phone);
        mail = (TextView) findViewById(R.id.practice_mail);
        website = (TextView) findViewById(R.id.practice_website);

        ArrayList<XmlParser.Doctor> doctor_array = AppController.practiceDoctors;
        String textDoctor = "";

        for (XmlParser.Doctor doctor : doctor_array){
            textDoctor += doctor.toString()+"\n";
        }






        name.setText(AppController.practiceName);
        doctors.setText(textDoctor);
        //openingHours.setText();//TODO to string horaires
        address.setText(AppController.practiceAddress.geographic);
        phone.setText(AppController.practiceContact.tel);
        mail.setText(AppController.practiceContact.email);
        website.setText(AppController.practiceContact.website);//TODO link





        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(ContactActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });



        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(ContactActivity.this, CallActivity.class);
                startActivity(intent);
            }
        });



        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(ContactActivity.this, EmailActivity.class);
                startActivity(intent);
            }
        });








    }

}
