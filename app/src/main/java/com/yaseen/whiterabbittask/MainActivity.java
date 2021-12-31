package com.yaseen.whiterabbittask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yaseen.whiterabbittask.Room.Employee;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static String FETCHURL = "http://www.mocky.io/v2/5d565297300000680030a986";
    List<Employee> employees;
    private RecyclerView recyclerview;
    private ArrayList<Employee> arrayList;
    private CustomAdapter adapter;
    private ProgressBar pb;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb = findViewById(R.id.progressBar);
        pb.setVisibility(View.GONE);

        recyclerview = findViewById(R.id.recyclerView);
        arrayList = new ArrayList<Employee>();
        adapter = new CustomAdapter(this, arrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setNestedScrollingEnabled(false);
        recyclerview.setAdapter(adapter);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
                            employee.getImage());

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

        JsonArrayRequest request = new JsonArrayRequest(FETCHURL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            pb.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, "Couldn't fetch the menu! Pleas try again.", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(), "Error connecting ", Toast.LENGTH_SHORT).show();

            }

        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        request.setShouldCache(false);

        requestQueue.add(request);
    }

    private void saveTask() {

        class SaveTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                //creating a task

                for (int i = 0; i < employees.size(); i++) {
                    Employee employee= new Employee();
                    employee.setName(employees.get(i).getName());
                    employee.setEmail(employees.get(i).getEmail());
                    employee.setImage(employees.get(i).getImage());
                    DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().employeeDao().insert(employee);
                }


                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
            }
        }

        SaveTask st = new SaveTask();
        st.execute();
    }


}