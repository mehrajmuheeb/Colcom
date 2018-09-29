package muheeb.com.colcom20;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class StudentNotification extends AppCompatActivity {

    String duration;
    int stbatch;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_student_notification);

        toolbar = (Toolbar) findViewById(R.id.studentnotification);
        setSupportActionBar(toolbar);                       //Sets Toolbar
        setTitle("Post Notification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        duration = Registration.sharedPref.getString("COURSE_DURATION","").trim();
        stbatch = Integer.parseInt(duration);
        LinearLayout LL = (LinearLayout)findViewById(R.id.stnotification);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,40,10,5);
        for (int i=1; i<=stbatch; i++)
        {
            final int val = i;
            final Integer value=val;
            Button button = new Button(StudentNotification.this);
            button.setText("Year "+i);
            button.setBackground(getResources().getDrawable(R.drawable.button_raised));
            button.setPadding(20,40,20,40);
            button.setWidth(700);
            button.setLayoutParams(params);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i=new Intent(StudentNotification.this,FacultyStudentNotification.class);
                    Log.e("Value is ", "onClick: "+value.toString() );
                    Bundle bundle=new Bundle();
                    bundle.putString("value",value.toString());
                    i.putExtras(bundle);
                    startActivity(i);


                }
            });
            LL.addView(button);                                             //Adds buttons to the activity.

        }

    }
}
