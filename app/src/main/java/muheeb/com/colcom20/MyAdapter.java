package muheeb.com.colcom20;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by muheeb on 27-Feb-17.
 */

public class MyAdapter extends ArrayAdapter<PojoList>
{
    Context context;
    int layoutResourceId;
    ArrayList<PojoList> data = null;

    public MyAdapter(Context context, int layoutResourceId, ArrayList<PojoList> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ServiceHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ServiceHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.text);

            row.setTag(holder);
        }
        else
        {
            holder = (ServiceHolder)row.getTag();
        }

        PojoList service = data.get(position);
        holder.txtTitle.setText(service.name);

        return row;
    }

    static class ServiceHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
    }
}


