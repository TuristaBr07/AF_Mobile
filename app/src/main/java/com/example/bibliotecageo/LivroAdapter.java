package com.example.bibliotecageo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class LivroAdapter extends ArrayAdapter<Livro> {

    private Context context;
    private ArrayList<Livro> listaLivros;

    public LivroAdapter(Context context, ArrayList<Livro> listaLivros) {
        super(context, R.layout.item_livro, listaLivros);
        this.context = context;
        this.listaLivros = listaLivros;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_livro, parent, false);
        }

        Livro livro = listaLivros.get(position);

        TextView txvTitulo = convertView.findViewById(R.id.txvTituloItem);
        TextView txvAutor = convertView.findViewById(R.id.txvAutorItem);
        TextView txvStatus = convertView.findViewById(R.id.txvStatusItem);
        TextView txvLocal = convertView.findViewById(R.id.txvLocalItem);
        TextView txvCoordenadas = convertView.findViewById(R.id.txvCoordenadasItem);

        txvTitulo.setText(livro.getTitulo());
        txvAutor.setText("Autor: " + livro.getAutor());
        txvStatus.setText("Status: " + livro.getStatus());
        txvLocal.setText("Local: " + livro.getLocal());
        txvCoordenadas.setText("Lat: " + livro.getLatitude() + " | Lng: " + livro.getLongitude());

        return convertView;
    }
}
