package muheeb.com.colcom20;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class FeedbackQuestions extends AppCompatActivity {


    private static final String RATING_URL =Config.RATING_URL+"/Project/Ratings.php?";
    private TextView mQuestionTextView;
    private FeedbackQuestionBank[]mQuestionBank;
    private int mCurrentIndex = 0;
    private Toolbar toolbar;
    SharedPreferences sharedPref2;
    SharedPreferences.Editor editor2;
    TextView text;
    TextView t;
    LinearLayout linearLayout;
    int i;
    Button next;
    RatingBar rating;
    RatingBar [] ratingBars;
    String []ratings;
    String [] key;
    Float [] values;
    HashMap<String,Float> qs=new HashMap();
    String urlSuffix="";
    String data="";
    int counter=1;
    CoordinatorLayout coordinatorLayout;
    SharedPreferences prefs;
    String feedback="";
    int reg;
    String course;
    String confirm_message = "";




    public  boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getQuestion();
        mQuestionTextView.setText(question);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_questions);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        setTitle("Feedback Form");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       sharedPref2 = getSharedPreferences(Registration.sharedFileName, MODE_PRIVATE);
        editor2 = sharedPref2.edit();
        coordinatorLayout= (CoordinatorLayout) findViewById(R.id.feedback_questionscoordinatorlayout);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(30, 0, 0, 40);
        prefs=FeedbackQuestions.this.getSharedPreferences("UserCriticalData",MODE_PRIVATE);
         feedback = prefs.getString("RegistrationNo", "");
        reg=Integer.parseInt(feedback);
        course= prefs.getString("COURSE","");


        Bundle bundle = getIntent().getExtras();
        String stuff = bundle.getString("stuff");
        final String[] items = stuff.split("#");
        ratingBars = new RatingBar[items.length];
        linearLayout = (LinearLayout) findViewById(R.id.relative);

        mQuestionBank = new FeedbackQuestionBank[] {
                new FeedbackQuestionBank(R.string.q1),
                new FeedbackQuestionBank(R.string.q2),
                new FeedbackQuestionBank(R.string.q3),
                new FeedbackQuestionBank(R.string.q4),
                new FeedbackQuestionBank(R.string.q5),
                new FeedbackQuestionBank(R.string.q6)
        };

        mQuestionTextView = (TextView)findViewById(R.id.question_text_view);
        updateQuestion();

        for (i = 0; i < items.length; i++) {

            t = new TextView(FeedbackQuestions.this);
            t.setText(items[i]);
            t.setTextColor(Color.BLACK);
            t.setLayoutParams(params);
            linearLayout.addView(t);

            if (i > 0){


                ratingBars[i] = new RatingBar(FeedbackQuestions.this);
                ratingBars[i].setLayoutParams(params);
                ratingBars[i].setNumStars(5);
                ratingBars[i].setRating(0);
                ratingBars[i].setStepSize((float) 1.0);
                ratingBars[i].setMinimumHeight(5);
                ratingBars[i].setId(i);
                linearLayout.addView(ratingBars[i]);
                ratingBars[i].setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        // TODO Auto-generated method stub
                        int gi=ratingBar.getId();



                        Float a=ratingBar.getRating();
                        String s=items[gi];
                        qs.put(s,a);
                        Log.e("punctuality: ", "onRatingChanged: "+qs );



                    }
                });
            }

        }



        next= new Button(FeedbackQuestions.this);
        next.setText("Next");
        next.setBackground(getResources().getDrawable(R.drawable.button_flat));
        linearLayout.addView(next);
        t=new TextView(FeedbackQuestions.this);
        linearLayout.addView(t);




        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Mindex", "onClick: "+mCurrentIndex);
                if (mCurrentIndex == 5) {
                    Log.e("Counter", "onClick: "+counter );

//                    sendData(qs);
//                    qs.clear();
//                    urlSuffix="";
                    new AlertDialog.Builder(FeedbackQuestions.this)
                            .setTitle("Feedback Forum")
                            .setMessage("Are you sure you want to submit the form?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (isNetworkAvailable()) {
                                        sendData(qs);
                                        qs.clear();
                                        urlSuffix = "";
                                        int b = MainActivity.batch + 1;
                                        Log.e("B is ", "onPostExecute: " + b);
                                        String batch = Integer.toString(b);
                                        Log.e("Batch is", "onPostExecute: " + batch);
                                        editor2.putString("FeedbackBatch", batch);
                                        editor2.commit();
                                        Log.e("shared prefs", "onClick: " + prefs.getString("FeedbackBatch", ""));
                                        Log.e("confirm msg is", "onClick: "+confirm_message );
                                        if (confirm_message.trim().equals("success"))
                                        {
                                            Log.e("success", "onClick: "+confirm_message );
//                                            Snackbar snackbar = Snackbar
//                                                    .make(coordinatorLayout, "Thank you for Your Feedback", Snackbar.LENGTH_LONG);
//
//
//                                            // Changing action button text color
//                                            snackbar.show();
                                            Toast.makeText(FeedbackQuestions.this,"Thank You for Your Feedback",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(FeedbackQuestions.this, MainActivity.class);
                                        startActivity(intent);


                                    }


                                        else{


                                            Intent intent = new Intent(FeedbackQuestions.this,MainActivity.class);
                                            startActivity(intent);




                                        }
                                }
                                    else{
                                        Intent intent = new Intent(FeedbackQuestions.this,MainActivity.class);
                                        startActivity(intent);
                                    }

                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {


                    if (isNetworkAvailable()){
                        mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                    sendData(qs);
                    qs.clear();
                    urlSuffix = "";
                    data = "";
                    counter++;
                    updateQuestion();
                    Log.e("RatingBars", ((Integer) ratingBars.length).toString());
                    for (int var = 1; var < ratingBars.length; var++) {
                        ratingBars[var].setRating(0);
                    }
                }

                    else{

                      Intent intent = new Intent(FeedbackQuestions.this,MainActivity.class);
                        startActivity(intent);





                    }





                }
            }
        });

   }

    private void sendData(HashMap qs) {
        Set mapSet = (Set) qs.entrySet();
        Iterator mapIterator = mapSet.iterator();
        key = new String[qs.size()];
        values = new Float[qs.size()];
        int i = 0;
        while (mapIterator.hasNext()) {

            Map.Entry mapEntry = (Map.Entry) mapIterator.next();
            String keyValue = (String) mapEntry.getKey();
            Float value = (Float) mapEntry.getValue();
            //iterate over the array and print each value
            key[i] = keyValue;
            values[i] = value;
            data += key[i] + "_" + values[i] + "_";
            i++;
        }
        switch (counter){

            case 1:
                Log.e("Counter", "sendData: "+counter );
                urlSuffix += "data="+"Punctuality_" +reg+"_"+course+"_"+ data;
                Log.e("Url suffix= ", "sendData: " + urlSuffix);
                break;
            case 2:
                urlSuffix += "data="+"CommunicationSkills_" +reg+"_"+course+"_"+ data;
                Log.e("Url suffix= ", "sendData: " + urlSuffix);
                break;
            case 3:
                urlSuffix += "data="+"TeachingMethodology_" +reg+"_"+course+"_"+ data;
                Log.e("Url suffix= ", "sendData: " + urlSuffix);
                break;
            case 4:

                urlSuffix += "data="+"Behaviour_" +reg+"_"+course+"_"+ data;
                Log.e("Url suffix= ", "sendData: " + urlSuffix);
                break;
            case 5:

                urlSuffix += "data="+"Presentation_" +reg+"_"+course+"_"+ data;
                Log.e("Url suffix= ", "sendData: " + urlSuffix);
                break;
            case 6:

                urlSuffix += "data=" +"HelpingAttitude_"+reg+"_"+course+"_"+ data;
                Log.e("Url suffix= ", "sendData: " + urlSuffix);
                break;




        }



        class Rating extends AsyncTask<String, Void, String> {
            ProgressDialog loading ;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(FeedbackQuestions.this, "Please Wait", null, true, true);

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.e("Values Returned :", s);
                if (s.trim().equals("success")) {
                    loading.dismiss();
                    confirm_message="";
                    if(mCurrentIndex==5)
                        confirm_message="success";

                } else{
                    loading.dismiss();
                    Intent intent =new Intent(FeedbackQuestions.this,MainActivity.class);
                    startActivity(intent);


            }


            }

            @Override
            protected String doInBackground(String... params) {
                String s = params[0];
                BufferedReader bufferedReader = null;

                try
                {
                    s=s.replaceAll(" ", "%20");
                    Log.e("URL = ", RATING_URL + s);
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
    @Override
    public void onBackPressed()
    {
        Log.d("CDA", "onBackPressed Called");
        Intent intent = new Intent(FeedbackQuestions.this, MainActivity.class);
        startActivity(intent);
    }
}




