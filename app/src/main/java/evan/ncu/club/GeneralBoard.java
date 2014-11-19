package evan.ncu.club;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class GeneralBoard extends Fragment {

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
        ((MainActivity) activity).onSectionAttached(1);
    }

    private class RetrieveList extends
            AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;
        final ConnectivityManager conMgr = (ConnectivityManager) getActivity()
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        private boolean isNetwork = true;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("讀取中");
            progressDialog.setCancelable(false);
            if (progressDialog != null)
                progressDialog.show();
            progressDialog.setOnCancelListener(new OnCancelListener() {
                public void onCancel(DialogInterface arg0) {
                }
            });
            itemsArray = new ArrayList<ListData>();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            if (activeNetwork != null && activeNetwork.isConnected()) {
                HttpClient client = new DefaultHttpClient();
                String result = "";
                try {
                    HttpGet get = new HttpGet("http://140.115.3.97/activity/v2/announce/common?size=40");

                    HttpResponse response = client.execute(get);

                    HttpEntity resEntity = response.getEntity();

                    if (resEntity != null) {
                        result = EntityUtils.toString(resEntity);
                        JSONArray jsonArray = new JSONArray(result);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonData = jsonArray.getJSONObject(i);
                            String name = jsonData.getString("title");
                            String content = jsonData.getString("content");
                            String time = jsonData.getString("time");
                            ListData listData = new ListData(name, time, content);
                            itemsArray.add(listData);
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    client.getConnectionManager().shutdown();
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
                Builder MyAlertDialog = new AlertDialog.Builder(getActivity());
                MyAlertDialog.setTitle("錯誤~");
                MyAlertDialog.setMessage("需要網際網路連線");
                MyAlertDialog.show();
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
