package com.example.yggdrasil.peoplephotos;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private DatabasePeople db=new DatabasePeople(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void showPeople(){
        ArrayList<Person> listOfPeople=new ArrayList<>();
        Cursor cursor=db.getRow();
        while(cursor.moveToNext())
        {
            int id=cursor.getInt(0);
            String name=cursor.getString(1);
            String surname=cursor.getString(2);
            String birthDate=cursor.getString(3);
            String photoPath=cursor.getString(4);
            Person person=new Person(id, name,surname,birthDate,photoPath);
            listOfPeople.add(person);
        }
        TableLayout tableLayout=(TableLayout)findViewById(R.id.tableLayout);
        tableLayout.removeAllViews();
        for (final Person person:listOfPeople)
        {
            TableRow tableRow=new TableRow(this);
            tableRow.setGravity(Gravity.CENTER_HORIZONTAL);
            tableRow.setId(person.getId());
            tableLayout.addView(tableRow);
            TextView textView=new TextView(this);
            textView.setText(person.getName()+" "+person.getSurname()+"\n"+person.getBirthDate());
            textView.setTextSize(18);
            TableRow.LayoutParams paramsExample = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT,1.0f);
            textView.setLayoutParams(paramsExample);
            textView.setTextColor(Color.BLACK);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            tableRow.addView(textView);

            tableRow=new TableRow(this);
            tableRow.setGravity(Gravity.CENTER_HORIZONTAL);
            tableRow.setId(person.getId());
            tableLayout.addView(tableRow);
            Button bDelete = new Button(this);
            bDelete.setText(R.string.button_delete);
            tableRow.addView(bDelete);
            bDelete.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            db.deletePerson(person.getId());
                            File file=new File(person.getPhotoPath());
                            file.delete();
                            showPeople();
                        }
                    }
            );
            Button buttonEdit = new Button(this);
            buttonEdit.setText(R.string.button_edit);
            tableRow.addView(buttonEdit);
            buttonEdit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(),DataActivity.class);
                        intent.putExtra("id",person.getId());
                        intent.putExtra("name",person.getName());
                        intent.putExtra("surname",person.getSurname());
                        intent.putExtra("birthDate",person.getBirthDate());
                        intent.putExtra("photoPathDB",person.getPhotoPath());
                        startActivity(intent);
                    }
                }
            );

            if(!person.getPhotoPath().equals(""))
            {
                tableRow=new TableRow(this);
                tableLayout.addView(tableRow);
                ImageView imageView=new ImageView(this);Bitmap bitmap= BitmapFactory.decodeFile(person.getPhotoPath());
                double x=(double)(maxi(bitmap.getWidth(),bitmap.getHeight()));
                x=300/x;
                int width=(int)(bitmap.getWidth()*x);
                int height=(int)(bitmap.getHeight()*x);
                bitmap=bitmap.createScaledBitmap(bitmap,width,height,false);

                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(person.getPhotoPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int e= exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,999);
                if(e!=1) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                }
                imageView.setImageBitmap(bitmap);
                tableRow.addView(imageView);
                tableRow.setGravity(Gravity.CENTER_HORIZONTAL);

                tableRow=new TableRow(this);
                tableLayout.addView(tableRow);
                paramsExample = new TableRow.LayoutParams(50, 50,1.0f);
                textView=new TextView(this);
                textView.setLayoutParams(paramsExample);
                tableRow.addView(textView);

            }
        }
    }

    private int maxi(int a, int b)
    {
        if (a>b)
            return a;
        else
            return b;
    }

    public void clickAddNew(View view){
        Intent intent=new Intent(this,DataActivity.class);
        intent.putExtra("language",language);
        startActivity(intent);
    }
    private static String language;
    protected void onResume(){
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
        ((Button)findViewById(R.id.buttonLanguages)).setText(R.string.button_lang);
        ((Button)findViewById(R.id.buttonAddNew)).setText(R.string.button_addNew);
        getSupportActionBar().setTitle(R.string.app_name);
        showPeople();
    }

    public void chooseLanguage(View view){
        Intent intent=new Intent(this,LanguageActivity.class);
        startActivity(intent);
    }
}
