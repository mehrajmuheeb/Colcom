package muheeb.com.colcom20;

/**
 * Created by Andleeb on 27/2/17.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

public class StudentListFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "TemporaryTag";
    int btnNo;
    int flag;
    View view;
    Button button;
    public static final String YearValue = "yearValue";

    public StudentListFragment()
    {

    }

    // newInstance constructor for creating fragment with arguments
    public static StudentListFragment newInstance(int page) {
        StudentListFragment fragmentFirst = new StudentListFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String duration = Registration.sharedPref.getString("COURSE_DURATION","").trim();       //Accesing COURSE_DURATION from sharedPrefence.
        flag = 0;
        btnNo = Integer.parseInt(duration);                                         //Converting string duration into Integer value.
    }


    //Creates View for Student Fragment
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_student_list_fragment, container, false);

        return createbutton(view);
    }


    //Called when Fragment is created. Creates dynamic buttons based on Course_Duration

    private View createbutton(View v) {

        final GridView gridView = (GridView)v.findViewById(R.id.gridview);
        gridView.setAdapter(new MyAdapter2(getContext()));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MyAdapter2.Item item = (MyAdapter2.Item) gridView.getItemAtPosition(position);
                int year =  item.getYear();

                Intent i = new Intent(getContext(), DisplayList.class);
                i.putExtra(DisplayList.YEAR_ID, year);
                startActivity(i);

                Log.e("onItemClick: ", ""+year );
            }
        });
        return v;
    }
}
