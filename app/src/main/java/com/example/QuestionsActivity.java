package com.example;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.qna.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QuestionsActivity extends AppCompatActivity {

    public static final String FILE_NAME="QUIZEA";
    public static final String KEY_NAME="QUESTIONS";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myReference = database.getReference();

    private TextView questionText;
    private FloatingActionButton bookmarkFAB;
    private TextView numberIndicator;
    private LinearLayout optionContainer;
    private Button shareBtn, nextBtn;
    private int count = 0;
    private List<QuestionModel> questionModelList;
    private int position = 0;
    private int score = 0;
    private Dialog loadingDialog;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private List<QuestionModel> bookmarkList;
    private int matchQuestionPosition;

    private   String setId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        questionText = findViewById(R.id.question_text);
        bookmarkFAB = findViewById(R.id.bookmark_fab);
        numberIndicator = findViewById(R.id.no_indicator);
        optionContainer = findViewById(R.id.option_container);
        shareBtn = findViewById(R.id.share_button);
        nextBtn = findViewById(R.id.next_button);

        sharedPreferences=getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        gson=new Gson();
        getBookmarks();

        bookmarkFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (modelMatched()){
                    bookmarkList.remove(matchQuestionPosition);
                    bookmarkFAB.setImageDrawable(getDrawable(R.drawable.bookmark_border));
                }else {
                    bookmarkList.add(questionModelList.get(position));
                    bookmarkFAB.setImageDrawable(getDrawable(R.drawable.bookmark));
                }
            }
        });

      //  String categoryTitle = getIntent().getStringExtra("categoryTitle");
       setId = getIntent().getStringExtra("setId");

        loadingDialog=new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.button_background));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);


        questionModelList = new ArrayList<>();
        loadingDialog.show();
        myReference.child("SETS").child(setId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String question = dataSnapshot.child("question").getValue().toString();
                    String a = dataSnapshot.child("optionA").getValue().toString();
                    String b = dataSnapshot.child("optionB").getValue().toString();
                    String c = dataSnapshot.child("optionC").getValue().toString();
                    String d = dataSnapshot.child("optionD").getValue().toString();
                    String correctAns = dataSnapshot.child("correctAns").getValue().toString();
                    String id = dataSnapshot.getKey();
                    questionModelList.add(new QuestionModel(question, id, a, b, c, d, correctAns, setId));
                }
                if (questionModelList.size()>0){

                    for (int i = 0; i < 4; i++) {
                        optionContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                checkOption(((Button) view));
                            }
                        });
                    }
                    playAnim(questionText, 0, questionModelList.get(position).getQuestion());

                    nextBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            nextBtn.setEnabled(false);
                            nextBtn.setAlpha(0.7f);
                           // enableOption(true);
                            position++;
                            if (position == questionModelList.size()) {
                                Intent scoreIntent=new Intent(QuestionsActivity.this, ScoreActivity.class);
                                scoreIntent.putExtra("score", score);
                                scoreIntent.putExtra("total", questionModelList.size());
                                startActivity(scoreIntent);
                                finish();
                                return;
                            }
                            count = 0;
                            playAnim(questionText, 0, questionModelList.get(position).getQuestion());
                        }
                    });

                    shareBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String body=questionModelList.get(position).getQuestion()+ "\n\n"+
                                  "(A) "+  questionModelList.get(position).getOptionA()+"\n"+
                                    "(B) "+  questionModelList.get(position).getOptionB()+"\n"+
                                    "(C) "+ questionModelList.get(position).getOptionC()+"\n"+
                                    "(D) "+ questionModelList.get(position).getOptionD();

                            Intent shareIntent=new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "'Quizea' question");
                           // shareIntent.setPackage("com.whatsapp");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, body);
                            try {
                               startActivity(Intent.createChooser(shareIntent, "share via"));
                            }catch (android.content.ActivityNotFoundException ex){
                                Toast.makeText(QuestionsActivity.this, "Initialising Error!", Toast.LENGTH_SHORT).show();
                            }
                            ///startActivity(Intent.createChooser(shareIntent, "share via"));
                        }
                    });

                }else {
                    finish();
                    Toast.makeText(QuestionsActivity.this, "No Questions", Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QuestionsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        storedBookmarks();
    }

    private void playAnim(final View view, final int value, final String data) {
        for (int i=0; i<4; i++){
            optionContainer.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#d8d8d8")));
        }

        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100)
                .setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

                if (value == 0 && count < 4) {
                    String option = "";
                    if (count == 0) {
                        option = questionModelList.get(position).getOptionA();
                    } else if (count == 1) {
                        option = questionModelList.get(position).getOptionB();
                    } else if (count == 2) {
                        option = questionModelList.get(position).getOptionC();
                    } else if (count == 3) {
                        option = questionModelList.get(position).getOptionD();

                    }

                    playAnim(optionContainer.getChildAt(count), 0, option);
                    count++;
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {

                if (value == 0) {
                    try {
                        ((TextView) view).setText(data);
                        numberIndicator.setText(position + 1 + "/" + questionModelList.size());
                        if (modelMatched()){
                            bookmarkFAB.setImageDrawable(getDrawable(R.drawable.bookmark));
                        }else {
                            bookmarkFAB.setImageDrawable(getDrawable(R.drawable.bookmark_border));
                        }
                    } catch (ClassCastException ex) {
                        ((Button) view).setText(data);
                    }
                    view.setTag(data);
                    playAnim(view, 1, data);
                }
                else {
                    enableOption(true);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

    }

    private void checkOption(Button selectedOption) {
        enableOption(false);
        nextBtn.setEnabled(true);
        nextBtn.setAlpha(1);
        if (selectedOption.getText().toString().equals(questionModelList.get(position).getCorrectAns())) {
            score++;
            selectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4caf50")));
        } else {
            //incorrect
            selectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff0000")));
            Button correctOption = (Button) optionContainer.findViewWithTag(questionModelList.get(position).getCorrectAns());
            correctOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4caf50")));

        }
    }

    private void enableOption(boolean enable) {
        for (int i = 0; i < 4; i++) {
            optionContainer.getChildAt(i).setEnabled(enable);
            /*if (enable) {
                optionContainer.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009FED")));
            }*/
        }
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

    private boolean modelMatched(){
        boolean match=false;
        int i=0;
        for (QuestionModel questionModel:bookmarkList){
            if (questionModel.getQuestion().equals(questionModelList.get(position).getQuestion())
            && questionModel.getCorrectAns().equals(questionModelList.get(position).getCorrectAns())
            && questionModel.getSetNo().equals(questionModelList.get(position).getSetNo())){
                match=true;
                matchQuestionPosition=i;
            }
            i++;
        }
        return match;
    }
}