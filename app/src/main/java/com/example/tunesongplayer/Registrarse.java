package com.example.tunesongplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Registrarse extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);
    }

    //Método para cuando se haga click en el botón para Registrarse
    public void onClick_Registrarse(View v) {

        //Recogemos los EditText de la vista
        EditText et_NombreRegistro = (EditText) findViewById(R.id.et_NombreRegistro);
        EditText et_ApellidosRegistro = (EditText) findViewById(R.id.et_ApellidosRegistro);
        EditText et_EmailRegistro = (EditText) findViewById(R.id.et_EmailRegistro);
        EditText et_NombreUsuarioRegistro = (EditText) findViewById(R.id.et_NombreUsuarioRegistro);
        EditText et_ContraseñaRegistro = (EditText) findViewById(R.id.et_ContraseñaRegistro);
        EditText et_RepetirContraseñaRegistro = (EditText) findViewById(R.id.et_RepetirContraseñaRegistro);

        //Recogemos el contenido de los EditText
        String nombre = et_NombreRegistro.getText().toString();
        String apellidos = et_ApellidosRegistro.getText().toString();
        String email = et_EmailRegistro.getText().toString();
        String usuario = et_NombreUsuarioRegistro.getText().toString();
        String password = et_ContraseñaRegistro.getText().toString();
        String repetirPassword= et_RepetirContraseñaRegistro.getText().toString();

        //Comprobamos que se hayan introducido todos los datos:
        if (nombre.length() != 0 && apellidos.length() != 0 && email.length() != 0 && usuario.length() != 0 && password.length() != 0 && repetirPassword.length() != 0) {

            //Comprobamos que "Contraseña" y "Repetir contraseña" tienen el mismo contenido
            if (password.equals(repetirPassword)) {

                //Ahora comprobaremos que el nombre de usuario no estaba ya registrado anteriormente

                //Creamos una conexión a la base de datos de la aplicación
                miBD GestorBD = new miBD(this, "BaseDeDatos", null, 1);

                //Recogemos la base de datos en modo escritura, porque si el usuario no estaba registrado lo registraremos
                SQLiteDatabase bd = GestorBD.getWritableDatabase();

                //Mediante un objeto de tipo Cursor, realizamos una consulta SQL y recogemos su resultado
                //Con esta consulta veremos si ya se había registrado un usuario con ese nombre
                String[] args = new String[] {usuario};
                Cursor cursor = bd.rawQuery("SELECT Usuario FROM Usuarios WHERE Usuario = ?", args);

                //Nos posicionamos en el dato devuelto por la consulta
                boolean hayDatoDevuelto = cursor.moveToNext();

                if (hayDatoDevuelto && usuario == cursor.getString(0)) {
                    Toast.makeText(getApplicationContext(), "Ya existe un usuario con ese nombre, elige otro", Toast.LENGTH_LONG).show();
                }
                else{
                    //El usuario no existía, podemos registrarlo en la base de datos

                    //Creamos una variable de tipo ContentValues e introducimos los datos
                    ContentValues nuevoUsuario = new ContentValues();
                    nuevoUsuario.put("Usuario", usuario);
                    nuevoUsuario.put("Password", password);
                    nuevoUsuario.put("Nombre", nombre);
                    nuevoUsuario.put("Apellidos", apellidos);
                    nuevoUsuario.put("Email", email);

                    //Insertamos el usuario en la base de datos
                    bd.insert("Usuarios", null, nuevoUsuario);

                    //Cerramos el cursor y la conexión a la base de datos
                    cursor.close();
                    bd.close();

                    Toast.makeText(getApplicationContext(), "Se ha registrado el usuario", Toast.LENGTH_LONG).show();

                    //Cerramos la actividad para que el usuario vuelva a la página de bienvenida de la aplicación
                    finish();
                }

                //Cerramos el cursor y la conexión a la base de datos
                cursor.close();
                bd.close();

            }
            else{
                Toast.makeText(getApplicationContext(), "Debes introducir la misma contraseña en los dos campos para ello", Toast.LENGTH_LONG).show();
            }

        }
        else{
            Toast.makeText(getApplicationContext(), "Por favor, rellena todos los campos", Toast.LENGTH_LONG).show();
        }

    }
}