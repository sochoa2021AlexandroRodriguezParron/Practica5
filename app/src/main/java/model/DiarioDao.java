package model;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Single;

public interface DiarioDao {

    //nuevo DiaDiario sustituyendo si ya existe
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DiaDiario diaDiario);

    //borrado del DiaDiario pasado por parámetro.
    @Delete
    void deleteByDiaDiario(DiaDiario diaDiario);

    @Update
    void update(DiaDiario diaDiario);

    //borrado de los DiaDiario
    @Query("DELETE FROM "+DiaDiario.TABLE_NAME)
    void deleteAll();

    //todos los DiaDiario
    @Query("SELECT * FROM "+DiaDiario.TABLE_NAME)
    LiveData<List<DiaDiario>> getAllDiaDiario();

    //todos los DiaDiario ordenados
    @Query("SELECT * FROM "+ DiaDiario.TABLE_NAME+" ORDER BY :resumen ASC")
    LiveData<List<DiaDiario>> getDiarioOrderBy(String resumen);

    @Query("SELECT AVG(valoracion_dia) FROM "+ DiaDiario.TABLE_NAME)
    Single<Integer> getValoracioTotal();

    //Encuentra el objeto por la fecha
    @Query("SELECT * FROM "+ DiaDiario.TABLE_NAME+" where fecha LIKE  '%' || :fecha || '%' ")
    LiveData<List<DiaDiario>> findByFecha(String fecha);

}
