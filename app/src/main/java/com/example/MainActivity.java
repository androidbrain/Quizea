package com.example;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.qna.R;

public class MainActivity extends AppCompatActivity {

    private Button startBtn, bookmarkBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startBtn=findViewById(R.id.start_btn);
        bookmarkBtn=findViewById(R.id.bookmarks_btn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categoryIntent=new Intent(MainActivity.this, CategoriesActivity.class);
                startActivity(categoryIntent);
            }
        });
        bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, BookmarkActivity.class);
                startActivity(intent);
            }
        });


    }
}