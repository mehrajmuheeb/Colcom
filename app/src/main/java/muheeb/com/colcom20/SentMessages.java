package muheeb.com.colcom20;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

public class SentMessages extends AppCompatActivity {

    Toolbar toolbar;
    private static final String TAG = Notification_Activity.class.getSimpleName();
    RelativeLayout l;
    public ArrayList<SentNotification> listFromServer = new ArrayList();
    public RecyclerView mRecyclerView;
    public RecyclerView.Adapter mAdapter;
    public RecyclerView.LayoutManager mLayoutManager;
    SharedPreferences prefs;
    String desig;
    TextView txt1;
    TextView txt;
    String batch;
    String course;
    String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_messages);
        toolbar = (Toolbar) findViewById(R.id.sentnotificationtoolbar);
        setSupportActionBar(toolbar);                       //Sets Toolbar
        setTitle("Sent Notifications");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        prefs=SentMessages.this.getSharedPreferences("UserCriticalData",MODE_PRIVATE);

        desig = prefs.getString("DESIGNATION", "");
        batch= prefs.getString("BATCH","");
        course = prefs.getString("COURSE","");
        name = prefs.getString("FullName","");
        name = name.replaceAll(" ", "%20");
        new MyTask().execute();
    }


    class MyTask extends AsyncTask<String, String, List<SentNotification>> {
        ProgressDialog loading ;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(SentMessages.this, "Please Wait", null, true, true);
        }




        @Override
        protected List doInBackground(String... params) {
            Log.e("Status", "doInBack");
            StringBuilder builder = new StringBuilder();


            try {
                Log.e(TAG, "doInBackground: "+"inside try" );
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.RATING_URL+"/Project/sentnotifications.php?course="+course+"&name="+name);

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


                for (int i = 0; i < jsonArray.length(); i++) {

                    json_data = jsonArray.getJSONObject(i);


                    String name=json_data.getString("Teacher");
                    String title=json_data.getString("Title");
                    String message=json_data.getString("Message");
                    String messagetime=json_data.getString("MessageTime");
                    String web=json_data.getString("Web");
                    String []date=messagetime.split(" ");
                    String d=date[0];
                    String a=date[2];
                    String t=date[1]+" "+a;



                    listFromServer.add(new SentNotification(name,title,message,t,d,web));


                }
            } catch (JSONException e) {
                Log.e("JSONException", e.toString());
                listFromServer= null;
            }
            Log.e(TAG, "doInBackground: "+listFromServer );
            return listFromServer;
            //return "Task Completed...";
        }

        @Override
        protected void onPostExecute(final List<SentNotification> s) {
            super.onPostExecute(s);
            if (s == null) {

                loading.dismiss();
                Intent intent = new Intent(SentMessages.this, MainActivity.class);
                startActivity(intent);

            }


            else{
              loading.dismiss();
            Log.e(TAG, "onPostExecute: " + s);
            mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(SentMessages.this);
            // mLayoutManager.setAutoMeasureEnabled(true);
            mRecyclerView.setLayoutManager(mLayoutManager);
            Log.e("list from server", "onCreate: " + listFromServer);
            mAdapter = new MyRecyclerViewAdapter2(listFromServer, SentMessages.this);
            mRecyclerView.setAdapter(mAdapter);
        }

        }


    }


}





class SentNotification{

    String name;
    String title;
    String message;
    String messagetime;
    String messagedate;
    String web;
    SentNotification(String name,String title, String message,String t,String d, String web)
    {
        this.name=name;
        this.title=title;
        this.message=message;
        messagetime=t;
        messagedate=d;
        this.web=web;
    }
    public String getName(){

        return this.name;
    }
    public String getTitle(){
        return this.title;
    }
    public String getMessage(){
        return this.message;
    }
    public String getMessagetime(){
        return this.messagetime;
    }
    public String getMessagedate(){
        return this.messagedate;
    }
    public String getLink(){
        return this.web;
    }

}











