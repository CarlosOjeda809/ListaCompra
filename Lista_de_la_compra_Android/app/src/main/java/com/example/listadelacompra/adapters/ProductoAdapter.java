package com.example.listadelacompra.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.listadelacompra.Constantes.Constantes;
import com.example.listadelacompra.MainActivity;
import com.example.listadelacompra.R;
import com.example.listadelacompra.modelos.ProductoModel;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoVH>{
    private List<ProductoModel> objects;
    private int resource;
    private Context context;
    private SharedPreferences spDatos;
    // private Gson gson;

    public ProductoAdapter(List<ProductoModel> objects, int resource, Context context) {
        this.objects = objects;
        this.resource = resource;
        this.context = context;
        //this.spDatos = context.getSharedPreferences(Constantes.DATOS,
        //        Context.MODE_PRIVATE);
        // gson = new Gson();
    }

    @NonNull
    @Override
    public ProductoVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View productoView = LayoutInflater.from(context).inflate(resource, null);
        productoView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ProductoVH(productoView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoVH holder, int position) {
        ProductoModel producto = objects.get(position);

        holder.lblNombre.setText(producto.getNombre());
        holder.txtCantidad.setText(String.valueOf(producto.getCantidad()));
        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarBorrado(producto).show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modificarProducto(producto).show();
            }
        });
        holder.txtCantidad.addTextChangedListener(new TextWatcher() {
            boolean cero = false;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!s.toString().isEmpty() && s.charAt(0) == '0') {
                    cero = true;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (cero && s.toString().length() > 1) {
                        holder.txtCantidad.setText(s.toString().substring(0, 1));
                        holder.txtCantidad.setSelection(1);
                        cero = false;
                    }
                    int cantidad = Integer.parseInt(s.toString());
                    producto.setCantidad(cantidad);
                } catch (NumberFormatException e) {
                    holder.txtCantidad.setText("0");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    private AlertDialog modificarProducto(ProductoModel producto){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Modificar producto a la cesta");
        builder.setCancelable(false);

        View productoViewModel = LayoutInflater.from(context).inflate(R.layout.producto_view_model, null);
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

        txtNombre.setText(producto.getNombre());
        txtCantidad.setText(String.valueOf(producto.getCantidad()));
        txtPrecio.setText(String.valueOf(producto.getImporte()));

        builder.setNegativeButton("CANCELAR", null);
        builder.setPositiveButton("MODIFICAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nombre = txtNombre.getText().toString();
                String cantidad = txtCantidad.getText().toString();
                String precio = txtPrecio.getText().toString();

                if (!nombre.isEmpty() && !cantidad.isEmpty() && !precio.isEmpty()){
                    producto.setNombre(txtNombre.getText().toString());
                    producto.setCantidad(Integer.parseInt(txtCantidad.getText().toString()));
                    producto.setImporte(Float.parseFloat(txtPrecio.getText().toString()));
                    producto.actualizarTotal();
                    notifyItemChanged(objects.indexOf(producto));
                    // guardarInformacion();
                } else {
                    Toast.makeText(context, "FALTAN DATOS", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return builder.create();
    }


    private AlertDialog confirmarBorrado(ProductoModel producto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("¿SEGURO?");
        builder.setCancelable(true);

        TextView textView = new TextView(context);
        textView.setText("  Esta acción no se puede deshacer.  ");
        textView.setTextColor(Color.RED);
        textView.setTextSize(24);
        builder.setView(textView);

        builder.setNegativeButton("CANCELAR", null);
        builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int posicion = objects.indexOf(producto);
                objects.remove(producto);
                notifyItemRemoved(posicion);
                // guardarInformacion();
            }
        });

        return builder.create();
    }

    public class ProductoVH extends RecyclerView.ViewHolder{
        TextView lblNombre;
        EditText txtCantidad;
        ImageButton btnEliminar;

        public ProductoVH(@NonNull View itemView) {
            super(itemView);
            lblNombre = itemView.findViewById(R.id.lblNombreProductoViewHolder);
            txtCantidad = itemView.findViewById(R.id.txtCantidadProductoViewHolder);
            btnEliminar = itemView.findViewById(R.id.btnEliminarProductoViewHolder);
        }
    }

//    private void guardarInformacion() {
//        String productosJSON = gson.toJson(objects);
//        SharedPreferences.Editor editor = spDatos.edit();
//        editor.putString(Constantes.LISTA_PRODUCTOS, productosJSON);
//        editor.apply();
//    }
}
