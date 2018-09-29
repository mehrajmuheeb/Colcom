package muheeb.com.colcom20;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class CurrentChatList extends Fragment {
    View view;
    private DatabaseReference rootNode;
    ArrayList<String> sender_list = new ArrayList<>();
    String name= OTPConfirm.sharedPref2.getString("NAME","");                                                   //sender's name
    final String sender_name= name.replace(" ", "_");
    private ArrayList<String> currentChatList = new ArrayList<>();
    private String []currentNameList;
    ListView list;
    ArrayAdapter adapter;
    ArrayList<PojoList> nameList = new ArrayList<>();
    public CurrentChatList() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("onPause: ", "Called" );
        if (!nameList.isEmpty() && !sender_list.isEmpty() && !currentChatList.isEmpty()) {
        }
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.e("onCreateView: ", "Called" );

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_current_chat_list, container, false);
        rootNode = FirebaseDatabase.getInstance().getReference();
        list = (ListView)view.findViewById(R.id.currentChatList);


        Log.e("onCreateView: ", rootNode.toString() );

        rootNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nameList.clear();
                sender_list.clear();
                currentChatList.clear();
                currentNameList = null;
                Iterator i = dataSnapshot.getChildren().iterator();
                int x = 0;

                if (i == null)
                {
                    Log.e("onDataChange: ", "Iterator is NULL" );
                }
                else
                {
                    while (i.hasNext()) {
                        x++;
                        sender_list.add(((DataSnapshot) i.next()).getKey());
                        Log.e("value of x", "" + x);
                    }
                    Log.e("root nodes", sender_list.toString());
                    int count = 0;
                    for (String search : sender_list) {

                        Log.e("searchList Inside: ", sender_list.toString());
                        String list[] = search.split("-");
                        Log.e("List Values", "" + list[0] + "  " + list[1]);

                        if (list[0].equals(sender_name)) {
                            currentChatList.add(list[1]);
                            Log.e("onDataChange: ", currentChatList.toString());
                        } else if (list[1].equals(sender_name)) {
                            currentChatList.add(list[0]);
                            Log.e("onDataChange: ", currentChatList.toString());
                        } else {

                        }
                        count++;
                    }

                    currentNameList = new String[currentChatList.size()];
                    int count2 = 0;
                    for (String s : currentChatList) {

                        currentNameList[count2] = s.replaceAll("_", " ");
                        count2++;
                    }

//                    nameList = new ArrayList<>();

                    for (String name : currentNameList) {
                        nameList.add(new PojoList(name));

                    }
                    Log.e("onDataChange: ", nameList.toString() );

                    if (nameList.isEmpty())
                    {
                        Log.e( "onDataChange: ", "NameList empty" );
                    }
                    else {

                        adapter = new MyAdapter(getContext(), R.layout.my_list2, nameList);

                        list.setAdapter(adapter);


                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                moveToMessages(position);
                            }
                        });

                        Log.e("onDataChange: ", currentChatList.toString());
                    }
                }
            }
                @Override
                public void onCancelled (DatabaseError databaseError){

                }

        });





        return view;


    }

    private void moveToMessages(int position)
    {

        PojoList obj = (PojoList) list.getItemAtPosition(position);
        String name = obj.getName();
        Intent i = new Intent(getContext(), MessageActivity.class);
        i.putExtra("reciever", name);
        startActivity(i);
    }
}
