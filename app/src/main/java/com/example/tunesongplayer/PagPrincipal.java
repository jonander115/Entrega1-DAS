package com.example.tunesongplayer;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentContainerView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class PagPrincipal extends AppCompatActivity {

    private String usuario;
    private PlaylistAdapter adapter;
    private int CODIGO_DE_PERMISO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pag_principal);

        //Recogemos el usuario, que nos viene dado desde la actividad de inicio de sesión
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario = extras.getString("usuario");
        }

        //Pedimos permiso para las notificaciones
        //La aplicación debe funcionar a partir de la versión Oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            //Comprobamos si el permiso está concedido
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

                //El permiso no está concedido, lo pedimos
                String[] permisos = new String[] {android.Manifest.permission.POST_NOTIFICATIONS};
                ActivityCompat.requestPermissions(this,permisos, CODIGO_DE_PERMISO);
            }

        }

        //Accedemos a los elementos de la vista

        //LinearLayout linearLayoutPagPrincipal = (LinearLayout) findViewById(R.id.linearLayoutPagPrincipal);

        TextView tv_TusPlaylists = (TextView) findViewById(R.id.tv_TusPlaylists);
        String texto = tv_TusPlaylists.getText().toString() + " " + usuario + ":";
        tv_TusPlaylists.setText(texto);

        Button bt_AñadirPlaylist = (Button) findViewById(R.id.bt_AñadirPlaylist);
        Button bt_EliminarPlaylist = (Button) findViewById(R.id.bt_EliminarPlaylist);

        //Listener para cuando se haga click en el botón de añadir una nueva playlist
        bt_AñadirPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogNuevaPlaylist dialogNuevaPlaylist = new DialogNuevaPlaylist();
                Bundle args = new Bundle();
                args.putString("usuario", usuario);
                dialogNuevaPlaylist.setArguments(args);
                dialogNuevaPlaylist.show(getSupportFragmentManager(), "nuevaPlaylist");
            }
        });

        //Listener para cuando se haga click en el botón de eliminar una playlist
        bt_EliminarPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogEliminarPlaylist dialogEliminarPlaylist = new DialogEliminarPlaylist();
                Bundle args = new Bundle();
                args.putString("usuario", usuario);
                dialogEliminarPlaylist.setArguments(args);
                dialogEliminarPlaylist.show(getSupportFragmentManager(), "eliminarPlaylist");
            }
        });

        refrescarLista();


    }

    //Para actualizar las playlists en cuanto se vuelve tras añadir elementos
    @Override
    protected void onResume() {
        super.onResume();
        refrescarLista();
    }


    //Método para cuando se haga click en el signo de interrogación
    //Muestra un diálogo que explica brevemente algunas acciones principales que se pueden realizar en la aplicación
    public void instrucciones_onClick(View v){
        DialogInstrucciones dialogoInstrucciones = new DialogInstrucciones();
        dialogoInstrucciones.show(getSupportFragmentManager(),"instrucciones");
    }


    //Método para cuando se haga click en la lupa
    //Permite buscar entre las canciones disponibles una que se haya escrito en el EditText del buscador
    public void lupa_onClick(View v){

        EditText et_Buscador = (EditText) findViewById(R.id.et_BuscaUnaCancion);
        if (et_Buscador.getText().toString().equals("")) {
            Toast.makeText(this, "Escribe el nombre de una canción", Toast.LENGTH_LONG).show();
        }
        else{
            Intent i = new Intent (this, CancionesBuscadas.class);
            i.putExtra("cancionBuscada",et_Buscador.getText().toString());
            i.putExtra("usuario", usuario);
            startActivity(i);
        }

    }


    //Método para cuando se haga click en el icono de información
    //Muestra información sobre la aplicación
    public void informacion_onClick(View v){
        DialogInformacion dialogInformacion = new DialogInformacion();
        dialogInformacion.show(getSupportFragmentManager(), "informacion");
    }


    //Método para cuando se haga click en el icono del perfil de usuario
    //Permite al usuario acceder a su perfil y ver y modificar su información
    public void perfilUsuario_onClick(View v){
        //Accedemos al perfil del usuario mediante una actividad
        Intent i = new Intent (this, PerfilUsuario.class);
        //Enviamos el nombre de usuario a esta nueva actividad
        i.putExtra("usuario", usuario);
        startActivity(i);
    }


    //Método para mostrar las playlists del usuario
    public void refrescarLista(){
        //Creamos una conexión a la base de datos de la aplicación
        miBD GestorBD = new miBD(this, "BaseDeDatos", null, 1);

        //Recogemos la base de datos en modo lectura
        SQLiteDatabase bd = GestorBD.getReadableDatabase();

        //Mediante un objeto de tipo Cursor, realizamos una consulta SQL y recogemos su resultado
        //Con esta consulta recogemos las paylists del usuario
        String[] args = new String[] {usuario};
        Cursor cursor = bd.rawQuery("SELECT Nombre, NumCanciones FROM Playlists WHERE Usuario = ?", args);

        ArrayList<String> nombresPlaylists = new ArrayList<String>();
        ArrayList<Integer> numsCanciones = new ArrayList<Integer>();

        //Vamos recorriendo los datos que nos devuelve la consulta
        while (cursor.moveToNext()) {

            //Recogemos lo que nos interesa del resultado:
            String nombrePlaylistBD = cursor.getString(0);
            int numCancionesBD = cursor.getInt(1);

            nombresPlaylists.add(nombrePlaylistBD);
            numsCanciones.add(numCancionesBD);

        }

        //Cerramos el cursor y la conexión a la base de datos
        cursor.close();
        bd.close();

        //Pasamos los ArrayList a Array
        String[] arrayPlaylists = nombresPlaylists.toArray(new String[0]);
        int[] arrayNumsCanciones = new int[numsCanciones.size()];

        for (int i=0; i<numsCanciones.size(); i++){
            arrayNumsCanciones[i] = numsCanciones.get(i);
        }

        ListView listViewPlaylists = (ListView) findViewById(R.id.listViewPlaylists);
        adapter = new PlaylistAdapter(this, arrayPlaylists, arrayNumsCanciones, usuario);
        listViewPlaylists.setAdapter(adapter);
    }


    //Método para cuando se hace click en una playlist y se puedan ver las canciones que contiene
    public void onClick_Playlist(View v){
        ListView listaPlaylists = (ListView) findViewById(R.id.listViewPlaylists);
        LinearLayout unaPlaylist = (LinearLayout) listaPlaylists.findViewById(R.id.layoutPlaylist);
        TextView nombrePlaylist = (TextView) unaPlaylist.findViewById(R.id.tv_NombrePlaylist);

        //Configuramos el onClick para que cuando se pulse una playlist se acceda a su contenido
        //He decidido poner el onClick en el LinearLayout en el que está la información de la playlist,
        // para que al pulsar en cualquier zona de él se entre al contenido de esa playlist
        unaPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (getApplicationContext(), CancionesPlaylist.class);
                //Enviamos el nombre de usuario y el nombre de la playlist a esta nueva actividad
                i.putExtra("usuario", usuario);
                i.putExtra("nombrePlaylist", nombrePlaylist.getText().toString());
                startActivity(i);
            }
        });
    }

}