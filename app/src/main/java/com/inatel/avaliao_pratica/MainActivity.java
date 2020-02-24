package com.inatel.avaliao_pratica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button botaoCadastrar,botaoPesquisar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        botaoCadastrar = findViewById(R.id.btCadastrar);
        botaoPesquisar = findViewById(R.id.btPesquisar);
        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(MainActivity.this,Cadastrar.class);
                startActivity(intent1);
            }
        });
        botaoPesquisar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(MainActivity.this,Pesquisar.class);
                startActivity(intent2);
            }
        });
    }

}


