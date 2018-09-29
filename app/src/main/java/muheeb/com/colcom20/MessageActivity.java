package muheeb.com.colcom20;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {

    EditText message;
    Button sendBtn;
    ListView list;
    private DatabaseReference senderDatabaseReference, root;
    private MessageAdapter adapter;
    private ArrayList<DataSet> msg_list = new ArrayList<>();
    private ArrayList<String> sender_list = new ArrayList<>();
    private ArrayList<DataSet> newList = new ArrayList<>();
    ArrayList<DataSet> mergedList = new ArrayList<>();
    Toolbar toolbar;
    SharedPreferences sharedPref2;
    String msg, tempKey, date;
    String name;                                                   //sender's name
    String sender_name;
    int Flag = 1;
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    final Calendar cal = Calendar.getInstance();
    boolean isMine = true;
    String toName;
    String []dummyList = {""};
    private  static final String MESSAGE_URL = Config.RATING_URL +"Project/messageNotification.php";
    String urlSuffix;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        //name= OTPConfirm.sharedPref2.getString("NAME","");
        sharedPref2 = getSharedPreferences(Registration.sharedFileName, MODE_PRIVATE);
        name = sharedPref2.getString("NAME","");
        sender_name = name.replace(" ", "_");
        boolean flag = false;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);                       //Sets Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        newList.add(new DataSet("", "", "", false));
        message = (EditText) findViewById(R.id.msgTxt);
        sendBtn = (Button) findViewById(R.id.sendBtn);
        list = (ListView) findViewById(R.id.lists);

        // Log.e("INTENT EXTRA STRING", getIntent().getStringExtra("data"));

        //Log.e("INTENT", getIntent().getExtras().toString());

        Bundle bundle=getIntent().getExtras();
        /////////////////////////////////////////////////////////////////
        if(bundle!=null)
        {
            Log.e("STATUS", "NOT NULL");
            for (String key : getIntent().getExtras().keySet())
            {
                Log.e("KEY", key);
                if(key.equals("sender"))
                {
                    String value = getIntent().getExtras().getString(key);
                    Log.e("VALUE HERE", value);
                    toName = value;
                    flag = true;
//                    try {
//                        JSONObject json = new JSONObject(value);
//                        toName = json.getString("data");
//                        Log.e("STATUS", "WE GOT IT..." + toName);
//                        flag = true;
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        }



        /////////////////////////////////////////////////////////////////

        if(!flag) {
            toName = getIntent().getExtras().getString("reciever");//"Muheeb Mehraj";
        }
        setTitle(toName);






        final String reciever= toName.replace(" ", "_");



        root = FirebaseDatabase.getInstance().getReference();

        sendBtn.setEnabled(false);

        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator i = dataSnapshot.getChildren().iterator();
                int x=0;
                while (i.hasNext())
                {
                    x++;
                    sender_list.add(((DataSnapshot) i.next()).getKey());
                    Log.e("value of x", ""+x);
                }
                Log.e("root nodes", sender_list.toString());
                int size = sender_list.size();
                HelpingClass[]obj = new HelpingClass[size];
                int count = 0;
                for (String search : sender_list)
                {

                    Log.e("searchList Inside: ", sender_list.toString());
                    String list[] = search.split("-");
                    Log.e("List Values", ""+list[0]+"  "+list[1]);
                    obj[count] = new HelpingClass(list[0], list[1]);
                    count++;
                }

                Log.e("Object Length", ""+obj.length);

                int j = 0;
                while(j<obj.length)
                {
                    if((obj[j].getSender().equals(sender_name)&&obj[j].getReciever().equals(reciever))||(obj[j].getSender().equals(reciever)&&obj[j].getReciever().equals(sender_name)))
                    {
                        Flag = 0;
                        Log.e("Value of J = ",""+j );
                        break;
                    }
                    j++;
                }

                if(Flag == 0)
                {
                    Log.e("Value of J in IF= ",""+j );
                    String sender= obj[j].getSender();
                    String recipient = obj[j].getReciever();
                    Log.e("Names", "Sender"+obj[j].getSender()+"Reciever"+obj[j].getReciever());
                    senderDatabaseReference = FirebaseDatabase.getInstance().getReference().child(sender+"-"+recipient);
                    sendBtn.setEnabled(true);
                    getSenderRecieverMsg();


                }
                else
                {
                    senderDatabaseReference = FirebaseDatabase.getInstance().getReference().child(sender_name+"-"+reciever);
                    sendBtn.setEnabled(true);
                    getSenderRecieverMsg();
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //Sends message to reciever
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> map = new HashMap<String, Object>();
                tempKey = senderDatabaseReference.push().getKey();
                Log.e("database reference ",""+senderDatabaseReference );
                senderDatabaseReference.updateChildren(map);
                final DatabaseReference message_ref = senderDatabaseReference.child(tempKey);
                final Map<String, Object> map2 = new HashMap<String, Object>();
                map2.put("sender", sender_name);
                map2.put("message", message.getText().toString());
                map2.put("date", dateFormat.format(cal.getTime()));
                Log.e("onClick: ", ""+"prints before passing control" + Thread.currentThread().getName());


                message_ref.updateChildren(map2);
                message.getText().clear();
                sendMessageNotification();
                urlSuffix="";
            }
        });



    }


    //Retrives messages: Sender to reciever
    private void getSenderRecieverMsg()
    {

        senderDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Boolean isMine = true;
                Log.e("getSenderReceiverMsg: ", ""+senderDatabaseReference );
                Iterator i = dataSnapshot.getChildren().iterator();
                msg_list.clear();
                int counter = 0;
                String sender;
                while (i.hasNext())
                {

                    date = (String) ((DataSnapshot) i.next()).getValue();
                    msg = (String) ((DataSnapshot) i.next()).getValue();
                    sender = (String) ((DataSnapshot) i.next()).getValue();
                    String newDate = "";

//                    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        Date date1 = dateFormat.parse(date);
                        SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm a");
                        newDate = timeFormatter.format(date1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    Log.e("New Date", newDate);

                    if(sender.equals(sender_name))
                    {
                        isMine = true;
                        Log.e("onChildAdded: ","NAMES COMPARE" );
                    }
                    else
                    {
                        isMine = false;
                        Log.e("onChildAdded: ","NAMES DONT COMPARE" );
                    }


                    msg_list.add(new DataSet(sender, msg, newDate ,isMine));

                    counter++;
                }
                Log.e("Get Sender Reciver msg", "sender-reciever msg"+ msg_list.toString());
                Log.e("Get Sender Reciver msg", "msgList size"+ msg_list.size());
                Log.e("Get Sender Reciver msg", "counter value"+counter);

                mergeLists();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void sendMessageNotification()
    {
        String title = "You have a new message from";
//        String message = "";
        urlSuffix ="?title="+title+"&receiver="+toName+"&sender="+name;
        class sendMessageNotif extends AsyncTask<String, Void, String>
        {



            @Override
            protected String doInBackground(String... params)
            {
                String s = params[0];


                Log.e("s is", "doInBackground: "+s );
                BufferedReader bufferedReader = null;
                String confirm_message = "";
                try
                {
                    s=s.replaceAll(" ", "%20");
                    URL url = new URL(MESSAGE_URL +s);
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

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s)
            {
                super.onPostExecute(s);
              //  Toast.makeText(MessageActivity.this,s.toString(),Toast.LENGTH_LONG).show();
            }
        }
        new sendMessageNotif().execute(urlSuffix);

    }


    private void mergeLists()
    {
        newList.clear();

        Log.e("newList Before: ", ""+newList.toString()+"   "+newList.size());

        newList = new ArrayList<>(msg_list);                                    //Merges messages from sender and reciever

        Log.e("newList After ",""+newList.size()+"   "+newList.size() );

        Log.e( "mergeLists: ",""+ msg_list.size());

        adapter = new MessageAdapter(this, R.layout.item_chat_left, mergedList);
        list.setAdapter(adapter);


        ArrayList<DataSet> tempList = new ArrayList<>(newList);

        Log.e("tempLists: " ,""+tempList.toString());


        Comparator<DataSet> byDate = new Comparator<DataSet>() {
            @Override
            public int compare(DataSet lhs, DataSet rhs) {

                try {
                    return dateFormat.parse(lhs.getDate()).compareTo(dateFormat.parse(rhs.getDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        };

        Collections.sort(tempList, byDate);

        for (DataSet data : newList) {
            Log.e("Name: ", data.getName() + data.getMessage() + data.getDate());
        }

        mergedList.addAll(newList);
        if(mergedList.get(0).getName().equals(name))
            Log.e("merged_list ", mergedList.toString());
        adapter.notifyDataSetChanged();
    }
}

