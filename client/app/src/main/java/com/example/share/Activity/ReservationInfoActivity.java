package com.example.share.Activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.media.Image;
import android.media.Rating;
import android.os.Bundle;
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
    private FromServerImage newImage = new FromServerImage();

    private ImageView pay;
    private Item item;

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
        //item_date =
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

        pay = (ImageView)findViewById(R.id.pay);

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),PayActivity.class);
                intent.putExtra("item_object",item);
                intent.putExtra("owner_name",name);
                startActivity(intent);
            }
        });
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
