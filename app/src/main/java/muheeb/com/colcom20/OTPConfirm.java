package muheeb.com.colcom20;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class OTPConfirm extends AppCompatActivity
{
    Button bConfirm;
    TextView textResendOTP, countDownText, timerText, timerFinishText;
    static EditText input_otp;
    String OTP, TAG = "OTP";
    public static SharedPreferences sharedPref2;
    public static SharedPreferences.Editor editor2;
    public static final String OTP_VALUE= "OTP FROM DATABASE";
    private static final String REGISTER_URL =Config.RATING_URL+"/Project/resend_otp.php";
    CountdownTimer timer;
    Toolbar toolbar;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpconfirm);
        setSupportActionBar(toolbar);
        setTitle("Registration");
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.otpconfirmcoordinatorlayout);

        initialise();
        countDownText.setText("Time elapsed ");
        timer.start();

        Log.e(TAG, "checkOTP: "+sharedPref2.getString("OTP","").trim());

        final String registration_no = sharedPref2.getString("REGISTRATION_NO","");     //Accessing REGISTRATION_NO from sharedPrefence.

        bConfirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                checkOTP();
            }
        });

        textResendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                countDownText.setText("Time elapsed ");
                timerFinishText.setText("");
                resendOtp(registration_no);
            }
        });


        //Terminates the Activity if OTP is verified

        if(sharedPref2.getString("OTP_PRESENT","").trim().equals("TRUE"))
        {
            Intent i = new Intent(OTPConfirm.this, MainActivity.class);
            startActivity(i);
            this.finish();
        }
    }


    //Called when "resend_otp" is clicked. Communicates with Database to generate new OTP.

    private void resendOtp(String registration_no)
    {
        String urlSuffix = "?registration_no="+registration_no;
        class ResendOtp extends AsyncTask<String, Void, String> {
            ProgressDialog loading ;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(OTPConfirm.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(s.equals("false")){

                    loading.dismiss();


                    Log.e(TAG, "onPostExecute: "+"check internet connection" );

                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_LONG)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(OTPConfirm.this,Registration.class);
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
                loading.dismiss();
                timer.start();                                                           //Starts Countdowntimer
                bConfirm.setClickable(true);
                // Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                editor2.putString("OTP", s.trim());
                editor2.commit();
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
                    bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));      //Reads data sent from
                    confirm_message = bufferedReader.readLine();                                            //database.
                }
                catch (Exception e)
                {
                    Log.e(TAG, "Exception : DIB" + e.getMessage());
                    confirm_message ="false";
                }
                Log.e(TAG, "Confirmmessage: "+confirm_message );
                return confirm_message;
            }
        }
        ResendOtp reg_user = new ResendOtp();
        reg_user.execute(urlSuffix);            //Starts Network Operations by calling doInBackground() function in Asynctask

    }


    //Called when Confirm button is clicked. Verfies OTP.

    private void checkOTP()
    {

        if(sharedPref2.getString("OTP","").trim().equals(input_otp.getText().toString().trim()))
        {
            timer.cancel();                                                 //Stops Countdown Timer.
            editor2.putString("OTP_PRESENT", "TRUE");
            editor2.commit();
            Log.e(TAG, "OTP_PResent: " + sharedPref2.getString("OTP_PRESENT",""));
            Intent i = new Intent(OTPConfirm.this, MainActivity.class);
            startActivity(i);
            OTPConfirm.this.finish();
        }
        else
        {
            Log.e(TAG, "doInBackground: confirm_message : " +OTP);
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "You have entered a wrong OTP", Snackbar.LENGTH_LONG);

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



    //Initialises Variables..

    private void initialise()
    {
        sharedPref2 = getSharedPreferences(Registration.sharedFileName, MODE_PRIVATE);
        editor2 = sharedPref2.edit();
        input_otp = (EditText) findViewById(R.id.input_OTP);
        textResendOTP = (TextView) findViewById(R.id.resendOTP);
        bConfirm = (Button) findViewById(R.id.btn_confirmOTP);
        OTP = input_otp.getText().toString().trim();
        toolbar = (Toolbar) findViewById(R.id.otptoolbar);
        countDownText = (TextView) findViewById(R.id.countDownText);
        timerText = (TextView) findViewById(R.id.timerText);
        timerFinishText = (TextView) findViewById(R.id.timerFinishText);
        timer = new CountdownTimer(60000, 1000);
    }

    public class CountdownTimer extends CountDownTimer
    {

        public CountdownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            long millis = millisUntilFinished;
            String sec = String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            timerText.setText(sec);
        }

        @Override
        public void onFinish()
        {
            timerFinishText.setText("Your registration window has elapsed. Click resend otp");
            countDownText.setText("");
            timerText.setText("");
            bConfirm.setClickable(false);
        }
    }
}
