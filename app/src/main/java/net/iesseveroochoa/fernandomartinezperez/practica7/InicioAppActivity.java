package net.iesseveroochoa.fernandomartinezperez.practica7;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import net.iesseveroochoa.fernandomartinezperez.practica7.domain.Conferencia;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class InicioAppActivity extends AppCompatActivity {
    FirebaseAuth auth;
    TextView tvSesion;
    TextView tvVerEmpresa;
    TextView tvConferenciaIniciada;
    Button btCerrarSesion;
    ArrayList<Conferencia> listaConferencias;
    Spinner spConferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_app);
        tvSesion = findViewById(R.id.tvSesion);
        tvVerEmpresa = findViewById(R.id.tvVerEmpresa);
        tvConferenciaIniciada =findViewById(R.id.tvConferenciaIniciada);
        btCerrarSesion = findViewById(R.id.btCerrarSesion);
        spConferencias = findViewById(R.id.spConferencias);

        auth = FirebaseAuth.getInstance();
        FirebaseUser usrFB = auth.getCurrentUser();
        tvSesion.setText(usrFB.getEmail());

        leerConferencias();
        iniciarConferenciasIniciadas();

        btCerrarSesion.setOnClickListener(view -> {
            auth.signOut();
            startActivity(new Intent(InicioAppActivity.this, MainActivity.class));
            finish();
        });

        tvVerEmpresa.setOnClickListener(view -> {
            startActivity(new Intent(this, EmpresaActivity.class));
        });


    }

    private void leerConferencias() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        listaConferencias = new ArrayList<Conferencia>();
        db.collection(FirebaseContract.ConferenciaEntry.COLLECTION_NAME)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " +
                                    document.getData());
                            listaConferencias.add(document.toObject(Conferencia.class));
                        }
                        cargaSpinner();
                    } else {
                        Log.d(TAG, "Error getting documents: ",
                                task.getException());
                    }
                });
    }

    private void cargaSpinner() {
        //spConferencias.addView();
    }

    private void iniciarConferenciasIniciadas() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef =
                db.collection(FirebaseContract.ConferenciaIniciadaEntry.COLLECTION_NAME).document(FirebaseContract.ConferenciaIniciadaEntry.ID);
        docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                String
                        conferenciaIniciada=snapshot.getString(FirebaseContract.ConferenciaIniciadaEntry.CONFERENCIA);
                tvConferenciaIniciada.setText("C.iniciada: "+conferenciaIniciada);
                Log.d(TAG, "Conferencia iniciada: " + snapshot.getData());
            } else {
                Log.d(TAG, "Current data: null");
            }
        });
    }
}