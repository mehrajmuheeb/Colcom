package muheeb.com.colcom20;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FacultyNotification extends AppCompatActivity {
        Toolbar toolbar;
        EditText title;
        EditText notify,link;
        Button post;
        String title1;
        String not;
        int reg;
        String link1;
        public static String con;
        String urlSuffix="";
        SharedPreferences prefs;
        String registration="";
        String name;
        private static final String TAG = MainActivity.class.getSimpleName();
        private BroadcastReceiver mRegistrationBroadcastReceiver;
        private  static final String RATING_URL =Config.RATING_URL+"/Project/facultynotification.php?";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_notification);
        toolbar = (Toolbar) findViewById(R.id.facultynotification);
        setSupportActionBar(toolbar);                       //Sets Toolbar
        setTitle("Post Notification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        title= (EditText) findViewById(R.id.subject);
        notify= (EditText) findViewById(R.id.notify);
        post= (Button) findViewById(R.id.post);
        link= (EditText) findViewById(R.id.link);
        prefs=FacultyNotification.this.getSharedPreferences("UserCriticalData",MODE_PRIVATE);
        registration = prefs.getString("RegistrationNo", "");

        reg=Integer.parseInt(registration);
        name = prefs.getString("FullName","");



        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                title1 =title.getText().toString();
                not=notify.getText().toString();
                link1=link.getText().toString();

                if(link1.isEmpty())
                    link1="www.kashmiruniversity.net";
                sendNotification();
                urlSuffix="";


            }
        });

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.hasExtra("run_activity")) {
                    Log.e(TAG, "Yes... " + intent.getExtras().getString("run_activity"));
                    String className = intent.getStringExtra("run_activity");

                    Class cls = null;
                    try {
                        cls = Class.forName(className);
                    }catch(ClassNotFoundException e){
                       Log.e("Exception Class", e.toString());
                    }
                    Intent i = new Intent(context, cls);

                    if (i != null) {
                        i.putExtras(intent.getExtras());
                        FacultyNotification.this.startActivity(i);
                    }
                }








                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");
                    Log.e(TAG + "Recieved : ", message);
                    Intent notificationIntent= new Intent(FacultyNotification.this,Notification_Activity.class);
                    Bundle bundle= new Bundle();
                    bundle.putString("message",message);
                    startActivity(notificationIntent);
                  //  Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                    //txtMessage.setText(message);
                }
            }
        };

        displayFirebaseRegId();


    }





    private void sendNotification() {

        Calendar c =Calendar.getInstance();
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm a");
        String strDate= sdf.format(c.getTime());
        Log.e(TAG, "sendNotification: "+strDate );
        Log.e(TAG, "sendNotification: "+link1 );

        urlSuffix+="title="+title1+"&message="+not+"&reg="+reg+"&time="+strDate+"&web="+link1;
        Log.e("urlSuffix", "sendNotification: "+urlSuffix );

        class Rating extends AsyncTask<String, Void, String> {
            ProgressDialog loading ;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();



            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.e("Values Returned :", s);
                String b ="true";
                if(s.trim().equals(b.trim())) {
                    Toast.makeText(FacultyNotification.this, "Post Successful", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(FacultyNotification.this, MainActivity.class);
                    startActivity(intent);
                }
                else
                {

                    Toast.makeText(FacultyNotification.this, "Fail in connection...Try Again", Toast.LENGTH_LONG).show();


               }

            }

            @Override
            protected String doInBackground(String... params) {
                String s = params[0];


                Log.e("s is", "doInBackground: "+s );
                BufferedReader bufferedReader = null;
                String confirm_message = "";
                try
                {

                    Log.e("URL = ", RATING_URL + s);
                    s=s.replaceAll(" ", "%20");
                    URL url = new URL(RATING_URL+s);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    Log.e("Connection", "doInBackground: "+conn );
                    bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    confirm_message = bufferedReader.readLine();

                }
                catch (Exception e)
                {

                }
                return confirm_message;
            }
        }
        Rating reg_user = new Rating();
        reg_user.execute(urlSuffix);


    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId))
        Log.e(TAG, "displayFirebaseRegId: "+regId );
        else
            Log.e(TAG, "displayFirebaseRegId: "+"Firebase Reg Id is not received yet!" );
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "Resumed...");
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }




}
