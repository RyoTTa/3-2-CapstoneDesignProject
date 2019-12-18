package com.example.share.Chatting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.share.Activity.BucketListActivity;
import com.example.share.Activity.MyPageActivity;
import com.example.share.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import java.util.ArrayList;

public class ChatListActivity extends AppCompatActivity {


    private ListView chatlist_view;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private ArrayList<ChatroomlistItem> mItems;
    private ArrayList<ChatroomlistItem> send_rooms;
    private SharedPreferences pref;
    private ChatroomlistItem chatroomlistitem;
    //mongoDB
    private String MongoDB_IP = "15.164.51.129";
    private int MongoDB_PORT = 27017;
    private String DB_NAME = "local";
    private String USER_COLLECTION= "users";

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        chatlist_view = (ListView)findViewById(R.id.chatroomlistview);
        mItems = new ArrayList<ChatroomlistItem>();
        showChatList();
        send_rooms = new ArrayList<ChatroomlistItem>();
        chatroomlistitem = new ChatroomlistItem();

        chatlist_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),ChattingActivity.class);

                Log.d("jihyee", Integer.toString(position));
                intent.putExtra("owner_email",send_rooms.get(position).getOther_email());
                intent.putExtra("owner_name",send_rooms.get(position).getOther_name());
                startActivity(intent);
            }
        });
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation);  //bottom navigation bar 에서 메뉴 클릭 리스너

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch(item.getItemId()) {
                            case R.id.navigation_menu1:
                                Toast toast = Toast.makeText(getApplicationContext(), "메시지화면입니다.", Toast.LENGTH_SHORT); toast.show();
                                break;
                            case R.id.navigation_menu2:
                                Intent BucketList_Intents = new Intent(getApplicationContext(), BucketListActivity.class);  //찜목록 activity 실행 수정필요
                                startActivity(BucketList_Intents);
                                finish();
                                break;
                            case R.id.navigation_menu3:
                                finish();
                                break;
                            case R.id.navigation_menu4:
                                Toast toast2 = Toast.makeText(getApplicationContext(), "결제 미구현.", Toast.LENGTH_SHORT); toast2.show();
                                break;
                            case R.id.navigation_menu5:
                                Intent Share_Intents = new Intent(getApplicationContext(), MyPageActivity.class);
                                startActivity(Share_Intents);
                                finish();
                                break;
                        }
                        return true;
                    }
                });

    }

    private void showChatList() {
        // 리스트 어댑터 생성 및 세팅
        final ChatroomAdapter adapter
                = new ChatroomAdapter(this, mItems);

        // 데이터 받아오기 및 어댑터 데이터 추가 및 삭제 등..리스너 관리

        databaseReference.child("chat").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("LOG", "dataSnapshot.getKey() : " + dataSnapshot.getKey());
                chatroomlistitem = dataSnapshot.getValue(ChatroomlistItem.class);

                int idx = dataSnapshot.getKey().indexOf("-");

                String email1 = dataSnapshot.getKey().substring(0,idx).concat(".com");
                String email2 = dataSnapshot.getKey().substring(idx+1).concat(".com");

                MongoClient mongoClient = new MongoClient(new ServerAddress(MongoDB_IP, MongoDB_PORT)); // failed here?
                DB db = mongoClient.getDB(DB_NAME);
                DBCollection collection2 = db.getCollection(USER_COLLECTION);
                BasicDBObject query2 = new BasicDBObject();

                pref = getSharedPreferences("pref", AppCompatActivity.MODE_PRIVATE);
                String user_email = pref.getString("user_email",null);

                if(user_email.equals(email1) ){
                    query2.put("email", email2);
                    DBObject dbObj = collection2.findOne(query2);
                    chatroomlistitem.setOther_name(dbObj.get("name").toString());
                    chatroomlistitem.setOther_email(email2);
                    chatroomlistitem.setRoomName(chatroomlistitem.getOther_name());
                    Log.d("jihye",chatroomlistitem.getOther_email());
                    adapter.add(chatroomlistitem.getOther_name(),chatroomlistitem.getMessage());
                    send_rooms.add(chatroomlistitem);

                    Log.d("jihye","add 후 : "+adapter.getCount());
                    adapter.notifyDataSetChanged();
                    chatlist_view.setAdapter(adapter);
                }else if(user_email.equals(email2)){
                    query2.put("email", email1);
                    DBObject dbObj = collection2.findOne(query2);
                    chatroomlistitem.setOther_name(dbObj.get("name").toString());
                    chatroomlistitem.setOther_email(email1);
                    chatroomlistitem.setRoomName(chatroomlistitem.getOther_name());
                    adapter.add(chatroomlistitem.getOther_name(),chatroomlistitem.getMessage());
                    send_rooms.add(chatroomlistitem);

                    Log.d("jihye","add 후 : "+adapter.getCount());
                    adapter.notifyDataSetChanged();
                    chatlist_view.setAdapter(adapter);
                }


            }



            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    public void addChatroom(DataSnapshot datasnapshot, ChatroomAdapter adapter){
        ChatroomlistItem chatroomlist = datasnapshot.getValue(ChatroomlistItem.class);
        adapter.add(chatroomlist.getRoomName(),chatroomlist.getMessage());
    }
}