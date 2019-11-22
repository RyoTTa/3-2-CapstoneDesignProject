package com.example.share.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.Rating;
import android.os.Build;
import android.os.Bundle;

import com.example.share.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RegisterReviewActivity extends AppCompatActivity {

    RatingBar review_star;
    EditText review_content;
    ImageView btn_register;
    int send_rate;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_review);


        Intent intent = getIntent();
        String reviewee = intent.getStringExtra("reviewee");
        String item_id = intent.getStringExtra("item_id");
        pref = getSharedPreferences("pref", AppCompatActivity.MODE_PRIVATE);
        String reviewer = pref.getString("user_email",null);

        review_star = (RatingBar)findViewById(R.id.register_review_star);
        review_content = (EditText)findViewById(R.id.register_review_content);
        btn_register = (ImageView)findViewById(R.id.btnregister_review);

        review_star.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                send_rate = (int)rating;
            }
        });


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = review_content.getText().toString();
                send_review(reviewer,reviewee, send_rate,content,item_id);
                //서버에 보내주셈 유저정보.별점.후기내용


            }
        });
    }

    public void send_review(String reviewer, String reviewee, int star, String content,String item_id)
    {
        String result="false";

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        try {
            //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("star",star);
            jsonObject.accumulate("contents",content);
            jsonObject.accumulate("reviewee",reviewee);
            jsonObject.accumulate("reviewer",reviewer);

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.accumulate("item_id",item_id);
            HttpURLConnection con = null;
            HttpURLConnection con2 = null;
            BufferedReader reader = null;
            BufferedReader reader2 = null;

            try{
                URL url = new URL("http://ec2-15-164-51-129.ap-northeast-2.compute.amazonaws.com:3000/review_insert");
                URL url2 = new URL("http://ec2-15-164-51-129.ap-northeast-2.compute.amazonaws.com:3000/complete");
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

                //image transmission 연결
                con2 = (HttpURLConnection) url2.openConnection();

                con2.setRequestMethod("POST");//POST방식으로 보냄
                con2.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                con2.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송

                con2.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                con2.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                con2.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                con2.connect();

                //서버로 보내기위해서 스트림 만듬
                OutputStream outStream2 = con2.getOutputStream();
                //버퍼를 생성하고 넣음
                BufferedWriter writer2 = new BufferedWriter(new OutputStreamWriter(outStream2));
                writer2.write(jsonObject2.toString());
                writer2.flush();
                writer2.close();//버퍼를 받아줌

                //서버로 부터 데이터를 받음
                InputStream stream2 = con2.getInputStream();
                reader2 = new BufferedReader(new InputStreamReader(stream2));

                StringBuffer buffer2 = new StringBuffer();

                String line2 = "";
                while((line2 = reader2.readLine()) != null){
                    buffer2.append(line2);
                }
                result = buffer2.toString();

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
        if(result.equals("true")) {
            android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(RegisterReviewActivity.this);
            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();     //닫기
                    finish();
                }
            });
            alert.setMessage("반납 완료");
            alert.show();
        }


    }

}