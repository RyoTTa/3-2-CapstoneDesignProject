package com.example.share.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.share.Card.AddCard;
import com.example.share.Chatting.ChattingActivity;
import com.example.share.Data.Item;
import com.example.share.Card.FirstCard;
import com.example.share.R;

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

public class PayActivity extends AppCompatActivity {

    private ImageView complete;
    ViewPager cardlist;
    Item item;
    String item_id ;
    String User_email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        Intent intent = getIntent();

        //get all the data passed
        item = (Item)intent.getSerializableExtra("item_object");
        item_id = item.getItem_id();
        String owner_name = intent.getStringExtra("owner_name");
        String owner_email = item.getOwner_email();

        complete = (ImageView)findViewById(R.id.complete);
        cardlist = (ViewPager)findViewById(R.id.cardlist);
        /*cardlist.setOnItemClickListener(new ClickableViewPager.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(getApplicationContext(),"kkkk",Toast.LENGTH_SHORT).show();
            }
        });*/
        cardlist.setAdapter(new pagerAdapter(getSupportFragmentManager()));

        cardlist.setCurrentItem(0);

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                User_email = pref.getString("user_email",null);
                Log.d("EMAILCHK","useremail : "+pref.getString("user_email",null));

                new PayActivity.JSONTask().execute("http://ec2-15-164-51-129.ap-northeast-2.compute.amazonaws.com:3000/reservation");


                Intent intent = new Intent(getApplicationContext(), ChattingActivity.class);
                Log.d("jihye","null"+item.getOwner_email());
                intent.putExtra("owner_email",owner_email);
                intent.putExtra("owner_name",owner_name);
                intent.putExtra("item",item);
                startActivity(intent);

            }
        });

    }
    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("_id",item_id);
                jsonObject.accumulate("borrower",User_email);

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
            if (result.equals("true")) {    //로그인 성공시 Home Intents 시작 메소드
                // reservation true

            }
            else{   //로그인 실패시 에러 메시지 출력
                AlertDialog.Builder alert = new AlertDialog.Builder(PayActivity.this);
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                    }
                });
                alert.setMessage(result);
                alert.show();
            }
        }
    }
    View.OnClickListener movePageListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            int tag = (int)v.getTag();
            cardlist.setCurrentItem(tag);
        }
    };

    private class pagerAdapter extends FragmentStatePagerAdapter
    {
        public pagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        public Fragment getItem(int position)
        {
            switch(position)
            {
                case 0:
                    return new FirstCard();
                case 1:
                    return new AddCard();
                default:
                    return null;
            }
        }

        public int getCount()
        {
            return 2;
        }
    }
}