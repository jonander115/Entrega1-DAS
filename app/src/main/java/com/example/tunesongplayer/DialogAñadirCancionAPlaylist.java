package com.example.tunesongplayer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class DialogAñadirCancionAPlaylist extends DialogFragment {

    private String usuario;
    private String cancion;
    private String autor;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        if (getArguments() != null){
            usuario = getArguments().getString("usuario");
            cancion = getArguments().getString("cancion");
            autor = getArguments().getString("autor");
        }

        //Definimos un builder para construir el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Añadir canción a playlist");

        //Para sacar las opciones tenemos que hacer una consulta a la base de datos y recoger los nombres de las playlists en las que pueda ser añadida la canción

        //Creamos una conexión a la base de datos de la aplicación
        miBD GestorBD = new miBD(getContext(), "BaseDeDatos", null, 1);

        //Recogemos la base de datos en modo escritura, porque si se pulsa el botón de Aceptar habiendo alguna playlist seleccionada habrá que añadir la canción a la playlist
        SQLiteDatabase bd = GestorBD.getWritableDatabase();

        //Mediante un objeto de tipo Cursor, realizamos una consulta SQL y recogemos su resultado
        //Esta consulta es algo compleja, porque una canción no puede estar más de una vez en la misma playlist.
        //Debemos recoger aquellas playlists que sean del usuario y que en ningún registro de la tabla que relaciona las
        // playlists con sus canciones esté presente el nombre de la playlist con el nombre de la canción (porque eso
        // significaría que la canción ya había sido añadida a la playlist con anterioridad)
        //Además, como distintos autores han podido publicar canciones con el mismo nombre, he incluido el autor en la consulta
        String[] args = new String[] {usuario, cancion, usuario, autor};
        Cursor cursor = bd.rawQuery("SELECT Nombre FROM Playlists WHERE Usuario = ? AND " +
                "Nombre NOT IN (SELECT NombrePlaylist FROM RelacionPlaylistsCanciones WHERE NombreCancion=? AND NombreUsuario=? AND AutorCancion=?)", args);

        final String[] arrayOpciones = new String[cursor.getCount()];
        final ArrayList<Integer> opcionesElegidas = new ArrayList<Integer>();

        int cont = 0;

        //Guardamos las opciones en un array
        while (cursor.moveToNext()) {
            String opcion = cursor.getString(0);
            arrayOpciones[cont] = opcion;
            cont++;
        }

        if (cursor.getCount() == 0){
            builder.setMessage("- No puedes añadir la canción a ninguna playlist -");
        }
        else {

            builder.setMultiChoiceItems(arrayOpciones, null, new DialogInterface.OnMultiChoiceClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                    if (b == true) {
                        opcionesElegidas.add(i);
                    } else if (opcionesElegidas.contains(i)) {
                        opcionesElegidas.remove(Integer.valueOf(i));
                    }
                }

            });

        }

        //Opción de Aceptar, que añade la canción a las playlist seleccionadas y cierra el diálogo
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (opcionesElegidas.size() == 0){
                    Toast.makeText(getContext(), "No se ha añadido la canción a ninguna playlist", Toast.LENGTH_LONG).show();
                }
                else{

                    //Recogemos y añadimos la canción a las playlists seleccionadas
                    for (int elem=0; elem<opcionesElegidas.size(); elem++){
                        int numeroOpcionElegida = opcionesElegidas.get(elem);
                        String nombrePlaylistAAñadir = arrayOpciones[numeroOpcionElegida];

                        //Añadimos la canción a la playlist
                        ContentValues nuevo = new ContentValues();
                        nuevo.put("NombrePlaylist", nombrePlaylistAAñadir);
                        nuevo.put("NombreCancion", cancion);
                        nuevo.put("AutorCancion", autor);
                        nuevo.put("NombreUsuario", usuario);
                        bd.insert("RelacionPlaylistsCanciones", null, nuevo);

                        //Es importante actualizar también el número de canciones de la playlist

                        //Primero recogemos el número de canciones de la playlist
                        String[] args = new String[] {usuario, nombrePlaylistAAñadir};
                        Cursor cursor = bd.rawQuery("SELECT NumCanciones FROM Playlists WHERE Usuario = ? AND Nombre = ?", args);

                        cursor.moveToNext();
                        int num = cursor.getInt(0);
                        num++;

                        //Actualizamos el número de canciones
                        ContentValues modificacion = new ContentValues();
                        modificacion.put("NumCanciones", num);
                        String[] argsMod = new String[] {nombrePlaylistAAñadir, usuario};
                        bd.update("Playlists", modificacion, "Nombre=? AND Usuario=?", argsMod);

                    }

                    Toast.makeText(getContext(), "Se ha añadido la canción a la playlist", Toast.LENGTH_LONG).show();


                }

                //Cerramos el cursor y la conexión a la base de datos
                cursor.close();
                bd.close();

                dismiss();
            }
        });

        //Opción de Cancelar, que cierra el diálogo
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //Cerramos el cursor y la conexión a la base de datos
                cursor.close();
                bd.close();

                dismiss();
            }
        });

        return builder.create();

    }

}
