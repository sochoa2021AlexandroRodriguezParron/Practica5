package ui;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import model.DiaDiario;
import practica5.AlexandroRodriguez.iesseveroochoa.net.R;
import viewmodels.DiarioViewModel;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fabAñadir;
    private DiarioViewModel diarioViewModel;

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //Si el usuario pulsa OK en la Activity que hemos llamado
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        //Recuperamos los dados
                        Intent data = result.getData();

                        DiaDiario dia = data.getParcelableExtra(EdicionDiaActivity.EXTRA_DIARIO);

                        //Obtengo la fecha en formato String y la formateo a formato DATE, y le actualizo la fecha al dia
                        String fechaDia = data.getStringExtra(EdicionDiaActivity.EXTRA_FECHA);
                        SimpleDateFormat formatoDelTexto = new SimpleDateFormat("DD-MM-yyyy");

                        try {
                            dia.setFecha(formatoDelTexto.parse(fechaDia));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        diarioViewModel.insert(dia);
                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        fabAñadir = findViewById(R.id.fabAñadir);

        fabAñadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, EdicionDiaActivity.class);
                //Lanzamos el result
                mStartForResult.launch(i);
            }
        });

        diarioViewModel= new ViewModelProvider(this).get(DiarioViewModel.class);
        diarioViewModel.getAllDiarios().observe(this, new
                Observer<List<DiaDiario>>() {
                    @Override
                    public void onChanged(List<DiaDiario> diario) {
                        //adapter.setDiario(diario);
                        Log.d("P5","tamaño: "+diario.size());

                    }
                });

    }



    @Override public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}