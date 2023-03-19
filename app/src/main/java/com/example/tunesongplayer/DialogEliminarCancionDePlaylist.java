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

public class DialogEliminarCancionDePlaylist extends DialogFragment {

    private String usuario;

    private String cancion;

    private String autor;
    private String playlist;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        if (getArguments() != null){
            usuario = getArguments().getString("usuario");
            cancion = getArguments().getString("cancion");
            autor = getArguments().getString("autor");
            playlist = getArguments().getString("playlist");
        }

        //Definimos un builder para construir el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Eliminar canción de playlist");
        builder.setMessage("¿Quieres eliminar la canción " + cancion + " de la playlist " + playlist + "?");

        //Opción de Aceptar, que elimina la canción de la playlist y cierra el diálogo

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //Creamos una conexión a la base de datos de la aplicación
                miBD GestorBD = new miBD(getContext(), "BaseDeDatos", null, 1);

                //Recogemos la base de datos en modo escritura, porque hay que quitar la canción de la playlist
                SQLiteDatabase bd = GestorBD.getWritableDatabase();

                //Eliminamos la canción de la tabla que relaciona las playlists con las canciones
                String[] args = new String[] {playlist, cancion, autor, usuario};
                bd.delete("RelacionPlaylistsCanciones", "NombrePlaylist=? AND NombreCancion=? AND AutorCancion=? AND NombreUsuario=?", args);

                //Obtenemos el número de canciones de la playlist
                args = new String[] {usuario, playlist};
                Cursor cursor = bd.rawQuery("SELECT NumCanciones FROM Playlists WHERE Usuario = ? AND Nombre = ?", args);

                cursor.moveToNext();
                int numC = cursor.getInt(0);
                numC--;

                //Actualizamos el número de canciones de la playlist
                ContentValues modificacion = new ContentValues();
                modificacion.put("NumCanciones",numC);

                args = new String[] {playlist, usuario};
                bd.update("Playlists", modificacion, "Nombre=? AND Usuario=?", args);

                //Cerramos el cursor y la conexión a la base de datos
                cursor.close();
                bd.close();

                Toast.makeText(getContext(), "Se ha quitado la canción de la playlist", Toast.LENGTH_LONG).show();

                ((CancionesPlaylist) getActivity()).refrescarLista();

                dismiss();
            }
        });

        //Opción de Cancelar, que cierra el diálogo
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //Cerramos el cursor y la conexión a la base de datos
                dismiss();
            }
        });

        return builder.create();
    }

}
