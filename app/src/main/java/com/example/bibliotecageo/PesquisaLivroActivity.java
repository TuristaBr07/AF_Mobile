package com.example.bibliotecageo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class PesquisaLivroActivity extends AppCompatActivity {

    private EditText edtPesquisa;
    private ListView listViewResultados;
    private ArrayList<LivroAPI> listaLivrosAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisa_livro);

        edtPesquisa = findViewById(R.id.edtPesquisa);
        listViewResultados = findViewById(R.id.listViewResultados);
        listaLivrosAPI = new ArrayList<>();

        listViewResultados.setOnItemClickListener((parent, view, position, id) -> {
            LivroAPI livroSelecionado = listaLivrosAPI.get(position);
            Intent intent = new Intent(PesquisaLivroActivity.this, CadastroLivroActivity.class);
            intent.putExtra("titulo", livroSelecionado.getTitle());
            intent.putExtra("autor", livroSelecionado.getAutorFormatado());
            intent.putExtra("ano", livroSelecionado.getAnoFormatado());
            intent.putExtra("editora", livroSelecionado.getEditoraFormatada());
            startActivity(intent);
        });
    }

    public void pesquisarLivros(View view) {
        String termoPesquisa = edtPesquisa.getText().toString().trim();

        if (termoPesquisa.isEmpty()) {
            Toast.makeText(this, "Digite um termo para pesquisar!", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String query = URLEncoder.encode(termoPesquisa, "UTF-8");
                    URL url = new URL("https://openlibrary.org/search.json?q=" + query
                            + "&limit=20&fields=title,author_name,first_publish_year,publisher");
                    HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                    conexao.setRequestMethod("GET");

                    int responseCode = conexao.getResponseCode();

                    if (responseCode == 200) {
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(conexao.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        String resultado = response.toString();
                        Gson gson = new Gson();
                        ResultadoPesquisa resultadoPesquisa = gson.fromJson(resultado, ResultadoPesquisa.class);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listaLivrosAPI.clear();
                                if (resultadoPesquisa.getDocs() != null) {
                                    listaLivrosAPI.addAll(resultadoPesquisa.getDocs());
                                }

                                ArrayList<String> itensExibidos = new ArrayList<>();
                                for (LivroAPI livro : listaLivrosAPI) {
                                    itensExibidos.add(livro.getTitle() + "\n"
                                            + livro.getAutorFormatado()
                                            + " (" + livro.getAnoFormatado() + ")");
                                }

                                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                        PesquisaLivroActivity.this,
                                        android.R.layout.simple_list_item_1,
                                        itensExibidos);
                                listViewResultados.setAdapter(adapter);

                                if (listaLivrosAPI.isEmpty()) {
                                    Toast.makeText(PesquisaLivroActivity.this,
                                            "Nenhum livro encontrado!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PesquisaLivroActivity.this,
                                        "Erro ao consultar a API!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PesquisaLivroActivity.this,
                                    "Verifique sua conexão com a internet!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }
}
