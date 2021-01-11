package com.ifsp.pmd.calculadorapdm20202;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.mariuszgromada.math.mxparser.Expression;

public class MainActivity extends AppCompatActivity {
    private final String VALOR_VISOR_TV = "VALOR_VISOR_TV";
    private final int CALL_PHONE_PERMISSION_REQUEST_CODE = 0;
    private final int CONFIGURACOES_REQUEST_CODE = 1;

    public static final String EXTRA_CONFIGURACOES = "EXTRA_CONFIGURACOES";

    private Configuracoes configuracoes = new Configuracoes(false);
    private TextView visorTv;

    private StringBuilder buildExpression = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(getString(R.string.app_name), "onCreate executado - iniciado ciclo completo");
        setContentView(R.layout.activity_main);

        visorTv = findViewById(R.id.visorTv);
        if (savedInstanceState != null) {
            visorTv.setText(savedInstanceState.getString(VALOR_VISOR_TV, ""));
        }
        getSupportActionBar().setSubtitle("Tela principal");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(getString(R.string.app_name), "onStart executado - iniciado ciclo visível");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(getString(R.string.app_name), "onResume executado - iniciado ciclo em primeiro plano");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(getString(R.string.app_name), "onPause executado - finalizado ciclo em primeiro plano");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(getString(R.string.app_name), "onStop executado - finalizado ciclo visível");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(getString(R.string.app_name), "onDestroy executado - finalizado ciclo completo");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(getString(R.string.app_name), "onSaveInstanceState executado - salvando dados de instância");
        outState.putString(VALOR_VISOR_TV, visorTv.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.v(getString(R.string.app_name), "onRestoreInstanceState executado - restaurando dados de instância");
        //visorTv.setText(savedInstanceState.getString(VALOR_VISOR_TV, ""));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.configuracoesMi:
                Intent configuracoesIntent = new Intent(this, ConfiguracoesActivity.class);
                configuracoesIntent.putExtra(EXTRA_CONFIGURACOES, configuracoes);
                startActivityForResult(configuracoesIntent, CONFIGURACOES_REQUEST_CODE);
                return true;

            case R.id.siteIfspMi:
                Uri siteIfspUri = Uri.parse("https://www.ifsp.edu.br");
                Intent siteIfspIntent = new Intent(Intent.ACTION_VIEW, siteIfspUri);
                startActivity(siteIfspIntent);
                return true;

            case R.id.sairMi:
                finish();
                return true;

            case R.id.chamarIfspMi:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_PERMISSION_REQUEST_CODE);
                    }
                }
                chamarIfsp();
                return true;

            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONFIGURACOES_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            configuracoes = data.getParcelableExtra(EXTRA_CONFIGURACOES);
            if (configuracoes != null && configuracoes.getAvancada()) {
                findViewById(R.id.raizQuadradaBt).setVisibility(View.VISIBLE);
                findViewById(R.id.potenciacaoBt).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.raizQuadradaBt).setVisibility(View.GONE);
                findViewById(R.id.potenciacaoBt).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALL_PHONE_PERMISSION_REQUEST_CODE) {
            for (int resultado : grantResults) {
                if (resultado != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permissão necessária não concedida", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            chamarIfsp();
        }
    }

    private void chamarIfsp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                Uri chamarIfspUri = Uri.parse("tel:1137754501");
                Intent chamarIfspIntent = new Intent(Intent.ACTION_CALL, chamarIfspUri);
                startActivity(chamarIfspIntent);
            }
        }
    }

    public void onClick(View view) {
        Button button = (Button) view;
        CharSequence btnText = button.getText();

        if (btnText.equals(getString(R.string.resultado)) || btnText.equals(getString(R.string.limpeza))) {
            if (btnText.equals(getString(R.string.limpeza))) {
                visorTv.setText("");
                buildExpression = new StringBuilder();
            } else {

                if (visorTv.getText().toString().matches(".*√\\d+")) {
                    buildExpression.append(")");
                }

                String str = buildExpression.toString();
                Expression expression = new Expression(str);
                String result = Double.toString(expression.calculate());
                visorTv.setText(result);
                buildExpression = new StringBuilder(result);
            }
        } else {
            String txt = btnText.toString();

            if (btnText.equals(getString(R.string.raiz_quadrada))) {
                buildExpression.append("sqrt(");
            }
            String current = visorTv.getText().toString().concat(txt);
            visorTv.setText(current);

            if (current.matches(".*√\\d+") && isOperator(txt)) {
                buildExpression.append(")");
            }

            if (!txt.equals(getString(R.string.raiz_quadrada))) {
                buildExpression.append(txt);
            }
        }
    }

    private boolean isOperator(String txt) {
        return txt.equals(getString(R.string.soma)) || txt.equals(getString(R.string.subtracao)) ||
                txt.equals(getString(R.string.multiplicacao)) || txt.equals(getString(R.string.divisao))
                || txt.equals(getString(R.string.potenciacao));
    }

}