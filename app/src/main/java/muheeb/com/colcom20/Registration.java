package muheeb.com.colcom20;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

public class Registration extends AppCompatActivity
{
    EditText registration_no;
    Button btn_register;
    public static SharedPreferences sharedPref;
    SharedPreferences pref;
    public static String sharedFileName = "UserCriticalData";
    public static SharedPreferences.Editor editor;
    private static final String REGISTER_URL =Config.RATING_URL+"/Project/RegistrationValidation.php";
    CoordinatorLayout coordinatorLayout;
    public static String TAG;
    String desig,course,batch;
    int FLAG =0;
    StringBuilder builder;
    SharedPreferences prefs;


    //Method to check if Internet is available
    public  boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //Called when the activity launches
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registration);
        setTitle("ColCom");
        FirebaseInstanceId.getInstance().getToken();
        builder = new StringBuilder();

        prefs=Registration.this.getSharedPreferences("UserCriticalData",MODE_PRIVATE);                  //
        desig = prefs.getString("DESIGNATION", "");                                                     // Data
        batch= prefs.getString("BATCH","");                                                             // accessed from
        course = prefs.getString("COURSE","");                                                          // SharedPreferences.
        TAG = "STATUS";

        initialise();                                                                                   //Initialises references

        Log.e(TAG, "onCreate: "+REGISTER_URL );
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable())
                    checkRegisterUser();
                else{

                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_LONG)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(Registration.this,Registration.class);
                                    startActivity(intent);
                                }
                            });

                    // Changing message text color
                    snackbar.setActionTextColor(Color.BLACK);

                    // Changing action button text color
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(Color.WHITE);
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.BLACK);
                    snackbar.show();
            }


            }
        });

        //Terminates the current Activity if Registration is done

        if (sharedPref.getString("COURSE","")!="")
        {
            Intent i = new Intent(Registration.this, OTPConfirm.class);
            startActivity(i);
            this.finish();
        }

    }

    //Called when Register button is clicked.

    private void checkRegisterUser()
    {
        Log.e(TAG, "registerUser: ");
        String registrationNo = registration_no.getText().toString().trim().toLowerCase();
        register(registrationNo);
    }


    //Called from checkRegisterUser(). Communicates with Database to verify user.

    private void register(String registration_no)
    {

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        Log.e("Firebase regid is...", "Firebase reg id: " + regId);
        String urlSuffix = "?registration_no="+registration_no+"&firebaseid="+regId;

        class RegisterUser extends AsyncTask<String, Void, String> {
            ProgressDialog loading ;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Registration.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s)
            {
                super.onPostExecute(s);
                Log.e(TAG, "onPostExecute: "+s );
                if (s.equals("false")) {

                    loading.dismiss();
                    Log.e(TAG, "onPostExecute: "+"check internet connection" );

                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_LONG)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(Registration.this,Registration.class);
                                    startActivity(intent);
                                }
                            });

                    // Changing message text color
                    snackbar.setActionTextColor(Color.BLACK);

                   // Changing action button text color
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(Color.WHITE);
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.BLACK);
                    snackbar.show();
                }
                else
                {
                    loading.dismiss();
                String message[] = s.split("#");
                Log.e(TAG, "onPostExecute: " + s);

                editor = sharedPref.edit();

                for (int i = 0; i < message.length; i++)
                    Log.e("Message", "onPostExecute: " + message[i] + "Length= " + message.length);

                    if (message[0].trim().equals("Valid User"))
                    {
                        FLAG = 1;
                    }

                if(FLAG == 1)
                {

                    //Stores data in SharedPreference
                    editor.putString("OTP", message[1].trim());
                    editor.putString("COURSE", message[2].trim());
                    editor.putString("COURSE_DURATION", message[3].trim());
                    editor.putString("DESIGNATION", message[4].trim());
                    editor.putString("BATCH", message[5].trim());
                    editor.putString("REGISTRATION_NO", message[6].trim());
                    editor.putString("FeedbackBatch", message[7].trim());
                    editor.putString("RegistrationNo", message[8].trim());
                    editor.putString("Month_Batch", message[9].trim());
                    editor.putString("FullName", message[10].trim());
                    editor.putString("NAME", message[11].trim());
                    editor.commit();

                    Intent i = new Intent(Registration.this, OTPConfirm.class);
                    startActivity(i);
                    Registration.this.finish();                     //Terminates activity after User is verified..
                }else
                {
                    Log.e(TAG, "doInBackground: confirm_message : " +message[0]);
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Your Registration Id is invalid", Snackbar.LENGTH_LONG);

                    // Changing message text color
                    snackbar.setActionTextColor(Color.BLACK);

                    // Changing action button text color
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(Color.WHITE);
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.BLACK);
                    snackbar.show();

                }
                }

                }


            @Override
            protected String doInBackground(String... params) {
                String s = params[0];
                BufferedReader bufferedReader = null;
                String confirm_message = "";

                try
                {
                    Log.e(TAG, "doInBackground: URL = " + REGISTER_URL+s);
                    URL url = new URL(REGISTER_URL+s);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));		//Reads data sent from
                    confirm_message = bufferedReader.readLine();											//database
                }
                catch (Exception e)
                {
                        confirm_message="false";
                        Log.e(TAG, "doInBackground: "+confirm_message );

                }
                return confirm_message;

            }
        }
        RegisterUser reg_user = new RegisterUser();
        reg_user.execute(urlSuffix);                   //Starts Network Operations by calling doInBackground() function in Asynctask

    }


    //Initialises variables
    private void initialise()
    {
        registration_no = (EditText) findViewById(R.id.registrationInput);
        btn_register = (Button) findViewById(R.id.registerButton);
        sharedPref = getSharedPreferences(sharedFileName, MODE_PRIVATE);
        coordinatorLayout= (CoordinatorLayout) findViewById(R.id.registrationcoordinatorlayout);
    }
}
