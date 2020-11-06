package com.example;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qna.R;

import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.MyViewHolder> {
    private List<QuestionModel> list;

    public BookmarkAdapter(List<QuestionModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String qus= list.get(position).getQuestion();
        String ans= list.get(position).getCorrectAns();

        holder.setData(qus,ans, position);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView question, answer;
        private ImageView deletebtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            question=itemView.findViewById(R.id.question);
            answer=itemView.findViewById(R.id.answer);
            deletebtn=itemView.findViewById(R.id.delete);
        }
        private void setData(String questionTxt, String answerTxt, final int position){
            question.setText(questionTxt);
            answer.setText(answerTxt);
            deletebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    list.remove(position);
                    notifyItemRemoved(position);
                }
            });

        }
    }
}
