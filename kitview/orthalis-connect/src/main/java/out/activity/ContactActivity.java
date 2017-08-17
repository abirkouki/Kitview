package out.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orthalis.connect.R;

import java.util.ArrayList;

import util.app.AppController;
import util.helper.ActionBarHelper;
import util.helper.XmlParser;

public class ContactActivity extends AppCompatActivity {

    private TextView name;
    private TextView doctors;
    private TextView text;
    private TextView address;
    private TextView phone;
    private TextView mail;
    private TextView website;
    private Intent intent;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_contact);
        ActionBarHelper.actionBarCustom(this,true);

        Context context = getApplicationContext();

        //Nom du cabinet
        name = (TextView) findViewById(R.id.practice_name);
        name.setText(AppController.practiceName);
        //name.setPadding(0,10,0,10);

        //Praticiens du cabinet
        if (!AppController.practiceDoctors.isEmpty()){
            doctors = (TextView) findViewById(R.id.practice_doctors);
            ArrayList<XmlParser.Doctor> doctor_array = AppController.practiceDoctors;
            String textDoctor = "";
            for (XmlParser.Doctor doctor : doctor_array){
                textDoctor += doctor.toString()+"\n";
            }
            doctors.setText(textDoctor);
        }

        //Texte
        if (AppController.practiceText != null){
            text = (TextView) findViewById(R.id.practice_text);
            text.setText(AppController.practiceText);
        }

        //Adresse du cabinet
        address = (TextView) findViewById(R.id.practice_address);
        Button addressButton = (Button) findViewById(R.id.map_button);
        address.setText(AppController.practiceAddress.geographic);
        addressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(ContactActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        //Téléphone du cabinet
        if (AppController.practiceContact.tel != null){
            TextView phoneDesc = (TextView) findViewById(R.id.phone);
            Button phoneButton = (Button) findViewById(R.id.call_button);
            phoneDesc.setText(context.getString(R.string.telephone));
            phone = (TextView) findViewById(R.id.practice_phone);
            phone.setText(AppController.practiceContact.tel);
            phoneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(ContactActivity.this, CallActivity.class);
                    startActivity(intent);
                }
            });
        }
        else {
            LinearLayout llphone1 = (LinearLayout) findViewById(R.id.llphone1);
            llphone1.setVisibility(View.GONE);
            LinearLayout llphone2 = (LinearLayout) findViewById(R.id.llphone2);
            llphone2.setVisibility(View.GONE);
        }

        //Email du cabinet
        if (AppController.practiceContact.email != null){
            TextView mailDesc = (TextView) findViewById(R.id.email);
            Button mailButton = (Button) findViewById(R.id.email_button);
            mailDesc.setText(context.getString(R.string.email));
            mail = (TextView) findViewById(R.id.practice_mail);
            mail.setText(AppController.practiceContact.email);
            mailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(ContactActivity.this, EmailActivity.class);
                    startActivity(intent);
                }
            });
        }
        else {
            LinearLayout llemail1 = (LinearLayout) findViewById(R.id.llemail1);
            llemail1.setVisibility(View.GONE);
            LinearLayout llemail2 = (LinearLayout) findViewById(R.id.llemail2);
            llemail2.setVisibility(View.GONE);
        }

        //Site web du cabinet
        if (AppController.practiceContact.website != null){
            TextView websiteDesc = (TextView) findViewById(R.id.website);
            websiteDesc.setText(context.getString(R.string.website));
            website = (TextView) findViewById(R.id.practice_website);
            website.setText(AppController.practiceContact.website);
            website.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = AppController.practiceContact.website;
                    if (!url.startsWith("http://") && !url.startsWith("https://"))
                        url = "http://" + url;
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
            });
        }
        else{
            LinearLayout llwebsite = (LinearLayout) findViewById(R.id.llwebsite);
            llwebsite.setVisibility(View.GONE);
        }

    }

}
