package muheeb.com.colcom20;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FacultyDisplayList extends AppCompatActivity
{
    private static final String TAG ="URL_EXCEPTION" ;
    ListView displayListView;
    ListAdapter listAdapter;
    private Toolbar toolbar;
    public static final String YEAR_ID = "year_id";
    private static final String SERVER_URL =Config.RATING_URL+"/Project/facultyList.php";
    String nameList="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_display_list);

//        final int year_id = getIntent().getIntExtra(YEAR_ID, 0);
        initialise();
        setSupportActionBar(toolbar);               //Adds toolBar to the top of Activity
        setTitle("Faculty");
        showList();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    //Called when activity starts. Displays list of registered student from Database.

    private void showList()
    {
        String course = getIntent().getStringExtra("department");                  //Accesses course from SharedPref.
        String designation = "Teaching";        //Accesses designation from SharedPref.
        String urlSuffix = "?course="+course;                        //String to be appended on Url.

        class showUserList extends AsyncTask<String, Void, String>
        {
            BufferedReader bufferedReader = null;
            ProgressDialog loading ;

            @Override
            protected void onPreExecute()
            {

                super.onPreExecute();
                loading = ProgressDialog.show(FacultyDisplayList.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s)
            {
                super.onPostExecute(s);
                loading.dismiss();
                final String faculty_contacts[] = s.split("#");

                ArrayList<PojoList> nameList = new ArrayList<>();

                for(String name: faculty_contacts)
                {
                    nameList.add(new PojoList(name));
                }


                listAdapter = new MyAdapter(FacultyDisplayList.this, R.layout.my_list3, nameList);
                displayListView.setAdapter(listAdapter);
                displayListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        Log.e("name fethced", faculty_contacts[0]);

                        if(faculty_contacts[0].trim().equals("No Students registered"))
                        {
                            //do nothing
                        }
                        else
                        {
                            PojoList obj = (PojoList) displayListView.getItemAtPosition(position);
                            String name = obj.getName();


                            Intent i = new Intent(FacultyDisplayList.this, MessageActivity.class);
                            i.putExtra("reciever", name);
                            startActivity(i);
//                            Intent i = new Intent(DisplayList.this, MessageActivity.class);
//                            startActivity(i);
                        }
                    }
                });


            }

            @Override
            protected String doInBackground(String... params)
            {
                String s = params[0];

                try
                {
                    Log.e(TAG, "urlSuffix" + s);
                    URL url = new URL(SERVER_URL + s);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    Log.e(TAG, "Connection Established" + url);

                    bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));      //Reads data sent from
                    nameList = bufferedReader.readLine();                                                   //database.

                    Log.e(TAG, "doInBackground: confirm_message : " + nameList);
                }

                catch(Exception e)
                {
                    Log.e(TAG, "Exception : DIB" + e.toString());
                }

                return nameList;
            }
        }
        new showUserList().execute(urlSuffix);                 //Starts Network Operations by calling doInBackground() function in Asynctask
    }


    //Called when Activity starts. Initialises variables.

    private void initialise()
    {
        toolbar = (Toolbar) findViewById(R.id.facultyListtoolbar);
        displayListView = (ListView) findViewById(R.id.facultydisplayList);

    }
}



