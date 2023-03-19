package com.example.tunesongplayer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class DialogEliminarPlaylist extends DialogFragment {

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
        builder.setTitle("Eliminar playlists");

        //Para sacar las opciones tenemos que hacer una consulta a la base de datos y recoger los nombres de las playlists del usuario

        //Creamos una conexión a la base de datos de la aplicación
        miBD GestorBD = new miBD(getContext(), "BaseDeDatos", null, 1);

        //Recogemos la base de datos en modo escritura, porque si se pulsa el botón de Aceptar habiendo alguna playlist seleccionada habrá que borrarla
        SQLiteDatabase bd = GestorBD.getWritableDatabase();

        //Mediante un objeto de tipo Cursor, realizamos una consulta SQL y recogemos su resultado
        String[] args = new String[] {usuario};
        Cursor cursor = bd.rawQuery("SELECT Nombre FROM Playlists WHERE Usuario = ?", args);

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
            builder.setMessage("- No tienes ninguna playlist -");
        }
        else{

            builder.setMultiChoiceItems(arrayOpciones, null, new DialogInterface.OnMultiChoiceClickListener(){

                @Override
                public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                    if (b == true){
                        opcionesElegidas.add(i);
                    }
                    else if (opcionesElegidas.contains(i)){
                        opcionesElegidas.remove(Integer.valueOf(i));
                    }
                }

            });


        }

        //Opción de Aceptar, que borra las playlist seleccionadas y cierra el diálogo
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (opcionesElegidas.size() == 0){
                    Toast.makeText(getContext(), "No se ha eliminado ninguna playlist", Toast.LENGTH_LONG).show();
                }
                else{

                    //Recogemos y eliminamos las playlists seleccionadas
                    for (int elem=0; elem<opcionesElegidas.size(); elem++){
                        int numeroOpcionElegida = opcionesElegidas.get(elem);
                        String nombrePlaylistABorrar = arrayOpciones[numeroOpcionElegida];

                        //Eliminamos la playlist
                        String[] argsBorrar = new String[] {nombrePlaylistABorrar, usuario};
                        bd.delete("Playlists", "Nombre=? AND Usuario=?", argsBorrar);
                    }

                    Toast.makeText(getContext(), "Se han eliminado las playlists seleccionadas", Toast.LENGTH_LONG).show();

                    ((PagPrincipal) getActivity()).refrescarLista();

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
