package com.yaseen.whiterabbittask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.yaseen.whiterabbittask.Room.Employee;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static String FETCHURL = "http://www.mocky.io/v2/5d565297300000680030a986";
    List<ModelClass> employees;
    private RecyclerView recyclerview;
    private ArrayList<ModelClass> arrayList;
    private CustomAdapter adapter;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb = findViewById(R.id.progressBar);
        pb.setVisibility(View.GONE);

        recyclerview = findViewById(R.id.recyclerView);
        arrayList = new ArrayList<>();
        adapter = new CustomAdapter(this, arrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setNestedScrollingEnabled(false);
        recyclerview.setAdapter(adapter);

        ConnectivityManager cm = (ConnectivityManager) MyApplication.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting() && arrayList != null) {
            fetchfromServer();
        } else {


            fetchfromRoom();
        }

    }



    private void fetchfromRoom() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {


                List<Employee> employeeList = DatabaseClient.getInstance(MainActivity.this).getAppDatabase().employeeDao().getAll();
                arrayList.clear();
                    for (Employee employee: employeeList) {
                    ModelClass repo = new ModelClass(employee.getId(),employee.getName(),
                            employee.getName(),
                            employee.getEmail(),
                            employee.getImage(),

                    arrayList.add(employee);
                }
                // refreshing recycler view
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
        thread.start();
    }


    private void fetchfromServer() {

        pb.setVisibility(View.VISIBLE);

        JSONArrayRequest request = new JsonArrayRequest(FETCHURL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            pb.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, "Couldn't fetch the menu! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        recipes = new Gson().fromJson(response.toString(), new TypeToken<List<ModelClass>>() {
                        }.getType());

                        arrayList.clear();
                        arrayList.addAll(employees);

                        adapter.notifyDataSetChanged();

                        pb.setVisibility(View.GONE);

                        saveTask();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Volley error) {
                // error in getting json
                pb.setVisibility(View.GONE);
                Log.e("TAG", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        request.setShouldCache(false);

        requestQueue.add(request);
    }

    private void saveTask() {

        pb.setVisibility(View.VISIBLE);

        JsonArrayRequest request = new JsonArrayRequest(FETCHURL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            pb.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Couldn't fetch the menu! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        employees = new Gson().fromJson(response.toString(), new TypeToken<List<ModelClass>>() {
                        }.getType());

                        arrayList.clear();
                        arrayList.addAll(employees);

                        adapter.notifyDataSetChanged();

                        pb.setVisibility(View.GONE);

                        saveTask();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                pb.setVisibility(View.GONE);
                Log.e("TAG", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        request.setShouldCache(false);

        requestQueue.add(request);


    }
}