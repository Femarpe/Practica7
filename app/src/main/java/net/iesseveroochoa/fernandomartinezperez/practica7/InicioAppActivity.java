package net.iesseveroochoa.fernandomartinezperez.practica7;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import net.iesseveroochoa.fernandomartinezperez.practica7.model.Conferencia;
import net.iesseveroochoa.fernandomartinezperez.practica7.model.Mensaje;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class InicioAppActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private TextView tvSesion;
    private TextView tvVerEmpresa;
    private TextView tvConferenciaIniciada;
    private Button btCerrarSesion;
    private ArrayList<Conferencia> listaConferencias;
    private Spinner spConferencias;
    private Conferencia conferenciaActual;
    private EditText etMensaje;
    private String usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_app);
        tvSesion = findViewById(R.id.tvSesion);
        tvVerEmpresa = findViewById(R.id.tvVerEmpresa);
        tvConferenciaIniciada = findViewById(R.id.tvConferenciaIniciada);
        btCerrarSesion = findViewById(R.id.btCerrarSesion);
        spConferencias = findViewById(R.id.spConferencias);
        etMensaje = findViewById(R.id.etMensaje);

        auth = FirebaseAuth.getInstance();
        FirebaseUser usrFB = auth.getCurrentUser();
        usuario = usrFB.getEmail();
        tvSesion.setText(usuario);

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
        ArrayList<String> nombresConf = new ArrayList<>();
        for (Conferencia conferencia : listaConferencias) {
            nombresConf.add(conferencia.getNombre());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(this, R.layout.spinner,nombresConf);
        spConferencias.setAdapter(arrayAdapter);
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
                        conferenciaIniciada = snapshot.getString(FirebaseContract.ConferenciaIniciadaEntry.CONFERENCIA);
                tvConferenciaIniciada.setText("C.iniciada: " + conferenciaIniciada);
                Log.d(TAG, "Conferencia iniciada: " + snapshot.getData());
            } else {
                Log.d(TAG, "Current data: null");
            }
        });
    }

    private void enviarMensaje() {
        String body = etMensaje.getText().toString();
        if (!body.isEmpty()) {

            Mensaje mensaje = new Mensaje(usuario, body);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(FirebaseContract.ConferenciaEntry.COLLECTION_NAME)

                    .document(conferenciaActual.getId())

                    .collection(FirebaseContract.ChatEntry.COLLECTION_NAME)


                    .add(mensaje);
            etMensaje.setText("");
            ocultarTeclado();
        }
    }

    /**
     * Permite ocultar el teclado
     */
    private void ocultarTeclado() {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(etMensaje.getWindowToken(), 0);
        }
    }
}