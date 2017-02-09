package com.example.yggdrasil.calculator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity
{
    private ArrayList<String> operationList;
    private ArrayList<Character> signs;
    private ArrayList<Double> numberList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Bundle extras=getIntent().getExtras();
        operationList=extras.getStringArrayList("operationList");
        signs=(ArrayList<Character>) extras.getSerializable("signs");
        numberList=(ArrayList<Double>) extras.getSerializable("numberList");
        showHistory();
    }

    private void showHistory(){
        final TextView textView=(TextView) findViewById(R.id.textViewHistory);
        textView.setMovementMethod(new ScrollingMovementMethod());
        String text="";
        for(String s:operationList)
        {
            text+=s;
            text+="\n\n";
        }
        textView.setText(text);
        final ScrollView scrollView=(ScrollView)findViewById(R.id.ScrollView);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    public void onClickDeleteHisory(View view){
        operationList.clear();
        showHistory();
    }

    public void toMainActivity(View view)
    {
        Intent intent=new Intent(this, MainActivity.class);
        Bundle bundle=new Bundle();
        bundle.putStringArrayList("operationList",operationList);
        bundle.putSerializable("signs",signs);
        bundle.putSerializable("numberList",numberList);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}