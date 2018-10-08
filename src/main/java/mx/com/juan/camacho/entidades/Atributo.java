package mx.com.juan.camacho.entidades;

public class Atributo implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    private String nombre;
    private Object valor;

    public Atributo() {

    }

    public Atributo(String nombre, Object valor) {
        this.nombre = nombre;
        this.valor = valor;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Object getValor() {
        return this.valor;
    }

    public void setValor(Object valor) {
        this.valor = valor;
    }
}
