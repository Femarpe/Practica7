package net.iesseveroochoa.fernandomartinezperez.practica7.adapter;

import android.app.Notification;
import android.graphics.Color;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import net.iesseveroochoa.fernandomartinezperez.practica7.model.Mensaje;

import java.text.BreakIterator;

public class ChatAdapter extends FirestoreRecyclerAdapter<Mensaje,
        ChatAdapter.ChatHolder> {
    private String usuario;

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public ChatAdapter(@NonNull FirestoreRecyclerOptions<Mensaje> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatAdapter.ChatHolder holder,
                                    int position, @NonNull Mensaje mensaje){

        holder.tvMensaje.setText(mensaje.getUsuario() + "=>" + mensaje.getBody());
//si el mensaje es del usuario lo colocamos a la izquierda
        if(mensaje.getUsuario().equals(usuario)){
            holder.cvContenedor.setCardBackgroundColor(Color.YELLOW);
            holder.lytContenedor.setGravity(Gravity.LEFT);
        }else {
            holder.lytContenedor.setGravity(Gravity.RIGHT);
            holder.cvContenedor.setCardBackgroundColor(Color.WHITE);
        }
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    public class ChatHolder extends RecyclerView.ViewHolder {

        public TextView tvMensaje;
        public CardView cvContenedor;
        public GridLayout.LayoutParams lytContenedor;

        public ChatHolder(@NonNull View itemView) {
            super(itemView);

        }
    }
}
