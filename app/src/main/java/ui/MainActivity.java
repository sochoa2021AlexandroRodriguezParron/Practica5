package ui;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import model.DiaDiario;
import practica5.AlexandroRodriguez.iesseveroochoa.net.R;
import viewmodels.AdapterView;
import viewmodels.DiarioViewModel;

public class MainActivity extends AppCompatActivity{

    public final static String EXTRA_MAIN="net.iessochoa.alexandrorodriguez.practica5.MainActivity.extra";
    public final static String EXTRA_FECHA = "MainActivity.fecha";


    private FloatingActionButton fabAñadir;
    private DiarioViewModel diarioViewModel;
    private RecyclerView rv_dias;
    private AdapterView adapterView;
    private SearchView svBusqueda;


    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    int requestCodeNuevo = 1;
                    int requestCodeEditar = 0;
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
        rv_dias = findViewById(R.id.rv_dias);
        svBusqueda = findViewById(R.id.svBusqueda);

        //RecyclerView
        adapterView = new AdapterView();
        rv_dias.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        rv_dias.setAdapter(adapterView);


        diarioViewModel= new ViewModelProvider(this).get(DiarioViewModel.class);
        diarioViewModel.getAllDiarios().observe(this, new
                Observer<List<DiaDiario>>() {
                    @Override
                    public void onChanged(List<DiaDiario> diario) {
                        adapterView.setDiarios(diario);
                        Log.d("P5","tamaño: "+diario.size());


                    }
                });

        fabAñadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, EdicionDiaActivity.class);
                //Lanzamos el result
                mStartForResult.launch(i);
            }
        });

        adapterView.setOnClickEditarListener(new AdapterView.OnItemClickEditarListener() {
            @Override
            public void onItemEditarClick(DiaDiario diaDiario) {
                editarDia(diaDiario);
            }
        });

        adapterView.setOnClickBorrarListener(new AdapterView.OnItemClickBorrarListener() {
            @Override
            public void onItemBorrarClick(DiaDiario diaDiario) {
                borrarDia(diaDiario);
            }
        });


        svBusqueda.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        svBusqueda.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                diarioViewModel.setCondicionBusqueda(query);
                Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Este método nos ayudara a buscar el texto que escribe el usuario
                if(newText.length()==0)
                    diarioViewModel.setCondicionBusqueda("");



                return false;
            }
        });
    }

    //Método para borrar un día
    private void borrarDia(DiaDiario diaDiario) {
        //Obtenemos la fecha
        String fecha = diaDiario.getStaticFechaFormatoLocal(diaDiario.getFecha());

        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle(getResources().getString(R.string.tituloDialogo));
        dialog.setMessage(getResources().getString(R.string.mensajeDialogo, fecha));
        dialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                diarioViewModel.delete(diaDiario);
            }
        });
        dialog.setNegativeButton(getResources().getString(R.string.no), null);

        dialog.show();
    }

    //Método para poder editar un día.
    private void editarDia(DiaDiario diaDiario) {
        SimpleDateFormat formatoDelTexto = new SimpleDateFormat("DD-MM-yyyy");
        //Pasa de esta actividad a la Actividad EdicionDiaActivity
        Intent i = new Intent(MainActivity.this, EdicionDiaActivity.class);        //Le manda el objeto tarea
        i.putExtra(EXTRA_MAIN, diaDiario);
        i.putExtra(EXTRA_FECHA, formatoDelTexto.format(diaDiario.getFecha()));
        mStartForResult.launch(i);
    }


    @Override public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}