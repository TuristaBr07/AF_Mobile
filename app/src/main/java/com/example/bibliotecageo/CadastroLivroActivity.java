package com.example.bibliotecageo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class CadastroLivroActivity extends AppCompatActivity {

    private TextView txvTitulo, txvAutor, txvAno, txvEditora;
    private Spinner spinnerLocal, spinnerStatus;
    private EditText edtObservacao;
    private TextView txvLatitude, txvLongitude;
    private Button btnCapturarLocalizacao, btnSalvar;

    private double latitude = 0.0;
    private double longitude = 0.0;
    private boolean modoEdicao = false;
    private int livroId = -1;

    private String tituloLivro, autorLivro, anoLivro, editoraLivro;

    private BancoHelper bancoHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_livro);

        txvTitulo = findViewById(R.id.txvTitulo);
        txvAutor = findViewById(R.id.txvAutor);
        txvAno = findViewById(R.id.txvAno);
        txvEditora = findViewById(R.id.txvEditora);
        spinnerLocal = findViewById(R.id.spinnerLocal);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        edtObservacao = findViewById(R.id.edtObservacao);
        txvLatitude = findViewById(R.id.txvLatitude);
        txvLongitude = findViewById(R.id.txvLongitude);
        btnCapturarLocalizacao = findViewById(R.id.btnCapturarLocalizacao);
        btnSalvar = findViewById(R.id.btnSalvar);

        bancoHelper = new BancoHelper(this);

        ArrayAdapter<CharSequence> adapterLocal = ArrayAdapter.createFromResource(this,
                R.array.locais_livro, android.R.layout.simple_spinner_item);
        adapterLocal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocal.setAdapter(adapterLocal);

        ArrayAdapter<CharSequence> adapterStatus = ArrayAdapter.createFromResource(this,
                R.array.status_leitura, android.R.layout.simple_spinner_item);
        adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapterStatus);

        if (getIntent().hasExtra("livroId")) {
            modoEdicao = true;
            livroId = getIntent().getIntExtra("livroId", -1);
            carregarDadosLivro(livroId);
            btnSalvar.setText(getString(R.string.btn_atualizar));
            spinnerLocal.setEnabled(false);
            btnCapturarLocalizacao.setVisibility(View.GONE);
        } else {
            tituloLivro = getIntent().getStringExtra("titulo");
            autorLivro = getIntent().getStringExtra("autor");
            anoLivro = getIntent().getStringExtra("ano");
            editoraLivro = getIntent().getStringExtra("editora");

            txvTitulo.setText(tituloLivro);
            txvAutor.setText(autorLivro);
            txvAno.setText("Ano: " + anoLivro);
            txvEditora.setText("Editora: " + editoraLivro);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }
    }

    private void carregarDadosLivro(int id) {
        Cursor cursor = bancoHelper.buscarLivroPorId(id);
        if (cursor.moveToFirst()) {
            tituloLivro = cursor.getString(1);
            autorLivro = cursor.getString(2);
            anoLivro = cursor.getString(3);
            editoraLivro = cursor.getString(4);
            String local = cursor.getString(5);
            String status = cursor.getString(6);
            String observacao = cursor.getString(7);
            latitude = cursor.getDouble(8);
            longitude = cursor.getDouble(9);

            txvTitulo.setText(tituloLivro);
            txvAutor.setText(autorLivro);
            txvAno.setText("Ano: " + anoLivro);
            txvEditora.setText("Editora: " + editoraLivro);
            txvLatitude.setText("Latitude: " + latitude);
            txvLongitude.setText("Longitude: " + longitude);
            edtObservacao.setText(observacao);

            ArrayAdapter adapterLocal = (ArrayAdapter) spinnerLocal.getAdapter();
            int posLocal = adapterLocal.getPosition(local);
            spinnerLocal.setSelection(posLocal);

            ArrayAdapter adapterStatus = (ArrayAdapter) spinnerStatus.getAdapter();
            int posStatus = adapterStatus.getPosition(status);
            spinnerStatus.setSelection(posStatus);
        }
    }

    public void capturarLocalizacao(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,
                    "Permissão de localização negada! Conceda a permissão nas configurações.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        FusedLocationProviderClient fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);

        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    txvLatitude.setText("Latitude: " + latitude);
                    txvLongitude.setText("Longitude: " + longitude);
                    Toast.makeText(CadastroLivroActivity.this,
                            "Localização capturada com sucesso!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CadastroLivroActivity.this,
                            "Não foi possível obter a localização. Ative o GPS do aparelho.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void salvarLivro(View view) {
        String status = spinnerStatus.getSelectedItem().toString();
        String observacao = edtObservacao.getText().toString();

        if (modoEdicao) {
            int resultado = bancoHelper.atualizarLivro(livroId, status, observacao);
            if (resultado > 0) {
                Toast.makeText(this, "Livro atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Erro ao atualizar o livro!", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (latitude == 0.0 && longitude == 0.0) {
                Toast.makeText(this, "Capture a localização antes de salvar!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            String local = spinnerLocal.getSelectedItem().toString();
            Livro livro = new Livro(tituloLivro, autorLivro, anoLivro, editoraLivro,
                    local, status, observacao, latitude, longitude);

            long resultado = bancoHelper.inserirLivro(livro);
            if (resultado != -1) {
                Toast.makeText(this, "Livro salvo com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Erro ao salvar o livro!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissão de localização concedida!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,
                        "Permissão de localização negada. O GPS não funcionará.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
