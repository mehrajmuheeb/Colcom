package muheeb.com.colcom20;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class Notification_Activity extends AppCompatActivity {
    Toolbar toolbar;
    TextView textView;
    private  Context mContext;

    private static final String TAG = Notification_Activity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    RelativeLayout l;
    public ArrayList<Notification> listFromServer = new ArrayList();
    public RecyclerView mRecyclerView;
    public RecyclerView.Adapter mAdapter;
    public RecyclerView.LayoutManager mLayoutManager;
    public static String LOG_TAG = "CardViewActivity";
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
        setContentView(R.layout.activity_notification_);
        toolbar = (Toolbar) findViewById(R.id.notificationtoolbar);
        setSupportActionBar(toolbar);                       //Sets Toolbar
        setTitle("Notifications");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        l= (RelativeLayout) findViewById(R.id.view);

        prefs=Notification_Activity.this.getSharedPreferences("UserCriticalData",MODE_PRIVATE);

        desig = prefs.getString("DESIGNATION", "");
        batch= prefs.getString("BATCH","");
        course = prefs.getString("COURSE","");
        name = prefs.getString("FullName","");
        name = name.replaceAll(" ", "%20");
        new MyTask().execute();




        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
               // new MyTask().execute();

                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {

                    String message = intent.getStringExtra("message");
                  Log.e(TAG + "Recieved : ", message);
                }
            }
        };


    }

           class MyTask extends AsyncTask<String, String, List<Notification>> {


               ProgressDialog loading ;

               @Override
               protected void onPreExecute() {
                   super.onPreExecute();
                   loading = ProgressDialog.show(Notification_Activity.this, "Please Wait", null, true, true);
               }



               @Override
        protected List doInBackground(String... params) {
            Log.e("Status", "doInBack");
            StringBuilder builder = new StringBuilder();


            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.RATING_URL+"/Project/tofaculty.php?designation="+desig+"&batch="+batch+"&course="+course+"&name="+name);

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



                    listFromServer.add(new Notification(name,title,message,t,d,web));


                }
            } catch (JSONException e) {
                Log.e("JSONException", e.toString());
                listFromServer=null;
            }
            Log.e(TAG, "doInBackground: "+listFromServer );
            return listFromServer;
            //return "Task Completed...";
        }

        @Override
        protected void onPostExecute(final List<Notification> s) {
            super.onPostExecute(s);
            if (s == null) {

                loading.dismiss();
                Intent intent = new Intent(Notification_Activity.this, MainActivity.class);
                startActivity(intent);


            }
            else{
            loading.dismiss();
            Log.e(TAG, "onPostExecute: " + s);
            mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(Notification_Activity.this);
            // mLayoutManager.setAutoMeasureEnabled(true);
            mRecyclerView.setLayoutManager(mLayoutManager);
            Log.e("list from server", "onCreate: " + listFromServer);
            mAdapter = new MyRecyclerViewAdapter(listFromServer, Notification_Activity.this);
            mRecyclerView.setAdapter(mAdapter);
        }

        }


    }















    @Override
    protected void onResume() {
        super.onResume();


        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(Notification_Activity.this);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }







    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.e(TAG, "onCreateOptionsMenu: "+desig );

       if (desig.equals("Teaching")) {
            Log.e(TAG, "onCreateOptionsMenu: " + "inside if");
            getMenuInflater().inflate(R.menu.menu_notification, menu);
       }
        return true;
//        else
//            return false;




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_postnotification)
        {

            final Dialog dialog = new Dialog(Notification_Activity.this);
            dialog.setContentView(R.layout.test);
            dialog.setTitle("Send Notification To: ");

            //adding text dynamically
            txt = (TextView) dialog.findViewById(R.id.faculty);
            txt.setTextSize(20);
            txt.setText("Faculty");

            txt1 = (TextView) dialog.findViewById(R.id.students);
            txt1.setTextSize(20);
            txt1.setText("Students");
            dialog.show();

            txt1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i3 = new Intent(getApplicationContext(), StudentNotification.class);

                    startActivity(i3);



                }
            });

            txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i3 = new Intent(getApplicationContext(), FacultyNotification.class);

                    startActivity(i3);

                }
            });

        }
        if (id == R.id.action_sentitem)
        {
            Intent intent =new Intent(Notification_Activity.this,SentMessages.class);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }









}


class Notification{

    String name;
    String title;
    String message;
    String messagetime;
    String messagedate;
    String web;
    Notification(String name,String title, String message,String t,String d, String web)
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




