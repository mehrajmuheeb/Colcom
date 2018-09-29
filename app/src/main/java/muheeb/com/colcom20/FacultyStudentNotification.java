package muheeb.com.colcom20;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

public class FacultyStudentNotification extends AppCompatActivity
{
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

    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private  static final String RATING_URL =Config.RATING_URL+"/Project/facultystudentnotification.php?";

    String buttonvalue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_student_notification);

        Bundle bundle = getIntent().getExtras();
        buttonvalue = bundle.getString("value");
        Log.e(TAG, "onCreate: "+buttonvalue );


        toolbar = (Toolbar) findViewById(R.id.facultystudentnotification);
        setSupportActionBar(toolbar);                       //Sets Toolbar
        setTitle("Post Notification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        title = (EditText) findViewById(R.id.stsubject);
        notify = (EditText) findViewById(R.id.stnotify);
        post = (Button) findViewById(R.id.stpost);
        link = (EditText) findViewById(R.id.stlink);

        prefs = FacultyStudentNotification.this.getSharedPreferences("UserCriticalData", MODE_PRIVATE);
        registration = prefs.getString("RegistrationNo", "");
        reg = Integer.parseInt(registration);


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                title1 = title.getText().toString();
                not = notify.getText().toString();
                link1 = link.getText().toString();

                if (link1.isEmpty())
                    link1 = "www.kashmiruniversity.net";
                sendNotification();
                urlSuffix = "";

            }
        });

    }


    private void sendNotification() {

        Calendar c =Calendar.getInstance();
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm a");
        String strDate= sdf.format(c.getTime());
        Log.e(TAG, "sendNotification: "+strDate );
        Log.e(TAG, "sendNotification: "+link1 );
        Log.e(TAG, "sendNotification: "+buttonvalue );

        urlSuffix+="title="+title1+"&message="+not+"&reg="+reg+"&time="+strDate+"&web="+link1+"&buttonvalue="+buttonvalue;
        Log.e("urlSuffix", "sendNotification: "+urlSuffix );

        class Notifications extends AsyncTask<String, Void, String> {
            ProgressDialog loading ;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(FacultyStudentNotification.this, "Please Wait", null, true, true);
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);


                loading.dismiss();
                Log.e("Values Returned :", s);
                String b = "true";
                if (s.trim().equals(b.trim())) {
                    Toast.makeText(FacultyStudentNotification.this, "Post Successful", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(FacultyStudentNotification.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    
                    Toast.makeText(FacultyStudentNotification.this, "Fail in connection...Try Again", Toast.LENGTH_LONG).show();


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
                    confirm_message ="null";
                }
                return confirm_message;
            }
        }
        Notifications n = new Notifications();
        n.execute(urlSuffix);


    }













}
