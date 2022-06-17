package com.pat.laundryapps;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationFragment extends Fragment {
    DBHandler mydb;
    ArrayList<String> id, telp, tgl, stat, metode, diterima;
    //String url = "http://192.168.139.204:8000";
    String url;
    ListView lv;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NotificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        url = getString(R.string.urlServer);

        id = new ArrayList<>();
        telp = new ArrayList<>();
        tgl = new ArrayList<>();
        stat = new ArrayList<>();
        metode = new ArrayList<>();
        diterima = new ArrayList<>();
        lv = (ListView) view.findViewById(R.id.lvNotification);

        String urlReq = getData();
        NotificationFragment.ReqTask reqTask = (NotificationFragment.ReqTask) new NotificationFragment.ReqTask(new NotificationFragment.ReqTask.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                JSONArray myRec = null;
                try {
                    myRec = new JSONArray(output);
                    for(int i = 0; i < myRec.length(); i++) {
                        JSONObject myRecObj = myRec.getJSONObject(i);
                        if (myRecObj.getString("receive").equals("Sudah Diterima")) {
                            id.add(myRecObj.getString("OrderID"));
                            telp.add(myRecObj.getString("username"));
                            tgl.add(myRecObj.getString("entryDate"));
                            stat.add(myRecObj.getString("status"));
                            metode.add(myRecObj.getString("method"));
                            diterima.add(myRecObj.getString("receive"));
                            CustomAdapter customAdapter = new CustomAdapter(getActivity(), id, telp, tgl, stat, metode, diterima);
                            lv.setAdapter(customAdapter);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).execute(urlReq, "GET");
        // Inflate the layout for this fragment
        return view;
    }

    public static class ReqTask extends AsyncTask<String, String, String> {


        public interface AsyncResponse {
            void processFinish(String output);
        }

        public NotificationFragment.ReqTask.AsyncResponse delegate = null;

        public ReqTask(NotificationFragment.ReqTask.AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected String doInBackground(String... uri) {
            try {
                URL url = new URL((uri[0]));
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                //GET Method
                if(uri[1] == "GET"){
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
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
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
        mydb = new DBHandler(getActivity());
        Cursor c = mydb.getData(1);
        String sendUrl = null;
        if(c.moveToFirst()) {
            String dataget = c.getString(0);
            if(dataget != null) {
                String myname = c.getString(1);
                sendUrl = url + "/api/data/" + myname;
            }
        }
        return sendUrl;
    }
}