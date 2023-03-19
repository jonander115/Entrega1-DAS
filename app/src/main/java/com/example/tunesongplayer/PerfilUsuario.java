package com.example.tunesongplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PerfilUsuario extends AppCompatActivity {

    private miBD GestorBD;
    private SQLiteDatabase bd;
    private String usuario;
    private EditText et_nombre;
    private EditText et_apellidos;
    private EditText et_email;
    private EditText et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        //Recogemos el usuario, que nos viene dado desde la actividad de la página principal
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario = extras.getString("usuario");
        }

        //Recogemos los elementos de la vista
        et_nombre = (EditText) findViewById(R.id.et_NombrePerfil);
        et_apellidos = (EditText) findViewById(R.id.et_ApellidosPerfil);
        et_email = (EditText) findViewById(R.id.et_EmailPerfil);
        et_password = (EditText) findViewById(R.id.et_ContraseñaPerfil);

        //Tenemos que mostrar al usuario sus datos, para ello hay que cogerlos de la base de datos

        //Creamos una conexión a la base de datos de la aplicación
        GestorBD = new miBD(this, "BaseDeDatos", null, 1);

        //Recogemos la base de datos en modo lectura
        bd = GestorBD.getReadableDatabase();

        //Mediante un objeto de tipo Cursor, realizamos una consulta SQL y recogemos su resultado
        String[] args = new String[] {usuario};
        Cursor cursor = bd.rawQuery("SELECT Nombre, Apellidos, Email, Password FROM Usuarios WHERE Usuario = ?", args);

        //Nos posicionamos en el dato devuelto por la consulta
        cursor.moveToNext();

        //Recogemos lo que nos interesa del resultado:
        String nombreBD = cursor.getString(0);
        String apellidosBD = cursor.getString(1);
        String emailBD = cursor.getString(2);
        String passwordBD = cursor.getString(3);

        //Cerramos el cursor y la conexión a la base de datos
        cursor.close();
        bd.close();

        //Mostramos al usuario sus datos
        et_nombre.setText(nombreBD);
        et_apellidos.setText(apellidosBD);
        et_email.setText(emailBD);
        et_password.setText(passwordBD);
    }


    //Método para guardar los cambios en la base de datos cuando el usuario pulse el botón
    public void onClick_GuardarCambios(View v){

        //Creamos una conexión a la base de datos de la aplicación
        GestorBD = new miBD(this, "BaseDeDatos", null, 1);

        //Recogemos la base de datos en modo escritura, para poder modificar los datos
        bd = GestorBD.getWritableDatabase();

        //Creamos una variable de tipo ContentValues e introducimos los datos
        ContentValues modificacionUsuario = new ContentValues();
        modificacionUsuario.put("Nombre",et_nombre.getText().toString());
        modificacionUsuario.put("Apellidos",et_apellidos.getText().toString());
        modificacionUsuario.put("Email",et_email.getText().toString());
        modificacionUsuario.put("Password",et_password.getText().toString());

        //Actualizamos la base de datos
        String[] args = new String[] {usuario};
        bd.update("Usuarios", modificacionUsuario, "Usuario=?",args);

        //Cerramos la conexión a la base de datos
        bd.close();

        Toast.makeText(getApplicationContext(), "Cambios guardados", Toast.LENGTH_LONG).show();

    }
}