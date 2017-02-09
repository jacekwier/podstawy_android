package com.example.yggdrasil.peoplephotos;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DataActivity extends AppCompatActivity {

    private static String language;
    private Button buttonChangeBirthDate;
    private TextView textViewBirthDate;
    private DateFormat formatDateTime=DateFormat.getDateInstance(DateFormat.SHORT,Locale.FRANCE);
    private Calendar dateTime;
    private EditText editTextName;
    private EditText editTextSurname;
    private String photoPathDB="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        buttonChangeBirthDate=(Button)findViewById(R.id.buttonChangeBirthDate);
        buttonChangeBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDate();
            }
        });
        textViewBirthDate=(TextView) findViewById(R.id.textViewBirhDate);
        editTextName=(EditText)findViewById(R.id.editTextName);
        editTextSurname=(EditText)findViewById(R.id.editTextSurname);
        textViewBirthDate=(TextView)findViewById(R.id.textViewBirhDate);
        textViewBirthDate.setText("...");

        if(savedInstanceState!=null){
            editTextName.setText(savedInstanceState.getString("name"));
            editTextSurname.setText(savedInstanceState.getString("surname"));
            Date date=null;
            try {
                date = formatDateTime.parse(savedInstanceState.getString("birthDate"));
            } catch (ParseException e) {
                textViewBirthDate.setText("...");
            } finally {
                if (date != null) {
                    dateTime = Calendar.getInstance();
                    dateTime.setTime(date);
                    textViewBirthDate.setText(formatDateTime.format(date));
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String languageFromIntent=getIntent().getStringExtra("language");
        String nameFromIntent=getIntent().getStringExtra("name");
        String surnameFromIntent=getIntent().getStringExtra("surname");
        String birthDateFromIntent=getIntent().getStringExtra("birthDate");

        if(languageFromIntent!=null) {
            language = languageFromIntent;
        }
        if(TextUtils.isEmpty(editTextName.getText())) {//odnosnie przekrecania ekranu
            if (nameFromIntent != null) {
                editTextName.setText(nameFromIntent);
            }
        }
        if(TextUtils.isEmpty(editTextSurname.getText())){
            if (surnameFromIntent != null){
                editTextSurname.setText(surnameFromIntent);
            }
        }
        if(textViewBirthDate.getText().equals("...")) {
            if (birthDateFromIntent != null) {
                Date date = null;
                try {
                    date = formatDateTime.parse(birthDateFromIntent);
                } catch (ParseException e) {
                    textViewBirthDate.setText("...");
                } finally {
                    if (date != null) {
                        dateTime = Calendar.getInstance();
                        dateTime.setTime(date);
                        textViewBirthDate.setText(formatDateTime.format(date));
                    }
                }

            }
        }
        if(getIntent().getStringExtra("photoPathDB")!=null){
            photoPathDB=getIntent().getStringExtra("photoPathDB");
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
        ((TextView)findViewById(R.id.textViewName)).setText(R.string.textView_name);
        ((TextView)findViewById(R.id.textViewSurname)).setText(R.string.textView_surname);
        ((TextView)findViewById(R.id.textViewBirthDateCaption)).setText(R.string.textView_BirthDateCaption);
        ((Button)findViewById(R.id.buttonCancel)).setText(R.string.button_cancel);
        ((Button)findViewById(R.id.buttonSave)).setText(R.string.button_next);
        ((Button)findViewById(R.id.buttonChangeBirthDate)).setText(R.string.button_ChangeBirthDate);
    }

    public void onClickCancel(View view){
        Intent intent=new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void updateDate(){
        if(dateTime==null){
            dateTime=Calendar.getInstance();
        }
        new DatePickerDialog(this, d, dateTime.get(Calendar.YEAR),dateTime.get(Calendar.MONTH),dateTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            dateTime.set(Calendar.YEAR,year);
            dateTime.set(Calendar.MONTH,month);
            dateTime.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            textViewBirthDate.setText(formatDateTime.format(dateTime.getTime()));
        }
    };

    public void onClickNext(View view){
        Boolean flag=true;
        String name=editTextName.getText().toString();
        String surname=editTextSurname.getText().toString();
        String birthDate=textViewBirthDate.getText().toString();
        if(name.length()<2 || name.length()>15){
            flag=false;
        }
        else{
            for(int i=0;i<name.length();i++){
                if(!Character.isLetter(name.charAt(i))){
                    flag=false;
                }
            }
        }

        if(surname.length()<2 || surname.length()>15){
            flag=false;
        }
        else{
            for(int i=0;i<surname.length();i++){
                if(!Character.isLetter(surname.charAt(i))){
                    flag=false;
                }
            }
        }

        if(flag==false){
            Toast.makeText(getApplication().getBaseContext(),R.string.toast_wrongNameSurname, Toast.LENGTH_SHORT).show();
        }
        if(flag==true && birthDate=="..."){
            Toast.makeText(getApplication().getBaseContext(),R.string.toast_SetBirthDate, Toast.LENGTH_SHORT).show();
            flag=false;
        }
        if(flag==true){
            Intent intent=new Intent(this,PhotoActivity.class);
            intent.putExtra("language",language);
            intent.putExtra("name",name);
            intent.putExtra("surname",surname);
            intent.putExtra("birthDate",birthDate);

            if(getIntent().getIntExtra("id",-1)!=-1){
                intent.putExtra("id",getIntent().getIntExtra("id",0));
                intent.putExtra("photoPathDB",photoPathDB);
            }
            startActivity(intent);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("name",editTextName.getText().toString());
        savedInstanceState.putString("surname",editTextSurname.getText().toString());
        savedInstanceState.putString("birthDate",textViewBirthDate.getText().toString());
    }
}
