package com.example.share.Activity;

import android.content.Intent;
import android.media.Image;
import android.media.Rating;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.share.Data.Item;
import com.example.share.MongoDB.FromServerImage;
import com.example.share.R;

import org.w3c.dom.Text;

public class ReservationInfoActivity extends AppCompatActivity {

    private ImageView item_photo;
    private TextView item_title;
    private TextView item_date;
    private TextView item_price;
    private TextView item_content;
    private TextView item_location;
    private TextView item_category;
    private TextView user_name;
    private RatingBar user_star;
    private TextView user_reviewnum;
    private FromServerImage newImage = new FromServerImage();

    private ImageView pay;
    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_info);

        item_photo = (ImageView)findViewById(R.id.item_photo);
        item_title = (TextView)findViewById(R.id.item_title);
        item_date = (TextView)findViewById(R.id.item_reservation_date);
        item_price =(TextView)findViewById(R.id.item_reservation_price);
        item_content = (TextView)findViewById(R.id.item_content);
        item_location = (TextView)findViewById(R.id.location);
        item_category = (TextView)findViewById(R.id.category);
        user_name = (TextView)findViewById(R.id.user_name);
        user_star = (RatingBar)findViewById(R.id.revinfo_star);
        user_reviewnum = (TextView)findViewById(R.id.review_number);

        Intent intent = getIntent();
        //get all the data passed
        item = (Item)intent.getSerializableExtra("item_object");
        item_photo.setImageBitmap(newImage.getImage(item.getFilepath()));
        item_title.setText(item.getItem_name());
        //item_date.setText(item.get
        pay = (ImageView)findViewById(R.id.pay);

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),PayActivity.class);
                intent.putExtra("item_object",item);
                startActivity(intent);
            }
        });
    }
}
