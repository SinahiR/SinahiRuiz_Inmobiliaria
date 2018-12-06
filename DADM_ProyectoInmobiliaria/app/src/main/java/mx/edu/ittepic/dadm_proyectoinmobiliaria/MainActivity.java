package mx.edu.ittepic.dadm_proyectoinmobiliaria;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText idp, nombre, domicilio, telefono;
    Button insertar, consultar, eliminar, actualizar, inmuebles;
    BaseDatos base; //la manera en la que conectare la interfaz a la bd

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idp=findViewById(R.id.idp);
        nombre=findViewById(R.id.nombre);
        domicilio=findViewById(R.id.domicilio);
        telefono=findViewById(R.id.telefono);

        insertar=findViewById(R.id.insertar);
        consultar=findViewById(R.id.consultar);
        eliminar=findViewById(R.id.eliminar);
        actualizar=findViewById(R.id.actualizar);
        inmuebles=findViewById(R.id.inmuebles);

        //Asignarle memoria y configuracion--> lo ejecuta this
        //this o main activity si estas en un boton
        //primera es el NOMBRE DE LA BD
        //Persona es la tabla
        //BaseDatos es quien hace la conexion
        base= new BaseDatos(this, "inmobiliaria", null, 2);

        //Proceso de insersion
        insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codigoInsertar();
            }
        });

        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pedirID(1);
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pedirID(3);
            }
        });

       actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(actualizar.getText().toString().startsWith("MODIFICAR"))
                {
                    invocarConfirmacionActualizacion();
                }
                else {
                    pedirID(2);
                }
            }
        });

       inmuebles.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent otraventana=new Intent(MainActivity.this, Main2Activity.class);
               startActivity(otraventana);
           }
       });

    }

    private void invocarConfirmacionActualizacion(){
        AlertDialog.Builder confir = new AlertDialog.Builder(this);
        confir.setTitle("ALERTA!!").setMessage("" +
                "Seguro que deseas actualizar?")
                .setPositiveButton("si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        aplicarActualizar();
                        dialog.dismiss();
                    }
                }).setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        }).show();
    }
    private void aplicarActualizar(){
        try{
            SQLiteDatabase tabla = base.getWritableDatabase();

            String SQL= "UPDATE PROPIETARIO SET NOMBRE='"+nombre.getText().toString()+"', DOMICILIO='"
                    +domicilio.getText().toString()+"', TELEFONO='"+telefono.getText().toString()+"' " +
                    "WHERE IDP="+idp.getText().toString();
            tabla.execSQL(SQL);
            tabla.close();
            Toast.makeText(this,"Se modificaron los datos ",Toast.LENGTH_LONG).show();

        }catch (SQLiteException e){
            Toast.makeText(this,"Erorr al modificar",Toast.LENGTH_LONG).show();
        }

        idp.setText("");
        nombre.setText("");
        domicilio.setText("");
        telefono.setText("");
        insertar.setEnabled(true);
        consultar.setEnabled(true);
        eliminar.setEnabled(true);
        actualizar.setText("MODIFICAR");
        idp.setEnabled(true);

    }

    private void invocarConfirmacionEliminacion(final String dato) {
        String datos[]=dato.split("&");
        final String id=datos[0];
        final String nombre=datos[1];
        final String domicilio=datos[2];
        final String telefono=datos[3];

        AlertDialog.Builder alerta= new AlertDialog.Builder(this);
        alerta.setTitle("ATENCION").setMessage("Seguro que deseas eliminar: "+"\n"+
                "IDP: "+id+"\n"+
                "Nombre: " + nombre+  "\n"+
                "Domicilo: " + domicilio+ "\n"+
                "Telefono: " + telefono+ "\n")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        eliminarMetodo(id);
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Cancelar", null).show();

    }

    private void eliminarMetodo(String idEliminar) {
        try
        {
            SQLiteDatabase tabla=base.getReadableDatabase();
            String SQL= "DELETE FROM PROPIETARIO WHERE IDP="+idEliminar;
            tabla.execSQL(SQL);
            tabla.close();

            Toast.makeText(MainActivity.this,"Se elimino correctamente", Toast.LENGTH_LONG).show();

            idp.setText("");
            nombre.setText("");
            domicilio.setText("");
            telefono.setText("");



        }
        catch (SQLiteException e)
        {
            Toast.makeText(MainActivity.this, "Error al eliminar datos",Toast.LENGTH_LONG).show();
        }
    }
    private void pedirID(final int origen)
    {

        final EditText pidoId=new EditText(this); // campo a utilizar
        String mensaje="Escriba el ID a buscar";
        pidoId.setInputType(InputType.TYPE_CLASS_NUMBER);
        pidoId.setHint("VALOR ENTERO MAYOR DE 0");

        AlertDialog.Builder alerta= new AlertDialog.Builder(this);
        if(origen==2)
        {
            mensaje="ESCRIBA ID A MODIFICAR";
        }
        if(origen==3)
        {
            mensaje="ESCRIBA ID QUE DESEA ELIMINAR";
        }
        alerta.setTitle("ATENCION").setMessage(mensaje)
                .setView(pidoId)
                .setPositiveButton("BUSCAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(pidoId.getText().toString().isEmpty())
                        {
                            Toast.makeText(MainActivity.this,"DEBES ESCRIBIR UN VALOR", Toast.LENGTH_LONG).show();
                            return;
                        }
                        buscarDato(pidoId.getText().toString(), origen);
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("CANCELAR", null)
                .show();
    }

    //Por cada que se envia se tiene que recibir uno
    private void buscarDato(String idBuscar, int origen) {

        try{
            //en caso de nombre
            //String SQL="SELECT * FORM PERSONA WHERE NOMBRE LIKE '%" +idBuscar + "%'";
            SQLiteDatabase tabla=base.getReadableDatabase();

            String SQL="SELECT * FROM PROPIETARIO WHERE IDP=" + idBuscar;
            Cursor resultado=tabla.rawQuery(SQL, null);

            if(resultado.moveToFirst())
            {
                if(origen==3)
                {
                    //Se consulto para borrar
                    String datos=idBuscar+"&"+resultado.getString(1)+"&"+resultado.getString(2)+"&"+resultado.getString(3);
                    invocarConfirmacionEliminacion(datos);
                    return;
                }
                //Si hay resultado
                idp.setText(resultado.getString(0));
                nombre.setText(resultado.getString(1));
                domicilio.setText(resultado.getString(2));
                telefono.setText(resultado.getString(3));
                Toast.makeText(this, "DATO ENCONTRADO",Toast.LENGTH_LONG).show();

                if(origen==2) {
                    insertar.setEnabled(false);
                    consultar.setEnabled(false);
                    eliminar.setEnabled(false);
                    actualizar.setText("MODIFICAR");
                    idp.setEnabled(false);
                }
            }

            else
            {
                Toast.makeText(this, "NO SE ENCONTRO EL RESULTADO",Toast.LENGTH_LONG).show();
            }
            tabla.close(); //Si lo pusieramos antes del if no podria mostrar los resultado ya que no podria acceder a ella
        }
        catch (SQLiteException e)
        {
            Toast.makeText(this,"NO SE PUDO BUSCAR", Toast.LENGTH_LONG).show();
        }
    }



    private void codigoInsertar() {
        try {

            //metodo que compete a la inserccion,
            SQLiteDatabase tabla = base.getWritableDatabase();
            String SQL= "INSERT INTO PROPIETARIO VALUES("+idp.getText().toString()+",'"+nombre.getText().toString()
                    +"','"+domicilio.getText().toString()+"','"+telefono.getText().toString()+"')";
            tabla.execSQL(SQL);
            tabla.close();
            Toast.makeText(this,"Si se pudo",Toast.LENGTH_LONG).show();

        }catch (SQLiteException e){

            Toast.makeText(this,"Error",Toast.LENGTH_LONG).show();

        }
    }
}