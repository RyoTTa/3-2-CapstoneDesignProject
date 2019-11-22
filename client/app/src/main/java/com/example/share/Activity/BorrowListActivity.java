package com.example.share.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.share.Data.Item;
import com.example.share.MongoDB.FromServerImage;
import com.example.share.R;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class BorrowListActivity extends AppCompatActivity {

    private SharedPreferences pref;
    private String User_email;

    private ArrayList<Item> items_from_db = null;
    private ArrayList<Item> items_displaying = null;
    private ItemAdapter adapter = null;
    private GridView gridView = null;
    private LocationManager lm = null;
    private TextView page_header = null;
    String[] text = {"장소", "공구", "음향기기", "의료", "유아용품", "기타"};
    String[] text_send = {"place", "tool", "sound_equipment", "medical_equipment", "baby_goods", "etc"};
    String UserEmail;

    //util
    private SimpleDateFormat sdf= null;

    private EditText searchBar = null;

    //within the dialog
    private String dateFrom =null;
    private String dateTo =null;

    //activityForResult
    private int MAP_ACT = 1;
    private int DATE_INFO = 2;

    //mongoDB
    private String MongoDB_IP = "15.164.51.129";
    private int MongoDB_PORT = 27017;
    private String DB_NAME = "local";
    private String COLLECTION_NAME = "items";

    //
    private String currentCategory = "tool";

    //
    private Bitmap bmimg;
    private String new_id;
    private String newName;
    private String newPPD;
    private double newLatitude;
    private double newLongitude;
    private Date newDateFrom;
    private Date newDateTo;
    private String newCategory;
    private String newFilePath;
    private String newContent;
    private String newOwner_email;
    private FromServerImage newImage = new FromServerImage();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_list);

        Intent intent = getIntent();

        pref = getSharedPreferences("pref", AppCompatActivity.MODE_PRIVATE);
        User_email = pref.getString("user_email","");

        //for DB connection, replace this with proper solution later..
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //init date filter
        sdf= new SimpleDateFormat("yyyy-MM-dd");
        items_from_db = new ArrayList<Item>();

        //Connect to MongoDB
        MongoClient mongoClient = new MongoClient(new ServerAddress(MongoDB_IP, MongoDB_PORT)); // failed here?
        DB db = mongoClient.getDB(DB_NAME);
        DBCollection collection = db.getCollection(COLLECTION_NAME);

        //Check Data in Database with query
        BasicDBObject query = new BasicDBObject();
        query.put("borrower",User_email);
        DBCursor cursor = collection.find(query);
        while (cursor.hasNext()) {

            DBObject dbo = (BasicDBObject) cursor.next();
            try {

                new_id = dbo.get("_id").toString();
                newName = dbo.get("name").toString();
                newPPD = dbo.get("price_per_date").toString();
                newLatitude = Double.parseDouble(dbo.get("latitude").toString());
                newLongitude = Double.parseDouble(dbo.get("longitude").toString());
                newDateFrom = (Date) dbo.get("available_date_start");
                newDateTo = (Date) dbo.get("available_date_end");
                newCategory = dbo.get("category").toString();
                newFilePath = dbo.get("image_path").toString();
                newContent = dbo.get("contents").toString();
                newOwner_email = dbo.get("owner_email").toString();
                Log.d("MONGODB", new_id);
                Log.d("MONGODB", "");
                Log.d("MONGODB", newName);
                Log.d("MONGODB", newPPD);
                Log.d("MONGODB", "" + newLatitude);
                Log.d("MONGODB", "" + newLongitude);
                Log.d("MONGODB", newDateFrom.toString());
                Log.d("MONGODB", newDateTo.toString());
                Log.d("MONGODB", newCategory);
                //Log.d("MONGODB","-");
                //TODO: change to imagePath
                items_from_db.add(new Item(new_id, newName, newPPD, newLatitude, newLongitude, newDateFrom, newDateTo, newFilePath,newCategory,newContent,newOwner_email));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        //gps
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            Log.d("MYGPS","getting permissoin");
            ActivityCompat.requestPermissions( BorrowListActivity.this, new String[] {  Manifest.permission.ACCESS_FINE_LOCATION  },
                    0 );
        }else{
            Log.d("MYGPS","gogo");
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000,
                    1,
                    gpsLocationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    1000,
                    1,
                    gpsLocationListener);
        }

        //arraylist, adapter and gridview
        items_displaying = new ArrayList<Item>(items_from_db);
        adapter = new ItemAdapter(this, items_from_db);
        gridView = (GridView) findViewById(R.id.item_grid_view);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item item = items_from_db.get(position);

                Intent intent = new Intent(BorrowListActivity.this,ReservationInfoActivity.class);
                intent.putExtra("item_object",item);
                intent.putExtra("type","borrow_detail");

                startActivity(intent);
            }
        });

    }

    final LocationListener gpsLocationListener = new LocationListener() {

        public void onLocationChanged(Location location) {

            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            double altitude = location.getAltitude();
            float accuracy = location.getAccuracy();

            String info = "위치정보: " + provider + " 위도: " + latitude  + " 경도: " + longitude + " 고도 : " + altitude + " 정확도(the lower the better): "+accuracy;
            Log.d("MYGPSCHK_lisnter",info);

            if(accuracy <35.0f) {
                Log.d("MYGPSCHK","sort~~");

                for(Item i : items_from_db){
                    Log.d("MYGPSCHK","dist");
                    float [] distance = new float [10];

                    Location.distanceBetween(latitude,longitude,i.getLatitude(),i.getLongitude(),distance);
                    i.setDistanceToUser(distance);
                    Log.d("MYGPSCHK",""+distance[0]);
                }
                Collections.sort(items_from_db);
                adapter = new ItemAdapter(getApplicationContext(), items_from_db);
                Log.d("MYGPSCHK", "refresh"  );
                gridView.setAdapter(adapter);

                Log.d("MYGPSCHK", "removeUpdates");
                lm.removeUpdates(this);
            }
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };
}