package viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import model.DiaDiario;
import repository.DiarioRepositorio;

public class DiarioViewModel extends AndroidViewModel {

    private DiarioRepositorio mRepository;
    private LiveData<List<DiaDiario>> mAllDiarios;
    private MutableLiveData<String> condicionBusquedaLiveData;
    private MutableLiveData<String> condicionOrdenarLiveData;
    private MutableLiveData<Integer> condicionMediaLiveData;


    public DiarioViewModel(@NonNull Application application) {
        super(application);
        mRepository=DiarioRepositorio.getInstance(application);
        //Recuperamos el LiveData de todos los Diarios
        mAllDiarios=mRepository.getAllDia();

        //Este Livedata estará asociado al editext de busqueda por nombre
        condicionBusquedaLiveData=new MutableLiveData<String>();
        //en el primer momento no hay condición
        condicionBusquedaLiveData.setValue("");

        //Este Livedata estará asociado al menu de ordenar
        condicionOrdenarLiveData=new MutableLiveData<String>();
        //en el primer momento no hay condición
        condicionOrdenarLiveData.setValue("");

        //Este Livedata estará asociado al menu de ordenar
        condicionMediaLiveData=new MutableLiveData<Integer>();
        //en el primer momento no hay condición
        condicionMediaLiveData.setValue(0);


        //version lambda
        mAllDiarios= Transformations.switchMap(condicionBusquedaLiveData,
                resumen -> mRepository.getByResumen(resumen));

        //version lambda
        mAllDiarios= Transformations.switchMap(condicionOrdenarLiveData,
                query -> mRepository.getDiarioOrderBy(query));


    }

    public LiveData<List<DiaDiario>> getByResumen()
    {
        return mAllDiarios;
    }

    //Para buscar
    public void setCondicionBusqueda(String condicionBusqueda) {
        condicionBusquedaLiveData.setValue(condicionBusqueda);
    }

    //Para ordenar
    public void setCondicionOrdenar(String condicionBusqueda) {
        condicionOrdenarLiveData.setValue(condicionBusqueda);
    }

    public Single<Integer> geMediaValoracion(){
        return mRepository.getValoracioTotal();
    }

    public LiveData<List<DiaDiario>> getAllDiarios() {
        return mAllDiarios;
    }

    //Inserción y borrado que se reflejará automáticamente gracias al observador creado en la
    //actividad
    public void insert(DiaDiario diaDiario){
        mRepository.insert(diaDiario);
    }
    public void delete(DiaDiario diaDiario){ mRepository.delete(diaDiario); }

}
