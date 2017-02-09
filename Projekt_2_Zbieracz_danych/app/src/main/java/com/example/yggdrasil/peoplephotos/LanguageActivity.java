package com.example.yggdrasil.peoplephotos;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.Locale;

public class LanguageActivity extends AppCompatActivity {

    String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setTitle(R.string.app_name);
    }

    public void clickPlButton(View view)
    {
        language="pl";
        updateLocale();
    }

    public void clickEnButton(View view) {
        language = "default";
        updateLocale();
    }

    public void updateLocale()
    {
        Locale locale=new Locale(language);
        Resources resources=getResources();
        DisplayMetrics displayMetrics=resources.getDisplayMetrics();
        Configuration configuration=resources.getConfiguration();
        configuration.locale=locale;
        resources.updateConfiguration(configuration,displayMetrics);
        Intent intent=new Intent(this,MainActivity.class);
        intent.putExtra("language",language);
        startActivity(intent);
    }
}
