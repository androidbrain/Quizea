package com.example;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.qna.R;

import java.util.List;

public class GridAdapter extends BaseAdapter {
    private List<String> sets;
    private String categoryTitle;

    public GridAdapter(List<String> sets, String categoryTitle) {
        this.sets = sets;
        this.categoryTitle=categoryTitle;
    }

    @Override
    public int getCount() {
        return sets.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View view;
        if(convertView == null){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.set_items,parent,false);
        }
        else {
            view = convertView;
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent questionIntent=new Intent(parent.getContext(), QuestionsActivity.class);
                questionIntent.putExtra("categoryTitle", categoryTitle);
                questionIntent.putExtra("setId", sets.get(position));
                parent.getContext().startActivity(questionIntent);
            }
        });

        ( (TextView)view.findViewById(R.id.textView_si)).setText(String.valueOf(position+1));
        return view;
    }
}
