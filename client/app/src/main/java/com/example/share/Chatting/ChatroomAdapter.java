package com.example.share.Chatting;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.share.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ChatroomAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<ChatroomlistItem> mItems;

    private TextView name;
    private TextView content;

    public ChatroomAdapter(){}
    public ChatroomAdapter(Context context, ArrayList<ChatroomlistItem> mItems){
        this.mItems = mItems;
        this.inflater = (LayoutInflater)context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public ChatroomlistItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();

        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.chatlist_item, parent, false);
        }
        ChatroomlistItem myItem = getItem(position);
        name = (TextView)convertView.findViewById(R.id.chatlist_username);
        content = (TextView)convertView.findViewById(R.id.chatlist_content);

        name.setText(myItem.getRoomName());
        content.setText(myItem.getMessage());

        return convertView;
    }

    public void add(String name, String content) {

        Log.d("jihye",mItems+"");
        ChatroomlistItem chatroomlistItem= new ChatroomlistItem(name,content);
        Log.d("jihye",chatroomlistItem+"");
        mItems.add(chatroomlistItem);
    }
}