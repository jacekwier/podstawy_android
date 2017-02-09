package com.example.yggdrasil.calculator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private ArrayList<Double> numberList;
    private ArrayList<Character> signs;
    private StringBuffer number;
    private String operation;
    private ArrayList<String> operationList;
    TextView textView;
    String buttonText;
    Boolean toClear;
    private Logger logger= Logger.getLogger("LOGGER");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        numberList=new ArrayList<>();
        signs=new ArrayList<>();
        operationList=new ArrayList<>();
        textView=(TextView) findViewById(R.id.textView);
        number=new StringBuffer("");
        textView.setMovementMethod(new ScrollingMovementMethod());
        toClear=false;
        operation="";
    }

    public void clickNumber(View view)
    {
        if (toClear)
            clear();
        Button b=(Button)view;
        buttonText=b.getText().toString();
        switch (buttonText)
        {
            case "+":
                if(tryMakeNumber())
                {
                    signs.add('+');
                    logger.debug("PLUS SIGN CLICKED");
                }
                break;
            case "-":
                if(number.toString().equals(""))
                {
                    number.append("-");
                    logger.debug("MINUS TO NUMBER CLICKED");
                }
                else if (tryMakeNumber())
                {
                    signs.add('-');
                    logger.debug("MINUS SIGN CLICKED");
                }
                break;
            case "*":
                if(tryMakeNumber())
                {
                    signs.add('*');
                    logger.debug("MULTIPLE SIGN CLICKED");
                }
                break;
            case "/":
                if(tryMakeNumber())
                {
                    signs.add('/');
                    logger.debug("DIVIDE SIGN CLICKED");
                }
                break;
            case ".": //zabezpieczenie kropki
                if (isT0oLong(number))
                    break;
                if (number.length()>=1 )
                {
                    if (number.indexOf(".")==-1)
                    {
                        number.append(".");
                        logger.debug("DOT SIGN CLICKED");
                    }
                }
                break;
            case "0": //zabezpieczenie 0
                if (isT0oLong(number))
                    break;
                if (number.length()>2)
                {
                    number.append("0");
                    logger.debug("DIGIT 0 CLICKED");
                }
                else if(number.toString().equals("0") || number.toString().equals("-0"))
                {
                    break;
                }
                else
                    number.append("0");
                break;
            default:
                if (isT0oLong(number))
                    break;
                number.append(buttonText);
                logger.debug("DIGIT "+buttonText+" CLICKED");
        }
        textView.setText(fastHistory()+ getOperationText()+number.toString());
    }

    private boolean isT0oLong(StringBuffer string)
    {
        if (string.length()<12)
            return false;
        else
            return true;
    }

    private boolean tryMakeNumber()
    {
        Double newNumber;
        if (!number.toString().equals("") && (!number.toString().equals("-"))) {
            try
            {
                newNumber = Double.parseDouble(number.toString());
                if (newNumber==0 && signs.get(signs.size()-1).equals('/')){
                    return false;
                }
                numberList.add(newNumber);
                number.delete(0,number.length());
                return true;
            }
            catch (Exception e)
            {
                return false;
            }
        }
        return false;
    }


    private String getOperationText()
    {
        String textToReturn="";
        int numberListSize=numberList.size();
        int signsSize=signs.size();
        int max;
        if (numberListSize>=signsSize)
            max=numberListSize;
        else
            max=signsSize;
        for(int i=0;i<max;i++)
        {
            if (i<numberListSize)
                textToReturn+=numberList.get(i);
            if(i<signsSize)
                textToReturn+=signs.get(i).toString();
        }
        return textToReturn;
    }

    public void clickEqual(View view)
    {
        logger.debug("EQUAL SIGN CLICKED");
        if(toClear){
            clear();
        }
        tryMakeNumber();
        Double result=null;
        if((signs.size()+1)==numberList.size())
        {
            operation= getOperationText();
            for(int i=0;i<signs.size();i++){
                switch(signs.get(i)){
                    case '*':
                        result=numberList.get(i)*numberList.get(i+1);
                        numberList.set(i,result);
                        numberList.remove(i+1);
                        signs.remove(i);
                        i--;
                        break;
                    case '/':
                        result=numberList.get(i)/numberList.get(i+1);
                        numberList.set(i,result);
                        numberList.remove(i+1);
                        signs.remove(i);
                        i--;
                        break;
                }
            }
            result=numberList.get(0);
            for (int i=0;i<signs.size();i++)
            {
                switch (signs.get(i))
                {
                    case '+':
                        result+=numberList.get(i+1);
                        break;
                    case '-':
                        result-=numberList.get(i+1);
                        break;
                }
            }
            operation+="="+result;
            operationList.add(operation);
            textView.setText(fastHistory()+"=");
            toClear=true;
        }
    }

    private void clear()
    {
        signs.removeAll(signs);
        numberList.removeAll(numberList);
        operation="";
        number.delete(0,number.length());
        textView.setText(fastHistory()+"=");
        toClear=false;
    }

    public void toHistoryActivity(View view)
    {
        logger.debug("HISTORY CLICKED");
        tryMakeNumber();
        if(toClear){
            clear();
        }
        Intent intent=new Intent(this, HistoryActivity.class);
        intent.putExtra("operationList",operationList);
        intent.putExtra("signs",signs);
        intent.putExtra("numberList",numberList);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle extras=getIntent().getExtras();
        try {
        operationList=extras.getStringArrayList("operationList");
        signs=(ArrayList<Character>) extras.getSerializable("signs");
        numberList=(ArrayList<Double>)extras.getSerializable("numberList");
            if ((numberList.size() != 0) && (numberList.size() > signs.size())) {
                number.append(numberList.get(numberList.size() - 1));
                numberList.remove(numberList.size() - 1);
            }
            if (getOperationText().equals("")) {
                textView.setText(fastHistory() + "=");
            }
            else{
                textView.setText(fastHistory() + getOperationText() + number);
            }
        }
        catch (Exception e){
            numberList=new ArrayList<>();
            signs=new ArrayList<>();
            operationList=new ArrayList<>();
            textView=(TextView) findViewById(R.id.textView);
            number=new StringBuffer("");
            textView.setMovementMethod(new ScrollingMovementMethod());
            toClear=false;
            operation="";
            BasicConfigurator.configure();
            logger.setLevel(Level.DEBUG);
        }

    }

    public void CE(View view)
    {
        logger.debug("C SIGN CLICKED");
        if(toClear){
            clear();
        }
        if(number.length()!=0){
            number.delete(0,number.length());
            textView.setText(fastHistory()+ getOperationText());
            if(numberList.size()==0){
                textView.setText(fastHistory()+"=");
            }
        }
        else{
            if(numberList.size()>0){
                signs.remove(signs.size()-1);
                number.append(numberList.get(numberList.size()-1));
                numberList.remove(numberList.size()-1);
                textView.setText(fastHistory()+ getOperationText()+number);
            }
        }
    }

    private String fastHistory(){
        String toReturn="";
        int sizeOfOperationList=operationList.size();
        if(sizeOfOperationList>=3){
            toReturn+=operationList.get(sizeOfOperationList-3)+"\n";
        }
        if(sizeOfOperationList>=2){
            toReturn+=operationList.get(sizeOfOperationList-2)+"\n";
        }
        if(sizeOfOperationList>=1){
            toReturn+=operationList.get(sizeOfOperationList-1)+"\n";
        }
        return toReturn;
    }
}
