package model;

import android.icu.text.DateFormat;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import java.util.Date;
import java.util.Locale;

public class DiaDiario implements Parcelable {
    //Constantes
    public static final String TABLE_NAME="diario";
    public static final String ID= BaseColumns._ID;
    public static final String FECHA="fecha";
    public static final String VALORACION_DIA="valoracion_dia";
    public static final String RESUMEN="resumen";
    public static final String CONTENIDO="contenido";
    public static final String FOTO_URI="foto_uri";

    //Atributos
    private int id; //clave del dia.
    private Date fecha; //Dia del diario. No tendremos dos días con la misma fecha.
    private int valoracionDia; //Entero de 0 a 10 que el usuario podrá valorar como ha sido el día.
    private String resumen; //Breve resumen del día.
    private String contenido; //Contiene ttodo el contenido escrito en el diario por parte del usuario.
    private String fotoUri; //Mantendremos una foto representativa del día.

    //Constructores
    //Ttodo menos id.
    public DiaDiario(Date fecha, int valoracionDia, String resumen, String contenido, String fotoUri) {
        this.fecha = fecha;
        this.valoracionDia = valoracionDia;
        this.resumen = resumen;
        this.contenido = contenido;
        this.fotoUri = fotoUri;
    }
    //Ttodo menos id y fotoUri (se le asignará cadena vacía)
    public DiaDiario(Date fecha, int valoracionDia, String resumen, String contenido) {
        this.fecha = fecha;
        this.valoracionDia = valoracionDia;
        this.resumen = resumen;
        this.contenido = contenido;
        this.fotoUri = "";
    }

    protected DiaDiario(Parcel in) {
        id = in.readInt();
        valoracionDia = in.readInt();
        resumen = in.readString();
        contenido = in.readString();
        fotoUri = in.readString();
    }

    //Getters & Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Date getFecha() {
        return fecha;
    }
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    public int getValoracionDia() {
        return valoracionDia;
    }
    public void setValoracionDia(int valoracionDia) {
        this.valoracionDia = valoracionDia;
    }
    public String getResumen() {
        return resumen;
    }
    public void setResumen(String resumen) {
        this.resumen = resumen;
    }
    public String getContenido() {
        return contenido;
    }
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
    public String getFotoUri() {
        return fotoUri;
    }
    public void setFotoUri(String fotoUri) {
        this.fotoUri = fotoUri;
    }

    //Otros métodos

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
     * @param entero
     * @return
     */
    public static int getStaticValoracionResumida(int entero){
        //TO_DO:
        return 0;
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
        //TO_DO:
        return "";
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

