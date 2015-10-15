package evan.ncu.club;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LatestSchedule extends Fragment {

    private static final int INITIAL_DELAY_MILLIS = 500;
    private ListView m_ListView;
    public MyExpandableListItemAdapter listAdapter;
    public AlphaInAnimationAdapter alphaInAnimationAdapter;
    public ArrayList<ListData> itemsArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_latest, container,
                false);
        m_ListView = (ListView) rootView.findViewById(R.id.m_ListView);

        new RetrieveList().execute();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(2);
    }

    private class RetrieveList extends
            AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;
        final ConnectivityManager conMgr = (ConnectivityManager) getActivity()
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        private boolean isNetwork = true;
        private boolean serverStatus = true;
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("讀取中");
            progressDialog.setCancelable(false);
            if (progressDialog != null)
                progressDialog.show();
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface arg0) {
                }
            });
            itemsArray = new ArrayList<ListData>();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            if (activeNetwork != null && activeNetwork.isConnected()) {
                String result = "";
                try {

                    String url = "https://api.cc.ncu.edu.tw/activity/v1/announces?type=group&size=40";
                    URL myURL = new URL(url);
                    HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
                    myURLConnection.setRequestMethod("GET");
                    myURLConnection.setRequestProperty("X-NCU-API-TOKEN", getString(R.string.ncu_api_token));
                    myURLConnection.setUseCaches(false);
                    myURLConnection.setDoInput(true);
                    myURLConnection.connect();

                    int response = myURLConnection.getResponseCode();
                    BufferedReader r = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        total.append(line);
                    }
                    result = total.toString();

                    if (response == 200) {

                        JSONArray jsonArray = new JSONArray(result);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonData = jsonArray.getJSONObject(i);
                                String name = jsonData.getString("title");
                                String content = jsonData.getString("content");
                                String time = jsonData.getString("time");
                                String link = jsonData.getString("attachment");
                                String time_parsed;
                                Date date;
                                SimpleDateFormat simple = new java.text.SimpleDateFormat();
                                simple.applyPattern("yyyy-MM-dd HH:mm");
                                date = simple.parse(time);
                                simple.applyPattern("yyyy-MM-dd");
                                time_parsed = simple.format(date);
                                ListData listData;
                                if (link != "null")
                                    listData = new ListData(name, time, content + "\n" + link, time_parsed);
                                else
                                    listData = new ListData(name, time, content, time_parsed);
                                itemsArray.add(listData);
                            }
                            catch (JSONException je) {
                                Log.e("Debug", je.toString());
                            }
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    serverStatus = false;
                }
            } else {
                Log.e("Debug", "No network");
                isNetwork = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void arg0) {
            if (!isNetwork) {
                AlertDialog.Builder MyAlertDialog = new AlertDialog.Builder(getActivity());
                MyAlertDialog.setTitle("錯誤~");
                MyAlertDialog.setMessage("需要網際網路連線");
                MyAlertDialog.show();
            }
            if(!serverStatus){
                Toast.makeText(getActivity(), "無法與伺服器連線", Toast.LENGTH_SHORT).show();
            }
            listAdapter = new MyExpandableListItemAdapter(
                    getActivity(), itemsArray);
            alphaInAnimationAdapter = new AlphaInAnimationAdapter(listAdapter);
            alphaInAnimationAdapter.setAbsListView(m_ListView);
            assert alphaInAnimationAdapter.getViewAnimator() != null;
            alphaInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);
            m_ListView.setAdapter(alphaInAnimationAdapter);

            if (progressDialog != null)
                progressDialog.dismiss();

        }
    }

}
