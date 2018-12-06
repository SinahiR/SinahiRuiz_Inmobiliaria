package mx.edu.ittepic.dadm_proyectoinmobiliaria;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {

    EditText idinmu,domicilioinmu,preciov,precior,fechat,idp;
    Button insertar,consultar,eliminar,actualizar;
    BaseDatos base;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        idinmu=findViewById(R.id.idinmu);
        domicilioinmu=findViewById(R.id.domicilioinmu);
        preciov=findViewById(R.id.preciov);
        precior=findViewById(R.id.precior);
        fechat=findViewById(R.id.fechat);
        idp=findViewById(R.id.idp);

        insertar=findViewById(R.id.insertar);
        consultar=findViewById(R.id.consultar);
        eliminar=findViewById(R.id.eliminar);
        actualizar=findViewById(R.id.actualizar);

        //asignarle memoria y configuracion
        //cursosr, navegar entre los datos
        base = new BaseDatos(this,"inmobiliaria",null,2);


        insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codigoInsertar();
            }
        });

        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirID(1);
            }
        });

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actualizar.getText().toString().startsWith("MODIFICAR")){
                    invocarConfirmacionActualizacion();
                }else{
                    pedirID(2);
                }

            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pedirID(3);
            }
        });


    }

    private void invocarConfirmacionActualizacion() {
        AlertDialog.Builder confir = new AlertDialog.Builder(this);
        confir.setTitle("IMPORTANTE, PONER ATENCION").setMessage("estas Totalmente seguro que deseas aplicar cambios")
                .setPositiveButton("si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        aplicarActualizar();
                        dialog.dismiss();
                    }
                }).setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                habilitarBotonesYLimpiarCampos();
                dialog.cancel();
            }
        }).show();
    }
    private void habilitarBotonesYLimpiarCampos() {
        idinmu.setText("");
        domicilioinmu.setText("");
        preciov.setText("");
        precior.setText("");
        fechat.setText("");
        idp.setText("");

        insertar.setEnabled(true);
        consultar.setEnabled(true);
        eliminar.setEnabled(true);
        actualizar.setText("ACTUALIZAR");
        idinmu.setEnabled(true);
    }

    private void aplicarActualizar() {
        try{
            SQLiteDatabase tabla = base.getWritableDatabase();

            String SQL= "UPDATE INMUEBLE SET DOMICILIOINMU='"+domicilioinmu.getText().toString()+"', PRECIOVENTA="
                    +preciov.getText().toString()+", PRECIORENTA="+precior.getText().toString()+" ,FECHATRANSACCION='"+fechat.getText().toString()+
                    "' WHERE IDINMUEBLE="+idinmu.getText().toString();
            tabla.execSQL(SQL);
            tabla.close();
            Toast.makeText(this,"Datos Actualizados",Toast.LENGTH_LONG).show();

        }catch (SQLiteException e){
            Toast.makeText(this,"No se pudo actualizar",Toast.LENGTH_LONG).show();
        }
        habilitarBotonesYLimpiarCampos();
    }



    private void pedirID(final int origen) {
        final EditText pidoID = new EditText(this);
        pidoID.setInputType(InputType.TYPE_CLASS_NUMBER);
        pidoID.setHint("Valor entero mayor de 0");
        String mensaje ="Escriba el id a buscar";

        AlertDialog.Builder alerta = new AlertDialog.Builder(this);

        if(origen ==2){
            mensaje ="Ecriba el id a modificar";
        }
        if(origen ==3){
            mensaje ="Escriba que desea eliminar";
        }

        alerta.setTitle("atencion").setMessage(mensaje)
                .setView(pidoID)
                .setPositiveButton("Buscar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(pidoID.getText().toString().isEmpty()){
                            Toast.makeText(Main2Activity.this,"DEbes escribir un numero",Toast.LENGTH_LONG).show();
                            return;
                        }
                        buscarDato(pidoID.getText().toString(), origen);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancelar",null).show();
    }

    private void buscarDato(String idaBuscar, int origen){
        try{

            SQLiteDatabase tabla = base.getReadableDatabase();

            String SQL = "SELECT *FROM INMUEBLE WHERE IDINMUEBLE="+idaBuscar;

            Cursor resultado = tabla.rawQuery(SQL,null);
            if(resultado.moveToFirst()){ //mover le primer resultado obtenido de la consulta
                //si hay resulta´do
                if(origen==3){
                    //se consulto para borrar
                    String dato = idaBuscar+"&"+ resultado.getString(1)+"&"+resultado.getString(2)+
                            "&"+resultado.getString(3) +"&"+resultado.getString(4)+
                            "&"+resultado.getString(5);
                    invocarConfirmacionEliminacion(dato);
                    return;
                }

                idinmu.setText(resultado.getString(0));
                domicilioinmu.setText(resultado.getString(1));
                preciov.setText(resultado.getString(2));
                precior.setText(resultado.getString(3));
                fechat.setText(resultado.getString(4));
                idp.setText(resultado.getString(5));
                if(origen==2){
                    //modificar
                    insertar.setEnabled(false);
                    consultar.setEnabled(false);
                    eliminar.setEnabled(false);
                    actualizar.setText("MODIFICAR");
                    idinmu.setEnabled(false);
                    idp.setEnabled(false);
                }
            }else {
                //no hay resultado!
                Toast.makeText(this,"No se encontro resultado",Toast.LENGTH_LONG).show();
            }
            tabla.close();

        }catch (SQLiteException e){
            Toast.makeText(this,"No se pudo buscar",Toast.LENGTH_LONG).show();
        }
    }

    private void invocarConfirmacionEliminacion(final String dato) {
        String datos[]=dato.split("&");
        final String id=datos[0];
        final String domicilio=datos[1];
        final String preciov=datos[2];
        final String precior=datos[3];
        final String fechat=datos[4];
        final String idp=datos[5];

        AlertDialog.Builder alerta= new AlertDialog.Builder(this);
        alerta.setTitle("ATENCION").setMessage("Seguro que deseas eliminar: "+"\n"+
                "ID INMUEBLE: "+id+"\n"+
                "Domicilio: " + domicilio+  "\n"+
                "Precio Venta: " + preciov+ "\n"+
                "Precio Renta: " + precior+ "\n"+
                "Fecha Transaccion: " + fechat+ "\n"+
                "IDP: " + idp+ "\n")
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
        try {
            SQLiteDatabase tabla = base.getReadableDatabase();
            String SQL = "DELETE FROM INMUEBLE WHERE IDINMUEBLE=" + idEliminar;
            tabla.execSQL(SQL);
            tabla.close();

            Toast.makeText(Main2Activity.this, "Se elimino correctamente", Toast.LENGTH_LONG).show();

            idp.setText("");
            domicilioinmu.setText("");
            preciov.setText("");
            precior.setText("");
            fechat.setText("");
            idp.setText("");

        }
        catch (SQLiteException e)
        {
            Toast.makeText(Main2Activity.this, "Error al eliminar datos",Toast.LENGTH_LONG).show();
        }


        }

    private void codigoInsertar() {
        try {



            //metodo que compete a la inserccion,
            SQLiteDatabase tabla = base.getWritableDatabase();
                String SQL= "INSERT INTO INMUEBLE VALUES("+idinmu.getText().toString()+",'"+domicilioinmu.getText().toString()
                +"','"+preciov.getText().toString()+"','"+precior.getText().toString()+"', '"+fechat.getText().toString()+"', "+idp.getText().toString()+")";

            tabla.execSQL(SQL);

            Toast.makeText(this,"Si se pudo",Toast.LENGTH_LONG).show();
            tabla.close();

            idp.setText("");
            idinmu.setText("");
            domicilioinmu.setText("");
            precior.setText("");
            preciov.setText("");
            fechat.setText("");

        }catch (SQLiteException e){

            Toast.makeText(this,"No se pudo",Toast.LENGTH_LONG).show();

        }
    }
}
