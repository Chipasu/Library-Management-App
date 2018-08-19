package com.kk2ak.library;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BookedActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private myAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<BookDetails> myDataset;
    private String[] genreList = {"All", "Arts & Photography", "Biographies & Memoirs", "Business & Money", "Calendars", "Children's Books", "Christian Books & Bibles", "Comics & Graphic Novels", "Computers & Technology", "Cookbooks, Food & Wine", "Crafts, Hobbies & Home", "Engineering & Transportation", "Health, Fitness & Dieting", "History", "Humor & Entertainment", "Law", "Literature & Fiction", "Medical Books", "Mystery, Thriller & Suspense", "Parenting & Relationships", "Politics & Social Sciences", "Reference", "Religion & Spirituality", "Romance", "Science Fiction & Fantasy", "Science & Math", "Self-Help", "Sports & Outdoors", "Teen & Young Adult", "Test Preparation", "Travel"};
    private Spinner myGenre;
    private SharedPreferences sf;

    private void getData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        Map<String, String> params = new HashMap<>();
        sf = getSharedPreferences("access_token", MODE_PRIVATE);
        final String token = sf.getString("value", null);
        if (token == null) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();

        }
        params.put("access_token", token);
        CustomJsonArrayRequest jsonArrayRequest = new CustomJsonArrayRequest
                (Request.Method.POST, getString(R.string.url) + "/library/booked.php",new JSONObject(params), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i("response",response.toString());
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        BookDetails book = new BookDetails(jsonObject.getString("name"), jsonObject.getString("genre"), jsonObject.getString("author"), jsonObject.getString("image"),  jsonObject.getString("bookid"));
                        myDataset.add(book);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }
                mAdapter.updateDataSet(myDataset);
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                progressDialog.dismiss();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        myDataset = new ArrayList<>();

        myGenre = findViewById(R.id.genre);
        ArrayAdapter<String> a = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, genreList);
        myGenre.setAdapter(a);
        myGenre.setSelection(0);

        mRecyclerView = (RecyclerView) findViewById(R.id.books);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
//        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new myAdapter(myDataset, BookedActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        getData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        myGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                mAdapter.filter(myGenre.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                mAdapter.filter("All");
            }

        });
    }
}
