package out.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.kitview.out.mobile.R;


public class MainActivity2 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ImageButton buttonBalance = (ImageButton) findViewById(R.id.imageBalance);
        ImageButton buttonNotif = (ImageButton) findViewById(R.id.imageNotif);
        ImageButton buttonRdv = (ImageButton) findViewById(R.id.imageRdv);
        ImageButton buttonPhone = (ImageButton) findViewById(R.id.imagePhone);
        ImageButton buttonEmail = (ImageButton) findViewById(R.id.imageEmail);
        ImageButton buttonMap = (ImageButton) findViewById(R.id.imageMap);
        ImageButton buttonSettings = (ImageButton) findViewById(R.id.imageSettings);
        //balance intent
        buttonBalance.setOnClickListener(new View.OnClickListener(){
            // When the button is pressed/clicked, it will run the code below
            @Override
            public void onClick(View v){

            Intent intent = new Intent(MainActivity2.this, BalanceActivity.class);
            startActivity(intent);
        }
    });

        //notif intent
        buttonNotif.setOnClickListener(new View.OnClickListener(){
            // When the button is pressed/clicked, it will run the code below
            @Override
            public void onClick(View v){

                Intent intent = new Intent(MainActivity2.this, NotificationsActivity.class);
                startActivity(intent);
            }
        });

        //rdv intent
        buttonRdv.setOnClickListener(new View.OnClickListener(){
            // When the button is pressed/clicked, it will run the code below
            @Override
            public void onClick(View v){

                Intent intent = new Intent(MainActivity2.this, ProchainRdvActivity.class);
                startActivity(intent);
            }
        });

        buttonPhone.setOnClickListener(new View.OnClickListener(){
            // When the button is pressed/clicked, it will run the code below
            @Override
            public void onClick(View v){

                Intent intent = new Intent(MainActivity2.this, CallActivity.class);
                startActivity(intent);
            }
        });

        buttonEmail.setOnClickListener(new View.OnClickListener(){
            // When the button is pressed/clicked, it will run the code below
            @Override
            public void onClick(View v){

                Intent intent = new Intent(MainActivity2.this, EmailActivity.class);
                startActivity(intent);
            }
        });

        buttonMap.setOnClickListener(new View.OnClickListener(){
            // When the button is pressed/clicked, it will run the code below
            @Override
            public void onClick(View v){

                Intent intent = new Intent(MainActivity2.this, MapsActivity.class);
                startActivity(intent);
            }
        });
        buttonSettings.setOnClickListener(new View.OnClickListener(){
            // When the button is pressed/clicked, it will run the code below
            @Override
            public void onClick(View v){

                Intent intent = new Intent(MainActivity2.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }



}
