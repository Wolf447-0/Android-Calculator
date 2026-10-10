package com.example.calculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.License;

public class MainActivity extends AppCompatActivity {

    private String currentStr;
    private final static String currentStrKey = "CURRENT_STRING";
    private final static String historyKey = "HISTORY";
    private History history;
    private TextView display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        License.iConfirmNonCommercialUse("MyCalculatorApp");

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.tvDisplay);
        Button btnEqual = findViewById(R.id.btnEqual);
        Button btnHistory = findViewById(R.id.btnHistory);

        history = new History();

        Intent intent = new Intent(this, HistoryActivity.class);


        View.OnClickListener onClickListenerNumberBtn = v -> {
            Button button = (Button) v;
            String buttonText = button.getText().toString();
            if (display.getText().toString().charAt(0) == '0') {
                display.setText(buttonText);
            } else display.append(buttonText);

        };

        View.OnClickListener onClickListenerActionBtn = v -> {
            Button button = (Button) v;
            String buttonText = button.getText().toString();
            int id = button.getId();
            currentStr = display.getText().toString();
            char lastChar = currentStr.charAt(currentStr.length() - 1);

            switch (buttonText) {
                case "÷":
                    buttonText = "/";
                    break;
                case "×":
                    buttonText = "*";
                    break;
                case "√":
                    buttonText = "sqrt(";
                    break;
                case "log":
                    buttonText = "log(";
                    break;
                case "sin":
                    buttonText = "sin(";
                    break;
                case "cos":
                    buttonText = "cos(";
                    break;
                case "tg":
                    buttonText = "tg(";
                    break;
                case "ln":
                    buttonText = "ln(";
                    break;
            }

            if (display.getText().toString().charAt(0) == '0') {
                if (!isAction(id)) {
                    display.setText(buttonText);
                    return;
                }
                else return;
            }

            if (id == R.id.btnEqual) {
                if (!(currentStr.equals("0") || checkLastAction(currentStr, id))) {
                    try {
                        history.addLine(currentStr);
                        double res_double = calculate(currentStr);
                        if(res_double == Math.floor(res_double)
                                && !Double.isInfinite(res_double)
                                && !Double.isNaN(res_double)){
                            int res_int = (int)res_double;
                            display.setText(String.valueOf(res_int));
                            history.addLine("Ответ: " + String.valueOf(res_int));
                        }
                        else{
                            display.setText(String.valueOf(res_double));
                            history.addLine("Ответ: " + String.valueOf(res_double));
                        }

                    } catch (Exception ex) {
                        display.setText("Error");
                    }
                }
                return;
            }


            if (id == R.id.btnAC) {
                display.setText("0");
                return;
            }

            if (id == R.id.btnDelete) {
                display.setText(currentStr.substring(0, currentStr.length() - 1));
                if (display.getText().toString().isEmpty()) {
                    display.setText("0");
                }
                return;
            }



            if(isAction(id)) {
                if (checkLastAction(currentStr, id)) {
                   return;
                }
                else if (id == R.id.btnDot && hasDotInCurrentNumber(currentStr)) {
                    return;
                }
                else display.append(buttonText);
            }

            else if(id == R.id.btnOpenBracket){
                if (lastChar == '+' || lastChar == '-' || lastChar == '*' || lastChar == '/' || lastChar == '(') {
                    display.append(buttonText);
                }
            }

            else if(id == R.id.btnCloseBracket){
                int openBrackets = countChar(currentStr, '(');
                int closeBrackets = countChar(currentStr, ')');

                boolean hasUnclosedBracket = openBrackets > closeBrackets;
                boolean isValidLastChar = Character.isDigit(lastChar) || lastChar == ')'
                        || lastChar == '!' || lastChar == 'π';

                if (hasUnclosedBracket && isValidLastChar) {
                    display.append(")");
                }
            }

           else if(isFunction(id)){

                if(id == R.id.btnFact || id == R.id.btnPower){
                    if(Character.isDigit(lastChar) || lastChar == ')'){
                        display.append(buttonText);
                    } else if (lastChar == '.' || lastChar == '(') {
                        return;
                    }
                }
                if(Character.isDigit(lastChar) || lastChar == '.'){
                    return;
                }
                display.append(buttonText);
            }

        };

        btnHistory.setOnClickListener( v ->
        {
            intent.putExtra(History.class.getSimpleName(),history);
            startActivity(intent);
        });

        btnEqual.setOnClickListener(onClickListenerActionBtn);

        setListers(onClickListenerNumberBtn, onClickListenerActionBtn);


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putString(currentStrKey, display.getText().toString());
        outState.putSerializable(historyKey,history);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        history = (History) savedInstanceState.getSerializable(historyKey);
        currentStr = savedInstanceState.getString(currentStrKey);
        display.setText(currentStr);
    }

    int countChar(String str, char ch) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ch) {
                count++;
            }
        }
        return count;
    }

    boolean checkLastSForD(String currentStr){
        for (int i = 0; i < 10; i++) {
            if(currentStr.charAt(currentStr.length()-1) == (char)i){
                return true;
            }
        }
        return false;
    }

    double calculate(String currentStr) {
        Expression expression = new Expression(currentStr);
        return expression.calculate();
    }

    int[] arrayActionId() {
        int[] actionsId = new int[]{
                R.id.btnPlus, R.id.btnMinus, R.id.btnMultiply, R.id.btnDivide,
                R.id.btnDot,
                R.id.btnComma,
                R.id.btnAC, R.id.btnDelete
        };
        return actionsId;
    }

    int[] arrayFunctionId() {
        int[] actionsId = new int[]{
                R.id.btnSin, R.id.btnCos, R.id.btnTg, R.id.btnLn, R.id.btnLog,
                R.id.btnFact, R.id.btnPower, R.id.btnSqrt, R.id.btnPi,

        };
        return actionsId;
    }

    boolean isAction(int actID) {
        for (int id : arrayActionId()) {
            if (actID == id || actID == R.id.btnEqual) {
                return true;
            }
        }
        return false;
    }

    boolean isFunction(int actID) {
        for (int id : arrayFunctionId()) {
            if (actID == id) {
                return true;
            }
        }
        return false;
    }

    boolean checkLastAction(String currentStr, int id) {
        char lastAction = currentStr.charAt(currentStr.length() - 1);
        char[] lastAct = new char[]{'*','-','+','/','(','.',','};

        if (isAction(id)) {
            for (char i : lastAct) {

                if (lastAction == i) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasDotInCurrentNumber(String currentStr) {
        if (currentStr == null || currentStr.isEmpty()) return false;
        String[] parts = currentStr.split("[+\\-*/(]");
        String currentNumber = parts[parts.length - 1];
        return currentNumber.contains(".");
    }


    void setListers(View.OnClickListener listerNum, View.OnClickListener listenerAct) {
        int[] inputNumberButtonIds = new int[]{
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };

        int[] inputActionButtonIds = arrayActionId();
        int[] inputFunctionButtonIds = arrayFunctionId();

        findViewById(R.id.btnOpenBracket).setOnClickListener(listenerAct);
        findViewById(R.id.btnCloseBracket).setOnClickListener(listenerAct);

        for (int id : inputNumberButtonIds) {
            findViewById(id).setOnClickListener(listerNum);
        }

        for (int id : inputActionButtonIds) {
            findViewById(id).setOnClickListener(listenerAct);
        }

        for (int id : inputFunctionButtonIds) {
            findViewById(id).setOnClickListener(listenerAct);
        }
    }

}