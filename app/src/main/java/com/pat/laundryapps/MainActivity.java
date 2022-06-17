package com.pat.laundryapps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    DBHandler mydb;
    DataModel usermodel;

    String url;

    TextView errMsg;
    Button bLogin;
    EditText user, pass;
    CheckBox autoLog;

    private long pressedTime;

    public static String a, b, z, myname, mypw, myadd;

    private View.OnClickListener myClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.bLog:
                    myname = user.getText().toString();
                    mypw = pass.getText().toString();
                    String sendUrl;
                    if(myname.equals("") | mypw.equals("")){
                        sendUrl = url;
                        Log.d("URL 1", sendUrl);
                    }
                    else {
                        sendUrl = url + "/api/users/"+myname+"/"+mypw;
                        Log.d("URL 2", sendUrl);
                    }
                    new ReqTask().execute(sendUrl,"GET");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        url = getString(R.string.urlServer);
        errMsg = (TextView) findViewById(R.id.msgError);
        bLogin = (Button) findViewById(R.id.bLog);

        new ReqTask().execute(url, "HEAD");

        user = (EditText) findViewById(R.id.user);
        pass = (EditText) findViewById(R.id.pass);

        autoLog = (CheckBox) findViewById(R.id.cbLog);

        bLogin.setOnClickListener(myClickListener);

        mydb = new DBHandler(this);
        getData();

    }

    @Override
    public void onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            finish();
            System.exit(0);
        } else {
            Toast.makeText(getBaseContext(), "Press back again to Exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }

    private void checkPw() {
        mydb = new DBHandler(this);
        Cursor c = mydb.getAllData();
        if(myname != null | mypw != null) {
            if (myname.equals(String.valueOf(a)) & mypw.equals(String.valueOf(b))) {
                errMsg.setVisibility(View.INVISIBLE);
                if(autoLog.isChecked()) {
                    Log.d("CB", "Auto Login On!");
                    if(c.getCount() == 0) {
                        Log.d("CB", "Entry DB");
                        addUser(null);
                    }
                }
                else {
                    Log.d("CB", "Manual Login");
                    addUser("first");
                }
                goToHome();
                //Toast.makeText(this, "Berhasil Login!!", Toast.LENGTH_SHORT).show();
            }
            else {
                errMsg.setText("ID / Password salah!");
                errMsg.setVisibility(View.VISIBLE);
                //Toast.makeText(this, "Login Gagal!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class ReqTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
            try {
                URL url = new URL((uri[0]));
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                //GET Method
                if(uri[1] == "GET"){
                    con.setRequestMethod(uri[1]);
                    con.setRequestProperty("User-Agent", "Mozilla/5.0");
                    con.setConnectTimeout(5000); //set timeout to 5 seconds
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String input;
                    StringBuffer response = new StringBuffer();
                    while ((input = in.readLine())!=null) {
                        response.append(input);
                    }
                    in.close();
                    return String.valueOf(response);
                }
                else if (uri[1] == "HEAD") {
                    con.setRequestMethod(uri[1]);
                    con.setConnectTimeout(5000); //set timeout to 5 seconds
                    return "false";
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "false";
            } catch (IOException e) {
                e.printStackTrace();
                return "false";
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            JSONArray myRec = null;
            if(s != "false") {
                try {
                    myRec = new JSONArray(s);
                    JSONObject arrObj = myRec.getJSONObject(0);
                    a = arrObj.getString("username");
                    b = arrObj.getString("password");
                    z = arrObj.getString("address");
                    checkPw();
                } catch (JSONException e) {
                    checkPw();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getBaseContext(), "Failed connect to server.\nPlease try again later!", Toast.LENGTH_SHORT).show();
                errMsg.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void addUser(String a) {
        if (a == null) {
            mydb = new DBHandler(this);
            usermodel = new DataModel(1, user.getText().toString(), pass.getText().toString(), String.valueOf(z));
            mydb.insertUser(usermodel);
        }

        else if (a == "first") {
            mydb = new DBHandler(this);
            usermodel = new DataModel(1, user.getText().toString(), null, String.valueOf(z));
            mydb.insertUser(usermodel);
        }
    }

    public void getData(){
        mydb = new DBHandler(this);
        Cursor c = mydb.getData(1);
        if(c.moveToFirst()) {
            String dataget = c.getString(0);
            if(dataget != null) {
                myname = c.getString(1);
                mypw = c.getString(2);

                if(mypw != null) {
                    autoLog.setChecked(true);
                    String sendUrl = url + "/api/users/" + myname + "/" + mypw;
                    new ReqTask().execute(sendUrl, "GET");

                    user.setText(String.valueOf(myname));
                    pass.setText(String.valueOf(mypw));

                    checkPw();
                }
                else {
                    mydb = new DBHandler(this);
                    mydb.deleteData(1);
                }
            }
        }
    }

    public void goToHome() {
        Intent i = new Intent(this, MainActivity2.class);
        i.putExtra("goAct", "Home");
        startActivity(i);
        overridePendingTransition(0,0);
        finish();
    }
}