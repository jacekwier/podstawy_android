package com.example.yggdrasil.peoplephotos;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PhotoActivity extends AppCompatActivity {

    private static String language;
    private String photoPath="";
    private ImageButton imageButton;

    private String name;
    private String surname;
    private String birthDate;
    private String photoPathDB="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        imageButton=(ImageButton)findViewById(R.id.imageButtonPhoto);
        if (savedInstanceState!=null){
            name=savedInstanceState.getString("name");
            surname=savedInstanceState.getString("surname");
            birthDate=savedInstanceState.getString("birthDate");
            photoPath=savedInstanceState.getString("photoPath");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        if(getIntent().getStringExtra("name")!=null){
            name=getIntent().getStringExtra("name");
            surname=getIntent().getStringExtra("surname");
            birthDate=getIntent().getStringExtra("birthDate");
        }
        if(getIntent().getStringExtra("photoPathDB")!=null){
            photoPathDB=getIntent().getStringExtra("photoPathDB");
            if(photoPath.equals("")) {
                photoPath = photoPathDB;
            }
        }
        if(getIntent().getStringExtra("photoPath")!=null){
            photoPath=getIntent().getStringExtra("photoPath");
        }
        getSupportActionBar().setTitle(R.string.app_name);
        ((TextView)findViewById(R.id.textViewNameSurname)).setText(name+" "+surname+"\n"+birthDate);
        ((Button)findViewById(R.id.buttonSave)).setText(R.string.button_next);
        ((Button)findViewById(R.id.buttonBack)).setText(R.string.button_back);
        if(!photoPath.equals("")){
            showImage();
        }
    }

    public void makePhoto(View view)
    {
        Intent intentPhoto=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp+".jpg";
        if(!photoPath.equals(""))
        {
            if(!photoPathDB.equals(photoPath)) {
                imageFileName = photoPath.substring(photoPath.lastIndexOf("/") + 1);
            }
        }

        File imageFile=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),imageFileName);
        Uri tempuri=Uri.fromFile(imageFile);
        intentPhoto.putExtra(MediaStore.EXTRA_OUTPUT, tempuri);
        photoPath=imageFile.getAbsolutePath();
        startActivityForResult(intentPhoto, 1);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK) {
            showImage();
        }
        else{
            try{
                showImage();
            }catch (NullPointerException e){
                File file=new File(photoPath);
                file.delete();
                photoPath=photoPathDB;
            }
        }
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

        imageButton.setImageBitmap(bitmap);
    }

    private int maxi(int a, int b)
    {
        if (a>b)
            return a;
        else
            return b;
    }

    public void onClickBack(View view)
    {
        Intent intent=new Intent(this,DataActivity.class);
        intent.putExtra("language",language);
        intent.putExtra("name",name);
        intent.putExtra("surname",surname);
        intent.putExtra("birthDate",birthDate);
        if(getIntent().getIntExtra("id",-1)!=-1)
        {
            intent.putExtra("id",getIntent().getIntExtra("id",-1));
            intent.putExtra("photoPathDB",photoPathDB);
        }
        if(!photoPath.equals("") && !photoPath.equals(photoPathDB)){
            File file=new File(photoPath);
            file.delete();
        }
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("name",name);
        savedInstanceState.putString("surname",surname);
        savedInstanceState.putString("birthDate",birthDate);
        savedInstanceState.putString("photoPath",photoPath);
    }

    public void onClickNext(View view){
        if(photoPath.equals("")) {
            Toast.makeText(getApplication().getBaseContext(), R.string.toast_takePhoto, Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent = new Intent(this, SummaryActivity.class);
            intent.putExtra("language", language);
            intent.putExtra("name", name);
            intent.putExtra("surname", surname);
            intent.putExtra("birthDate", birthDate);
            intent.putExtra("photoPath", photoPath);
            if(getIntent().getIntExtra("id",-1)!=-1){
                intent.putExtra("id",getIntent().getIntExtra("id",-1));
                intent.putExtra("photoPathDB",photoPathDB);
            }
            startActivity(intent);
        }
    }
}
