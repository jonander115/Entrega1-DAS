package com.example.tunesongplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class IniciarSesion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);
    }

    //Método para cuando se haga click en el botón para Iniciar sesión
    public void onClick_IniciarSesion(View v) {

        //Recogemos del layout los EditText en los que el usuario introduce su usuario y contraseña
        EditText et_Usuario = (EditText) findViewById(R.id.et_NombreUsuario);
        EditText et_Password = (EditText) findViewById(R.id.et_Contraseña);

        //Comprobamos que se ha escrito algo en los EditText del usuario y la contraseña
        if (et_Usuario.getText().toString().length() != 0 && et_Password.getText().toString().length() != 0) {

            //Recogemos los valores introducidos en los EditText
            String usuario = et_Usuario.getText().toString();
            String password = et_Password.getText().toString();

            //Creamos una conexión a la base de datos de la aplicación
            miBD GestorBD = new miBD(this, "BaseDeDatos", null, 1);

            //Recogemos la base de datos en modo lectura
            SQLiteDatabase bd = GestorBD.getReadableDatabase();

            //Mediante un objeto de tipo Cursor, realizamos una consulta SQL y recogemos su resultado
            //Con esta consulta recogemos el usuario y la contraseña de la base de datos dado el usuario introducido
            String[] args = new String[] {usuario};
            Cursor cursor = bd.rawQuery("SELECT Usuario, Password FROM Usuarios WHERE Usuario = ?", args);

            //Nos posicionamos en el dato devuelto por la consulta
            boolean hayDatoDevuelto = cursor.moveToNext();

            //Si se ha devuelto algo, significa que el usuario estaba previamente registrado
            //Ahora nos queda comprobar que la contraseña introducida coincide con la registrada para ese usuario
            if (hayDatoDevuelto) {

                //Recogemos lo que nos interesa del resultado:
                //String usuarioBD = cursor.getString(0);
                String passwordBD = cursor.getString(1);

                //Cerramos el cursor y la conexión a la base de datos
                cursor.close();
                bd.close();

                //Comprobamos que la contraseña introducida por el usuario es correcta
                if (password.equals(passwordBD)) {

                    //Si el usuario y la contraseña introducidos coinciden con unos previamente registrados,
                    // se inicia la sesión del usuario mediante una nueva actividad que lo dirige a la página principal
                    // de la aplicación
                    Intent i = new Intent (this, PagPrincipal.class);
                    //Enviamos el nombre de usuario a esta nueva actividad
                    i.putExtra("usuario", usuario);
                    startActivity(i);
                    finish();

                    Toast.makeText(getApplicationContext(), "¡Bienvenido, " + usuario + "!", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Contraseña incorrecta, inténtalo de nuevo", Toast.LENGTH_LONG).show();
                }

            }
            else {
                Toast.makeText(getApplicationContext(), "Usuario incorrecto, inténtalo de nuevo", Toast.LENGTH_LONG).show();
            }

            //Cerramos el cursor y la conexión a la base de datos
            cursor.close();
            bd.close();

        }
        else {
            Toast.makeText(getApplicationContext(), "Introduce usuario y contraseña", Toast.LENGTH_LONG).show();
        }

    }

}