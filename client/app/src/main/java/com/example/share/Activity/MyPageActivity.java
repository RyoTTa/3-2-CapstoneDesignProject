package com.example.share.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.share.Chatting.ChatListActivity;
import com.example.share.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.w3c.dom.Text;

public class MyPageActivity extends AppCompatActivity {

    private ImageButton sharing_button;
    private ImageButton borrow_button;
    private ImageButton review_button;
    private ImageButton qrcode_button;
    private ImageButton share_complete;
    private ImageButton keyword_alarm;
    private ImageButton logout_button;
    private TextView user_email;
    private TextView user_name;

    private BottomNavigationView bottomNavigationView;

    private String UserEmail;
    private String UserName;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        sharing_button = (ImageButton)findViewById(R.id.share1);
        borrow_button = (ImageButton)findViewById(R.id.share2);
        review_button = (ImageButton)findViewById(R.id.share3);
        qrcode_button = (ImageButton)findViewById(R.id.share4);
        share_complete = (ImageButton)findViewById(R.id.share5);
        keyword_alarm = (ImageButton)findViewById(R.id.share6);
        logout_button = (ImageButton)findViewById(R.id.Logout);

        user_email = (TextView)findViewById(R.id.mypage_user_email);
        user_name = (TextView)findViewById(R.id.mypage_user_name);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        UserEmail = pref.getString("user_email",null);
        UserName =pref.getString("user_name",null);

        user_name.setText(UserName);
        user_email.setText(UserEmail);

        sharing_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, ShareListActivity.class);
                intent.putExtra("list_kind","sharing");
                startActivity(intent);
            }
        });
        borrow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, BorrowListActivity.class);
                intent.putExtra("list_kind","borrow");
                startActivity(intent);
            }
        });
        review_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, ReviewActivity.class);
                intent.putExtra("user_email",UserEmail);
                intent.putExtra("user_name",UserName);
                startActivity(intent);

            }
        });

        qrcode_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, CreateQRcodeActivity.class);
                startActivity(intent);

            }
        });

        share_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, CompleteActivity.class);
                startActivity(intent);

            }
        });

        keyword_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, KeywordAlarmActivity.class);
                startActivity(intent);

            }
        });

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences("pref", AppCompatActivity.MODE_PRIVATE);

                SharedPreferences.Editor autoLogin_edit = pref.edit();

                autoLogin_edit.putInt("autoLogin",0);
                autoLogin_edit.commit();

                Intent intent = new Intent(MyPageActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation);  //bottom navigation bar 에서 메뉴 클릭 리스너

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch(item.getItemId()) {
                            case R.id.navigation_menu1:
                                Intent ChatList_Intents = new Intent(getApplicationContext(), ChatListActivity.class);  //메시지 activity 실행 수정필요
                                startActivity(ChatList_Intents);
                                finish();
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
                                Toast toast = Toast.makeText(getApplicationContext(), "결제 미구현.", Toast.LENGTH_SHORT); toast.show();
                                break;
                            case R.id.navigation_menu5:
                                Toast toast2 = Toast.makeText(getApplicationContext(), "마이페이지 화면입니다.", Toast.LENGTH_SHORT); toast2.show();
                                break;
                        }
                        return true;
                    }
                });


    }
}