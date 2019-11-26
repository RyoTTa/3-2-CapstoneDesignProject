package com.example.share.Activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.media.Image;
import android.media.Rating;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.share.Data.Item;
import com.example.share.MongoDB.FromServerImage;
import com.example.share.R;
import com.google.android.gms.maps.model.LatLng;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import org.bson.types.ObjectId;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private TextView date;
    private FromServerImage newImage = new FromServerImage();

    private ImageView pay;
    private ImageView complete;
    private Item item;

    private String start_date;
    private String end_date;

    private final int GET_DATE_INFO = 300;

    //mongoDB
    private String MongoDB_IP = "15.164.51.129";
    private int MongoDB_PORT = 27017;
    private String DB_NAME = "local";
    private String ITEM_COLLECTION = "items";
    private String USER_COLLECTION= "users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_info);

        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");

        item_photo = (ImageView)findViewById(R.id.item_photo);
        item_title = (TextView)findViewById(R.id.item_title);
        date = (TextView) findViewById(R.id.reservation_date);
        item_price =(TextView)findViewById(R.id.item_reservation_price);
        item_content = (TextView)findViewById(R.id.item_content);
        item_location = (TextView)findViewById(R.id.location);
        item_category = (TextView)findViewById(R.id.category);
        complete = (ImageView)findViewById(R.id.complete);
        user_name = (TextView)findViewById(R.id.user_name);
        user_star = (RatingBar)findViewById(R.id.revinfo_star);
        user_reviewnum = (TextView)findViewById(R.id.review_number);
        pay = (ImageView)findViewById(R.id.pay);

        Intent intent = getIntent();
        //get all the data passed
        item = (Item)intent.getSerializableExtra("item_object");

        String type = intent.getStringExtra("type");
        if(type.equals("reservation_info")){
            //compelte btn 안보여야함
            complete.setVisibility(View.GONE);

        }else if(type.equals("borrow_detail")){
            //pay btn 안보이고 위에 타이틀 안보여야함
            item_title.setVisibility(View.GONE);
            pay.setVisibility(View.GONE);
        }

        item_photo.setImageBitmap(newImage.getImage(item.getFilepath()));
        item_title.setText(item.getItem_name());
        //item_price =
        item_content.setText(item.getContent());
        LatLng add = new LatLng(item.getLatitude(), item.getLongitude());
        String set_address = getCurrentAddress(add);
        item_location.setText(set_address);
        item_category.setText(item.getCategory());

        //Connect to MongoDB
        MongoClient mongoClient = new MongoClient(new ServerAddress(MongoDB_IP, MongoDB_PORT)); // failed here?
        DB db = mongoClient.getDB(DB_NAME);
        DBCollection collection = db.getCollection( USER_COLLECTION);

        /** Qurey 2: get Owner Info */

        BasicDBObject query = new BasicDBObject();
        query.put("email", item.getOwner_email());
        DBObject dbObj = collection.findOne(query);
        String name = dbObj.get("name").toString();
        float star_save = Float.parseFloat(dbObj.get("star_save").toString());
        float star_count = Float.parseFloat(dbObj.get("star_count").toString());
        user_name.setText(name);
        float setstar = star_save/star_count;
        user_star.setRating(setstar);
        user_reviewnum.setText("("+Integer.toString((int)star_count)+")");

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),PayActivity.class);
                intent.putExtra("item_object",item);
                intent.putExtra("owner_name",name);
                startActivity(intent);
            }
        });

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RegisterReviewActivity.class);
                intent.putExtra("reviewee",item.getOwner_email());
                intent.putExtra("item_id",item.getItem_id());
                startActivity(intent);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_DATE_INFO && data !=null) {

            String selectdate = "   " + data.getStringExtra("date");
            start_date = data.getStringExtra("startdate");
            end_date = data.getStringExtra("enddate");
            SimpleDateFormat fm = new SimpleDateFormat(("yyyy-MM-dd"));
            try {
                Date start_temp = fm.parse(start_date);
                Date end_temp = fm.parse(end_date);

                if(start_temp.compareTo(item.getAvailableFrom()) == 0){

                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.d("날짜",start_date + end_date);
            date.setText(selectdate);
        }
    }

    public void selectDate(View v)
    {
        Intent intent = new Intent(getApplicationContext(),SelectDateActivity.class);
        startActivityForResult(intent,GET_DATE_INFO);
    }

    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }
}
