package com.example.tunesongplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
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
    private boolean sonandoMusica;
    private MediaPlayer musicaMP3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canciones_buscadas);

        sonandoMusica = false;

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

        //click largo para añadir una canción a una playlist
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

        //click corto para reproducir una canción
        listaCancionesBuscadas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Recogemos nombre y autor de la canción
                TextView tv_NombreCancionEnBuscador = (TextView) findViewById(R.id.tv_NombreCancionEnBuscador);
                String nombreCancionDelBotonPulsado = tv_NombreCancionEnBuscador.getText().toString();
                TextView tv_AutorCancionEnBuscador = (TextView) findViewById(R.id.tv_AutorCancionEnBuscador);
                String autorCancionDelBotonPulsado = tv_AutorCancionEnBuscador.getText().toString();

                //Ponemos / Paramos la música
                if (sonandoMusica==false){
                    musicaMP3 = iniciarMusica(nombreCancionDelBotonPulsado);
                    musicaMP3.start();
                    sonandoMusica = true;
                }
                else{
                    musicaMP3.stop();
                    sonandoMusica = false;
                }

                //Definimos el Manager y el Builder
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "canal2");

                //El Builder nos permite definir las características de la notificación
                builder.setSmallIcon(android.R.drawable.stat_sys_headset)
                        .setContentTitle("Escuchando música")
                        .setContentText("Escuchando " + nombreCancionDelBotonPulsado + " de " + autorCancionDelBotonPulsado)
                        .setSubText("Has encontrado esta canción buscando: " + cancionBuscada)
                        .setAutoCancel(true);

                //las versiones mayores que la Oreo necesitan un canal
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel canal = new NotificationChannel("canal2", "CanalNotificacionBuscador", NotificationManager.IMPORTANCE_DEFAULT);

                    canal.setDescription("Canal de audio del buscador");
                    canal.setLightColor(Color.GREEN);

                    //Unimos el canal al manager
                    manager.createNotificationChannel(canal);

                    //El manager lanza la notificación
                    manager.notify(2, builder.build());
                }

            }
        });


    }

    public MediaPlayer iniciarMusica(String nombreCancion){

        musicaMP3 = new MediaPlayer();
        if (nombreCancion.equals("Boundless space")){
            musicaMP3 = MediaPlayer.create(this, R.raw.boundlessspace);
        }
        else if (nombreCancion.equals("Closer to the sun")) {
            musicaMP3 = MediaPlayer.create(this, R.raw.closertothesun);
        }
        else if (nombreCancion.equals("Plain")) {
            musicaMP3 = MediaPlayer.create(this, R.raw.plain);
        }
        /*
        else if (nombreCancion.equals("Burning fire")) {
            musicaMP3 = MediaPlayer.create(this, R.raw.burningfire);
        }
        else if (nombreCancion.equals("Faster, stronger")) {
            musicaMP3 = MediaPlayer.create(this, R.raw.fasterstronger);
        }
        else if (nombreCancion.equals("Get what you want")) {
            musicaMP3 = MediaPlayer.create(this, R.raw.getwhatyouwant);
        }
        else if (nombreCancion.equals("Hit the ground")) {
            musicaMP3 = MediaPlayer.create(this, R.raw.hittheground);
        }
        else if (nombreCancion.equals("Rabbits Hop")) {
            musicaMP3 = MediaPlayer.create(this, R.raw.rabbitshop);
        }
        else if (nombreCancion.equals("Rock and dust")) {
            musicaMP3 = MediaPlayer.create(this, R.raw.rockanddust);
        }
        else if (nombreCancion.equals("Slider")) {
            musicaMP3 = MediaPlayer.create(this, R.raw.slider);
        }

         */

        return musicaMP3;
    }


}

