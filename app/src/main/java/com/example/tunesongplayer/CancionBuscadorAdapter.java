package com.example.tunesongplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class CancionBuscadorAdapter extends BaseAdapter {

    private Context contexto;
    private LayoutInflater inflater;
    private String[] nombresCanciones;
    private String[] autoresCanciones;

    //Constructora
    public CancionBuscadorAdapter(Context pContexto, String[] pNombresCanciones, String[] pAutoresCanciones) {
        contexto = pContexto;
        nombresCanciones = pNombresCanciones;
        autoresCanciones = pAutoresCanciones;
        inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //Devuelve el número de elementos
    @Override
    public int getCount() {
        return nombresCanciones.length;
    }

    //Devuelve el elemento i (en este caso, el nombre de la canción)
    @Override
    public Object getItem(int i) {
        return nombresCanciones[i];
    }

    //Devuelve el identificador del elemento i
    @Override
    public long getItemId(int i) {
        return i;
    }

    //Devuelve la vista (cómo se visualiza el elemento i)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.listview_cancion_buscador,null);

        //Recogemos los elementos de la vista
        TextView nombreCancion = (TextView) view.findViewById(R.id.tv_NombreCancionEnBuscador);
        TextView autorCancion = (TextView) view.findViewById(R.id.tv_AutorCancionEnBuscador);
        ImageView fotoCancionEnBuscador = (ImageView) view.findViewById(R.id.fotoCancionEnBuscador);
        //ImageButton bt_PlayBuscador = (ImageButton) view.findViewById(R.id.bt_PlayBuscador);
        //ImageButton bt_StopBuscador = (ImageButton) view.findViewById(R.id.bt_StopBuscador);
       // Button bt_AñadirBuscador = (Button) view.findViewById(R.id.bt_añadir);

        //Asignamos a los elementos de la lista los valores que el adaptador ha recibido en la constructora
        // (los valores de los atributos)
        nombreCancion.setText(nombresCanciones[i]);
        autorCancion.setText(autoresCanciones[i]);
        fotoCancionEnBuscador.setImageResource(R.drawable.iconocancion);
        //bt_PlayBuscador.setImageResource(android.R.drawable.ic_media_play);
        //bt_StopBuscador.setImageResource(android.R.drawable.picture_frame);


        return view;
    }
}
