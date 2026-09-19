package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.License;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        License.iConfirmNonCommercialUse("MyCalculatorApp");

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        TextView display = findViewById(R.id.tvDisplay);
        Button btnEqual = findViewById(R.id.btnEqual);


        View.OnClickListener onClickListenerNumberBtn = v -> {
            Button button = (Button) v;
            String buttonText = button.getText().toString();
            if (display.getText().toString().charAt(0) == '0') {
                display.setText(buttonText);
            }
            else display.append(buttonText);

        };

        View.OnClickListener onClickListenerActionBtn = v -> {
            Button button = (Button) v;
            String buttonText = button.getText().toString();
            int id = button.getId();
            String currentStr = display.getText().toString();

            if (id == R.id.btnEqual) {
                if (!(currentStr.equals("0") || checkLastAction(currentStr))){
                    try {
                        double res = calculate(currentStr);
                        display.setText(String.valueOf(res));
                    }
                    catch (Exception ex){
                        display.setText("Error");
                    }
                }
                return;
            }

            if (id == R.id.btnAC){
                display.setText("0");
                return;
            }

            if (id == R.id.btnDelete) {
                display.setText(currentStr.substring(0,currentStr.length()-1));
                if (display.getText().toString().isEmpty()){
                    display.setText("0");
                }
                return;
            }

            switch (buttonText) {
                case "÷":  buttonText = "/"; break;
                case "×":  buttonText = "*"; break;
                case "√":  buttonText = "sqrt("; break;
                case "log": buttonText = "log("; break;
                case "sin": buttonText = "sin("; break;
                case "cos": buttonText = "cos("; break;
                case "tg": buttonText = "tg("; break;
                case "ln": buttonText = "ln("; break;
            }


             if (!checkLastAction(currentStr)) {
                display.append(buttonText);
            }


        };

        btnEqual.setOnClickListener(onClickListenerActionBtn);

      setListeers(onClickListenerNumberBtn,onClickListenerActionBtn);



    }

    double calculate(String currentStr){
        Expression expression = new Expression(currentStr);
        return expression.calculate();
    }

    int[] arrayActionId(){
        int[] actionsId = new int[]{
                R.id.btnPlus, R.id.btnMinus, R.id.btnMultiply, R.id.btnDivide,
                R.id.btnSin, R.id.btnCos, R.id.btnTg, R.id.btnLn, R.id.btnLog,
                R.id.btnFact, R.id.btnPower, R.id.btnSqrt, R.id.btnDot,
                R.id.btnOpenBracket, R.id.btnCloseBracket, R.id.btnComma, R.id.btnPi,
                R.id.btnAC, R.id.btnDelete
        };
        return actionsId;
    }
    
    boolean checkLastAction(String currentStr){
       char lastAction = currentStr.charAt(currentStr.length()-1);
       Button button;
       for(int id: arrayActionId()){
           button = findViewById(id);
           if (lastAction == button.getText().toString().charAt(button.getText().toString().length()-1)){
               return true;
           }
       }
       return false;
    }


    void setListeers(View.OnClickListener listerNum, View.OnClickListener listenerAct) {
        int[] inputNumberButtonIds = new int[] {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };

        int[] inputActionButtonIds = arrayActionId();

        for (int id : inputNumberButtonIds) {
            findViewById(id).setOnClickListener(listerNum);
        }

        for (int id : inputActionButtonIds) {
            findViewById(id).setOnClickListener(listenerAct);
        }
    }

}