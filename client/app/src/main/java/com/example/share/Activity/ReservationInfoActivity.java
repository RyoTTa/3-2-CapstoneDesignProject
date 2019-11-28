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
    String name="test";

    private final int GET_DATE_INFO = 300;

    //mongoDB
    private String MongoDB_IP = "15.164.51.129";
    private int MongoDB_PORT = 27017;
    private String DB_NAME = "local";
    private String USER_COLLECTION= "users";
    private String RESV_COLLECTION= "reservations";

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

        //Connect to MongoDB
        MongoClient mongoClient = new MongoClient(new ServerAddress(MongoDB_IP, MongoDB_PORT)); // failed here?
        DB db = mongoClient.getDB(DB_NAME);
        DBCollection collection = db.getCollection(USER_COLLECTION);

        Intent intent = getIntent();
        //get all the data passed
        item = (Item)intent.getSerializableExtra("item_object");

        String type = intent.getStringExtra("type");
        if(type.equals("reservation_info")){
            //compelte btn 안보여야함
            complete.setVisibility(View.GONE);

        }else if(type.equals("borrow_detail")){
            //pay btn 안보이고 위에 타이틀 안보여야함
            pay.setVisibility(View.GONE);
            date.setClickable(false);
            collection = db.getCollection(RESV_COLLECTION);
            BasicDBObject resv_query = new BasicDBObject();
            resv_query.put("item_id",item.getItem_id());
            DBObject resv_dbObj = collection.findOne(resv_query);
            Date r_dateS = (Date)resv_dbObj.get("date_start");
            Date r_dateE = (Date)resv_dbObj.get("date_end");
            SimpleDateFormat fm = new SimpleDateFormat(("yyyy-MM-dd"));

            String start = fm.format(r_dateS);
            String end = fm.format(r_dateE);
            date.setText(start+"~"+end);
            long diffDay = ((r_dateE.getTime() - r_dateS.getTime()) / (24*60*60*1000) + 1)  * Integer.decode(item.getItem_price_per_day());
            item_price.setText(diffDay + "원");
        }else if(type.equals("complete_detail")){
            complete.setVisibility(View.GONE);
            pay.setVisibility(View.GONE);
            date.setClickable(false);

            collection = db.getCollection(RESV_COLLECTION);
            BasicDBObject resv_query = new BasicDBObject();
            resv_query.put("item_id",item.getItem_id());
            DBObject resv_dbObj = collection.findOne(resv_query);
            Date r_dateS = (Date)resv_dbObj.get("date_start");
            Date r_dateE = (Date)resv_dbObj.get("date_end");
            SimpleDateFormat fm = new SimpleDateFormat(("yyyy-MM-dd"));

            String start = fm.format(r_dateS);
            String end = fm.format(r_dateE);
            date.setText(start+"~"+end);
            long diffDay = ((r_dateE.getTime() - r_dateS.getTime()) / (24*60*60*1000) + 1)  * Integer.decode(item.getItem_price_per_day());
            item_price.setText(diffDay + "원");

            TextView member_title =(TextView)findViewById(R.id.member_title);
            member_title.setText("대여자 정보");
        }

        item_photo.setImageBitmap(newImage.getImage(item.getFilepath()));
        item_title.setText(item.getItem_name());

        item_content.setText(item.getContent());
        LatLng add = new LatLng(item.getLatitude(), item.getLongitude());
        String set_address = getCurrentAddress(add);
        item_location.setText(set_address);
        item_category.setText(item.getCategory());

        collection = db.getCollection(USER_COLLECTION);

        /** Qurey 2: get Owner Info */

        if(type.equals("complete_detail")) {
            MongoClient mongoClient2 = new MongoClient(new ServerAddress(MongoDB_IP, MongoDB_PORT)); // failed here?
            DB db2 = mongoClient2.getDB(DB_NAME);
            DBCollection collection2 = db2.getCollection(RESV_COLLECTION);

            //Check Data in Database with query
            BasicDBObject query2 = new BasicDBObject();
            query2.put("item_id",item.getItem_id());
            DBObject resv_dbObj = collection2.findOne(query2);

            String borrower_email = resv_dbObj.get("borrower_email").toString();

            BasicDBObject query = new BasicDBObject();
            query.put("email", borrower_email);
            DBObject dbObj = collection.findOne(query);
            name = dbObj.get("name").toString();
            float star_save = Float.parseFloat(dbObj.get("star_save").toString());
            float star_count = Float.parseFloat(dbObj.get("star_count").toString());
            float setstar = star_save / star_count;
            user_name.setText(name);
            user_star.setRating(setstar);
            user_reviewnum.setText("(" + Integer.toString((int) star_count) + ")");
        }else {
            BasicDBObject query = new BasicDBObject();
            query.put("email", item.getOwner_email());
            DBObject dbObj = collection.findOne(query);
            name = dbObj.get("name").toString();
            float star_save = Float.parseFloat(dbObj.get("star_save").toString());
            float star_count = Float.parseFloat(dbObj.get("star_count").toString());
            float setstar = star_save / star_count;
            user_name.setText(name);
            user_star.setRating(setstar);
            user_reviewnum.setText("(" + Integer.toString((int) star_count) + ")");
        }


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

    //editting.....수정중
    public String getHangulCategory(String englishCategory){

        String[] text = {"장소", "공구", "음향기기", "의료", "유아용품", "기타"};
        String[] text_send = {"place", "tool", "sound_equipment", "medical_equipment", "baby_goods", "etc"};

        //for(int i=0 ; i < )

        return "";
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

                if(start_temp.compareTo(item.getAvailableFrom()) == 1 && end_temp.compareTo(item.getAvailableTo()) == -1){
                    date.setText(selectdate);
                    long diffDay = ((end_temp.getTime() - start_temp.getTime()) / (24*60*60*1000) + 1)  * Integer.decode(item.getItem_price_per_day());
                    item_price.setText(diffDay + "원");
                }
                else{
                    date.setText("잘못된 날짜입니다.");
                    item_price.setText("--" + "원");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.d("날짜",start_date + end_date);

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
