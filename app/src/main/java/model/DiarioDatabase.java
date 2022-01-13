package model;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Esta clase nos comunica con SQLite. Mantenemos una sola instancia mediante el patrón
 * Sigleton
 * https://developer.android.com/training/data-storage/room/prepopulate
 */
//le indicamos las entidades de la base de datos y la versión
@Database(entities = {DiaDiario.class}, version = 1)
//Nos transforma automáticamente las fechas a entero
@TypeConverters({TransformaFechaSQLite.class})
public abstract class DiarioDatabase extends RoomDatabase {
    //Permite el acceso a los metodos CRUD
    public abstract DiarioDao diarioDao();

    //la base de datos
    private static volatile DiarioDatabase INSTANCE;
    //para el manejo de base de datos con room necesitamos realizar las tareas CRUD en hilos,
    //las consultas Select que devuelve LiveData, Room crea los hilos automáticamente, pero para las
    //insercciones, acutalizaciones y borrado, tenemos que crear el hilo nosotros
    //Utilizaremos ExecutorService para el control de los hilos. Para saber más información de la clase
    //https://www.youtube.com/watch?v=Hc5xo-JjIMQ
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static DiarioDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DiarioDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            //nombre del fichero de la base de datos
                            DiarioDatabase.class, "tarea_database")
                            //nos permite realizar tareas cuando es nueva o se ha creado una
                            //nueva versión del programa
                            .addCallback(sRoomDatabaseCallback)//para ejecutar al crear o al abrir
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    //crearemos una tarea en segundo plano que nos permite cargar los datos de ejemplo la primera
    //vez que se abre la base de datos
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // new PopulateDbAsync(INSTANCE).execute();
            //creamos algunos Diarios en un hilo
            databaseWriteExecutor.execute(() -> {
                //obtenemos la base de datos
                DiarioDao mDao = INSTANCE.diarioDao();
                SimpleDateFormat formatoDelTexto = new SimpleDateFormat("DD-MM-yyyy");
                DiaDiario diaDiario = null;
                //creamos unos Dias
                try {
                    //Dia 1
                    diaDiario = new DiaDiario(formatoDelTexto.parse("27-12-2021"), 5, "Un día un poco aburrido, solo he visto Netflix", "Lorem ipsum dolor sit amet,"+
                            "consectetur adipiscing elit. Nam rutrum lectus vestibulum, consectetur urna"+
                            "vel, rutrum tortor. Phasellus at leo nibh. Pellentesque lacinia blandit dui"+
                            "eu aliquam. Cras et suscipit nibh. Cras vehicula lobortis ante, vel hendrerit"+
                            "diam convallis at. Nullam egestas vel dui sed tincidunt. In placerat ac"+
                            "mauris eu faucibus. Nullam eu pretium justo. Suspendisse in leo nisi. Nulla"+
                            "hendrerit erat a finibus egestas. Nulla et libero eu purus euismod"+
                            "maximus.");
                    mDao.insert(diaDiario);
                    //Dia 2
                    diaDiario = new DiaDiario(formatoDelTexto.parse("29-12-2021"), 7, "Un día un divertido, he visto YouTube", "Lorem ipsum dolor sit amet,"+
                            "consectetur adipiscing elit. Nam rutrum lectus vestibulum, consectetur urna"+
                            "vel, rutrum tortor. Phasellus at leo nibh. Pellentesque lacinia blandit dui"+
                            "eu aliquam. Cras et suscipit nibh. Cras vehicula lobortis ante, vel hendrerit"+
                            "diam convallis at. Nullam egestas vel dui sed tincidunt. In placerat ac"+
                            "mauris eu faucibus. Nullam eu pretium justo. Suspendisse in leo nisi. Nulla"+
                            "hendrerit erat a finibus egestas. Nulla et libero eu purus euismod"+
                            "maximus.");
                    mDao.insert(diaDiario);
                    //Dia 3
                    diaDiario = new DiaDiario(formatoDelTexto.parse("31-12-2021"), 10, "Un día asombroso, hemos salido de fiesta", "Lorem ipsum dolor sit amet,"+
                            "consectetur adipiscing elit. Nam rutrum lectus vestibulum, consectetur urna"+
                            "vel, rutrum tortor. Phasellus at leo nibh. Pellentesque lacinia blandit dui"+
                            "eu aliquam. Cras et suscipit nibh. Cras vehicula lobortis ante, vel hendrerit"+
                            "diam convallis at. Nullam egestas vel dui sed tincidunt. In placerat ac"+
                            "mauris eu faucibus. Nullam eu pretium justo. Suspendisse in leo nisi. Nulla"+
                            "hendrerit erat a finibus egestas. Nulla et libero eu purus euismod"+
                            "maximus.");
                    mDao.insert(diaDiario);
                    //Dia 4
                    diaDiario = new DiaDiario(formatoDelTexto.parse("01-01-2022"), 8, "Un día fantástico, hemos cambiado de año", "Lorem ipsum dolor sit amet,"+
                            "consectetur adipiscing elit. Nam rutrum lectus vestibulum, consectetur urna"+
                            "vel, rutrum tortor. Phasellus at leo nibh. Pellentesque lacinia blandit dui"+
                            "eu aliquam. Cras et suscipit nibh. Cras vehicula lobortis ante, vel hendrerit"+
                            "diam convallis at. Nullam egestas vel dui sed tincidunt. In placerat ac"+
                            "mauris eu faucibus. Nullam eu pretium justo. Suspendisse in leo nisi. Nulla"+
                            "hendrerit erat a finibus egestas. Nulla et libero eu purus euismod"+
                            "maximus.");
                    mDao.insert(diaDiario);
                    //Dia 5
                    diaDiario = new DiaDiario(formatoDelTexto.parse("07-01-2022"), 2, "Un día cansado, hemos vuelto a las clases", "Lorem ipsum dolor sit amet,"+
                            "consectetur adipiscing elit. Nam rutrum lectus vestibulum, consectetur urna"+
                            "vel, rutrum tortor. Phasellus at leo nibh. Pellentesque lacinia blandit dui"+
                            "eu aliquam. Cras et suscipit nibh. Cras vehicula lobortis ante, vel hendrerit"+
                            "diam convallis at. Nullam egestas vel dui sed tincidunt. In placerat ac"+
                            "mauris eu faucibus. Nullam eu pretium justo. Suspendisse in leo nisi. Nulla"+
                            "hendrerit erat a finibus egestas. Nulla et libero eu purus euismod"+
                            "maximus.");
                    mDao.insert(diaDiario);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            //si queremos realizar alguna tarea cuando se abre
        }
    };
}
