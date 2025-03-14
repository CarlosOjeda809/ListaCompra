package com.example.listadelacompra;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.listadelacompra.Constantes.Constantes;
import com.example.listadelacompra.adapters.ProductoAdapter;
import com.example.listadelacompra.databinding.ActivityMainBinding;
import com.example.listadelacompra.modelos.ProductoModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ArrayList<ProductoModel> listaCompra;
    private ProductoAdapter adapter;
    private RecyclerView.LayoutManager lm;
    // private SharedPreferences spDatos;
    // private Gson gson;
    private FirebaseAuth auth;
    private FirebaseUser user;

    private FirebaseDatabase baseDatos;
    private DatabaseReference referenciaProducto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // spDatos = getSharedPreferences(Constantes.DATOS, MODE_PRIVATE);
        // gson = new Gson();

        setSupportActionBar(binding.toolbar);

        listaCompra = new ArrayList<>();
        adapter = new ProductoAdapter(listaCompra, R.layout.producto_view_holder, this);
        lm = new GridLayoutManager(this, 1);
        binding.contentMain.contenedor.setAdapter(adapter);
        binding.contentMain.contenedor.setLayoutManager(lm);

        auth = FirebaseAuth.getInstance();

        baseDatos = FirebaseDatabase.getInstance("https://fir-13cf6-default-rtdb.europe-west1.firebasedatabase.app/");
        referenciaProducto = baseDatos.getReference(auth.getCurrentUser().getUid())
                .child("productos");

        //leerDatos();



        referenciaProducto.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    GenericTypeIndicator<ArrayList<ProductoModel>> gti =
                            new GenericTypeIndicator<ArrayList<ProductoModel>>() {
                            };
                    ArrayList<ProductoModel> auxiliar = snapshot.getValue(gti);
                    Toast.makeText(MainActivity.this, String.valueOf(auxiliar.size()), Toast.LENGTH_SHORT).show();
                    listaCompra.addAll(auxiliar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearProducto().show();
            }
        });
    }


//    private void leerDatos() {
//        if(spDatos.contains(Constantes.LISTA_PRODUCTOS)) {
//            String prductosJSON =
//                    spDatos.getString(Constantes.LISTA_PRODUCTOS, "[]");
//            Type tipo = new
//                    TypeToken<ArrayList<ProductoModel>>(){}.getType();
//            ArrayList<ProductoModel> temp = gson.fromJson(prductosJSON,
//                    tipo);
//            listaCompra.clear();
//            listaCompra.addAll(temp);
//            adapter.notifyItemRangeInserted(0, listaCompra.size());
//        }
//    }

    private AlertDialog crearProducto(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agregar producto a la cesta");
        builder.setCancelable(false);

        View productoViewModel = LayoutInflater.from(this).inflate(R.layout.producto_view_model, null);
        EditText txtNombre = productoViewModel.findViewById(R.id.txtNombreProductoModel);
        EditText txtCantidad = productoViewModel.findViewById(R.id.txtCantidadProductoViewModel);
        EditText txtPrecio = productoViewModel.findViewById(R.id.txtPrecioProductoViewModel);
        TextView lblTotal = productoViewModel.findViewById(R.id.lblTotalProductoViewModel);
        builder.setView(productoViewModel);

        TextWatcher vigilante = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int cantidad = Integer.parseInt(txtCantidad.getText().toString());
                    float precio = Float.parseFloat(txtPrecio.getText().toString());
                    float total = cantidad * precio;

                    NumberFormat nf = NumberFormat.getCurrencyInstance();
                    lblTotal.setText(nf.format(total));
                } catch (NumberFormatException ignored) {
                }
            }
        };

        txtCantidad.addTextChangedListener(vigilante);
        txtPrecio.addTextChangedListener(vigilante);

        builder.setNegativeButton("CANCELAR", null);
        builder.setPositiveButton("AGREGAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nombre = txtNombre.getText().toString();
                String cantidad = txtCantidad.getText().toString();
                String precio = txtPrecio.getText().toString();

                if (!nombre.isEmpty() && !cantidad.isEmpty() && !precio.isEmpty()){
                    ProductoModel producto =
                            new ProductoModel(nombre, Integer.parseInt(cantidad), Float.parseFloat(precio));
                    listaCompra.add(0, producto);
                    adapter.notifyItemInserted(0);
                    referenciaProducto.setValue(listaCompra);
                    // guardarInformacion();
                } else {
                    Toast.makeText(MainActivity.this, "FALTAN DATOS", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return builder.create();
    }


//    private void guardarInformacion() {
//        String productosJSON = gson.toJson(listaCompra);
//        SharedPreferences.Editor editor = spDatos.edit();
//        editor.putString(Constantes.LISTA_PRODUCTOS, productosJSON);
//        editor.apply();
//    }

}