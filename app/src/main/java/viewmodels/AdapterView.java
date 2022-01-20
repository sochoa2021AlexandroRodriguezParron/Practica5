package viewmodels;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import model.DiaDiario;
import practica5.AlexandroRodriguez.iesseveroochoa.net.R;

public class AdapterView extends RecyclerView.Adapter<AdapterView.AdapterViewHolder>{

    private List<DiaDiario> listaDiario;

    private OnItemClickBorrarListener listenerBorrar;
    private OnItemClickEditarListener listenerEditar;


    //cuando se modifique la lista, actualizamos el recyclerview
    public void setDiarios(List<DiaDiario> diarios){
        listaDiario=diarios;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dia, parent, false);
        return new AdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {



        if(!listaDiario.isEmpty()){
            final DiaDiario diaDiario = listaDiario.get(position);

            //Para definir la valoracion del dia y el fondo del item
            switch (diaDiario.getValoracionDia()){
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                    holder.iv_valoracionDia.setImageResource(R.mipmap.ic_sad_foreground);
                    break;
                case 5:
                    holder.iv_valoracionDia.setImageResource(R.mipmap.ic_neutral_foreground);
                    break;
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                    holder.iv_valoracionDia.setImageResource(R.mipmap.ic_smile_foreground);
                    break;

            }
            //Para definir el resumen
            holder.tv_resumen.setText(diaDiario.getResumen());

            //Para definir la fecha
            String fecha = diaDiario.getStaticFechaFormatoLocal(diaDiario.getFecha());
            holder.tv_fecha.setText(fecha);


        }

    }

    @Override
    public int getItemCount() {
        if (listaDiario!= null)
            return listaDiario.size();
        else
            return 0;
    }

    //Creamos la clase ViewHolder para hacer referencia a los objetos del RecyclerView
    public class AdapterViewHolder extends RecyclerView.ViewHolder {

        private ImageButton ib_delete;
        private ImageView iv_valoracionDia;
        private TextView tv_resumen;
        private TextView tv_fecha;
        private CardView cv_fondo;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            ib_delete = itemView.findViewById(R.id.ib_delete);
            iv_valoracionDia = itemView.findViewById(R.id.iv_valoracionDia);
            tv_resumen = itemView.findViewById(R.id.tv_resumen);
            tv_fecha = itemView.findViewById(R.id.tv_fecha);
            cv_fondo = itemView.findViewById(R.id.cv_fondo);

            cv_fondo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listenerEditar != null){
                        //si se pulsa al icono editar, le pasamos la nota. Podemos saber la posición del item en la lista
                        listenerEditar.onItemEditarClick(listaDiario.get(AdapterViewHolder.this.getBindingAdapterPosition()));
                    }
                }
            });

            ib_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listenerBorrar != null){
                        //si se pulsa al icono borrar, le pasamos la tarea. Podemos saber la posición del item en la lista
                        listenerBorrar.onItemBorrarClick(listaDiario.get(AdapterViewHolder.this.getBindingAdapterPosition()));
                    }
                }
            });
        }

        public DiaDiario getDia(){
            return listaDiario.get(AdapterViewHolder.this.getBindingAdapterPosition());
        }

    }



    //Interfaz para poder ejecutar el ImageView Borrar al darle click
    public interface OnItemClickBorrarListener {
        void onItemBorrarClick(DiaDiario diaDiario);
    }
    public void setOnClickBorrarListener(OnItemClickBorrarListener listener) {
        this.listenerBorrar = listener;
    }

    //Interfaz para poder ejecutar el ImageView Editar al darle click
    public interface OnItemClickEditarListener {
        void onItemEditarClick(DiaDiario diaDiario);
    }
    public void setOnClickEditarListener(OnItemClickEditarListener listener) {
        this.listenerEditar = listener;
    }
}
