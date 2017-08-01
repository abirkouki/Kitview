package out.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.orthalis.connect.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import util.app.AppConfig;
import util.app.AppController;
import util.network.NetworkUtils;
import util.session.SQLiteHandler;
import util.session.SessionManager;

/**
 * Created by orthalis on 13/06/2017.
 */

public class ProchainRdvActivity extends AppCompatActivity {
    private static final String TAG = ProchainRdvActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    SQLiteHandler db;
    SessionManager session;
    TextView txtRdv;
    TextView txtDuree;
    TextView txtLibelle;
    TextView txtMaj;
    Button callButton;
    String rdv;
    String rdvDuree;
    String rdvLibelle;
    String rdvMaj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String tag_string_req = "req_prochain_rdv_activity";

        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_rdv);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.logo98);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());


        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();
        final String uid = user.get("uid");


        txtRdv = (TextView) findViewById(R.id.dateRdv);
        txtDuree = (TextView) findViewById(R.id.dureeRdv);
        txtLibelle = (TextView) findViewById(R.id.libelleRdv);
        txtMaj = (TextView) findViewById(R.id.majRdv);
        callButton = (Button) findViewById(R.id.callButton);
        if (!NetworkUtils.isWifiConnected(getApplicationContext()) && !NetworkUtils.isMobileConnected(getApplicationContext())) {
            String[][] rdvDetails = db.getRdvDetails();
            rdv = rdvDetails[0][0];
            rdvMaj = rdvDetails[0][1];
            rdvDuree = rdvDetails[0][3];
            rdvLibelle = rdvDetails[0][2];
            if (rdv != null) {
                String tmp;
                tmp = getApplicationContext().getString(R.string.next_rdv) + rdv;
                txtRdv.setText(tmp);
                tmp = getApplicationContext().getString(R.string.duration) + rdvDuree + getApplicationContext().getString(R.string.minutes);
                txtDuree.setText(tmp);
                tmp = getApplicationContext().getString(R.string.label) + rdvLibelle;
                txtLibelle.setText(tmp);
                tmp = getApplicationContext().getString(R.string.update)+ rdvMaj;
                txtMaj.setText(tmp);
            } else {
                txtRdv.setText(getApplicationContext().getString(R.string.no_rdv));
            }

        } else {
            StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_RECUPERER_PROCHAIN_RDV, new Response.Listener<String>() {
                public void onResponse(String response) {
                    //System.out.println(response);
                    try {
                        JSONObject jObj = new JSONObject(response);

                        boolean error = jObj.getBoolean("error");

                        // Check for error node in json
                        if (!error) {
                            JSONObject user = jObj.getJSONObject("user");
                            rdv = user.getString("rdv");
                            rdvDuree = user.getString("rdvDuree");
                            rdvLibelle = (user.getString("rdvLibelle"));
                                //pour gérer les accents
                                byte[] bytes = rdvLibelle.getBytes("UTF-8");
                                String s2 = new String(bytes, "UTF-8");
                                // transformation de la date en 'mardi 05 mai...' etc..
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date d = sdf.parse(rdv);
                                DateFormat fullDateFormat = DateFormat.getDateTimeInstance(
                                        DateFormat.FULL,
                                        DateFormat.FULL, new Locale("FR","fr"));
                                int lastIndexDeuxPoints = fullDateFormat.format(d).lastIndexOf(':');
                                String firstPartDate = fullDateFormat.format(d).substring(0,lastIndexDeuxPoints-6);
                                String secondPartDate = fullDateFormat.format(d).substring(lastIndexDeuxPoints-5,lastIndexDeuxPoints-3);
                                String thirdPartDate = fullDateFormat.format(d).substring(lastIndexDeuxPoints-2,lastIndexDeuxPoints);
                                String tmp;
                                tmp = getApplicationContext().getString(R.string.next_rdv) +firstPartDate+getApplicationContext().getString(R.string.at)+secondPartDate+"h"+thirdPartDate;
                                txtRdv.setText(tmp);
                                tmp = getApplicationContext().getString(R.string.duration) + rdvDuree + getApplicationContext().getString(R.string.minutes);
                                txtDuree.setText(tmp);
                                tmp = getApplicationContext().getString(R.string.label) + s2;
                                txtLibelle.setText(tmp);
                                //date d'aujourd'hui
                                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                Date date = new Date();
                                tmp = getApplicationContext().getString(R.string.update) + dateFormat.format(date);
                                txtMaj.setText(tmp);

                                //ajouter à la table rdv

                                db.addRdv(rdv, dateFormat.format(date),s2, rdvDuree);

                        } else {
                            txtRdv.setText(getApplicationContext().getString(R.string.no_rdv));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_LONG).show();
                    hideDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to recuperer_balance url
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("uid", uid);

                    return params;
                }

            };

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        }

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProchainRdvActivity.this, CallActivity.class);
                startActivity(intent);
            }
        });

    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
