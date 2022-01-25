package model;

import android.icu.text.DateFormat;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.Locale;

@Entity(tableName = DiaDiario.TABLE_NAME,
        indices = {@Index(value = {DiaDiario.FECHA},unique = true)})
public class DiaDiario implements Parcelable {

    /**CONSTANTES ÚTILES**/
    public static final String TABLE_NAME="diario";
    public static final String ID= BaseColumns._ID;
    public static final String FECHA="fecha";
    public static final String VALORACION_DIA="valoracion_dia";
    public static final String RESUMEN="resumen";
    public static final String CONTENIDO="contenido";
    public static final String FOTO_URI="foto_uri";

    /**ATRIBUTOS**/
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name=ID)
    private int id; //clave del dia.

    @ColumnInfo(name = FECHA)
    @NonNull
    private Date fecha; //Dia del diario. No tendremos dos días con la misma fecha.

    @ColumnInfo(name = VALORACION_DIA)
    @NonNull
    private int valoracionDia; //Entero de 0 a 10 que el usuario podrá valorar como ha sido el día.

    @ColumnInfo(name = RESUMEN)
    @NonNull
    private String resumen; //Breve resumen del día.

    @ColumnInfo(name = CONTENIDO)
    @NonNull
    private String contenido; //Contiene ttodo el contenido escrito en el diario por parte del usuario.

    @ColumnInfo(name = FOTO_URI)
    @NonNull
    private String fotoUri; //Mantendremos una foto representativa del día.

    /**
     * CONSTRUCTOR SIN ID
     * @param fecha
     * @param valoracionDia
     * @param resumen
     * @param contenido
     * @param fotoUri
     */
    public DiaDiario(@NonNull Date fecha,@NonNull int valoracionDia,@NonNull String resumen,@NonNull String contenido,@NonNull String fotoUri) {
        this.fecha = fecha;
        this.valoracionDia = valoracionDia;
        this.resumen = resumen;
        this.contenido = contenido;
        this.fotoUri = fotoUri;
    }

    /**
     * CONSTRUCTOR SIN ID, NI FOTO_URI
     * @param fecha
     * @param valoracionDia
     * @param resumen
     * @param contenido
     */
    @Ignore
    public DiaDiario(@NonNull Date fecha,@NonNull int valoracionDia,@NonNull String resumen,@NonNull String contenido) {
        this.fecha = fecha;
        this.valoracionDia = valoracionDia;
        this.resumen = resumen;
        this.contenido = contenido;
        this.fotoUri = "";
    }

    /**
     * CONSTRUCTOR VACÍO
     */
    public DiaDiario() {
    }

    /**
     * GETTERS & SETTERS DE LOS ATRIBUTOS
     * @return
     */
    public int getId() {
        return id;
    }
    public void setId(@NonNull int id) {
        this.id = id;
    }
    public Date getFecha() {
        return fecha;
    }
    public void setFecha(@NonNull Date fecha) {
        this.fecha = fecha;
    }
    public int getValoracionDia() {
        return valoracionDia;
    }
    public void setValoracionDia(@NonNull int valoracionDia) {
        this.valoracionDia = valoracionDia;
    }
    public String getResumen() {
        return resumen;
    }
    public void setResumen(@NonNull String resumen) {
        this.resumen = resumen;
    }
    public String getContenido() {
        return contenido;
    }
    public void setContenido(@NonNull String contenido) {
        this.contenido = contenido;
    }
    public String getFotoUri() {
        return fotoUri;
    }
    public void setFotoUri(@NonNull String fotoUri) {
        this.fotoUri = fotoUri;
    }

    /**--------OTROS MÉTODOS---------**/

    /**
     * Nos permite mostrar el emoticono correspondiente en la lista.
     * @return
     */
    public int getValoracionResumida(){
        if(valoracionDia < 5){
            return 1;
        }else{
            if(valoracionDia < 8){
                return 2;
            }else{
                return 3;
            }
        }
    }

    /**
     * Igual que el anterior pero que le pasamos un entero y nos devuelve la valoración resumida.
     * Metodo estático.
     * @param entero
     * @return
     */
    public static int getStaticValoracionResumida(int entero){
        if(entero < 5){
            return 1;
        }else{
            if(entero < 8){
                return 2;
            }else{
                return 3;
            }
        }
    }

    /**
     * Devuelve la fecha en formato local
     * @return
     */
    public String getFechaFormatoLocal() {
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        return df.format(fecha);
    }

    /**
     * Devuelve la fecha en formato local
     * @param fecha
     * @return
     */
    public static String getStaticFechaFormatoLocal(Date fecha) {
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        return df.format(fecha);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(valoracionDia);
        dest.writeString(resumen);
        dest.writeString(contenido);
        dest.writeString(fotoUri);
        dest.writeLong(this.fecha != null ? this.fecha.getTime() : -1);
    }

    protected DiaDiario(Parcel in) {
        this.id = in.readInt();
        this.valoracionDia = in.readInt();
        this.resumen = in.readString();
        this.contenido = in.readString();
        this.fotoUri = in.readString();
        long tmpFecha = in.readLong();
        this.fecha = tmpFecha == -1 ? null : new Date(tmpFecha);
    }

    public static final Creator<DiaDiario> CREATOR = new Creator<DiaDiario>() {
        @Override
        public DiaDiario createFromParcel(Parcel in) {
            return new DiaDiario(in);
        }

        @Override
        public DiaDiario[] newArray(int size) {
            return new DiaDiario[size];
        }
    };
}

