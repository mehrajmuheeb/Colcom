package muheeb.com.colcom20;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class FacultyListFragment extends android.support.v4.app.Fragment
{
    View view;
    ListView departmentListView;
    ListAdapter listAdapter;
    StringBuilder builder = new StringBuilder();
    Set<String> departmentlistFromServer = new HashSet<>();
    CoordinatorLayout coordinatorLayout;

    private static final String SERVER_URL =Config.RATING_URL+"/Project/courseList.php";

    public FacultyListFragment()
    {

    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_faculty_list_fragment, container, false);
        //coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.facultycoordinatorlayout);
        departmentListView = (ListView) view.findViewById(R.id.facultyList);


        return createCategory(view);
    }

    private View createCategory(View view)
    {
//        String course = Registration.sharedPref.getString("COURSE","").trim();                  //Accesses course from SharedPref.
//        String designation = Registration.sharedPref.getString("DESIGNATION","").trim();        //Accesses designation from SharedPref.
//        String urlSuffix = "?btn_no="+year_id+"&course="+course+"&designation="+designation;    //String to be appended on Url.
        class CourseList extends AsyncTask<String, String, Set>
        {
            BufferedReader bufferedReader = null;
            ProgressDialog loading ;


            @Override
            protected void onPreExecute()
            {

                super.onPreExecute();
                loading = ProgressDialog.show(getActivity(), "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(Set s)
            {
                super.onPostExecute(s);

                loading.dismiss();

                if (s.isEmpty()) {

                    Log.e("FacultyList :", "Data Not Present");

                }
                else {
                    Iterator<String> i = s.iterator();

                    final String course_list[] = new String[s.size()];
                    int x = 0;
                    while (i.hasNext()) {
                        course_list[x] = i.next();
                        x++;
                    }

                    ArrayList<PojoList> nameList = new ArrayList<>();

                    for (String name : course_list) {
                        nameList.add(new PojoList(name));
                    }

                    listAdapter = new MyAdapter(getActivity(), R.layout.my_list, nameList);
                    departmentListView.setAdapter(listAdapter);
                    departmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Log.e("name fethced", course_list[0]);
                            Toast.makeText(getActivity(), "Teacher Name  = " + course_list[0], Toast.LENGTH_SHORT).show();
                            if (course_list[0].trim().equals("No Students registered")) {
                                //do nothing
                            } else {
                                PojoList obj = (PojoList) departmentListView.getItemAtPosition(position);
                                String name = obj.getName();

                                Intent i = new Intent(getActivity(), FacultyDisplayList.class);
                                i.putExtra("department", name);
                                startActivity(i);
                            }
                        }
                    });

                }
            }

            @Override
            protected Set<String> doInBackground(String... params)
            {

                try
                {
                    URL url = new URL(SERVER_URL );
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    Log.e("Connection Established",""+url);

                    bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));      //Reads data sent from
                    String valFromServer = null;

                    while ((valFromServer = bufferedReader.readLine()) != null) {
                        builder.append(valFromServer);
                    }
                    Log.e("DATA", builder.toString());                                                  //database.

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

                        departmentlistFromServer.add(json_data.getString("Department_Name"));

                    }
                } catch (JSONException e) {
                    Log.e("JSONException", e.toString());
                }
                return departmentlistFromServer;

            }
        }
        new CourseList().execute();
        return  view;
    }
}
