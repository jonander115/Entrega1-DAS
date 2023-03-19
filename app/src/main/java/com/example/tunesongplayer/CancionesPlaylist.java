package com.example.tunesongplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class CancionesPlaylist extends AppCompatActivity {

    private String playlist;
    private String usuario;
    private ListView listaCancionesPlaylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canciones_playlist);

        //Recogemos el usuario y el nombre de la playlist
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario = extras.getString("usuario");
            playlist = extras.getString("nombrePlaylist");
            TextView tv_CancionesPlaylist = (TextView) findViewById(R.id.tv_CancionesPlaylist);
            String texto = tv_CancionesPlaylist.getText().toString() + " " + playlist;
            tv_CancionesPlaylist.setText(texto);
        }

        listaCancionesPlaylist = (ListView) findViewById(R.id.listaCancionesPlaylist);
        listaCancionesPlaylist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Recogemos información para enviar
                TextView tv_NombreCancionEnPlaylist = (TextView) findViewById(R.id.tv_NombreCancionEnPlaylist);
                String nombreCancionDePlaylistPulsada = tv_NombreCancionEnPlaylist.getText().toString();
                TextView tv_AutorCancionEnPlaylist = (TextView) findViewById(R.id.tv_AutorCancionEnPlaylist);
                String autorCancionDePlaylistPulsada = tv_AutorCancionEnPlaylist.getText().toString();

                //Preparamos y lanzamos el diálogo
                DialogEliminarCancionDePlaylist dialogEliminarCancionDePlaylist = new DialogEliminarCancionDePlaylist();
                Bundle args = new Bundle();
                args.putString("usuario", usuario);
                args.putString("cancion", nombreCancionDePlaylistPulsada);
                args.putString("autor", autorCancionDePlaylistPulsada);
                args.putString("playlist", playlist);
                dialogEliminarCancionDePlaylist.setArguments(args);
                dialogEliminarCancionDePlaylist.show(getSupportFragmentManager(), "eliminarCancion");

                return true;

            }
        });

        refrescarLista();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        refrescarLista();
    }

    //Método para mostrar las canciones de la playlist
    public void refrescarLista() {
        //Recogemos los datos a mostrar desde la base de datos

        //Creamos una conexión a la base de datos de la aplicación
        miBD GestorBD = new miBD(this, "BaseDeDatos", null, 1);

        //Recogemos la base de datos en modo lectura
        SQLiteDatabase bd = GestorBD.getReadableDatabase();

        //Mediante un objeto de tipo Cursor, realizamos una consulta SQL y recogemos su resultado
        //Con esta consulta recogemos las canciones de la playlist
        String[] args = new String[]{playlist, usuario};
        Cursor cursor = bd.rawQuery("SELECT NombreCancion, AutorCancion FROM RelacionPlaylistsCanciones WHERE NombrePlaylist = ? AND NombreUsuario = ?", args);

        String[] cancionesPlaylist = new String[cursor.getCount()];
        String[] autoresCanciones = new String[cursor.getCount()];

        int cont = 0;

        while (cursor.moveToNext()) {
            cancionesPlaylist[cont] = cursor.getString(0);
            autoresCanciones[cont] = cursor.getString(1);
            cont++;
        }

        //Cerramos el cursor y la conexión a la base de datos
        cursor.close();
        bd.close();

        //Le pasamos a la vista los datos a mostrar mediante el adaptador
        CancionPlaylistAdapter adapter = new CancionPlaylistAdapter(getApplicationContext(), cancionesPlaylist, autoresCanciones);
        listaCancionesPlaylist.setAdapter(adapter);
    }

}