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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import com.example.share.MongoDB.FromServerImage;
import com.example.share.R;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.ServerAddress;
import com.mongodb.DB;
import com.mongodb.MongoClient;

import com.example.share.Data.Item;

public class ItemListActivity extends AppCompatActivity {
    //arraylist, adapter and gridview
    private ArrayList<Item> items_from_db = null;
    private ArrayList<Item> items_displaying = null;
    private ItemAdapter adapter = null;
    private GridView gridView = null;
    private LocationManager lm = null;
    private TextView page_header = null;
    private ArrayList<String> Mybucketlist;
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
    private String BUCKET_NAME = "buckets";

    //
    private Integer currentCategory;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemlist);
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        UserEmail = pref.getString("user_email",null);

        //bucketlist 불러와서 bucket itemid list에 저장하기
        Mybucketlist = new ArrayList<String>();
        MongoClient  mongoClient = new MongoClient(new ServerAddress(MongoDB_IP, MongoDB_PORT)); // failed here?
        DB db = mongoClient.getDB(DB_NAME);
        DBCollection bucket = db.getCollection(BUCKET_NAME);
        BasicDBObject querybucket = new BasicDBObject();
        querybucket.put("email",UserEmail);
        DBCursor cursorbucket = bucket.find(querybucket);
        while (cursorbucket.hasNext()) {

            DBObject dbo = (BasicDBObject) cursorbucket.next();
            try {
                Mybucketlist.add(dbo.get("item_id").toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }


        Intent intent = getIntent();
        currentCategory = Integer.decode(intent.getStringExtra("category"));

        page_header = (TextView) findViewById(R.id.page_header);
        page_header.setText(text[currentCategory]);



        //for DB connection, replace this with proper solution later..
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //init date filter
        sdf= new SimpleDateFormat("yyyy-MM-dd");
        items_from_db = new ArrayList<Item>();

        //Connect to MongoDB
        DBCollection collection = db.getCollection(COLLECTION_NAME);

        //Check Data in Database with query
        BasicDBObject query = new BasicDBObject();
        query.put("category",text_send[currentCategory]);
        query.put("reservation",false);
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

                /*
                    Log.d("MONGODB", new_id);
                    Log.d("MONGODB", "");
                    Log.d("MONGODB", newName);
                    Log.d("MONGODB", newPPD);
                    Log.d("MONGODB", "" + newLatitude);
                    Log.d("MONGODB", "" + newLongitude);
                    Log.d("MONGODB", newDateFrom.toString());
                    Log.d("MONGODB", newDateTo.toString());
                    Log.d("MONGODB", newCategory);
                    Log.d("MONGODB", newFilePath);
                    Log.d("MONGODB", newContents);
                    //Log.d("MONGODB","-");
                 */

                //TODO: change to imagePath
                if (text_send[currentCategory].equals(newCategory)) {
                    items_from_db.add(new Item(new_id, newName, newPPD, newLatitude, newLongitude,
                            newDateFrom, newDateTo, newFilePath,newCategory,newContent,newOwner_email));
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        //gps
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            Log.d("MYGPS","getting permissoin");
            ActivityCompat.requestPermissions( ItemListActivity.this, new String[] {  Manifest.permission.ACCESS_FINE_LOCATION  },
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

                Intent intent = new Intent(ItemListActivity.this, ItemDetailActivity.class);
                intent.putExtra("item_object",item);
                int bucket_check =0;
                for(int i=0; i<Mybucketlist.size(); i++)
                {
                    if(Mybucketlist.get(i).equals(item.getItem_id())){
                        bucket_check = 1;
                    }
                }
                if(bucket_check == 0) {
                    intent.putExtra("bucket","false");
                }else if(bucket_check == 1){
                    intent.putExtra("bucket","true");
                }
                startActivity(intent);
            }
        });

        searchBar = (EditText)findViewById(R.id.seach_bar);
        searchBar.setOnEditorActionListener((v, actionId, event) -> {

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filterResult();

                Log.d("MYSEARCH","joo");
                return true;
            }
            return false;
        });

        /** map button */
        ImageView mapsButton = findViewById(R.id.map_button);
        mapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), MapsMarkerActivity.class);
                intent.putExtra("items",items_displaying);
                startActivityForResult(intent,MAP_ACT);

            }

        });


        /** Date button */
        ImageView dateButton = findViewById(R.id.date_button);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MYDIALOG","start date filtering");
                Intent intent = new Intent(getApplicationContext(),SelectDateActivity.class);
                startActivityForResult(intent,DATE_INFO);

            }

        });

        /** register button */
        ImageView regiButton = findViewById(R.id.register_button);
        regiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), RegisterItemActivity.class);
                intent.putExtra("UserEmail",UserEmail);
                startActivity(intent);

            }

        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DATE_INFO && data != null) {

            if(data != null) {
           /* dateFrom=data.getStringExtra("syear")+"-"+data.getStringExtra("smonth")+"-"+data.getStringExtra("sday");
            dateTo=data.getStringExtra("eyear")+"-"+data.getStringExtra("emonth")+"-"+data.getStringExtra("eday");*/
                dateFrom = data.getStringExtra("startdate");
                dateTo = data.getStringExtra("enddate");

                filterResult();
            }
        }


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
    private void filterResult(){
        Date selectedDateFrom = null;
        Date selectedDateTo = null;
        String keyword = null;
        boolean dateFiltered = true;

        try {
            selectedDateFrom = sdf.parse(""+dateFrom);
            selectedDateTo = sdf.parse(""+dateTo);
        } catch (ParseException e){
            Log.d("DATECHK","parse Error_filter,date",e);
        }
        if( selectedDateFrom ==null || selectedDateTo ==null) {
            dateFiltered = false;
        }else{
            Log.d("DATECHK", "dstart: " + selectedDateFrom.toString());
            Log.d("DATECHK", "dend: " + selectedDateTo.toString());
        }

        keyword = searchBar.getText().toString();
        Log.d("DATECHK","keyword: "+keyword);

        items_displaying = new ArrayList<Item>();
        for (int i = 0; i < items_from_db.size(); i++){
            Item item = items_from_db.get(i);
            Date iAvailableFrom = item.getAvailableFrom();
            Date iAvailableTo = item.getAvailableTo();
            String itemName = item.getItem_name();
            if(itemName.contains(keyword)) {
                if(dateFiltered) {
                    if ((iAvailableFrom.compareTo(selectedDateFrom) <= 0) && (selectedDateTo.compareTo(iAvailableTo) <= 0))
                        items_displaying.add(item);
                }else
                    items_displaying.add(item);
            }
        }
        adapter = new ItemAdapter(getApplicationContext(), items_displaying);
        gridView.setAdapter(adapter);

        return;
    }

}

class ItemAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private ArrayList<Item> items = null;
    private int count = 0;
    private FromServerImage newImage = new FromServerImage();

    public ItemAdapter(Context context, ArrayList<Item> items) {
        this.items = items;
        this.count = items.size();
        this.inflater = (LayoutInflater)context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.itemlist_item, parent, false);
        }

        Item item = items.get(position);
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");

        ImageView image = (ImageView)convertView.findViewById(R.id.item_image);
        image.setImageBitmap(newImage.getImage(item.getFilepath()));

        TextView name = (TextView)convertView.findViewById(R.id.item_name);
        name.setText(item.getItem_name());

        TextView pricePerDay = (TextView)convertView.findViewById(R.id.item_price_per_day);
        pricePerDay.setText(item.getItem_price_per_day() + "원 / 하루");

        TextView distanceToUser = (TextView)convertView.findViewById(R.id.distance_to_user);
        if(item.getDistanceToUser() !=null)
            distanceToUser.setText(String.format("%.0f",item.getDistanceToUser()[0])+" 미터");

        TextView avilableDays = (TextView)convertView.findViewById(R.id.available_days);
        avilableDays.setText(sdf.format(item.getAvailableFrom())+" ~ " +sdf.format(item.getAvailableTo()));

        return convertView;

    }

}