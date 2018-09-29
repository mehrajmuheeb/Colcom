package muheeb.com.colcom20;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andleeb on 27/2/17.
 */

public  class MyAdapter2 extends BaseAdapter {
    private final List<Item> mItems = new ArrayList<Item>();
    private final LayoutInflater mInflater;

    String duration = Registration.sharedPref.getString("COURSE_DURATION","").trim();       //Accesing COURSE_DURATION from sharedPrefence.
    int count = Integer.parseInt(duration);

//    int count = 3;
    int imageArray[] = {R.drawable.grid_red, R.drawable.grid_cyan, R.drawable.grid_green, R.drawable.grid_magenta, R.drawable.grid_purple, R.drawable.grid_orange};
    //String []yearArray = {"Year 1","Year 2", "Year 3","Year 4","Year 5","Year 6"};

    public MyAdapter2(Context context) {
        mInflater = LayoutInflater.from(context);

        for(int i = 0; i<count; i++) {
            mItems.add(new Item(i+1, imageArray[i]));

        }

    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Item getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mItems.get(i).drawableId;
    }



    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        ImageView picture;
        TextView name;

        if (v == null) {
            v = mInflater.inflate(R.layout.grid_item, viewGroup, false);
            v.setTag(R.id.picture, v.findViewById(R.id.picture));
            v.setTag(R.id.text, v.findViewById(R.id.text));
        }

        picture = (ImageView) v.getTag(R.id.picture);
        name = (TextView) v.getTag(R.id.text);

        Item item = getItem(i);

        picture.setImageResource(item.drawableId);
        name.setText("Year "+item.year);

        return v;
    }

    public static class Item {
      public final int year;
        public final int drawableId;

        Item(int name, int drawableId) {
        this.year = name;
            this.drawableId = drawableId;
        }

        public int getYear() {
            return year;
        }

        public int getDrawableId() {
            return drawableId;
        }
    }
}