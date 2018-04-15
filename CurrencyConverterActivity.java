package com.arifulislam.tourguidepro.currency;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.arifulislam.tourguidepro.R;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class CurrencyConverterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText fromMoneyEdit;
    private EditText toMoneyEdit;
    private TextView fromMoneySpinner;
    private Spinner toMoneySpinner;
    private Button convertMoneyBtn;
    private double convertFromMoney;
    double convertToMoney;
    double fromMoney ;
    double result;

    private Map<String,Double> currency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_converter);

        fromMoneyEdit = findViewById(R.id.from_money_edit);
        toMoneyEdit = findViewById(R.id.to_money_edit);
        fromMoneySpinner = findViewById(R.id.from_money_spinner);
        toMoneySpinner = findViewById(R.id.to_money_spinner);
        convertMoneyBtn = findViewById(R.id.convert_money_btn);
        convertMoneyBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        DecimalFormat df = new DecimalFormat("#.###");
        String fromMoneyString = fromMoneyEdit.getText().toString();
        double money = Double.parseDouble(fromMoneyString);
        //toMoneyEdit.setText("0.0");
        String toSp = toMoneySpinner.getSelectedItem().toString();

        if (!TextUtils.isEmpty(fromMoneyString)) {
            if (currency != null) {
                currency = new HashMap<String,Double>();
                currency.put("US Dollar", 82.57);
                currency.put("EURO", 102.31);
                currency.put("Indian Rupee", 1.2);
                currency.put("Indonesian Rupiah", 0.0060);
                currency.put("Malaysian Ringgit", 21.48);
                fromMoney = Double.parseDouble(fromMoneyString);

                if (toSp.equals("US Dollar")) {
                    result = money * 82.57;
                    String r = String.valueOf(result);
                    toMoneyEdit.setText(r);
                } else if (toSp.equals("Euro")) {
                    result = money * 102.31;
                    String r = String.valueOf(result);
                    toMoneyEdit.setText(r);
                } else if (toSp.equals("Indian Rupee")) {
                    result = money * 1.28;
                    String r = String.valueOf(result);
                    toMoneyEdit.setText(r);
                } else if (toSp.equals("Indonesian Rupiah")) {
                    result = money * 0.0060;
                    String r = String.valueOf(result);
                    toMoneyEdit.setText(r);
                } else if (toSp.equals("Malaysian Ringgit")) {
                    result = money * 21.48;
                    String r = String.valueOf(result);
                    toMoneyEdit.setText(r);
                }
            }

               /* Log.e("error","entered not null");
                 convertToMoney = currency.get(toSp);*/

/*
                double moneyInTk = (fromMoney * convertToMoney);
                double resultMoney = Double.valueOf(df.format((moneyInTk * convertToMoney)));
                toMoneyEdit.setText(moneyInTk + "");*/


            }
        }
    }

