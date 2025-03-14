package com.example.listadelacompra.modelos;

public class ProductoModel {
    private String nombre;
    private int cantidad;
    private float importe;
    private float total;

    public ProductoModel() {
    }

    public ProductoModel(String nombre, int cantidad, float importe) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.importe = importe;
        this.total = this.cantidad * this.importe;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public float getImporte() {
        return importe;
    }

    public void setImporte(float importe) {
        this.importe = importe;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "ProductoModel{" +
                "nombre='" + nombre + '\'' +
                ", cantidad=" + cantidad +
                ", importe=" + importe +
                ", total=" + total +
                '}';
    }

    public void actualizarTotal() {
        this.total = this.cantidad * this.importe;
    }
}
