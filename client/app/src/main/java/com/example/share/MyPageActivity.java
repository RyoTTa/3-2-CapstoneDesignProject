package com.example.share;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class MyPageActivity extends AppCompatActivity {

    private ImageButton sharing_button;
    private ImageButton borrow_button;
    private ImageButton review_button;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        sharing_button = (ImageButton)findViewById(R.id.share1);
        borrow_button = (ImageButton)findViewById(R.id.share2);
        review_button = (ImageButton)findViewById(R.id.share3);

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
                Intent intent = new Intent(MyPageActivity.this, ItemListActivity.class);
                startActivity(intent);
            }
        });

    }
}
