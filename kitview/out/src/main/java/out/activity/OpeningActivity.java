package out.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.TextView;


import com.orthalis.connect.R;

import util.app.AppController;

public class OpeningActivity extends AppCompatActivity {

    private TextView monday;
    private TextView tuesday;
    private TextView wednesday;
    private TextView thursday;
    private TextView friday;
    private TextView saturday;
    private TextView sunday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_opening);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.logo98);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        //Horaires du cabinet
        monday = (TextView) findViewById(R.id.monday);
        tuesday = (TextView) findViewById(R.id.tuesday);
        wednesday = (TextView) findViewById(R.id.wednesday);
        thursday = (TextView) findViewById(R.id.thursday);
        friday = (TextView) findViewById(R.id.friday);
        saturday = (TextView) findViewById(R.id.saturday);
        sunday = (TextView) findViewById(R.id.sunday);

        monday.setText(AppController.practiceOpeningHours.monday);
        tuesday.setText(AppController.practiceOpeningHours.tuesday);
        wednesday.setText(AppController.practiceOpeningHours.wednesday);
        thursday.setText(AppController.practiceOpeningHours.thursday);
        friday.setText(AppController.practiceOpeningHours.friday);
        saturday.setText(AppController.practiceOpeningHours.saturday);
        sunday.setText(AppController.practiceOpeningHours.sunday);

    }
}
