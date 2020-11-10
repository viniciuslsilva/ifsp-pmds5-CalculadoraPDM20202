package com.ifsp.pmd.calculadorapdm20202;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.mariuszgromada.math.mxparser.Expression;

public class MainActivity extends AppCompatActivity {
    private TextView visorTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        visorTv = findViewById(R.id.visorTv);
    }

    public void onClick(View view) {
        Button button = (Button) view;
        CharSequence btnText = button.getText();

        if (btnText.equals("=") || btnText.equals("C")) {
            if (btnText.equals("C")) {
                visorTv.setText("");
            } else {
                Expression expression = new Expression(visorTv.getText().toString());
                String result = Double.toString(expression.calculate());
                visorTv.setText(result);
            }
        } else {
            visorTv.setText(visorTv.getText().toString().concat(btnText.toString()));
        }
    }

}