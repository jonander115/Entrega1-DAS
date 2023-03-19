package com.example.tunesongplayer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogNuevaPlaylist extends DialogFragment {

    private String usuario;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        if (getArguments() != null){
            usuario = getArguments().getString("usuario");
        }

        //Definimos un builder para construir el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Nueva playlist");
        builder.setMessage("Introduce el nombre de la nueva playlist");

        //Mediante un LayoutInflater hacemos que la vista del diálogo sea un layout que hemos diseñado en un xml
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View aspectoDialog = inflater.inflate(R.layout.dialog_nueva_playlist, null);
        builder.setView(aspectoDialog);

        //Opción de crear playlist, que añade la playlist y cierra el diálogo
        builder.setPositiveButton("Crear playlist", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //Recogemos el texto del EditText donde el usuario ha escrito el nombre de la playlist
                EditText et_NombreNuevaPlaylist = (EditText) aspectoDialog.findViewById(R.id.et_NombreNuevaPlaylist);

                //Comprobamos que el nombre de la playlist no está en blanco
                if (et_NombreNuevaPlaylist.getText().toString().length() != 0){

                    String nombrePlaylist = et_NombreNuevaPlaylist.getText().toString();

                    //Comprobamos que el usuario no tiene ya una playlist con ese nombre


                    //Creamos una conexión a la base de datos de la aplicación
                    miBD GestorBD = new miBD(getContext(), "BaseDeDatos", null, 1);

                    //Recogemos la base de datos en modo escritura, porque si el usuario no tiene ya una playlist con ese nombre la crearemos
                    SQLiteDatabase bd = GestorBD.getWritableDatabase();

                    //Mediante un objeto de tipo Cursor, realizamos una consulta SQL y recogemos su resultado
                    String[] args = new String[] {usuario,nombrePlaylist};
                    Cursor cursor = bd.rawQuery("SELECT NumCanciones FROM Playlists WHERE Usuario = ? AND Nombre = ?", args);

                    boolean hayDatoDevuelto = cursor.moveToNext();

                    //Si no se devuelve dato es porque el usuario no tiene una playlist con ese nombre
                    //Podemos crear la playlist
                    if (!hayDatoDevuelto){

                        //Creamos una variable de tipo ContentValues e introducimos los datos
                        ContentValues nuevaPlaylist = new ContentValues();
                        nuevaPlaylist.put("Nombre", nombrePlaylist);
                        nuevaPlaylist.put("Usuario", usuario);
                        nuevaPlaylist.put("NumCanciones", 0);

                        //Insertamos la nueva playlist en la base de datos
                        bd.insert("Playlists", null, nuevaPlaylist);

                        Toast.makeText(getContext(), "Playlist creada", Toast.LENGTH_LONG).show();

                        ((PagPrincipal) getActivity()).refrescarLista();

                    }
                    else{
                        Toast.makeText(getContext(), "Ya tienes una playlist con ese nombre", Toast.LENGTH_LONG).show();
                    }

                    //Cerramos el cursor y la conexión a la base de datos
                    cursor.close();
                    bd.close();

                    dismiss();

                }
                else{
                    Toast.makeText(getContext(), "Por favor, introduce un nombre para la playlist", Toast.LENGTH_LONG).show();
                }

            }
        });

        //Opción de cancelar, que cierra el diálogo
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });


        return builder.create();
    }

}
