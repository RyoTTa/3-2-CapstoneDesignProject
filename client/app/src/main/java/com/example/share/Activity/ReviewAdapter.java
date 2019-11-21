package com.example.share.Activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.share.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ReviewAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<ReviewItem> mItems;

    private TextView reviewer;
    private RatingBar star;
    private TextView content;

    public ReviewAdapter(){}

    public ReviewAdapter(Context context, ArrayList<ReviewItem> mItems){
        this.mItems = mItems;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public ReviewItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.review_item,parent, false);

        }
        ReviewItem myItem = getItem(position);
        reviewer = (TextView)convertView.findViewById(R.id.rev_user_name);
        star = (RatingBar)convertView.findViewById(R.id.rev_user_star);
        content = (TextView)convertView.findViewById(R.id.rev_content);

        reviewer.setText(myItem.getReviewer());
        star.setRating(myItem.getStar());
        content.setText(myItem.getContent());
        return convertView;
    }

    public void add(String reviewer,int star, String content){
        ReviewItem reviewitem = new ReviewItem(reviewer,star,content);
        mItems.add(reviewitem);
    }
}
