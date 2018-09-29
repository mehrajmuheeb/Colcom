package muheeb.com.colcom20;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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

public class FeedbackTeacher extends AppCompatActivity {



    SharedPreferences prefs;

    Toolbar toolbar;
    String fullName;
    String course;
    public ArrayList<FeedbackForm> listFromServer = new ArrayList();
    RatingBar r1,r2,r3,r4,r5,r6;
    LinearLayout linearLayout;
    TextView p,cs,tm,b,pr,ha;
    String punctuality,communicationskills,teachingmethodology,behaviour,presentation,helpingattitude;
    String items[]={"Punctuality","Communication Skills","Teaching Methodology","Behaviour","Presentation","Helping Attitude"};;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_teacher);
        toolbar = (Toolbar) findViewById(R.id.feedbackteacher);
        setSupportActionBar(toolbar);                       //Sets Toolbar
        setTitle("Feedback");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        prefs = FeedbackTeacher.this.getSharedPreferences("UserCriticalData", MODE_PRIVATE);
        fullName = prefs.getString("FullName", "");
        fullName = fullName.replaceAll(" ", "%20");
        course = prefs.getString("COURSE", "");
        Log.e("full name is", "onCreate: " + fullName);
        new MyTask().execute();
    }


    class MyTask extends AsyncTask<String, String, List<FeedbackForm>> {


        ProgressDialog loading ;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(FeedbackTeacher.this, "Please Wait", null, true, true);

        }



        @Override
        protected List doInBackground(String... params) {
            Log.e("Status", "doInBack");
            StringBuilder builder = new StringBuilder();


            try {
                HttpClient httpClient = new DefaultHttpClient();
                Log.e("Url is", "doInBackground: "+Config.RATING_URL+"/Project/fetchfeedback_teacher.php?name="+fullName+"&course="+course );
                HttpPost httpPost = new HttpPost(Config.RATING_URL+"/Project/fetchfeedback_teacher.php?name="+ fullName+"&course="+course);
                Log.e("full name is", "doInBackground: "+fullName );
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


                    punctuality=json_data.getString("Punctuality");
                    communicationskills=json_data.getString("CommunicationSkills");
                    teachingmethodology=json_data.getString("TeachingMethodology");
                    behaviour=json_data.getString("Behaviour");
                    presentation=json_data.getString("Presentation");
                    helpingattitude=json_data.getString("HelpingAttitude");
                    Log.e("punctuality is", "doInBackground: "+punctuality );

                    listFromServer.add(new FeedbackForm(punctuality,communicationskills,teachingmethodology,behaviour,presentation,helpingattitude));
                    Log.e("InBackground", "doInBackground: "+listFromServer );

                }
            } catch (JSONException e) {
                Log.e("JSONException", e.toString());
                listFromServer=null;
            }
            Log.e("listfromserver: ", "doInBackground: "+listFromServer );
            return listFromServer;
            //return "Task Completed...";
        }


        @Override
        protected void onPostExecute(final List<FeedbackForm> s) {
            super.onPostExecute(s);
            Log.e("list is", "onPostExecute: " + s);
            if (s == null) {

                loading.dismiss();
                Intent intent = new Intent(FeedbackTeacher.this, MainActivity.class);
                startActivity(intent);
            }
            else{

                loading.dismiss();


            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(30, 0, 0, 40);
            linearLayout = (LinearLayout) findViewById(R.id.rating_teacher);


            p = new TextView(FeedbackTeacher.this);
            p.setText(items[0]);
            Log.e("question", "onPostExecute: " + p.toString());
            p.setTextSize(18);
            p.setTypeface(null, Typeface.BOLD);
            p.setTextColor(Color.BLACK);
            p.setLayoutParams(params);
            linearLayout.addView(p);

            r1 = new RatingBar(FeedbackTeacher.this);
            r1.setLayoutParams(params);
            r1.setNumStars(5);
            r1.setRating(Integer.parseInt(punctuality));
            r1.setEnabled(false);
            if (Build.VERSION_CODES.LOLLIPOP >= Build.VERSION.SDK_INT) {

                Drawable drawable = r1.getProgressDrawable();
                drawable.setColorFilter(Color.parseColor("#009688"), PorterDuff.Mode.SRC_ATOP);

            }
            r1.setStepSize((float) 1.0);
            r1.setMinimumHeight(5);
            linearLayout.addView(r1);

            cs = new TextView(FeedbackTeacher.this);
            cs.setText(items[1]);
            cs.setTextSize(17);
            cs.setTextColor(Color.BLACK);
            cs.setLayoutParams(params);
            cs.setTypeface(null, Typeface.BOLD);
            linearLayout.addView(cs);

            r2 = new RatingBar(FeedbackTeacher.this);
            r2.setLayoutParams(params);
            r2.setNumStars(5);
            r2.setRating(Integer.parseInt(communicationskills));
            r2.setStepSize((float) 1.0);
            r2.setMinimumHeight(5);
            r2.setEnabled(false);
            if (Build.VERSION_CODES.LOLLIPOP >= Build.VERSION.SDK_INT) {

                Drawable drawable = r2.getProgressDrawable();
                drawable.setColorFilter(Color.parseColor("#009688"), PorterDuff.Mode.SRC_ATOP);

            }
            linearLayout.addView(r2);

            tm = new TextView(FeedbackTeacher.this);
            tm.setText(items[2]);
            tm.setTextSize(17);
            tm.setTextColor(Color.BLACK);
            tm.setLayoutParams(params);
            tm.setTypeface(null, Typeface.BOLD);
            linearLayout.addView(tm);

            r3 = new RatingBar(FeedbackTeacher.this);
            r3.setLayoutParams(params);
            r3.setNumStars(5);
            r3.setRating(Integer.parseInt(teachingmethodology));
            r3.setStepSize((float) 1.0);
            r3.setMinimumHeight(5);
            r3.setEnabled(false);
            if (Build.VERSION_CODES.LOLLIPOP >= Build.VERSION.SDK_INT) {

                Drawable drawable = r3.getProgressDrawable();
                drawable.setColorFilter(Color.parseColor("#009688"), PorterDuff.Mode.SRC_ATOP);

            }
            linearLayout.addView(r3);

            b = new TextView(FeedbackTeacher.this);
            b.setText(items[3]);
            b.setTextSize(17);
            b.setTextColor(Color.BLACK);
            b.setLayoutParams(params);
            b.setTypeface(null, Typeface.BOLD);
            linearLayout.addView(b);

            r4 = new RatingBar(FeedbackTeacher.this);
            r4.setLayoutParams(params);
            r4.setNumStars(5);
            r4.setRating(Integer.parseInt(behaviour));
            r4.setStepSize((float) 1.0);
            r4.setMinimumHeight(5);
            r4.setEnabled(false);
            if (Build.VERSION_CODES.LOLLIPOP >= Build.VERSION.SDK_INT) {

                Drawable drawable = r4.getProgressDrawable();
                drawable.setColorFilter(Color.parseColor("#009688"), PorterDuff.Mode.SRC_ATOP);

            }
            linearLayout.addView(r4);

            pr = new TextView(FeedbackTeacher.this);
            pr.setText(items[4]);
            pr.setTextSize(17);
            pr.setTextColor(Color.BLACK);
            pr.setLayoutParams(params);
            pr.setTypeface(null, Typeface.BOLD);
            linearLayout.addView(pr);

            r5 = new RatingBar(FeedbackTeacher.this);
            r5.setLayoutParams(params);
            r5.setNumStars(5);
            r5.setRating(Integer.parseInt(presentation));
            r5.setStepSize((float) 1.0);
            r5.setMinimumHeight(5);
            r5.setEnabled(false);
            if (Build.VERSION_CODES.LOLLIPOP >= Build.VERSION.SDK_INT) {

                Drawable drawable = r5.getProgressDrawable();
                drawable.setColorFilter(Color.parseColor("#009688"), PorterDuff.Mode.SRC_ATOP);

            }
            linearLayout.addView(r5);

            ha = new TextView(FeedbackTeacher.this);
            ha.setText(items[5]);
            ha.setTextSize(17);
            ha.setTextColor(Color.BLACK);
            ha.setLayoutParams(params);
            ha.setTypeface(null, Typeface.BOLD);
            linearLayout.addView(ha);

            r6 = new RatingBar(FeedbackTeacher.this);
            r6.setLayoutParams(params);
            r6.setNumStars(5);
            r6.setRating(Integer.parseInt(helpingattitude));
            r6.setStepSize((float) 1.0);
            r6.setMinimumHeight(5);
            r6.setEnabled(false);
            if (Build.VERSION_CODES.LOLLIPOP >= Build.VERSION.SDK_INT) {

                Drawable drawable = r6.getProgressDrawable();
                drawable.setColorFilter(Color.parseColor("#009688"), PorterDuff.Mode.SRC_ATOP);

            }
            linearLayout.addView(r6);


        }

        }


    }

}









class FeedbackForm{

    String p;
    String cs;
    String tm;
    String b;
    String pr;
    String ha;
    FeedbackForm(String p,String cs, String tm,String b,String pr, String ha)
    {
        this.p=p;
        this.cs=cs;
        this.tm =tm;
        this.b =b;
        this.pr =pr;
        this.ha =ha;
    }
    public String getPunctuality(){

        return this.p;
    }
    public String getCommunicationSkills(){
        return this.cs;
    }
    public String getTeachingMethodology(){
        return this.tm;
    }
    public String getBehaviour(){
        return this.b;
    }
    public String getPresentation(){
        return this.pr;
    }
    public String getHelpingAttitude(){
        return this.ha;
    }

}





























