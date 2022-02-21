package net.iesseveroochoa.fernandomartinezperez.practica7;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import net.iesseveroochoa.fernandomartinezperez.practica7.domain.Empresa;

public class EmpresaActivity extends AppCompatActivity {
    private Empresa empresa;
    private TextView tvNEmpresa;
    private TextView tvDireccion;
    private TextView tvTelefono;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);
        obtenDatosEmpresa();
    }

    void obtenDatosEmpresa() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference
                docRef = db.collection(FirebaseContract.EmpresaEntry.COLLECTION_NAME).
                document(FirebaseContract.EmpresaEntry.ID);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            empresa = documentSnapshot.toObject(Empresa.class);

            asignaValoresEmpresa();
        });
    }

    private void crearEmpresa() {
        GeoPoint geoPoint = new GeoPoint(38.279635, 0.715042);
        empresa = new Empresa();
        empresa.setDireccion("Carrer Illueca, 28, 03206 Elx, Alicante");
        empresa.setLocalizacion(geoPoint);
        empresa.setNombre("IES Severo Ochoa");
        empresa.setTelefono("9669122222");
    }

    private void asignaValoresEmpresa() {
        tvNEmpresa = findViewById(R.id.tvNombreEmpresa);
        tvDireccion = findViewById(R.id.tvDireccion);
        tvTelefono = findViewById(R.id.tvTelefono);

        tvNEmpresa.setText(empresa.getNombre());
        tvDireccion.setText(empresa.getDireccion());
        tvTelefono.setText(empresa.getTelefono());
    }


}