package com.example.share.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.share.Chatting.ChatroomAdapter;
import com.example.share.Chatting.ChatroomlistItem;
import com.example.share.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ReviewActivity extends AppCompatActivity {

    private ListView revlistView;
    private RatingBar myStar;
    private TextView myStarNum;
    private TextView myName;


    private SharedPreferences pref;
    private ArrayList<ReviewItem> mItems;

    //mongoDB
    private String MongoDB_IP = "15.164.51.129";
    private int MongoDB_PORT = 27017;
    private String DB_NAME = "local";
    private String REVIEW_COLLECTION = "reviews";
    private String USER_COLLECTION= "users";
    private DBCollection reviewcollection;
    private DBCollection usercollection;
    String user_email;
    String user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        revlistView=(ListView)findViewById(R.id.reviewlistView);
        myStar = (RatingBar)findViewById(R.id.review_starrating);
        myStarNum = (TextView)findViewById(R.id.review_starnum);
        myName = (TextView)findViewById(R.id.review_username);
        mItems = new ArrayList<ReviewItem>();

        Intent intent = getIntent();

        user_email = intent.getStringExtra("user_email");
        user_name = intent.getStringExtra("user_name");


        //db열기
        MongoClient mongoClient = new MongoClient(new ServerAddress(MongoDB_IP, MongoDB_PORT)); // failed here?
        DB db = mongoClient.getDB(DB_NAME);
        reviewcollection = db.getCollection(REVIEW_COLLECTION);
        usercollection = db.getCollection(USER_COLLECTION);
        BasicDBObject query = new BasicDBObject();

        //db에서 찾기
        query.put("email",user_email);
        DBObject dbObj = usercollection.findOne(query);

        float star_save = Float.parseFloat(dbObj.get("star_save").toString());
        float star_count = Float.parseFloat(dbObj.get("star_count").toString());
        float setstar = star_save/star_count;
        int starcount = (int)star_count;

        //내정보 세팅하기~
        myStar.setRating(setstar);
        myStarNum.setText("("+Integer.toString(starcount)+")");
        myName.setText(user_name+"님의 평점");

        //review 불러오기
        final ReviewAdapter adapter = new ReviewAdapter(this, mItems);
        BasicDBObject reviewquery = new BasicDBObject();
        reviewquery.put("reviewee",user_email);
        DBCursor cursor = reviewcollection.find(reviewquery);
        while(cursor.hasNext()){
            DBObject dbo = (BasicDBObject) cursor.next();
            try{
                String reviewer_email = dbo.get("reviewer").toString();
                BasicDBObject query2 = new BasicDBObject();
                query2.put("email",reviewer_email);
                DBObject dbObj2 = usercollection.findOne(query2);
                String reviewer = dbObj2.get("name").toString();

                int reviewstar = Integer.parseInt(dbo.get("star").toString());
                String contents = dbo.get("contents").toString();
                Log.d("jihye",reviewer+"/"+reviewstar+"/"+contents);
                adapter.add(reviewer,reviewstar,contents);

            }catch(NumberFormatException e){
                e.printStackTrace();
            }
        }

        revlistView.setAdapter(adapter);


    }


}