package com.example.tunesongplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Método para cuando se haga click en el botón para Iniciar sesión
    public void bt_IniciarSesion_onClick(View v) {
        //Al hacer click en el botón de Iniciar sesión, se abre la actividad correspondiente
        Intent i = new Intent (this, IniciarSesion.class);
        startActivity(i);
    }

    //Método para cuando se haga click en el botón para Registrarse
    public void bt_Registrarse_onClick(View v) {
        //Al hacer click en el botón de Registrarse, se abre la actividad correspondiente
        Intent i = new Intent (this, Registrarse.class);
        startActivity(i);
    }

}