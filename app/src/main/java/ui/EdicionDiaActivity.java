package ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

public class EdicionDiaActivity extends AppCompatActivity {

    public final static String EXTRA_FECHA = "EdicionDiaActivity.fecha";
    public final static String EXTRA_DIARIO = "EdicionDiaActivity.diario";

    private SeekBar sb_ValoracionDia;
    private TextView tv_ValoraDia;
    private ImageView iv_fecha;
    private TextView tv_fechaElegida;
    private Calendar calendar = Calendar.getInstance();
    private FloatingActionButton fabGuardarEdicion;
    private DiaDiario diaDiario;


    private Date fechaActual;
    private int valorarDia;
    private EditText et_Resumen;
    private EditText etContenido;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edicion_dia_activty);

        sb_ValoracionDia = findViewById(R.id.sb_ValoracionDia);
        tv_ValoraDia = findViewById(R.id.tv_ValoraDia);
        iv_fecha = findViewById(R.id.iv_fecha);
        tv_fechaElegida = findViewById(R.id.tv_fechaElegida);
        fabGuardarEdicion = findViewById(R.id.fabGuardarEdicion);
        et_Resumen = findViewById(R.id.et_Resumen);
        etContenido = findViewById(R.id.etContenido);

        diaDiario = new DiaDiario();
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        date = calendar.getTime();

        tv_fechaElegida.setText(diaDiario.getStaticFechaFormatoLocal(date));


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

        fabGuardarEdicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String res = et_Resumen.getText().toString();
                String con = etContenido.getText().toString();

                if(res.isEmpty() || con.isEmpty()){
                    AlertDialog.Builder dia = new AlertDialog.Builder(EdicionDiaActivity.this);
                    dia.setTitle(getResources().getString(R.string.tituloAviso));
                    dia.setMessage(getResources().getString(R.string.mensajeAviso));
                    dia.setNeutralButton(getResources().getString(R.string.okAviso), null);
                    dia.show();
                }else{

                    SimpleDateFormat formatoDelTexto = new SimpleDateFormat("DD-MM-yyyy");

                    String fecha = formatoDelTexto.format(fechaActual);

                    Intent i = getIntent();

                    try {
                        diaDiario = new DiaDiario(formatoDelTexto.parse(fecha), valorarDia, res, con);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    i.putExtra(EXTRA_DIARIO ,(Parcelable) diaDiario);
                    i.putExtra(EXTRA_FECHA, fecha);

                    //Se la pasamos al MainActivity
                    setResult(RESULT_OK, i);
                    finish();
                }
            }
        });



    }

    public Date getFechaActual() {
        return fechaActual;
    }

    public void setFechaActual(Date fechaActual) {
        this.fechaActual = fechaActual;
    }

    public int getValorarDia() {
        return valorarDia;
    }

    public void setValorarDia(int valorarDia) {
        this.valorarDia = valorarDia;
    }

}