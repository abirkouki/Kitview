package out.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kitview.out.mobile.R;

import util.session.SQLiteHandler;
import util.session.SessionManager;

/**
 * Created by orthalis on 13/06/2017.
 */

public class NotificationsActivity extends AppCompatActivity {
    SQLiteHandler db;
    SessionManager session;
    ListView mListView;
    TextView noNotif;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        afficherNotifs();
        }
/*
    protected void onStart() {
        super.onStart();
        afficherNotifs();
    }*/



    public int getTableLength(String[][] table) {
        int res = 0;
        for (int i=0;i<10;i++)
        {
            if (table[i][0]!=null) {
                res++;
            }
        }
        return res;
    }

    public void afficherNotifs() {
        mListView = (ListView) findViewById(R.id.listView);
        noNotif = (TextView) findViewById(R.id.noNotif);
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        //session = new SessionManager(getApplicationContext());



        // Fetching notif details from sqlite
        String[][] notif = db.getNotifDetails();
        int taille = getTableLength(notif);

        if (notif[0][0] != null) {
            //initialisation du tableau pour l'affichage sans elements null

            String[] notifAffichage = new String[taille];
            for (int i = 0; i < 10; i++) {

                if (notif[i][0] != null) {
                    notifAffichage[i] = "Reception : " + notif[i][1] + "\nMessage : " + notif[i][0];

                }
            }

            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(NotificationsActivity.this, android.R.layout.simple_list_item_1, notifAffichage);
            mListView.setAdapter(adapter);

        } else {
            noNotif.setText("Vous n'avez pas de notifications");
        }

    }
}
