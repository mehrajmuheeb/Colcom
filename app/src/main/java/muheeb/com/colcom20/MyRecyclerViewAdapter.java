package muheeb.com.colcom20;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView
        .Adapter<MyRecyclerViewAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<Notification> mDataset;

    private static MyClickListener myClickListener;
    public Activity activity;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        public TextView title,message,date,time,name,web;

        public DataObjectHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.cardname);
            title= (TextView) itemView.findViewById(R.id.cardtitle);
            message = (TextView) itemView.findViewById(R.id.cardmessage);
            time= (TextView) itemView.findViewById(R.id.cardtime);
            date= (TextView) itemView.findViewById(R.id.carddate);
            web=(TextView) itemView.findViewById(R.id.cardweb);

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

    public MyRecyclerViewAdapter(ArrayList<Notification> myDataset, Activity a) {
        mDataset = myDataset;
        activity=a;

    }



    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_row, parent, false);

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

//        holder.p.setText(mDataset1.get(position).getPunctuality());
//        holder.cs.setText(mDataset1.get(position).getCommunicationSkills());
//        holder.tm.setText(mDataset1.get(position).getTeachingMethodology());
//        holder.b.setText(mDataset1.get(position).getBehaviour());
//        holder.pr.setText(mDataset1.get(position).getPresentation());
//        holder.ha.setText(mDataset1.get(position).getHelpingAttitude());


    }

    public void addItem(Notification dataObj, int index) {
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