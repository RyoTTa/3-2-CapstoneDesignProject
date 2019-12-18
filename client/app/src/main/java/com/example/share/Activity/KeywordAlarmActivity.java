package com.example.share.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.share.Data.Item;
import com.example.share.R;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import org.json.JSONObject;
import org.w3c.dom.Text;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class KeywordAlarmActivity extends AppCompatActivity {

    Button addButton;
    Button deleteButton;

    SharedPreferences pref;
    String User_email;

    ArrayList<String> items = new ArrayList<String>() ;
    ArrayAdapter adapter;

    private String MongoDB_IP = "15.164.51.129";
    private int MongoDB_PORT = 27017;
    private String DB_NAME = "local";
    private String COLLECTION_NAME = "keywords";

    public static final int REQUEST_CODE_CONTENTS = 1001;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyword_alarm);

        // ArrayAdapter 생성. 아이템 View를 선택(single choice)가능하도록 만듦.

        final ListView listview = (ListView) findViewById(R.id.listview1) ;
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, items);

        listview.setAdapter(adapter) ;

        pref = getSharedPreferences("pref", AppCompatActivity.MODE_PRIVATE);
        User_email = pref.getString("user_email",null);

         addButton = (Button)findViewById(R.id.add) ;
        deleteButton = (Button)findViewById(R.id.delete) ;

        init_keyword();

        addButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RegisterKeywordActivity.class);
                startActivityForResult(intent,REQUEST_CODE_CONTENTS);
            }
        }) ;

        deleteButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                int count, checked ;
                count = adapter.getCount() ;

                if (count > 0) {
                    // 현재 선택된 아이템의 position 획득.
                    checked = listview.getCheckedItemPosition();

                    if (checked > -1 && checked < count) {
                        delete_keyword(items.get(checked));
                        // 아이템 삭제
                        items.remove(checked) ;

                        // listview 선택 초기화.
                        listview.clearChoices();

                        // listview 갱신.
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }) ;

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);

        if(requestCode == REQUEST_CODE_CONTENTS){
            try{
                String contents = intent.getExtras().getString("contents");
                items.add(contents);

                // listview 갱신
                adapter.notifyDataSetChanged();
                Log.d("Result 내",contents);
            }catch(Exception e){

            }


            // 아이템 추가.

        }
    }

    public void delete_keyword(String content)
    {
        String result="false";

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        try {
            //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("keyword",content);
            jsonObject.accumulate("email",User_email);

            HttpURLConnection con = null;
            BufferedReader reader = null;

            try{
                URL url = new URL("http://ec2-15-164-51-129.ap-northeast-2.compute.amazonaws.com:3000/keyword_delete");
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
                result = buffer.toString();


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
        Log.d("전송 요청",result);
    }

    public void init_keyword()
    {
        MongoClient mongoClient = new MongoClient(new ServerAddress(MongoDB_IP, MongoDB_PORT)); // failed here?
        DB db = mongoClient.getDB(DB_NAME);
        DBCollection collection = db.getCollection(COLLECTION_NAME);
        BasicDBObject query = new BasicDBObject();

        //db에서 찾기
        query.put("email",User_email);
        DBCursor cursor = collection.find(query);

        while (cursor.hasNext()) {
            DBObject dbo = (BasicDBObject) cursor.next();
            try {
                String new_keyword = dbo.get("keyword").toString();
                Log.d("Init_keyword 내",new_keyword);
                items.add(new_keyword);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        // listview 갱신
        adapter.notifyDataSetChanged();
    }
}