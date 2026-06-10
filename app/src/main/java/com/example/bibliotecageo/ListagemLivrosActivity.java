package com.example.bibliotecageo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ListagemLivrosActivity extends AppCompatActivity {

    private ListView listViewLivros;
    private BancoHelper bancoHelper;
    private LivroAdapter adapter;
    private ArrayList<Livro> listaLivros;
    private ArrayList<Integer> listaIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listagem_livros);

        listViewLivros = findViewById(R.id.listViewLivros);
        bancoHelper = new BancoHelper(this);
        listaLivros = new ArrayList<>();
        listaIds = new ArrayList<>();

        carregarLivros();

        listViewLivros.setOnItemClickListener((parent, view, position, id) -> {
            int livroId = listaIds.get(position);
            Intent intent = new Intent(ListagemLivrosActivity.this, CadastroLivroActivity.class);
            intent.putExtra("livroId", livroId);
            startActivity(intent);
        });

        listViewLivros.setOnItemLongClickListener((adapterView, view, position, id) -> {
            int livroId = listaIds.get(position);
            int deletado = bancoHelper.excluirLivro(livroId);
            if (deletado > 0) {
                Toast.makeText(this, "Livro excluído!", Toast.LENGTH_SHORT).show();
                carregarLivros();
            }
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarLivros();
    }

    private void carregarLivros() {
        Cursor cursor = bancoHelper.listarLivros();
        listaLivros.clear();
        listaIds.clear();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String titulo = cursor.getString(1);
                String autor = cursor.getString(2);
                String ano = cursor.getString(3);
                String editora = cursor.getString(4);
                String local = cursor.getString(5);
                String status = cursor.getString(6);
                String observacao = cursor.getString(7);
                double lat = cursor.getDouble(8);
                double lng = cursor.getDouble(9);

                Livro livro = new Livro(titulo, autor, ano, editora, local, status, observacao, lat, lng);
                livro.setId(id);
                listaLivros.add(livro);
                listaIds.add(id);
            } while (cursor.moveToNext());
        }

        adapter = new LivroAdapter(this, listaLivros);
        listViewLivros.setAdapter(adapter);

        if (listaLivros.isEmpty()) {
            Toast.makeText(this, "Nenhum livro cadastrado ainda!", Toast.LENGTH_SHORT).show();
        }
    }
}
