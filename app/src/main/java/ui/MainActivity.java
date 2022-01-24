package ui;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import model.DiaDiario;
import practica5.AlexandroRodriguez.iesseveroochoa.net.R;
import viewmodels.AdapterView;
import viewmodels.DiarioViewModel;

public class MainActivity extends AppCompatActivity{

    public final static String EXTRA_MAIN="net.iessochoa.alexandrorodriguez.practica5.MainActivity.extra";
    public final static String EXTRA_FECHA = "MainActivity.fecha";
    public final static String CHICA = "Chica";
    public final static String CHICO = "Chico";


    private FloatingActionButton fabAñadir;
    private DiarioViewModel diarioViewModel;
    private RecyclerView rv_dias;
    private AdapterView adapterView;
    private SearchView svBusqueda;
    private SharedPreferences sharedPreferences;


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
    protected void onResume() {
        super.onResume();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        defineTituloApp();
        leerEstiloChicoChica();
    }

    private void leerEstiloChicoChica() {
        String sexoEscogido = sharedPreferences.getString("titulo_sexo_ajustes", "");
        if (sexoEscogido.equalsIgnoreCase(CHICO)) {
            rv_dias.setBackgroundResource(R.color.chico);
        } else {
            rv_dias.setBackgroundResource(R.color.chica);
        }
    }
    /**
     * Para definir el titulo de la aplicacion
     */
    private void defineTituloApp() {
        int pantalla=(getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);
        if ((pantalla == Configuration.SCREENLAYOUT_SIZE_LARGE) || pantalla==Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            String nombre = sharedPreferences.getString("nombre", "");
            MainActivity.this.setTitle(nombre);
        }
    }


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

        int orientation = getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_PORTRAIT) {//una fila
            rv_dias.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        }else {
            rv_dias.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));//2 es el número de columnas
        }
        rv_dias.setAdapter(adapterView);

        //Animacion Swiper
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT |
                        ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder
                                                  viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        //realizamos un cast del viewHolder y obtenemos el dia
                        // borrar
                        //
                        DiaDiario diaDelete=((AdapterView.AdapterViewHolder)viewHolder).getDia();
                        borrarDia(diaDelete);
                        //Obtenemos la posicion del viewHolder, para que al arrastrarlo, no se quede un hueco en caso de candelar
                        final int posicion=viewHolder.getBindingAdapterPosition();
                        adapterView.notifyItemChanged(posicion);
                    }
                };
                //Creamos el objeto de ItemTouchHelper que se encargará del trabajo
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
                //lo asociamos a nuestro reciclerView
                itemTouchHelper.attachToRecyclerView(rv_dias);





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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_ordenar:
                ordenar();
                break;
            case R.id.action_about:
                acercaDe();
                break;
            case R.id.action_valoravida:
                valoracionDia();
                break;
            case R.id.action_ult_dia:
                muestraUltimoDia();
                break;
            case R.id.action_setting:
                Intent i = new Intent(MainActivity.this, PreferenciasActivity.class);
                startActivity(i);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void muestraUltimoDia() {
        //recuperamos las preferencias
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE);
        //recuperamos la fecha del últimos día editado
        long ultimoDia = sharedPref.getLong(getString(R.string.pref_key_ultimo_dia),0);
        //mostramos el resultado
        Toast.makeText(this,"El último día editado "+DiaDiario.getStaticFechaFormatoLocal(new Date(ultimoDia)),Toast.LENGTH_LONG).show();
    }
    private void valoracionDia() {


        diarioViewModel.geMediaValoracion().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull Integer integer) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

                if(integer < 5){
                    dialog.setIcon(getResources().getDrawable(R.mipmap.ic_bigsad_foreground, null));
                }else{
                    if(integer > 5){
                        dialog.setIcon(getResources().getDrawable(R.mipmap.ic_bigsmile_foreground, null));
                    }else{
                        dialog.setIcon(getResources().getDrawable(R.mipmap.ic_bigneutral_foreground, getTheme()));
                    }
                }

                dialog.show();
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });




    }

    private void acercaDe() {
        String mensaje = getResources().getString(R.string.parte1) + "\n"
                +  getResources().getString(R.string.parte2)+ "\n"
                +  getResources().getString(R.string.parte3);


        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle(getResources().getString(R.string.action_about));
        dialog.setMessage(mensaje);
        dialog.show();
    }

    private void ordenar() {
        String[] opciones = getResources().getStringArray(R.array.ordenar);

        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle(getResources().getString(R.string.tituloOrdenar));
        dialog.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                diarioViewModel.setCondicionOrdenar(opciones[item]);
                dialog.dismiss();
            }
        });
        dialog.show();


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