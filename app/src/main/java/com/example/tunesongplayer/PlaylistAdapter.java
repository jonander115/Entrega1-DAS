package com.example.tunesongplayer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.navigation.Navigation;

public class PlaylistAdapter extends BaseAdapter {

    private Context contexto;
    private LayoutInflater inflater;
    private String[] nombresPlaylists;
    private int[] numCancEnPlaylists;
    private String usuario;

    //Constructora
    public PlaylistAdapter(Context pContexto, String[] pNombresPlaylists, int[] pNumCancEnPlaylists, String pUsuario) {
        contexto = pContexto;
        nombresPlaylists = pNombresPlaylists;
        numCancEnPlaylists = pNumCancEnPlaylists;
        inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        usuario = pUsuario;
    }


    //Devuelve el número de elementos
    @Override
    public int getCount() {
        return nombresPlaylists.length;
    }

    //Devuelve el elemento i (en este caso, el nombre de la playlist)
    @Override
    public Object getItem(int i) {
        return nombresPlaylists[i];
    }

    //Devuelve el identificador del elemento i
    @Override
    public long getItemId(int i) {
        return i;
    }

    //Devuelve la vista (cómo se visualiza el elemento i)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.listview_playlist,null);

        //Recogemos los elementos de la vista
        TextView nombrePlaylist = (TextView) view.findViewById(R.id.tv_NombrePlaylist);
        TextView numCancionesPlaylist = (TextView) view.findViewById(R.id.tv_NumCanciones);
        ImageView fotoPlaylist = (ImageView) view.findViewById(R.id.fotoPlaylist);
        LinearLayout layoutPlaylist = (LinearLayout) view.findViewById(R.id.layoutPlaylist);

        //Asignamos a los elementos de la lista los valores que el adaptador ha recibido en la constructora
        // (los valores de los atributos)
        nombrePlaylist.setText(nombresPlaylists[i]);
        numCancionesPlaylist.setText(numCancEnPlaylists[i] + " canciones");
        fotoPlaylist.setImageResource(R.drawable.iconoplaylist);

        return view;
    }



}
