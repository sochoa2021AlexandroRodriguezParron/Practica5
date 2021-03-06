package repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.Date;
import java.util.List;

import io.reactivex.Single;
import model.DiaDiario;
import model.DiarioDao;
import model.DiarioDatabase;

public class DiarioRepositorio {
    /**ATRIBUTOS**/
    private DiarioDao mDiarioDao;
    private LiveData<List<DiaDiario>> mAllDiario;

    /**IMPLEMENTAMOS EL PATRÓN SINGLETON**/
    private static volatile DiarioRepositorio INSTANCE;
    public static DiarioRepositorio getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (DiarioRepositorio.class) {
                if (INSTANCE == null) {
                    INSTANCE=new DiarioRepositorio(application);
                }
            }
        }
        return INSTANCE;
    }

    private DiarioRepositorio(Application application){
        /**Creamos la base de datos**/
        DiarioDatabase db= DiarioDatabase.getDatabase(application);
        /**Recuperamos el DAO necesario para el CRUD de la base de datos**/
        mDiarioDao = db.diarioDao();
        /**Recuperamos la lista como un LiveData**/
        mAllDiario = mDiarioDao.getAllDiaDiario();
    }
    public LiveData<List<DiaDiario>> getAllDia(){
        return mAllDiario;
    }


    public LiveData<List<DiaDiario>> getByResumen(String resumen){
        mAllDiario=mDiarioDao.findByResumen(resumen);
        return mAllDiario;
    }
    /**lista ordenado por columnas diferentes**/
    public LiveData<List<DiaDiario>> getDiarioOrderBy(String query){
        mAllDiario=mDiarioDao.getDiarioOrderBy(query);
        return mAllDiario;
    }

    public Single<Integer> getValoracioTotal(){
        return mDiarioDao.getValoracioTotal();
    }

    /**
     *Insertar: nos obliga a crear tarea en segundo plano
     */
    public void insert(DiaDiario diaDiario){
        //administramos el hilo con el Executor
        DiarioDatabase.databaseWriteExecutor.execute(()->{
            mDiarioDao.insert(diaDiario);
        });


    }

    /**
     *Borrar: nos obliga a crear tarea en segundo plano
     */
    public void delete(DiaDiario diaDiario){
        //administramos el hilo con el Executor
        DiarioDatabase.databaseWriteExecutor.execute(()->{
            mDiarioDao.deleteByDiaDiario(diaDiario);
        });
    }


}
