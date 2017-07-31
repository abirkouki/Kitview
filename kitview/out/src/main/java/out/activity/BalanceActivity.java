package out.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.orthalis.connect.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import util.app.AppConfig;
import util.app.AppController;
import util.network.NetworkUtils;
import util.session.SQLiteHandler;
import util.session.SessionManager;

/**
 * Created by orthalis on 12/06/2017.
 */

public class BalanceActivity extends AppCompatActivity {
    private static final String TAG = BalanceActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    SQLiteHandler db;
    SessionManager session;
    TextView txtBalance;
    TextView txtMaj;
    String balance;
    String balanceMaj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String tag_string_req = "req_balance";

        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_balance);

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


        txtBalance = (TextView) findViewById(R.id.balance);
        txtMaj = (TextView) findViewById(R.id.majBalance);

        // test d'accès à internet
        if (!NetworkUtils.isWifiConnected(getApplicationContext()) && !NetworkUtils.isMobileConnected(getApplicationContext())) {
            // si pas d'accès à internet => récupération des dernieres données de balance dans la bdd internet SQLite
            String[][] balanceDetails = db.getBalanceDetails();

            balance = balanceDetails[0][0];
            balanceMaj = balanceDetails[0][1];
            if (balance!=null) {
                //affichage
                txtBalance.setText(getApplicationContext().getString(R.string.payment) + balance + getApplicationContext().getString(R.string.devise));
                String tmp;
                tmp = getApplicationContext().getString(R.string.update) + balanceMaj;
                txtMaj.setText(tmp);
            } else {
                txtBalance.setText(getApplicationContext().getString(R.string.missing_info));
            }
        } else {
            // si accès à internet => récupération de balance
            StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_RECUPERER_BALANCE, new Response.Listener<String>() {
                public void onResponse(String response) {

                    try {
                        //récupération de la réponse
                        JSONObject jObj = new JSONObject(response);

                        boolean error = jObj.getBoolean("error");

                        // Check for error node in json
                        if (!error) {
                            JSONObject user = jObj.getJSONObject("user");

                            balance = user.getString("balance");
                            txtBalance.setText(getApplicationContext().getString(R.string.payment) + balance + getApplicationContext().getString(R.string.devise));
                            //date d'aujourd'hui
                            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            Date date = new Date();
                            String tmp;
                            tmp = getApplicationContext().getString(R.string.update) + dateFormat.format(date);
                            txtMaj.setText(tmp);
                            //ajout des informations dans la bdd SQLite
                            db.addBalance(balance,dateFormat.format(date));
                        }
                    } catch (JSONException e) {
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