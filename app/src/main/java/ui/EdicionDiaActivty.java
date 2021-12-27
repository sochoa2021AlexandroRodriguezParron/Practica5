package ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Date;

import model.DiaDiario;
import practica5.AlexandroRodriguez.iesseveroochoa.net.R;

public class EdicionDiaActivty extends AppCompatActivity {

    public static String EXTRA_DATOS = "EdicionDiaActivity.datos";

    private SeekBar sb_ValoracionDia;
    private TextView tv_ValoraDia;
    private ImageView iv_fecha;
    private TextView tv_fechaElegida;
    private Calendar calendar = Calendar.getInstance();
    private FloatingActionButton fabGuardarEdicion;


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
                DatePickerDialog dialogo = new DatePickerDialog(EdicionDiaActivty.this, new
                        DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int
                                    monthOfYear, int dayOfMonth) {
                                calendar.set(year, monthOfYear, dayOfMonth);
                                Date fecha=calendar.getTime();;

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
                DiaDiario diaDiario = new DiaDiario(fechaActual, valorarDia, res, con);

                if(res.isEmpty() || con.isEmpty()){
                    AlertDialog.Builder dia = new AlertDialog.Builder(EdicionDiaActivty.this);
                    dia.setTitle(getResources().getString(R.string.tituloAviso));
                    dia.setMessage(getResources().getString(R.string.mensajeAviso));
                    dia.setNeutralButton(getResources().getString(R.string.okAviso), null);
                    dia.show();
                }else{
                    Intent intent = new Intent(EdicionDiaActivty.this, MainActivity.class);
                    intent.putExtra(EXTRA_DATOS ,(Parcelable) diaDiario);
                    startActivity(intent);
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