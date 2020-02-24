package com.inatel.avaliao_pratica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class Detalhes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes);


    String parametro = null;
        TextView nome = (TextView) findViewById(R.id.textView2);
        nome.setText(parametro);
    }
}
