package muheeb.com.colcom20;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Repository extends AppCompatActivity {
    Toolbar toolbar;
    ListView list;
    SharedPreferences prefs;
    String department;

    public ArrayList listFromServer = new ArrayList();
    public ArrayList pathname = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repository);

        toolbar = (Toolbar) findViewById(R.id.repotoolbar);
        setSupportActionBar(toolbar);                       //Sets Toolbar
        setTitle("Repository");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs=Repository.this.getSharedPreferences("UserCriticalData",MODE_PRIVATE);
        department=prefs.getString("COURSE","");
        new MyTask().execute();
    }




    class MyTask extends AsyncTask<String, String, List> {


        String file_path;


        ProgressDialog loading ;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(Repository.this, "Please Wait", null, true, true);
        }


        @Override
        protected List doInBackground(String... params) {
            Log.e("Status", "doInBack");
            StringBuilder builder = new StringBuilder();


            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.RATING_URL+"/Project/fetch_repository.php?department="+department);

                Log.e("Status", "URL HIT");


                HttpResponse response = httpClient.execute(httpPost);

                HttpEntity entity = response.getEntity();
                Log.e("Status", "ABout to Read");
                BufferedReader buffer = new BufferedReader(new InputStreamReader(entity.getContent()));

                String valFromServer = null;

                while ((valFromServer = buffer.readLine()) != null) {

                    builder.append(valFromServer);
                    Log.e("TAG", "doInBackground: "+builder.toString());
                }
                Log.e("DATA", builder.toString());
                //in.close();


            } catch (ClientProtocolException e) {
                Log.e("CPException", e.toString());
            } catch (IOException e) {
                Log.e("IOException", e.toString());
            } catch (Exception e) {
                Log.e("Excep", e.toString());
            }

            JSONObject json_data = new JSONObject();
            try {
                JSONArray jsonArray = new JSONArray(builder.toString());


                Log.e("json length", "doInBackground: "+jsonArray.length() );
                for (int i = 0; i < jsonArray.length(); i++) {

                    json_data = jsonArray.getJSONObject(i);


                    String file_name=json_data.getString("file_name");
                     file_path=json_data.getString("file_path");
                    listFromServer.add(json_data.getString("file_name"));
                    pathname.add(json_data.getString("file_path"));

                }
            } catch (JSONException e) {
                Log.e("JSONException", e.toString());
                listFromServer =null;
            }

               Log.e("listfromserver: ", "doInBackground: "+listFromServer );
                return listFromServer;

        }

        @Override
        protected void onPostExecute(final List s) {
            super.onPostExecute(s);

            if (s == null) {
                loading.dismiss();
                Intent intent = new Intent(Repository.this, MainActivity.class);
                startActivity(intent);

            }
            else{

            loading.dismiss();;
            CustomList adapter = new CustomList(Repository.this, s);
            list = (ListView) findViewById(R.id.listrepo);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    String pn = pathname.get(position).toString().replaceAll(" ", "%20");
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://colcom.pe.hu/Project/" + pn)));

                }
            });
        }





        }


    }






}
