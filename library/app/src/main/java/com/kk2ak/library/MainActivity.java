package com.kk2ak.library;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    Button btnLogin;
    EditText etName, etId, etPassword;
    TextView tvRegister;
    TextView errorText;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = findViewById(R.id.btnLogin);
        etName = findViewById(R.id.etName);
        etId = findViewById(R.id.etId);
        etPassword = findViewById(R.id.etPassword);
        tvRegister = findViewById(R.id.tvRegister);
        errorText = findViewById(R.id.loginError);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progress = new ProgressDialog(MainActivity.this);
                progress.setTitle("Loading");
                progress.setMessage("Wait while loading...");
                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress.show();
                final String username = etId.getText().toString();
                final String password = etPassword.getText().toString();
                if (username.length() <= 3 || username.length() > 20) {
                    errorText.setText("Invalid Library ID");
                    progress.dismiss();
                    return;
                }

                if (password.length() <= 6 && password.length() > 200) {
                    errorText.setText("Invalid password");
                    progress.dismiss();
                    return;
                }

                String url = getString(R.string.url) + "/library/token.php";
                Map<String, String> params = new HashMap<String, String>();
                params.put("client_id", username);
                params.put("client_secret", password);
                params.put("grant_type", "client_credentials");
                JsonObjectRequest jsonRequest = new JsonObjectRequest
                        (Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // the response is already constructed as a JSONObject!
                                Log.i("response", response.toString());
                                try {
                                    String tokstr = response.getString("access_token");
                                    SharedPreferences loginState = getSharedPreferences("access_token", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = loginState.edit();
                                    editor.putString("value", tokstr);
                                    editor.commit();
                                    progress.dismiss();
                                    Intent i1 = new Intent(MainActivity.this, Welcome.class);
                                    i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i1);
                                    return;
                                } catch (JSONException e) {
                                    errorText.setText("Login failed");
                                    progress.dismiss();
                                    return;
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                errorText.setText("Login failed");
                                progress.dismiss();
                                if (error != null && error.getMessage() != null) {
                                    Log.i("error", error.getMessage());
                                }
                                return;
                            }
                        }) {
                };

                Volley.newRequestQueue(MainActivity.this).add(jsonRequest);


            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(MainActivity.this, Register.class);
                i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i1);
            }
        });

    }
}