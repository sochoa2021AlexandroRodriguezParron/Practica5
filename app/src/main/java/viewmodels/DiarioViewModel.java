package viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import model.DiaDiario;
import repository.DiarioRepositorio;

public class DiarioViewModel extends AndroidViewModel {

    private DiarioRepositorio mRepository;
    private LiveData<List<DiaDiario>> mAllDiarios;
    public DiarioViewModel(@NonNull Application application) {
        super(application);
        mRepository=DiarioRepositorio.getInstance(application);
        //Recuperamos el LiveData de todos los Diarios
        mAllDiarios=mRepository.getAllDia();
    }
    public LiveData<List<DiaDiario>> getAllDiarios()
    {
        return mAllDiarios;

    }
    //Inserción y borrado que se reflejará automáticamente gracias al observador creado en la
    //actividad
    public void insert(DiaDiario diaDiario){
        mRepository.insert(diaDiario);
    }
    public void delete(DiaDiario diaDiario){ mRepository.delete(diaDiario); }

}
