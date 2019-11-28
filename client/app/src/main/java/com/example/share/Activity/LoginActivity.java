package com.example.share.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.CheckBox;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.share.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

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

public class LoginActivity extends AppCompatActivity {
    EditText email_input;
    EditText pw_input;

    //mongoDB
    private String MongoDB_IP = "15.164.51.129";
    private int MongoDB_PORT = 27017;
    private String DB_NAME = "local";
    private String COLLECTION_NAME = "users";
    private String user_name;
    private SharedPreferences pref;
    private static final String TAG = "LoginActivity";
    private String token ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email_input = (EditText)findViewById(R.id.email_input);
        pw_input = (EditText)findViewById(R.id.pw_input);

        ImageButton loginButton = (ImageButton)findViewById(R.id.loginButton);
        ImageButton signupButton = (ImageButton)findViewById(R.id.signupButton);
        ImageView id_box =(ImageView) findViewById(R.id.id_box);
        ImageView pw_box =(ImageView) findViewById(R.id.pw_box);

        findViewById(R.id.email_input).bringToFront();
        findViewById(R.id.pw_input).bringToFront();

        // [START retrieve_current_token]
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        token = task.getResult().getToken();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        //Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
        // [END retrieve_current_token]

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoginActivity.JSONTask().execute("http://ec2-15-164-51-129.ap-northeast-2.compute.amazonaws.com:3000/login");
            }
        });
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signupintent = new Intent(LoginActivity.this,SignUpActivity.class);
                LoginActivity.this.startActivity(signupintent);
            }

        });


    }

    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("email",email_input.getText().toString());
                jsonObject.accumulate("password", pw_input.getText().toString());
                jsonObject.accumulate("token", token);
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
            if (result == null) {    //로그인 성공시 Home Intents 시작 메소드
                AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                    }
                });
                alert.setMessage("서버와의 통신이 원활하지 않습니다.");
                alert.show();
            }
            else if(result.equals("true")){
                LoginCheck();
            }
            else{   //로그인 실패시 에러 메시지 출력
                AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
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
    public void LoginCheck(){ // 로그인 성공시 Home Intents 시작

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //Connect to MongoDB
        MongoClient mongoClient = new MongoClient(new ServerAddress(MongoDB_IP, MongoDB_PORT)); // failed here?
        DB db = mongoClient.getDB(DB_NAME);
        DBCollection collection = db.getCollection(COLLECTION_NAME);

        //Check Data in Database with query
        BasicDBObject query = new BasicDBObject();
        query.put("email",email_input.getText().toString());
        DBCursor cursor = collection.find(query);
        while (cursor.hasNext()) {

            DBObject dbo = (BasicDBObject) cursor.next();
            try {

                user_name = dbo.get("name").toString();

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }


        pref = getSharedPreferences("pref", AppCompatActivity.MODE_PRIVATE);
        SharedPreferences.Editor email_edit = pref.edit();
        SharedPreferences.Editor name_edit = pref.edit();

        email_edit.putString("user_email",email_input.getText().toString());
        email_edit.commit();
        name_edit.putString("user_name",user_name);
        name_edit.commit();

        Intent homeintent = new Intent(this, HomeActivity.class);
        homeintent.putExtra("UserEmail",email_input.getText().toString());
        startActivity(homeintent);

    }
}
