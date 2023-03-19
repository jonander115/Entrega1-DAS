package com.example.tunesongplayer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class miBD extends SQLiteOpenHelper {

    public miBD (@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //Creación de la tabla de usuarios
        sqLiteDatabase.execSQL("CREATE TABLE Usuarios " +
                "('Usuario' VARCHAR(50) PRIMARY KEY NOT NULL, " +
                "'Password' VARCHAR(200)," +
                "'Nombre' VARCHAR(200)," +
                "'Apellidos' VARCHAR(200)," +
                "'Email' VARCHAR(200))");

        //Creación de la tabla de playlists
        sqLiteDatabase.execSQL("CREATE TABLE Playlists " +
                "('Nombre' VARCHAR(50), " +
                "'Usuario' VARCHAR(50)," +
                "'NumCanciones' INTEGER, " +
                "PRIMARY KEY ('Nombre','Usuario'))");

        //Creación de la tabla de canciones
        sqLiteDatabase.execSQL("CREATE TABLE Canciones " +
                "('Nombre' VARCHAR(200), " +
                "'Autor' VARCHAR(200), " +
                "PRIMARY KEY ('Nombre','Autor'))");

        //Creación de la tabla que relaciona las playlists con las canciones que tienen
        sqLiteDatabase.execSQL("CREATE TABLE RelacionPlaylistsCanciones " +
                "('NombrePlaylist' VARCHAR(50), " +
                "'NombreCancion' VARCHAR(200), " +
                "'AutorCancion' VARCHAR(200), " +
                "'NombreUsuario' VARCHAR(50), " +
                "PRIMARY KEY ('NombrePlaylist','NombreCancion','NombreUsuario','NombreAutor'))");

        //Inserción de las canciones
        //Los archivos mp3 de las canciones están en la carpeta res/raw, pero su información se almacena en la base de datos
        //Las canciones se han obtenido de https://www.bensound.com/royalty-free-music
        sqLiteDatabase.execSQL("INSERT INTO Canciones VALUES ('Boundless space','Denis Pavlov')");
        sqLiteDatabase.execSQL("INSERT INTO Canciones VALUES ('Burning fire','Dan Phillipson')");
        sqLiteDatabase.execSQL("INSERT INTO Canciones VALUES ('Closer to the sun','Veace D')");
        sqLiteDatabase.execSQL("INSERT INTO Canciones VALUES ('Faster, stronger','Eevert Zeevalkink')");
        sqLiteDatabase.execSQL("INSERT INTO Canciones VALUES ('Get what you want','Dan Phillipson')");
        sqLiteDatabase.execSQL("INSERT INTO Canciones VALUES ('Hit the ground','Ed Records')");
        sqLiteDatabase.execSQL("INSERT INTO Canciones VALUES ('Plain','Ester')");
        sqLiteDatabase.execSQL("INSERT INTO Canciones VALUES ('Rabbits Hop','TwinsMusic')");
        sqLiteDatabase.execSQL("INSERT INTO Canciones VALUES ('Rock and dust','Ed Records')");
        sqLiteDatabase.execSQL("INSERT INTO Canciones VALUES ('Slider','Yari')");

        //Esta canción se ha introducido dos veces cambiando el nombre del autor para comprobar que en el ListView aparece dos veces
        sqLiteDatabase.execSQL("INSERT INTO Canciones VALUES ('Boundless space','Denis Pavlov 2')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        //Creación de la tabla de usuarios
        sqLiteDatabase.execSQL("CREATE TABLE Usuarios " +
                "('Usuario' VARCHAR(50) PRIMARY KEY NOT NULL, " +
                "'Password' VARCHAR(200)," +
                "'Nombre' VARCHAR(200)," +
                "'Apellidos' VARCHAR(200)," +
                "'Email' VARCHAR(200))");

        //Creación de la tabla de playlists
        sqLiteDatabase.execSQL("CREATE TABLE Playlists " +
                "('Nombre' VARCHAR(50), " +
                "'Usuario' VARCHAR(50)," +
                "'NumCanciones' INTEGER, " +
                "PRIMARY KEY ('Nombre','Usuario'))");

        //Creación de la tabla de canciones
        sqLiteDatabase.execSQL("CREATE TABLE Canciones " +
                "('Nombre' VARCHAR(200), " +
                "'Autor' VARCHAR(200), " +
                "PRIMARY KEY ('Nombre','Autor'))");

        //Creación de la tabla que relaciona las playlists con las canciones que tienen
        sqLiteDatabase.execSQL("CREATE TABLE RelacionPlaylistsCanciones " +
                "('NombrePlaylist' VARCHAR(50), " +
                "'NombreCancion' VARCHAR(200), " +
                "'AutorCancion' VARCHAR(200), " +
                "'NombreUsuario' VARCHAR(50), " +
                "PRIMARY KEY ('NombrePlaylist','NombreCancion','NombreUsuario','NombreAutor'))");

        //Inserción de las canciones
        //Los archivos mp3 de las canciones están en la carpeta res/raw, pero su información se almacena en la base de datos
        //Las canciones se han obtenido de https://www.bensound.com/royalty-free-music
        sqLiteDatabase.execSQL("INSERT INTO Canciones VALUES ('Boundless space','Denis Pavlov')");
        sqLiteDatabase.execSQL("INSERT INTO Canciones VALUES ('Burning fire','Dan Phillipson')");
        sqLiteDatabase.execSQL("INSERT INTO Canciones VALUES ('Closer to the sun','Veace D')");
        sqLiteDatabase.execSQL("INSERT INTO Canciones VALUES ('Faster, stronger','Eevert Zeevalkink')");
        sqLiteDatabase.execSQL("INSERT INTO Canciones VALUES ('Get what you want','Dan Phillipson')");
        sqLiteDatabase.execSQL("INSERT INTO Canciones VALUES ('Hit the ground','Ed Records')");
        sqLiteDatabase.execSQL("INSERT INTO Canciones VALUES ('Plain','Ester')");
        sqLiteDatabase.execSQL("INSERT INTO Canciones VALUES ('Rabbits Hop','TwinsMusic')");
        sqLiteDatabase.execSQL("INSERT INTO Canciones VALUES ('Rock and dust','Ed Records')");
        sqLiteDatabase.execSQL("INSERT INTO Canciones VALUES ('Slider','Yari')");

        //Esta canción se ha introducido dos veces cambiando el nombre del autor para comprobar que en el ListView aparece dos veces
        sqLiteDatabase.execSQL("INSERT INTO Canciones VALUES ('Boundless space','Denis Pavlov 2')");
    }
}
