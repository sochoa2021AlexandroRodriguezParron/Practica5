package practica5.AlexandroRodriguez.iesseveroochoa.net.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import practica5.AlexandroRodriguez.iesseveroochoa.net.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));
    }
}