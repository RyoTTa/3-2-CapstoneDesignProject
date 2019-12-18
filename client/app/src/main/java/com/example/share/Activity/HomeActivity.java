package com.example.share.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.share.Chatting.ChatListActivity;
import com.example.share.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;


    private ImageButton etcButton;
    private ImageButton placeButton;
    private ImageButton toolButton;
    private ImageButton sound_equipmentButton;
    private ImageButton medical_equimentButton;
    private ImageButton baby_goodsButton;

    private String UserEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent homeintent = getIntent();

        UserEmail = homeintent.getExtras().getString("UserEmail");  //LoginActivity로 부터 email 읽어오기


        etcButton = (ImageButton)findViewById(R.id.etc);
        placeButton = (ImageButton)findViewById(R.id.place);
        toolButton = (ImageButton)findViewById(R.id.tools);
        sound_equipmentButton = (ImageButton)findViewById(R.id.music);
        medical_equimentButton = (ImageButton)findViewById(R.id.medical);
        baby_goodsButton = (ImageButton)findViewById(R.id.child);

        etcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ItemListActivity.class);
                intent.putExtra("category", "5");
                intent.putExtra("UserEmail",UserEmail);
                startActivity(intent);
            }

        });
        placeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ItemListActivity.class);
                intent.putExtra("category", "0");
                intent.putExtra("UserEmail",UserEmail);
                startActivity(intent);
            }

        });
        toolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ItemListActivity.class);
                intent.putExtra("category", "1");
                intent.putExtra("UserEmail",UserEmail);
                startActivity(intent);
            }

        });
        sound_equipmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ItemListActivity.class);
                intent.putExtra("category", "2");
                intent.putExtra("UserEmail",UserEmail);
                startActivity(intent);
            }

        });
        medical_equimentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ItemListActivity.class);
                intent.putExtra("category", "3");
                intent.putExtra("UserEmail",UserEmail);
                startActivity(intent);
            }

        });
        baby_goodsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ItemListActivity.class);
                intent.putExtra("category", "4");
                intent.putExtra("UserEmail",UserEmail);
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
                                Intent ChatList_Intents = new Intent(getApplicationContext(), ChatListActivity.class);  //메시지 activity 실행 수정필요
                                startActivity(ChatList_Intents);
                                break;
                            case R.id.navigation_menu2:
                                Intent BucketList_Intents = new Intent(getApplicationContext(), BucketListActivity.class);  //찜목록 activity 실행 수정필요
                                startActivity(BucketList_Intents);
                                break;
                            case R.id.navigation_menu3:
                                Toast toast = Toast.makeText(getApplicationContext(), "홈 화면입니다.", Toast.LENGTH_SHORT); toast.show();
                                break;
                            case R.id.navigation_menu4:
                                Toast toast2 = Toast.makeText(getApplicationContext(), "결제 미구현.", Toast.LENGTH_SHORT); toast2.show();
                                break;
                            case R.id.navigation_menu5:
                                Intent Share_Intents = new Intent(getApplicationContext(), MyPageActivity.class);
                                startActivity(Share_Intents);
                                break;
                        }
                        return true;
                    }
                });


    }

}