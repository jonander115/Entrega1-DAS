package com.example.tunesongplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class CancionesPlaylist extends AppCompatActivity {

    private String playlist;
    private String usuario;
    private ListView listaCancionesPlaylist;
    private boolean sonandoMusica;
    private MediaPlayer musicaMP3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canciones_playlist);

        sonandoMusica = false;

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

        //click largo para eliminar la canción
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

        //click corto para escuchar la canción. Aparece una notificación, y se han pedido los permisos previamente
        listaCancionesPlaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Recogemos nombre y autor de la canción
                TextView tv_NombreCancionEnPlaylist = (TextView) findViewById(R.id.tv_NombreCancionEnPlaylist);
                String nombreCancionDePlaylistPulsada = tv_NombreCancionEnPlaylist.getText().toString();
                TextView tv_AutorCancionEnPlaylist = (TextView) findViewById(R.id.tv_AutorCancionEnPlaylist);
                String autorCancionDePlaylistPulsada = tv_AutorCancionEnPlaylist.getText().toString();


                //Ponemos / Paramos la música
                if (sonandoMusica==false){
                    musicaMP3 = iniciarMusica(nombreCancionDePlaylistPulsada);
                    musicaMP3.start();
                    sonandoMusica = true;
                }
                else{
                    musicaMP3.stop();
                    sonandoMusica = false;
                }



                //Definimos el Manager y el Builder
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "canal1");

                //El Builder nos permite definir las características de la notificación
                builder.setSmallIcon(android.R.drawable.stat_sys_headset)
                        .setContentTitle("Escuchando música")
                        .setContentText("Escuchando " + nombreCancionDePlaylistPulsada + " de " + autorCancionDePlaylistPulsada)
                        .setSubText("De tu playlist " + playlist)
                        .setAutoCancel(true);

                //las versiones mayores que la Oreo necesitan un canal
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel canal = new NotificationChannel("canal1", "CanalNotificacionPlaylist", NotificationManager.IMPORTANCE_DEFAULT);

                    canal.setDescription("Canal de audio de playlist");
                    canal.setLightColor(Color.BLUE);

                    //Unimos el canal al manager
                    manager.createNotificationChannel(canal);

                    //El manager lanza la notificación
                    manager.notify(1, builder.build());
                }

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