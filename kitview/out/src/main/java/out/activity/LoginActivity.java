package out.activity;

/**
 * Created by orthalis on 02/06/2017.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.orthalis.connect.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import util.app.AppConfig;
import util.app.AppController;
import util.helper.ActionBarHelper;
import util.network.NetworkUtils;
import util.session.SQLiteHandler;
import util.session.SessionManager;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button btnLogin;
    private EditText inputUser;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);
        ActionBarHelper.actionBarCustom(this,false);

        inputUser = (EditText) findViewById(R.id.username);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {//TODO possibilité d'enlever ça puisque c'est deja dans le main

            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                String user = inputUser.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (NetworkUtils.isWifiConnected(getApplicationContext()) || NetworkUtils.isMobileConnected(getApplicationContext())) {
                    // login user
                    checkLogin(user, password);
                } else {

                    Toast.makeText(getApplicationContext(), R.string.no_connection, Toast.LENGTH_LONG).show();
                }
            }

        });


    }

    /**
     * function to verify login details in mysql db
     * */
    private void checkLogin(final String username, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage(getApplicationContext().getResources().getString(R.string.logging_in));
        showDialog();

            StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_LOGIN, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.d(TAG, "Login Response: " + response.toString());
                    hideDialog();

                    try {
                        JSONObject jObj = new JSONObject(response);
                        boolean error = jObj.getBoolean("error");

                        // Check for error node in json
                        if (!error) {
                            // user successfully logged in
                            // Create login session

                            session.setLogin(true);

                            // Now store the user in SQLite
                            String uid = jObj.getString("uid");
                            JSONObject user = jObj.getJSONObject("user");
                            String nom = user.getString("nom");
                            String prenom = user.getString("prenom");
                            byte[] bytes1 = nom.getBytes("UTF-8");
                            String s1 = new String(bytes1, "UTF-8");
                            nom = s1.trim();
                            byte[] bytes2 = prenom.getBytes("UTF-8");
                            String s2 = new String(bytes2, "UTF-8");
                            prenom = s2.trim();
                            // Inserting row in users table
                            db.addUser(prenom, nom, uid);
                            // Launch ParamNotif
                            Intent intent = new Intent(LoginActivity.this, ParamNotifActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Error in login. Get the error message
                            //TODO: r string
                            String errorMsg = jObj.getString("error_msg");
                            if (errorMsg.equals("1")) {
                                Toast.makeText(getApplicationContext(), "Login credentials are wrong", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Required parameters username or password is missing", Toast.LENGTH_LONG).show();
                                }


                        }
                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Login Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_LONG).show();
                    hideDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("username", username);
                    params.put("password", password);

                    return params;
                }

            };

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

        }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
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