package com.example.share.Chatting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.example.share.Activity.LoginActivity;
import com.example.share.Data.Item;
import com.example.share.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ChattingActivity extends AppCompatActivity {


    private String CHAT_NAME;
    private String USER_NAME;
    private String user_email;
    private Item item;
    private ListView chat_view;
    private EditText chat_edit;
    private Button chat_send;
    private int type;
    private String owner_email;
    private String owner_name;
    private TextView chatroom_title;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private ArrayList<ChatlistItem> mItems;
    private String contents;
    private SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        mItems = new ArrayList<ChatlistItem>();
        // 위젯 ID 참조
        chat_view = (ListView) findViewById(R.id.chattinglistView);
        chat_edit = (EditText) findViewById(R.id.chat_EditText);
        chat_send = (Button) findViewById(R.id.send);

        // 상대 저장
        Intent intent = getIntent();
        owner_email =intent.getStringExtra("owner_email");
        owner_name = intent.getStringExtra("owner_name");

        Log.d("jihye","null"+owner_email);
        //유저 정보 저장
        pref = getSharedPreferences("pref", AppCompatActivity.MODE_PRIVATE);
        USER_NAME = pref.getString("user_name",null);
        user_email = pref.getString("user_email",null);

        int idx1 = user_email.indexOf(".");
        int idx2 = owner_email.indexOf(".");

        String userE = user_email.substring(0,idx1);
        String owerE = owner_email.substring(0,idx2);

        Log.d("jihye","owner name : " + owner_name);
        Log.d("jihye","owner:"+owerE );
        Log.d("jihye","user:"+userE );
        if(owerE.compareTo(userE)<0) {
            CHAT_NAME = owerE+"-"+userE;
            Log.d("jihye","ownerE<userE" );
        }else{
            CHAT_NAME = userE+"-"+owerE;
            Log.d("jihye","ownerE>=userE" );
        }

        Log.d("jihye",CHAT_NAME);
        chatroom_title = (TextView)findViewById(R.id.chat_username);
        chatroom_title.setText(owner_name);

        item = (Item)intent.getSerializableExtra("item");

        //reservation일 경우
        if(item != null) {
            type = 1;
        }

        openChat(CHAT_NAME);
        // 채팅 방 입장


        // 메시지 전송 버튼에 대한 클릭 리스너 지정
        chat_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chat_edit.getText().toString().equals(""))
                    return;
                contents = chat_edit.getText().toString();
                ChatDTO chat = new ChatDTO(USER_NAME, chat_edit.getText().toString()); //ChatDTO를 이용하여 데이터를 묶는다.
                databaseReference.child("chat").child(CHAT_NAME).push().setValue(chat); // 데이터 푸쉬
                chat_edit.setText(""); //입력창 초기화
                new ChattingActivity.JSONTask().execute("http://ec2-15-164-51-129.ap-northeast-2.compute.amazonaws.com:3000/fcm_push");
            }
        });
    }

    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("my_email",user_email);
                jsonObject.accumulate("your_email",owner_email);
                jsonObject.accumulate("contents", contents);
                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    //URL url = new URL("http://192.168.25.16:3000/users");
                    URL url = new URL(urls[0]);
                    //연결을 함
                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송


                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();

                    //서버로 보내기위해서 스트림 만듬
                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();//버퍼를 받아줌

                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){
                            reader.close();//버퍼를 닫아줌
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
    private void addMessage(DataSnapshot dataSnapshot, ChattingAdapter adapter) {
        ChatlistItem chatlistItem = dataSnapshot.getValue(ChatlistItem.class);
        Log.d("datasnapshot","chatistItem : "+chatlistItem.getUserName()+","+chatlistItem.getMessage());

        if(chatlistItem.getUserName().equals(USER_NAME) == true) {
            adapter.addItem(1,chatlistItem.getUserName(), chatlistItem.getMessage());
        }else
            adapter.addItem(0,chatlistItem.getUserName(),chatlistItem.getMessage());
        //adapter.addItem(chatlistItem.getUserName(),chatlistItem.getMessage());
        Log.d("datasnapshot","chatistItem : "+adapter.getCount());
    }


    private void openChat(String chatName) {
        // 리스트 어댑터 생성 및 세팅
        final ChattingAdapter adapter = new ChattingAdapter(this,mItems);



        // 데이터 받아오기 및 어댑터 데이터 추가 및 삭제 등..리스너 관리
        databaseReference.child("chat").child(chatName).addChildEventListener(new ChildEventListener() {
            @Override

            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("datasnapshot","data : "+dataSnapshot);

                if(type==1){
                    ChatDTO chat = new ChatDTO(USER_NAME,item.getItem_name()+"예약 신청"); //ChatDTO를 이용하여 데이터를 묶는다.
                    databaseReference.child("chat").child(CHAT_NAME).push().setValue(chat); // 데이터 푸쉬
                    type = 100;
                }

                addMessage(dataSnapshot, adapter);
                adapter.notifyDataSetChanged();

                Log.e("LOG", "s:"+s);
                try{
                    chat_view.setAdapter(adapter);
                    chat_view.setSelection(adapter.getCount() - 1);
                }catch (NullPointerException ne){
                    ne.printStackTrace();
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //removeMessage(dataSnapshot, adapter);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });
    }
}