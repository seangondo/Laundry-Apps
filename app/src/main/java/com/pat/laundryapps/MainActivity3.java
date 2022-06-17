package com.pat.laundryapps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;

public class MainActivity3 extends AppCompatActivity {
    DBHandler mydb;
    TextView tv1, tv2, tv3, tv4;
    static Spinner sp1;
    Button b1;
    ImageButton b2;
    String url;
    static String myid;
    static String cId;

    private View.OnClickListener myClickList = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.acBack:
                    goBack();
                    break;
                case R.id.acSubmit:
                    MainActivity3.ReqTask reqTask = (MainActivity3.ReqTask) new MainActivity3.ReqTask(new MainActivity3.ReqTask.AsyncResponse() {
                        @Override
                        public void processFinish(String output) {
                            if(output.equals("ok")) {
                                Toast.makeText(getBaseContext(), "Berhasil mengubah metode pengiriman", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).execute(url + "/api/method", "PUT");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        url = getString(R.string.urlServer);

        tv1 = findViewById(R.id.acOrderID);
        tv2 = findViewById(R.id.acTelp);
        tv3 = findViewById(R.id.acOrderDate);
        tv4 = findViewById(R.id.acProcess);

        b1 = findViewById(R.id.acSubmit);
        b2 = findViewById(R.id.acBack);

        myid = getIntent().getStringExtra(ActivityFragment.GET_ID);
        tv1.setText(myid);

        b1.setOnClickListener(myClickList);
        b2.setOnClickListener(myClickList);


        sp1 = findViewById(R.id.acMetode);
        String[] metode = new String[]{"Dikirim", "Diambil"};

        ArrayAdapter<String> a = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, metode);
        sp1.setAdapter(a);

        String urlReq = getData();
        MainActivity3.ReqTask reqTask = (MainActivity3.ReqTask) new MainActivity3.ReqTask(new MainActivity3.ReqTask.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                JSONArray myRec = null;
                try {
                    myRec = new JSONArray(output);
                    JSONObject myRecObj = myRec.getJSONObject(0);
                    tv2.setText(myRecObj.getString("username"));
                    tv3.setText(myRecObj.getString("entryDate"));
                    tv4.setText(myRecObj.getString("status"));
                    cId = myRecObj.getString("clientID");
                    if(myRecObj.getString("method").equals("Dikirim")) {
                        sp1.setSelection(0);
                    } else {
                        sp1.setSelection(1);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).execute(urlReq, "GET");

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public static class ReqTask extends AsyncTask<String, String, String> {


        public interface AsyncResponse {
            void processFinish(String output);
        }

        public MainActivity3.ReqTask.AsyncResponse delegate = null;

        public ReqTask(MainActivity3.ReqTask.AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected String doInBackground(String... uri) {
            try {
                URL url = new URL((uri[0]));
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                Log.d("method", uri[1]);
                //GET Method
                if(uri[1] == "GET"){
                    Log.d("method", "MASUK GET");
                    con.setRequestMethod(uri[1]);
                    con.setRequestProperty("User-Agent", "Mozilla/5.0");
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String input;
                    StringBuffer response = new StringBuffer();
                    while ((input = in.readLine())!=null) {
                        response.append(input);
                    }
                    in.close();
                    return String.valueOf(response);
                }
                else if (uri[1] == "PUT") {
                    Log.d("method", "MASUK PUT");
                    Log.d("Client ID", cId);
                    JSONObject send = new JSONObject();
                    send.put("stat", sp1.getSelectedItem().toString());
                    send.put("id", cId);
                    send.put("oid", myid);

                    con.setRequestMethod(uri[1]);
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setDoOutput(false);
                    con.setDoInput(true);

                    OutputStream out = new BufferedOutputStream(con.getOutputStream());
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                    writer.write(send.toString());
                    writer.flush();

                    BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line;
                    while ((line = rd.readLine()) != null) {
                        Log.d("data", line);
                    }
                    return "ok";
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            delegate.processFinish(s);
        }
    }

    public String getData(){
        mydb = new DBHandler(this);
        Cursor c = mydb.getData(1);
        String sendUrl = null;
        if(c.moveToFirst()) {
            String dataget = c.getString(0);
            if(dataget != null) {
                String myname = c.getString(1);
                sendUrl = url + "/api/data_spec/" + myname + "/" + myid;
            }
        }
        return sendUrl;
    }

    private void goBack() {
        Intent c = new Intent(MainActivity3.this, MainActivity2.class);
        c.putExtra("goAct", "Activity");
        startActivity(c);
        finish();
    }
}