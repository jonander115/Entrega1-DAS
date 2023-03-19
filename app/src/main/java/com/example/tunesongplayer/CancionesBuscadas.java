package com.example.tunesongplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CancionesBuscadas extends AppCompatActivity {

    private String cancionBuscada;
    private String usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canciones_buscadas);

        //Recogemos la canción buscada, que nos viene dada desde la actividad de la página principal
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            cancionBuscada = extras.getString("cancionBuscada");
            usuario = extras.getString("usuario");
            TextView tv_Busqueda = (TextView) findViewById(R.id.tv_Busqueda);
            tv_Busqueda.setText(cancionBuscada);
        }

        //Recogemos los datos a mostrar desde la base de datos
        //Para no complicar mucho el código, solo se muestran las canciones que se llaman exactamente igual a lo que se ha buscado
        // (Lo cual incluye mayúsculas, tildes, espacios...)

        //Creamos una conexión a la base de datos de la aplicación
        miBD GestorBD = new miBD(this, "BaseDeDatos", null, 1);

        //Recogemos la base de datos en modo lectura
        SQLiteDatabase bd = GestorBD.getReadableDatabase();

        //Mediante un objeto de tipo Cursor, realizamos una consulta SQL y recogemos su resultado
        //Con esta consulta recogemos el usuario y la contraseña de la base de datos dado el usuario introducido
        String[] args = new String[] {cancionBuscada};
        Cursor cursor = bd.rawQuery("SELECT Nombre, Autor FROM Canciones WHERE Nombre = ?", args);

        ArrayList<String> nombresCanciones = new ArrayList<String>();
        ArrayList<String> autoresCanciones = new ArrayList<String>();

        //Vamos recorriendo los datos que nos devuelve la consulta
        while (cursor.moveToNext()) {

            //Recogemos lo que nos interesa del resultado:
            String nombreCancionBD = cursor.getString(0);
            String nombreAutorBD = cursor.getString(1);

            nombresCanciones.add(nombreCancionBD);
            autoresCanciones.add(nombreAutorBD);

        }

        //Cerramos el cursor y la conexión a la base de datos
        cursor.close();
        bd.close();

        //Pasamos los ArrayList a Array
        String[] arrayCanciones = nombresCanciones.toArray(new String[0]);
        String[] arrayAutores = autoresCanciones.toArray(new String[0]);

        //Recogemos la lista de la vista y le pasamos los datos a mostrar mediante el adaptador
        ListView listaCancionesBuscadas = (ListView) findViewById(R.id.lista_canciones_buscadas);
        CancionBuscadorAdapter adapter = new CancionBuscadorAdapter(getApplicationContext(), arrayCanciones, arrayAutores);
        listaCancionesBuscadas.setAdapter(adapter);

        listaCancionesBuscadas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Recogemos información para enviar
                TextView tv_NombreCancionEnBuscador = (TextView) findViewById(R.id.tv_NombreCancionEnBuscador);
                String nombreCancionDelBotonPulsado = tv_NombreCancionEnBuscador.getText().toString();
                TextView tv_AutorCancionEnBuscador = (TextView) findViewById(R.id.tv_AutorCancionEnBuscador);
                String autorCancionDelBotonPulsado = tv_AutorCancionEnBuscador.getText().toString();

                //Preparamos y lanzamos el diálogo
                DialogAñadirCancionAPlaylist dialogAñadirCancionAPlaylist = new DialogAñadirCancionAPlaylist();
                Bundle args = new Bundle();
                args.putString("usuario", usuario);
                args.putString("cancion", nombreCancionDelBotonPulsado);
                args.putString("autor", autorCancionDelBotonPulsado);
                dialogAñadirCancionAPlaylist.setArguments(args);
                dialogAñadirCancionAPlaylist.show(getSupportFragmentManager(), "añadirAPlaylist");

                return true;
            }
        });




    }


}

