package ui;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import model.DiaDiario;
import practica5.AlexandroRodriguez.iesseveroochoa.net.R;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fabAñadir;

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //Si el usuario pulsa OK en la Activity que hemos llamado
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        //Recuperamos los dados
                        Intent data = result.getData();
                        DiaDiario dia = (DiaDiario) data.getParcelableExtra(EdicionDiaActivity.EXTRA_DATOS);

                        Toast.makeText(MainActivity.this, ""+dia.getResumen(), Toast.LENGTH_SHORT).show();

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


    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}