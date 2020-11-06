package com.example;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.qna.R;

public class ScoreActivity extends AppCompatActivity {
    private TextView score, total;
    private Button doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        score=findViewById(R.id.score_text);
        total=findViewById(R.id.out_of_text);
        doneButton=findViewById(R.id.done_btn);

        score.setText(String.valueOf(getIntent().getIntExtra("score", -1)));
        total.setText("OUT OF "+getIntent().getIntExtra("total", -1));


        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}