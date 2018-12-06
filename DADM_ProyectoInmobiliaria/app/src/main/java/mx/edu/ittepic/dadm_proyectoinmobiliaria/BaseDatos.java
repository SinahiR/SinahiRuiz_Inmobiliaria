package mx.edu.ittepic.dadm_proyectoinmobiliaria;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDatos extends SQLiteOpenHelper {
    public BaseDatos(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    @Override
    //CREATETABLE y en caso de ser necesario inserta datos predeterminados (los insert)
    //Se ejecuta cuando la aplicacin se incia en el celular
    //Sirve para construir en el SQLITE que esta CEL las tablas que la APP requiere para funcionar
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //Funciona para insert, create table, delate, update
        sqLiteDatabase.execSQL( "CREATE TABLE PROPIETARIO(IDP INTEGER PRIMARY KEY NOT NULL, NOMBRE VARCHAR(200), DOMICILIO VARCHAR(500)" +
                ", TELEFONO VARCHAR (50) )" );

        sqLiteDatabase.execSQL("CREATE TABLE INMUEBLE(" +
                "IDINMUEBLE INTEGER PRIMARY KEY NOT NULL," +
                "DOMICILIOINMU VARCHAR(200)," +
                "PRECIOVENTA FLOAT," +
                "PRECIORENTA FLOAT," +
                "FECHATRANSACCION DATE," +
                "IDP INTEGER," +
                "FOREIGN KEY(IDP) REFERENCES PROPIETARIO(IDP)" +
                ")");

        //Si requieres otra tabla hacer lo mismo que la linea de arriba

        //Select, regresa un tipo cursor
        // sqLiteDatabase.rawQuery()

    }

    @Override
    //Se ejecuta cuando se va a modificar la estructura de la tabla
    //Alteracion de tablas y estructuras
    //Las versiones nunca va en descenso
    //Upgrate   ejecuta una actualizacion mayor, se modifican tablas no datos

    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}

