package com.example;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qna.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.example.QuestionsActivity.FILE_NAME;
import static com.example.QuestionsActivity.KEY_NAME;

public class BookmarkActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private List<QuestionModel> bookmarkList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Bookmark");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.boolmarkRecyclerview);

        sharedPreferences=getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        gson=new Gson();
        getBookmarks();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);


        BookmarkAdapter adapter=new BookmarkAdapter(bookmarkList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

    @Override
    protected void onPause() {
        super.onPause();
        storedBookmarks();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getBookmarks(){
        String json=sharedPreferences.getString(KEY_NAME,"");
        Type type=new TypeToken<List<QuestionModel>>(){}.getType();
        bookmarkList=gson.fromJson(json, type);
        if (bookmarkList==null){
            bookmarkList=new ArrayList<>();
        }
    }
    private void storedBookmarks(){
        String json=gson.toJson(bookmarkList);
        editor.putString(KEY_NAME, json);
        editor.commit();
    }
}