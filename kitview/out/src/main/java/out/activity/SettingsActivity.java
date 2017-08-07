package out.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.orthalis.connect.R;

import java.util.HashMap;
import java.util.Map;

import util.app.AppConfig;
import util.app.AppController;
import util.helper.ActionBarHelper;
import util.network.NetworkUtils;
import util.session.SQLiteHandler;
import util.session.SessionManager;

/**
 * Created by orthalis on 15/06/2017.
 */

public class SettingsActivity extends AppCompatActivity{
    Button button;
    SQLiteHandler db;
    SessionManager session;

    String tag_string_req = "req_settings_activity";

    private RadioGroup radioGroupDelta;
    private RadioButton radioButtonDelta;

    private RadioGroup radioGroupFamille;
    private RadioButton radioButtonFamille;

    private RadioButton radioButtonTrois;
    private RadioButton radioButtonDeux;
    private RadioButton radioButtonUn;

    private RadioButton radioButtonYes;
    private RadioButton radioButtonNo;

    private Switch switchVideo;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_settings);

        ActionBarHelper.actionBarCustom(this,true);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());


        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();
        final String uid = user.get("uid");

        //Selection case par défaut. La case checked = les parametres selectionnés précédemment
        radioButtonTrois = (RadioButton) findViewById(R.id.troisjours);
        radioButtonDeux = (RadioButton) findViewById(R.id.deuxjours);
        radioButtonUn = (RadioButton) findViewById(R.id.unjour);
        radioButtonYes = (RadioButton) findViewById(R.id.oui);
        radioButtonNo = (RadioButton) findViewById(R.id.non);
        switchVideo = (Switch) findViewById(R.id.switch_video);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        switchVideo.setChecked(prefs.getBoolean("KEY_VIDEO",false));

        //Récupération des parametres dans SQLite interne
        String[][] paramsDetails = db.getParamsDetails();
        String delta = paramsDetails[0][0];
        String famille = paramsDetails[0][1];

        if (delta.equals("72")) {
            radioButtonTrois.setChecked(true);
        } else {
            if (delta.equals("48")) {
                radioButtonDeux.setChecked(true);
            } else {
                radioButtonUn.setChecked(true);
            }
        }

        if (famille.equals("Y")) {
            radioButtonYes.setChecked(true);
        } else {
            radioButtonNo.setChecked(true);
        }

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

                final String resultFam;
                final String resultHeure;

                if (radioButtonYes.isChecked()) resultFam = "Y";
                else resultFam = "N";
                if (radioButtonUn.isChecked()) resultHeure = "24";
                else if (radioButtonDeux.isChecked()) resultHeure = "48";
                else resultHeure = "72";


                StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_MODIF_SETTINGS,
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
                        params.put("rdv_notif_delta",resultHeure);
                        params.put("patient_id", uid);
                        params.put("notif_famille",resultFam);

                        return params;


                    }
                };

                //Toast.makeText(getApplicationContext(),"notifdelta :"+ radioButtonDelta.getText(), Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(),"uid :"+ uid,Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(),transformeNotifFamille((String) radioButtonFamille.getText()),Toast.LENGTH_LONG).show();

                    AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);
                if (NetworkUtils.isWifiConnected(getApplicationContext()) || NetworkUtils.isMobileConnected(getApplicationContext())) {
                    //db.addParams((String) radioButtonDelta.getText(), (String) radioButtonFamille.getText());
                    db.addParams(resultHeure,resultFam);
                } else {
                    Toast.makeText(getApplicationContext(),"Veuillez vérifier votre accès à internet", Toast.LENGTH_LONG).show();//TODO string
                }

                if (switchVideo != null) {//activé = vrai
                    //System.out.println(switchVideo.isChecked());
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor ed = prefs.edit();
                    ed.putBoolean("KEY_VIDEO", switchVideo.isChecked());
                    //ed.commit();
                    ed.apply();
                }
                finish();
            }
        });
    }



}
