package out.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kitview.out.mobile.R;

import java.util.HashMap;
import java.util.Map;

import util.app.AppController;
import util.session.SQLiteHandler;
import util.session.SessionManager;

/**
 * Created by orthalis on 15/06/2017.
 */

public class SettingsActivity extends AppCompatActivity{
    Button button;
    String app_server_url = "http://192.168.2.74/blabla/modif_settings.php";
    SQLiteHandler db;
    SessionManager session;

    String tag_string_req = "req_settings_activity";

    private RadioGroup radioGroupDelta;
    private RadioButton radioButtonDelta;

    private RadioGroup radioGroupFamille;
    private RadioButton radioButtonFamille;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());


        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();
        final String uid = user.get("uid");

        //addListenerOnButton();
        radioGroupDelta = (RadioGroup) findViewById(R.id.radioGroupDelta);
        radioGroupFamille = (RadioGroup) findViewById(R.id.radioGroupFamille);
        button = (Button) findViewById(R.id.okButton);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // get selected radio button from radioGroup
                int selectedIdDelta = radioGroupDelta.getCheckedRadioButtonId();
                int selectedIdFamille = radioGroupFamille.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                radioButtonDelta = (RadioButton) findViewById(selectedIdDelta);
                radioButtonFamille = (RadioButton) findViewById(selectedIdFamille);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                })
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<String,String>();
                        params.put("rdv_notif_delta",(String) radioButtonDelta.getText());
                        params.put("patient_id", uid);
                        params.put("notif_famille",transformeNotifFamille((String) radioButtonFamille.getText()));

                        return params;


                    }
                };


                    AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);
                // Launch main activity
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }
    private String transformeNotifFamille (String reponse) {
        //permet de transformer la réponse Oui pas Y ou Non pas N pour l'envoyer à la BDD
        // TODO : plusieurs langues ??
       if (reponse.equals("Oui")) {
           return "Y";
       } else {
           return "N";
       }
    }
}
