package com.kk2ak.library;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DetailedList extends AppCompatActivity {
    TextView mTextView;
    ImageView mImageView;
    Button bookButton;
    SharedPreferences sf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_list);
        Bundle bundle = getIntent().getExtras();
        mTextView = findViewById(R.id.book_det_name);
        mImageView = findViewById(R.id.book_det_image);
        bookButton = findViewById(R.id.bookbutton);
        if (bundle != null) {
            final String bookid = bundle.getString("bookid");
            if (bookid != null) {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                Map<String, String> params = new HashMap<>();
                params.put("bookid",bookid);
                sf = getSharedPreferences("access_token", MODE_PRIVATE);
                final String token = sf.getString("value", null);
                if (token == null) {
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    finish();

                }
                params.put("access_token", token);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.POST, getString(R.string.url) + "/library/newbook.php", new JSONObject(params), new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // the response is already constructed as a JSONObject!
                                Log.i("response", response.toString());
                                try {
                                    BookDetails currBook = new BookDetails(response.getString("name"), response.getString("genre"), response.getString("author"), response.getString("image"),  response.getString("bookid"));
                                    String avail = response.getString("available");
                                    mTextView.setText(currBook.name + "\n" + currBook.author + "\nAvailable: " + avail);
                                    if(!avail.equals("0")) {
                                        bookButton.setVisibility(View.VISIBLE);
                                        bookButton.setOnClickListener(new View.OnClickListener() {
                                            Map<String, String> params2 = new HashMap<>();
                                            @Override
                                            public void onClick(View view) {
                                                params2.put("bookid",bookid);
                                                params2.put("access_token", token);
                                                JsonObjectRequest jsonRequest = new JsonObjectRequest
                                                        (Request.Method.POST, getString(R.string.url) + "/library/bookbook.php", new JSONObject(params2), new Response.Listener<JSONObject>() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                                // the response is already constructed as a JSONObject!
                                                                Log.i("response", response.toString());
                                                                try {
                                                                    response.getBoolean("success");
                                                                    Toast.makeText(getBaseContext(), "Successfully booked", Toast.LENGTH_LONG).show();
                                                                    Intent i1 = new Intent(DetailedList.this, Welcome.class);
                                                                    i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                    startActivity(i1);
                                                                    return;
                                                                } catch (JSONException e) {
                                                                    return;
                                                                }
                                                            }
                                                        }, new Response.ErrorListener() {

                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                Log.i("error", error.getMessage());
                                                                return;
                                                            }
                                                        }) {
                                                };

                                                Volley.newRequestQueue(DetailedList.this).add(jsonRequest);
                                            }
                                        });
                                    }
                                    Glide.with(DetailedList.this).load(getString(R.string.url) + "/library/images/images/" + currBook.imageURL).apply(new RequestOptions().override(600,600)).into(mImageView);
                                    progressDialog.dismiss();
                                    return;
                                } catch (JSONException e) {
                                    Toast.makeText(getBaseContext(), "Loading failed", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                    return;
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getBaseContext(), "Loading failed: "+error.getMessage(), Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                                //  Log.i("error",error.getMessage());
                                return;
                            }
                        });
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonObjectRequest);
        }
    }
}
}
