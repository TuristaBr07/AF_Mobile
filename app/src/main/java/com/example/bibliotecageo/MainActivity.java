package com.example.bibliotecageo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void abrirPesquisa(View view) {
        Intent intent = new Intent(MainActivity.this, PesquisaLivroActivity.class);
        startActivity(intent);
    }

    public void abrirListagem(View view) {
        Intent intent = new Intent(MainActivity.this, ListagemLivrosActivity.class);
        startActivity(intent);
    }
}
