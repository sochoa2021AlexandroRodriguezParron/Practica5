package viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

import model.DiaDiario;
import repository.DiarioRepositorio;

public class DiarioViewModel extends AndroidViewModel {

    private DiarioRepositorio mRepository;
    private LiveData<List<DiaDiario>> mAllDiarios;
    private MutableLiveData<String> condicionBusquedaLiveData;


    public DiarioViewModel(@NonNull Application application) {
        super(application);
        mRepository=DiarioRepositorio.getInstance(application);
        //Recuperamos el LiveData de todos los Diarios
        mAllDiarios=mRepository.getAllDia();

        //Este Livedata estará asociado al editext de busqueda por nombre
        condicionBusquedaLiveData=new MutableLiveData<String>();
        //en el primer momento no hay condición
        condicionBusquedaLiveData.setValue("");

        //version lambda
        mAllDiarios= Transformations.switchMap(condicionBusquedaLiveData,
                nombre -> mRepository.getByResumen(nombre));
    }

    public LiveData<List<DiaDiario>> getByResumen()
    {
        return mAllDiarios;
    }
    public void setCondicionBusqueda(String condicionBusqueda) {
        condicionBusquedaLiveData.setValue(condicionBusqueda);
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
