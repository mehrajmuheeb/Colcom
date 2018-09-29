

package muheeb.com.colcom20;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

public class Feedback extends AppCompatActivity {
    CheckBox checkbox;
    String h;
    LinearLayout l,l1,l2,l3;
    Button button;
    TextView textView;
    int numberOfCheckboxesChecked = 0;
    private Toolbar toolbar;
    int i = 0,j;
    int a;
    String selectedValue="";
    ArrayList<String> mylist= new ArrayList<String>();
    SharedPreferences prefs;
    String s;
    CoordinatorLayout coordinatorLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);
        mylist.add("");
        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        setTitle("Feedback");
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.feedbackcoordinatorlayout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs=Feedback.this.getSharedPreferences("UserCriticalData",MODE_PRIVATE);
         s = prefs.getString("COURSE", "");
        new MyTask().execute();

    }

    class MyTask extends AsyncTask<String, String, List> {


        ProgressDialog loading ;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(Feedback.this, "Please Wait", null, true, true);
        }











        @Override
        protected List doInBackground(String... params) {
            Log.e("Status", "doInBack");
            StringBuilder builder = new StringBuilder();
            ArrayList<String> listFromServer = new ArrayList();

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.RATING_URL+"/Project/faculty.php?course="+s);

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

                    listFromServer.add(json_data.getString("item"));

                }
            } catch (JSONException e) {
                Log.e("JSONException", e.toString());
                listFromServer=null;
            }
            return listFromServer;
            //return "Task Completed...";
        }

        @Override
        protected void onPostExecute(final List s) {
            super.onPostExecute(s);
            Log.e("s is", "onPostExecute: "+s );
            if (s == null) {
                loading.dismiss();
                Log.e("inside if", "onPostExecute: " + s);
                Intent intent= new Intent(Feedback.this,MainActivity.class);
                startActivity(intent);


            }

            else{
                loading.dismiss();


            Toast.makeText(getApplicationContext(), "Scroll down to view more items", Toast.LENGTH_SHORT).show();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(30, 0, 0, 40);

            a = s.size();
            l = (LinearLayout) findViewById(R.id.linearMain);
            // l1=(LinearLayout)findViewById(R.id.linearMain2);
            textView = new TextView(Feedback.this);
            textView.setTextColor(Color.BLACK);
            textView.setText("Select the respective faculty");
            textView.setTextSize(20);
            textView.setLayoutParams(params);
            l.addView(textView);
            CheckBox c[] = new CheckBox[a];

            for (i = 0; i < a; i++) {
                c[i] = new CheckBox(Feedback.this);
                c[i].setId(i);
                c[i].setText(s.get(i).toString());
                // c[i].setTextColor(getResources().getColor(R.color.grid_green));
                c[i].setLayoutParams(params);
                l.addView(c[i]);

            }

            selectedValue = "";

            for (final CheckBox box : c) {

                box.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (box.isChecked()) {
                            mylist.add(box.getText().toString());
                            numberOfCheckboxesChecked++;
                            Log.e("Array value", "onClick: " + mylist);

                        } else {
                            mylist.remove(box.getText().toString());
                            Log.e("else array value", "onClick: " + mylist);
                            numberOfCheckboxesChecked--;
                            Log.e("else", "onClick: " + numberOfCheckboxesChecked);
                        }

                    }
                });
            }


            button = new Button(Feedback.this);
            button.setText("Next");
            button.setBackground(getResources().getDrawable(R.drawable.button_flat));
            l.addView(button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("list size", "onClick: " + mylist.size());
                    for (int i = 1; i < mylist.size(); i++) {
                        Log.e("My list", "onClick: " + mylist.get(i));
                        selectedValue = selectedValue + "#" + mylist.get(i);
                    }
                    Log.e("Selected Value", "onPostExecute: " + selectedValue);
                    Log.e("Selectected List ", selectedValue);
                    if (numberOfCheckboxesChecked == 5) {


                        Intent intent = new Intent(Feedback.this, FeedbackQuestions.class);
                        Bundle bundle = new Bundle();

                        bundle.putString("stuff", selectedValue);


                        intent.putExtras(bundle);
                        startActivity(intent);


                    } else {
                        Toast.makeText(getApplicationContext(), "Select only 5", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        }


    }
}