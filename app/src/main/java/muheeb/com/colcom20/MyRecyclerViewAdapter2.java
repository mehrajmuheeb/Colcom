package muheeb.com.colcom20;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MyRecyclerViewAdapter2 extends RecyclerView
        .Adapter<MyRecyclerViewAdapter2
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<SentNotification> mDataset;

    private static MyClickListener myClickListener;
    public Activity activity;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        public TextView title,message,date,time,name,web;

        public DataObjectHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.cardname1);
            title= (TextView) itemView.findViewById(R.id.cardtitle1);
            message = (TextView) itemView.findViewById(R.id.cardmessage1);
            time= (TextView) itemView.findViewById(R.id.cardtime1);
            date= (TextView) itemView.findViewById(R.id.carddate1);
            web=(TextView) itemView.findViewById(R.id.cardweb1);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
           // myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public MyRecyclerViewAdapter2(ArrayList<SentNotification> myDataset, Activity a) {
        mDataset = myDataset;
        activity=a;

    }



    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_row2, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }


    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {
        holder.name.setText(mDataset.get(position).getName());
        holder.title.setText(mDataset.get(position).getTitle());
        holder.message.setText(mDataset.get(position).getMessage());
        holder.time.setText(mDataset.get(position).getMessagetime());
        holder.date.setText(mDataset.get(position).getMessagedate());
        holder.web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link= mDataset.get(position).getLink().toString();
                Log.e("on bind view holder", "onClick: "+link );
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://"+link));
                activity.startActivity(i);
            }
        });



    }

    public void addItem(SentNotification dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}