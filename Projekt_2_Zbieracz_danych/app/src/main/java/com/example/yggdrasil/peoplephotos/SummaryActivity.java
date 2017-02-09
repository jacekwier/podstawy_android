package com.example.yggdrasil.peoplephotos;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class SummaryActivity extends AppCompatActivity {

    private static String language;
    private String name;
    private String surname;
    private String birthDate;
    private String photoPath;
    private String photoPathDB;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
    }

    @Override
    protected void onResume() {
        super.onResume();

        name=getIntent().getStringExtra("name");
        surname=getIntent().getStringExtra("surname");
        birthDate=getIntent().getStringExtra("birthDate");
        photoPath=getIntent().getStringExtra("photoPath");
        if(getIntent().getIntExtra("id",-1)!=-1){
            id=getIntent().getIntExtra("id",-1);
            photoPathDB=getIntent().getStringExtra("photoPathDB");
        }

        if(getIntent().getStringExtra("language")!=null) {
            language = getIntent().getStringExtra("language");
        }
        if(language!=null){
            Locale locale = new Locale(language);
            Resources resources = getResources();
            DisplayMetrics displayMetrics = resources.getDisplayMetrics();
            Configuration configuration = resources.getConfiguration();
            configuration.locale = locale;
            resources.updateConfiguration(configuration, displayMetrics);
        }
        getSupportActionBar().setTitle(R.string.app_name);
        ((TextView)findViewById(R.id.textViewSummary)).setText(R.string.textView_summary);
        ((Button)findViewById(R.id.buttonBack)).setText(R.string.button_back);
        ((Button)findViewById(R.id.buttonSave)).setText(R.string.button_save);
        ((Button)findViewById(R.id.buttonDiscard)).setText(R.string.button_discard);
        ((TextView)findViewById(R.id.textViewData)).setText(name+" "+surname+"\n"+birthDate);
        showImage();
    }

    public void onClickBack(View view){
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra("language", language);
        intent.putExtra("name", name);
        intent.putExtra("surname", surname);
        intent.putExtra("birthDate", birthDate);
        intent.putExtra("photoPath", photoPath);
        if(getIntent().getIntExtra("id",-1)!=-1){
            intent.putExtra("id",id);
            intent.putExtra("photoPathDB",photoPathDB);
        }

        startActivity(intent);
    }

    public void onClickDiscard(View view){
        File file=new File(photoPath);
        file.delete();
        goToMainActivity();
    }

    private void showImage()
    {
        Bitmap bitmap= BitmapFactory.decodeFile(photoPath);
        double x=(double)(maxi(bitmap.getWidth(),bitmap.getHeight()));
        x=2000/x;
        int width=(int)(bitmap.getWidth()*x);
        int height=(int)(bitmap.getHeight()*x);
        bitmap=bitmap.createScaledBitmap(bitmap,width,height,false);

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int e= exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,999);
        if(e!=1) {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        ((ImageView)findViewById(R.id.imageView)).setImageBitmap(bitmap);
    }

    private int maxi(int a, int b)
    {
        if (a>b)
            return a;
        else
            return b;
    }

    public void onClickSave(View view){
        if(getIntent().getIntExtra("id",-1)==-1) {
            DatabasePeople db = new DatabasePeople(this);
            db.addPerson(name, surname, birthDate, photoPath);
        }
        else{
            DatabasePeople db = new DatabasePeople(this);
            db.updatePerson(getIntent().getIntExtra("id",0),name,surname,birthDate,photoPath);
            if(!photoPathDB.equals(photoPath)) {
                File file = new File(photoPathDB);
                file.delete();
            }
        }
        goToMainActivity();
    }

    private void goToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("language",language);
        startActivity(intent);
    }
}
