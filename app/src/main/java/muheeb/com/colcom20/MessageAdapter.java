package muheeb.com.colcom20;

/**
 * Created by muheeb on 22-Feb-17.
 */

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<DataSet> {

    private Activity activity;
    private List<DataSet> messages;

    public MessageAdapter(Activity context, int resource, List<DataSet> objects) {
        super(context, resource, objects);
        this.activity = context;
        this.messages = objects;
    }
    ////////////////////////////////////////////////////////////////////////
    @Override
    public int getCount() {
        if (messages != null) {
            return messages.size();
        } else {
            return 0;
        }
    }

    @Override
    public DataSet getItem(int position) {
        if (messages != null) {
            return messages.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }




////////////////////////////////////////////////////////////////////////////

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        DataSet message = getItem(position);
        LayoutInflater vi = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);


        if (convertView == null) {
            convertView = vi.inflate(R.layout.list_item_chat_message, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        boolean myMsg = message.isMine() ;//Just a dummy check
        //to simulate whether it is me or other sender
        setAlignment(holder, myMsg);
        holder.txtMessage.setText(message.getMessage());
        holder.txtInfo.setText(message.getDate());

        return convertView;
    }

    public void add(DataSet message) {
        messages.add(message);
    }

    public void add(List<DataSet> messages) {
        messages.addAll(messages);
    }

    private void setAlignment(ViewHolder holder, boolean isMe) {
        if (!isMe) {
            holder.contentWithBG.setBackgroundResource(R.drawable.in_message_bg);

            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtMessage.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtInfo.setLayoutParams(layoutParams);
        } else {
            holder.contentWithBG.setBackgroundResource(R.drawable.out_message_bg);

            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.content.setLayoutParams(lp);

            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtMessage.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtInfo.setLayoutParams(layoutParams);
        }
    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.txtMessage = (TextView) v.findViewById(R.id.txtMessage);
        holder.txtInfo = (TextView) v.findViewById(R.id.txtInfo);
        holder.content = (LinearLayout) v.findViewById(R.id.content);
        holder.contentWithBG = (LinearLayout) v.findViewById(R.id.contentWithBackground);
//        holder.txtInfo = (TextView) v.findViewById(R.id.txtInfo);
        return holder;
    }

    private static class ViewHolder {
        public TextView txtMessage;
        public TextView txtInfo;
        public LinearLayout content;
        public LinearLayout contentWithBG;
    }
}
////////////////////////////////////////////////////////////////////////////////////////
//        int layoutResource = 0; // determined by view type
//        DataSet chatMessage = getItem(position);
//        int viewType = getItemViewType(position);
//
//        if (chatMessage.isMine()) {
//            layoutResource = R.layout.item_chat_right;
//        } else {
//            layoutResource = R.layout.item_chat_left;
//        }
//
//        if (convertView != null) {
//            holder = (ViewHolder) convertView.getTag();
//        } else {
//            convertView = inflater.inflate(layoutResource, parent, false);
//            holder = new ViewHolder(convertView);
//            convertView.setTag(holder);
//        }
//
//        //set message content
//        holder.msg.setText(chatMessage.getMessage());
//
//        return convertView;
//    }

//    @Override
//    public int getViewTypeCount() {
//        // return the total number of view types. this value should never change
//        // at runtime
//        return 2;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        // return a value between 0 and (getViewTypeCount - 1)
//        return position % 2;
//    }
//
//    private class ViewHolder {
//        private TextView msg;
//
//        public ViewHolder(View v) {
//            msg = (TextView) v.findViewById(R.id.txt_msg);
//        }
//    }
//}
