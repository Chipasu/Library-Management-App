package com.kk2ak.library;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
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


public class Register extends AppCompatActivity {

    EditText etName, etPassword, etMail;
    TextView errorText;
    Button btnRegister;
    TextView tvLogin;

    private static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        etMail = findViewById(R.id.etMail);
        btnRegister = findViewById(R.id.btnRegister);
        errorText = findViewById(R.id.regError);
        tvLogin = findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progress = new ProgressDialog(Register.this);
                progress.setTitle("Loading");
                progress.setMessage("Wait while loading...");
                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress.show();
                final String username = etName.getText().toString();
                final String password = etPassword.getText().toString();
                final String email = etMail.getText().toString();
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

                if (!isValidEmail(email)) {
                    errorText.setText("Invalid email");
                    progress.dismiss();
                    return;
                }
                String url = getString(R.string.url) + "/library/register.php";
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("email", email);
                params.put("password", password);
                JsonObjectRequest jsonRequest = new JsonObjectRequest
                        (Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // the response is already constructed as a JSONObject!
                                Log.i("response", response.toString());
                                try {
                                    response.getBoolean("success");
                                    progress.dismiss();
                                    Intent i1 = new Intent(Register.this, Welcome.class);
                                    i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i1);
                                    return;
                                } catch (JSONException e) {
                                    errorText.setText("Registration failed");
                                    progress.dismiss();
                                    return;
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                errorText.setText("Registration failed");
                                progress.dismiss();
                                Log.i("error", error.getMessage());
                                return;
                            }
                        }) {
                };

                Volley.newRequestQueue(Register.this).add(jsonRequest);


            }
        });
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(Register.this, MainActivity.class);
                i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i1);


            }
        });
    }
}
