package muheeb.com.colcom20;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.Calendar;
import java.util.GregorianCalendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    NavigationView navigationView;
    TextView user_name, department, current_year;
    static SharedPreferences prefs;
    View divider;
    int year;
    int month;
    String bat;
    int cur_year, tempYear;
    static CircleImageView image;
    Button msg, notification;
    public static int batch;
    String regno;
    String fbatch;
    String designation;
    String course;
    public static View header;
    public static ImageView profilePic;
    CoordinatorLayout coordinatorLayout;
    TextView badgenotification;
    SharedPreferences sharedPref2;




    public  boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.mainactivitycoordinatorlayout);
        prefs = MainActivity.this.getSharedPreferences("UserCriticalData", MODE_PRIVATE);
        bat = prefs.getString("BATCH", "");
        regno = prefs.getString("REGISTRATION_NO","");
        designation = prefs.getString("DESIGNATION","");
        Log.e("designation is", "onCreate: "+designation );
        course = prefs.getString("COURSE","");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);                       //Sets Toolbar

        String XYZ = prefs.getString("FullName","");

        Log.e("onCreate: ", XYZ);
        /////////////////////////////////////////////////////////////////////////////////////////////////////


        setTitle("Home");

        initialise();

        if(COLCOMFirebaseMessagingService.counter == 0){

            // badgenotification.setText("0");
        }
        else{

            badgenotification.setText(COLCOMFirebaseMessagingService.counter);

        }


        Calendar calendar = Calendar.getInstance();
        cur_year = calendar.get(Calendar.YEAR);
        tempYear = Integer.parseInt(prefs.getString("BATCH", ""));

        tempYear= cur_year - tempYear;

        if(!prefs.getString("DESIGNATION","").equals("Teaching"))
        {
            if (tempYear == 0)
                current_year.setText("Year " + tempYear + 1);
            else {
                if (tempYear > Integer.parseInt(prefs.getString("COURSE_DURATION", "")))
                    current_year.setText("Graduated");
                else
                    current_year.setText("Year " + tempYear);
            }
        }
        else
        {
            LinearLayout ll = (LinearLayout) findViewById(R.id.setTagline);
            ll.removeView(divider);
            ll.removeView(current_year);
        }

        user_name.setText(prefs.getString("NAME", ""));
        department.setText("");
        department.setText((prefs.getString("COURSE", "")));




        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Settings.class);
                startActivity(i);
            }
        });

        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, TabActivity.class);
                startActivity(i);
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this,Notification_Activity.class);
                startActivity(intent);
            }
        });
//        obj = new Settings();
//        if (OTPConfirm.sharedPref2.getString("SAVED_IMAGE","").equals("TRUE"))
//            new Settings().loadImage(path);




















        /////////////////////////////////////////////////////////////////////////////////////////////////////

        GregorianCalendar gc = new GregorianCalendar();
        year = gc.get(Calendar.YEAR);
        month= gc.get(Calendar.MONTH);
        month=month+1;
        initNavigationDrawer();
        getfbatch();





    }

    private void initialise()
    {
        image = (CircleImageView) findViewById(R.id.profilePic);
        user_name = (TextView) findViewById(R.id.user_name);
        department = (TextView) findViewById(R.id.department);
        current_year = (TextView) findViewById(R.id.year);
        msg = (Button) findViewById(R.id.my_chat);
        notification = (Button) findViewById(R.id.mynotification);
        divider =  findViewById(R.id.divider);
        // badgenotification= (TextView) findViewById(R.id.badge_notification);
    }


    //Defines the navigation bar functions.
    public void initNavigationDrawer() {

        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id) {

                    case R.id.home:
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.chats:
                        Intent i = new Intent(getApplicationContext(), TabActivity.class);
                        startActivity(i);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.notification:
                        Intent i2 = new Intent(getApplicationContext(), Notification_Activity.class);
                        startActivity(i2);
                        break;
                    case R.id.feedback:
                        if (isNetworkAvailable()){

                            if (designation.equals("Teaching")) {

                                if (5 == month || 12 == month) {
                                    Intent i3 = new Intent(getApplicationContext(), FeedbackTeacher.class);
                                    startActivity(i3);


                                    drawerLayout.closeDrawers();
                                } else {
                                    drawerLayout.closeDrawers();

                                    Snackbar snackbar = Snackbar
                                            .make(coordinatorLayout, "Feedback Awaited...", Snackbar.LENGTH_LONG)
                                            .setAction("OK", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                }
                                            });


                                    // Changing action button text color
                                    snackbar.show();


                                }

                            }
                        }
                        else{
                            drawerLayout.closeDrawers();
                            Intent intent1 = new Intent(MainActivity.this,MainActivity.class);
                            startActivity(intent1);

                        }

                        if (designation.equals("Student")) {
                            if (batch == month) {

                                Intent i3 = new Intent(getApplicationContext(), Feedback.class);
                                startActivity(i3);

                                drawerLayout.closeDrawers();
                                Log.e("Batch is", "onNavigationItemSelected: " + batch);
                            } else {

                                drawerLayout.closeDrawers();
                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, "Feedback Forms are available only at the end of semester", Snackbar.LENGTH_LONG)
                                        .setAction("OK", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(MainActivity.this,MainActivity.class);
                                                startActivity(intent);
                                            }
                                        });


                                snackbar.show();


                            }


                        }
                        break;

                    case R.id.repository:
                        Intent i3 = new Intent(MainActivity.this,Repository.class);
                        startActivity(i3);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.about:
                        Intent i4 = new Intent(MainActivity.this,AboutActivity.class);
                        startActivity(i4);
                        drawerLayout.closeDrawers();
                        break;

                }
                return true;
            }
        });

        /////////////////////////////////////////////////////////////

        navigationView.setItemIconTintList(null);

        //Initialises Header of Navigation Bar.
        header = navigationView.getHeaderView(0);

        //Sets Image in the headerView
        profilePic = (ImageView)header.findViewById(R.id.profile_image);
        profilePic.setImageDrawable(getResources().getDrawable(R.drawable.ic_profile));
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Settings.class);
                startActivity(i);
            }
        });


        String previouslySetImage = prefs.getString("images","");

        if(!previouslySetImage.equalsIgnoreCase(""))
        {
            byte []b = Base64.decode(previouslySetImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b,0,b.length);
            image.setImageBitmap(bitmap);
            profilePic.setImageBitmap(bitmap);

        }


        //Intialises TextView of Navigation Bar header.
        TextView header_username = (TextView) header.findViewById(R.id.tv_email);
        header_username.setText(prefs.getString("NAME", ""));
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);      //Sets button for Naviagtion Bar on ActionBar.
        actionBarDrawerToggle.syncState();
    }


    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }


    public void getfbatch() {

        class MyTask extends AsyncTask<String, String, String> {

            @Override
            protected String doInBackground(String... params) {
                Log.e("Status", "doInBack");
                StringBuilder builder = new StringBuilder();
                String listFromServer="" ;

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(Config.RATING_URL + "/Project/getfbatch.php?batch=" + bat + "&registrationNo=" + regno);

                    Log.e("Status", "URL HIT");


                    HttpResponse response = httpClient.execute(httpPost);

                    HttpEntity entity = response.getEntity();
                    Log.e("Status", "ABout to Read");
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(entity.getContent()));

                    String valFromServer = null;

                    while ((valFromServer = buffer.readLine()) != null) {

                        builder.append(valFromServer);
                        Log.e("TAG", "doInBackground: " + builder.toString());
                    }
                    Log.e("DATA", builder.toString());
                    //in.close();


                } catch (ClientProtocolException e) {
                    Log.e("CPException", e.toString());
                    listFromServer="0";
                } catch (IOException e) {
                    Log.e("IOException", e.toString());
                    listFromServer="0";
                } catch (Exception e) {
                    Log.e("Excep", e.toString());
                    listFromServer="0";
                }

                JSONObject json_data = new JSONObject();
                try {
                    JSONArray jsonArray = new JSONArray(builder.toString());


                    for (int i = 0; i < jsonArray.length(); i++) {

                        json_data = jsonArray.getJSONObject(i);

                        listFromServer=(json_data.getString("FeedbackBatch")).toString();

                    }
                } catch (JSONException e) {

                    Log.e("JSONException", e.toString());
                    listFromServer="0";
                }
                Log.e("fbatch is", "doInBackground: "+listFromServer );
                fbatch = listFromServer;
                batch = Integer.parseInt(fbatch);
                Log.e("batchhh iss", "doInBackground: " + batch);
                Log.e("month is", "doInBackground: " + month);
                if (fbatch.equals("0")) {


                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Check your Internet Connection...", Snackbar.LENGTH_LONG)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            });


                    // Changing action button text color
                    snackbar.show();


                }
                else{


                    if (batch == month && designation.equals("Student")) {

                        try {
                            HttpClient httpClient1 = new DefaultHttpClient();
                            HttpPost httpPost1 = new HttpPost(Config.RATING_URL + "/Project/getfbatchnotification.php?registrationNo=" + regno);
                            Log.e("Status1", "URL HIT1");

                            HttpResponse response = httpClient1.execute(httpPost1);

                            HttpEntity entity = response.getEntity();
                            Log.e("Status", "ABout to Read");
                            BufferedReader buffer = new BufferedReader(new InputStreamReader(entity.getContent()));

                            String valFromServer = null;

                            while ((valFromServer = buffer.readLine()) != null) {

                                builder.append(valFromServer);
                                Log.e("TAG", "doInBackground: " + builder.toString());
                            }
                            Log.e("DATA", builder.toString());


                        } catch (Exception e) {
                            Log.e("Excep", e.toString());
                        }
                    }
                }


                if (5 == month || 12 == month) {
                    Log.e("inside if", "doInBackground: " + "insideiff");
                    if(designation.equals("Teaching")){

                        try {
                            HttpClient httpClient1 = new DefaultHttpClient();
                            HttpPost httpPost1 = new HttpPost(Config.RATING_URL + "/Project/fetchfeedback.php?registrationNo=" + regno);
                            Log.e("Status2", "URL HIT2");

                            HttpResponse response = httpClient1.execute(httpPost1);

                            HttpEntity entity = response.getEntity();
                            Log.e("Status", "ABout to Read");
                            BufferedReader buffer = new BufferedReader(new InputStreamReader(entity.getContent()));

                            String valFromServer = null;

                            while ((valFromServer = buffer.readLine()) != null) {

                                builder.append(valFromServer);
                                Log.e("TAG", "doInBackground: " + builder.toString());
                            }
                            Log.e("DATA", builder.toString());


                        } catch (Exception e) {
                            Log.e("Excep", e.toString());
                        }


                    }
                }

                if(6 == month || 1 == month){



                    Log.e("inside if", "doInBackground: " + "insideiff");
                    if(designation.equals("Teaching")){

                        try {
                            HttpClient httpClient1 = new DefaultHttpClient();
                            HttpPost httpPost1 = new HttpPost(Config.RATING_URL + "/Project/deletefeedbackteacher.php");
                            Log.e("Status2", "URL HIT2");

                            HttpResponse response = httpClient1.execute(httpPost1);

                            HttpEntity entity = response.getEntity();
                            Log.e("Status", "ABout to Read");
                            BufferedReader buffer = new BufferedReader(new InputStreamReader(entity.getContent()));

                            String valFromServer = null;

                            while ((valFromServer = buffer.readLine()) != null) {

                                builder.append(valFromServer);
                                Log.e("TAG", "doInBackground: " + builder.toString());
                            }
                            Log.e("DATA", builder.toString());


                        } catch (Exception e) {
                            Log.e("Excep", e.toString());
                        }


                    }

                }











                return listFromServer;
                //return "Task Completed...";
            }

            @Override
            protected void onPostExecute(final String s) {
                super.onPostExecute(s);

                // fbatch=s.get(0).toString();




            }
        }
        new MyTask().execute();
    }
}


