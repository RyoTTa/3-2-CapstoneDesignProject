package com.example.share.Chatting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.share.R;

import java.util.ArrayList;


public class ChattingAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<ChatlistItem> mItems;
    private static final int CHAT_TYPE_OHTER = 0;
    private static final int CHAT_TYPE_MY = 1;
    private static final int CHAT_TYPE_MAX = 2;

    public int getViewTypeCount(){
        return CHAT_TYPE_MAX;
    }

    public int getItemViewType(int position){
        return mItems.get(position).getType();
    }

    public ChattingAdapter(Context context, ArrayList<ChatlistItem> mItems) {
        this.mItems = mItems;
        this.inflater = (LayoutInflater)context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return mItems.size();
    }


    public ChatlistItem getItem(int position) {
        return mItems.get(position);
    }


    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        Context context = parent.getContext();
        int viewType = getItemViewType(position);

        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //convertView = inflater.inflate(R.layout.chatting_item, parent, false);
            ChatlistItem myItem = getItem(position);
            //viewType = 1;

            switch(viewType){
                case CHAT_TYPE_OHTER:
                    convertView = inflater.inflate(R.layout.chatting_item, parent, false);
                    TextView tv_name = (TextView) convertView.findViewById(R.id.chat_name) ;
                    TextView tv_contents = (TextView) convertView.findViewById(R.id.chat_content);
                    tv_name.setText(myItem.getUserName());
                    tv_contents.setText(myItem.getMessage());
                    break;
                case CHAT_TYPE_MY:
                    convertView = inflater.inflate(R.layout.chatting_item2, parent, false);
                    TextView tv_contents2 = (TextView) convertView.findViewById(R.id.chat_content2);
                    tv_contents2.setText(myItem.getMessage());
                    break;

            }

        }


        /* (위젯에 대한 이벤트리스너를 지정하고 싶다면 여기에 작성하면된다..)  */


        return convertView;
    }

    /* 아이템 데이터 추가를 위한 함수. 자신이 원하는대로 작성 */

    public void addItem(String name,String contents){
        mItems.add(new ChatlistItem(name,contents));
    }
    public void addItem(int type,String name,String contents) {

        /* MyItem에 아이템을 setting한다. */
        mItems.add(new ChatlistItem(type,name,contents));

        // mItems.add(new ChatlistItem(name,contents));
        /* mItems에 MyItem을 추가한다. */
    }




}