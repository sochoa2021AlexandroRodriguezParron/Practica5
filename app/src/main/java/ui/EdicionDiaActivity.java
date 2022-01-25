package ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.DateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import model.DiaDiario;
import model.DiarioDao;
import practica5.AlexandroRodriguez.iesseveroochoa.net.R;
import viewmodels.DiarioViewModel;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.CAMERA;


public class EdicionDiaActivity extends AppCompatActivity {

    /**CONSTANTES**/
    public final static String EXTRA_FECHA = "EdicionDiaActivity.fecha";
    public final static String EXTRA_DIARIO = "EdicionDiaActivity.diario";
    private static final int STATUS_CODE_SELECCION_IMAGEN = 300;
    private static final int MY_PERMISSIONS = 100;


    /**ATRIBUTOS**/
    private SeekBar sb_ValoracionDia;
    private TextView tv_ValoraDia;
    private ImageView iv_fecha;
    private TextView tv_fechaElegida;
    private Calendar calendar = Calendar.getInstance();
    private FloatingActionButton fabGuardarEdicion;
    private DiaDiario diaDiario;
    private ConstraintLayout clPrincipal;

    private Date fechaActual;
    private int valorarDia;
    private EditText et_Resumen;
    private EditText etContenido;
    private Uri uriFoto = null;
    private MenuItem action_galeria;
    private ImageView ivFotoDia;


    /**
     * Método OnCreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edicion_dia_activty);

        /**Referencia a los objetos del activity**/
        sb_ValoracionDia = findViewById(R.id.sb_ValoracionDia);
        tv_ValoraDia = findViewById(R.id.tv_ValoraDia);
        iv_fecha = findViewById(R.id.iv_fecha);
        tv_fechaElegida = findViewById(R.id.tv_fechaElegida);
        fabGuardarEdicion = findViewById(R.id.fabGuardarEdicion);
        et_Resumen = findViewById(R.id.et_Resumen);
        etContenido = findViewById(R.id.etContenido);
        action_galeria = findViewById(R.id.action_galeria);
        ivFotoDia = findViewById(R.id.ivFotoDia);
        clPrincipal = findViewById(R.id.clPrincipal);

        /**Fecha que se pondrá por defecto en el textView de la actividad**/
        diaDiario = null;
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        date = calendar.getTime();

        tv_fechaElegida.setText(diaDiario.getStaticFechaFormatoLocal(date));

        /**Para valorar el dia**/
        valorarDia = sb_ValoracionDia.getProgress();
        sb_ValoracionDia.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String textoFormateado = getResources().getString(R.string.tv_ValoraDiaFormateado, progress);
                valorarDia = progress;
                tv_ValoraDia.setText(textoFormateado);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                String textoFormateado = getResources().getString(R.string.tv_ValoraDia);
                tv_ValoraDia.setText(textoFormateado);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        /**Para darle función a la imagen de la fecha**/
        iv_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar newCalendar = Calendar.getInstance();
                DatePickerDialog dialogo = new DatePickerDialog(EdicionDiaActivity.this, new
                        DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int
                                    monthOfYear, int dayOfMonth) {
                                calendar.set(year, monthOfYear, dayOfMonth);

                                Date fecha=calendar.getTime();
                                fechaActual = fecha;

                                String fechaFormateada = getResources().getString(R.string.tv_fechaFormateada, DiaDiario.getStaticFechaFormatoLocal(fecha));

                                tv_fechaElegida.setText(fechaFormateada);
                            }
                        },newCalendar.get(Calendar.YEAR),
                        newCalendar.get(Calendar.MONTH),
                        newCalendar.get(Calendar.DAY_OF_MONTH));//esto último es el dia a mostrar
                dialogo.show();
            }
        });

        /**Actualizamos el atributo fechaActual**/
        fechaActual = calendar.getTime();


        DiaDiario diaExtra = getIntent().getParcelableExtra(MainActivity.EXTRA_MAIN);
        String fechaExtra = getIntent().getStringExtra(MainActivity.EXTRA_FECHA);
        SimpleDateFormat formatoDelTexto = new SimpleDateFormat("DD-MM-yyyy");
        /**En caso de que el dia pasado por el intent no sea nulo**/
        if(diaExtra != null){
            /**Actualizamos los valores de los campos**/
            this.setTitle(diaExtra.getResumen());
            et_Resumen.setText(diaExtra.getResumen());
            tv_ValoraDia.setText(getResources().getString(R.string.tv_ValoraDiaFormateado, diaExtra.getValoracionDia()));
            sb_ValoracionDia.setProgress(diaExtra.getValoracionDia());
            etContenido.setText(diaExtra.getContenido());
            diaDiario = diaExtra;

            //URI
            if(diaDiario.getFotoUri()!=""){
                uriFoto=Uri.parse(diaDiario.getFotoUri());
                muestraFoto();
            }

            Date fecha = null;
            try {
                fecha = formatoDelTexto.parse(fechaExtra);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            diaDiario.setFecha(fecha);
            fechaExtra = diaExtra.getStaticFechaFormatoLocal(fecha);
            tv_fechaElegida.setText(fechaExtra);
        }

        /**Al darle al botón de guardar**/
        fabGuardarEdicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String res = et_Resumen.getText().toString();
                String con = etContenido.getText().toString();
                String fecha = "";
                /**Creamos un Dialogo en caso de que falte algúno de los campos por rellenar**/
                if(res.isEmpty() || con.isEmpty()){
                    AlertDialog.Builder dia = new AlertDialog.Builder(EdicionDiaActivity.this);
                    dia.setTitle(getResources().getString(R.string.tituloAviso));
                    dia.setMessage(getResources().getString(R.string.mensajeAviso));
                    dia.setNeutralButton(getResources().getString(R.string.okAviso), null);
                    dia.show();
                }else{
                    /**En caso de que ya haya un diaDiario**/
                    if(diaDiario != null){
                        /**Actualizamos los valores del diaDiario y se lo volvemos a mandar a la siguiente actividad**/
                        //Fecha
                        SimpleDateFormat formatoDelTexto = new SimpleDateFormat("DD-MM-yyyy");
                        fecha = formatoDelTexto.format(fechaActual);
                        Date fechaActualizada = null;
                        try {
                            fechaActualizada = formatoDelTexto.parse(fecha);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        diaDiario.setFecha(fechaActualizada);
                        //Resumen
                        diaDiario.setResumen(res);
                        //Valoracion Dia
                        diaDiario.setValoracionDia(valorarDia);
                        //Contenido
                        diaDiario.setContenido(con);
                        //URI
                        if(!uriFoto.toString().equals("")){
                            diaDiario.setFotoUri(uriFoto.toString());
                        }

                        Intent i = getIntent();
                        i.putExtra(EXTRA_DIARIO ,(Parcelable) diaDiario);
                        //Mando la fecha en formato String, para formatearla
                        i.putExtra(EXTRA_FECHA, fecha);
                        guardarDiaPreferencias(new Date());
                        //Se la pasamos al MainActivity
                        setResult(RESULT_OK, i);
                        finish();

                    }else{
                        /**Si no hay ningún diaDiario**/
                        pasarDatosaOtraActividad(res, con);
                    }

                }
            }
        });
    }

    private void pasarDatosaOtraActividad(String res, String con) {
        /**Lo cremos mediante los campos obteniendo sus valores y se lo mandamos a la siguiente actividad**/
        diaDiario = new DiaDiario();
        SimpleDateFormat formatoDelTexto = new SimpleDateFormat("DD-MM-yyyy");

        String fecha = formatoDelTexto.format(fechaActual);

        try {
            diaDiario = new DiaDiario(formatoDelTexto.parse(fecha), valorarDia, res, con);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(!uriFoto.toString().equals("")){
            diaDiario.setFotoUri(uriFoto.toString());
        }

        Intent i = getIntent();

        i.putExtra(EXTRA_DIARIO ,(Parcelable) diaDiario);
        //Mando la fecha en formato String, para formatearla
        i.putExtra(EXTRA_FECHA, fecha);

        //Se la pasamos al MainActivity
        setResult(RESULT_OK, i);
        finish();
    }

    /**-------------PARTE 3-------------**/
    private void guardarDiaPreferencias(Date fecha) {
        //buscamos el fichero de preferencias
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file),
                Context.MODE_PRIVATE);
        //lo abrimos en modo edición
        SharedPreferences.Editor editor = sharedPref.edit();
        //guardamos la fecha del día como entero
        editor.putLong(getString(R.string.pref_key_ultimo_dia),fecha.getTime());
        //finalizamos
        editor.commit();
    }

    /**-------------PARTE 4-------------**/
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edicion, menu);
        return true;
    }
    /**
     * Método para saltar a la activity de la galeria
     */
    private void elegirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent.createChooser(intent, getResources().getString(R.string.elegirImagen)), STATUS_CODE_SELECCION_IMAGEN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case STATUS_CODE_SELECCION_IMAGEN:
                    uriFoto = data.getData(); //Establecemos la uri
                    muestraFoto();
                    break;
            }
        }
    }

    /**
     * Método para mostrar las opciones del menú
     * mediante un dialog.
     */
    private void muestraOpcionesImagen() {
        final CharSequence[] option = {getString(R.string.foto), getString(R.string.galeria), getString(android.R.string.cancel)};
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(android.R.string.dialog_alert_title);
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        // abrirCamara();//opcional
                        break;
                    case 1:
                        elegirGaleria();
                        break;
                }
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * Para mostrar la foto
     */
    private void muestraFoto(){
        Glide.with(this)
                .load(uriFoto) // Uri of the picture
                .into(ivFotoDia);//imageView
    }

    /**
     * Método para darle función al item del menú
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_galeria:
                ocultarTeclado();
                if (noNecesarioSolicitarPermisos()) {
                    muestraOpcionesImagen();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**PERMISOS**/
    private boolean noNecesarioSolicitarPermisos() {
        //si la versión es inferior a la 6
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;
        //comprobamos si tenemos los permisos
        if ((checkSelfPermission(WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(CAMERA) ==
                        PackageManager.PERMISSION_GRANTED))
            return true;
        //indicamos al usuario porqué necesitamos los permisos siempre que no
        //haya indicado que no lo volvamos a hacer
        if ((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) ||
                (shouldShowRequestPermissionRationale(CAMERA))) {
            Snackbar.make(clPrincipal, getResources().getString(R.string.textoPermisos),
                    Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok,
                    new View.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onClick(View v) {
                            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,
                                    CAMERA}, MY_PERMISSIONS);
                        }
                    }).show();
        } else {//pedimos permisos sin indicar el porqué
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA},
                    MY_PERMISSIONS);
        }
        return false;//necesario pedir permisos
    }

    /**
     * Si se deniegan los permisos mostramos las opciones de la aplicación
     para que el usuario acepte los permisos
     */
    private void muestraExplicacionDenegacionPermisos() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.peticionPermisos));
                builder.setMessage(getResources().getString(R.string.textoPermisos));
        builder.setPositiveButton(android.R.string.ok, new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();

                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        builder.show();
    }

    /**
     * Cuando se soliciten los permisos al usuario
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]
            permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions,
                grantResults);
        if (requestCode == MY_PERMISSIONS) {
            if (grantResults.length == 2 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED && grantResults[1] ==
                    PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getResources().getString(R.string.permisoAceptado),
                        Toast.LENGTH_SHORT).show();
                muestraOpcionesImagen();
            } else {//si no se aceptan los permisos
                muestraExplicacionDenegacionPermisos();
            }
        }
    }

    /**
     * Permite ocultar el teclado
     */
    private void ocultarTeclado() {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        // mgr.showSoftInput(etDatos, InputMethodManager.HIDE_NOT_ALWAYS);
        if (imm != null) {
            imm.hideSoftInputFromWindow(et_Resumen.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(etContenido.getWindowToken(), 0);
        }
    }

}
