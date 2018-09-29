package muheeb.com.colcom20;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomList extends ArrayAdapter<String>{

    private final Activity context;
   public List name;
    public CustomList(Activity context, List name) {
        super(context, R.layout.list_single, name);
        this.context = context;
        this.name =name;


    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        imageView.setImageResource(R.drawable.ic_adobe);
        txtTitle.setText(name.get(position).toString());


        return rowView;
    }
}
