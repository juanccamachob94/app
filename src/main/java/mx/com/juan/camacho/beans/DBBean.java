package mx.com.juan.camacho.beans;
/**
 * Bean de alcance de vista que posee funciones
 * que facilitan la gestión de datos con la base de datos
 * en relación con los componentes XHTML para su utilización
 */
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Date;
import mx.com.juan.camacho.entidades.Atributo;

@ManagedBean
@ViewScoped
public class DBBean<T> extends GeneralVistaBean implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    public int paginaActual = 1;
    public int paginaFinal;
    protected String select = " ";
    protected String from = " ";
    protected String where = " ";
    protected String groupBy = " ";
    protected String order = " ";
    protected String id = " ";
    protected int idMax = -1;
    protected int idMin = -1;
    protected int numRegistrosPorPagina = 10;
    protected int numRegistros;
    protected T objBusqueda;

    protected List<Atributo> atributosQuery = new ArrayList<Atributo>();

    protected Date fechaCreacionInicio;
    protected Date fechaCreacionFin;

    protected boolean datosCargados;
    protected String select_bk = " ";
    protected String from_bk = " ";
    protected String where_bk = " ";
    protected String groupBy_bk = " ";
    protected String order_bk = " ";

    protected void reestablecerQuery() {
            this.idMax = -1;
            this.idMin = -1;
            this.paginaActual = 1;
            getPaginaFinal();
            this.select = this.select_bk;
            this.from = this.from_bk;
            this.where = this.where_bk;
            this.groupBy = this.groupBy_bk;
            this.order = this.order_bk;
            this.atributosQuery = new ArrayList<Atributo>();
    }

    public DBBean() {
    }

    public int getPaginaActual() {
            return paginaActual;
    }

    public void setPaginaActual(int paginaActual) {
            this.paginaActual = paginaActual;
    }

    public int getPaginaFinal() {
            try {
            		getNumRegistros();
                    this.paginaFinal = (int)((double)(((double) this.numRegistros) / ((double)this.numRegistrosPorPagina)) + 0.9999);
                    if(this.paginaFinal == 0) this.paginaFinal = 1;
            } catch(Exception e) {
                    this.mostrarModal("No se pudo validar la paginación de la consulta " + "SELECT COUNT(*) " + this.from + this.where + this.groupBy + ". " + e.getMessage(), "fatal");
            }
            return paginaFinal;
    }

    @SuppressWarnings("unchecked")
    public List<T> getDatos() {
            this.limpiarQuery();
            List<Integer> lista;
            try {
                    if(this.idMax == -1 && this.idMin == -1) {//Condiciones iniciales de la consulta
                            if(!this.order.toLowerCase().contains("desc")) {
                                    lista = (List<Integer>)this.dataSource.consultarLista("SELECT " + this.id + this.from + this.where + this.groupBy + this.order,numRegistrosPorPagina,this.atributosQuery);
                                    this.idMax = lista.get(lista.size() - 1);
                                    this.idMin = (Integer)this.dataSource.consultarObjeto("SELECT MIN(" + this.id + ") " + this.from + this.where + this.groupBy,this.atributosQuery);
                            } else {
                                    this.idMax = (Integer)this.dataSource.consultarObjeto("SELECT MAX(" + this.id + ") " + this.from + this.where + this.groupBy,this.atributosQuery);
                                    lista = (List<Integer>)this.dataSource.consultarLista("SELECT " + this.id + this.from + this.where + this.groupBy + this.order,numRegistrosPorPagina,this.atributosQuery);
                                    this.idMin = lista.get(lista.size() - 1);
                            }
                    }
                    if(!this.datosCargados) hacerBackupQuery();
                    return this.dataSource.consultarLista((this.select + this.from + this.where + comparacion(this.where,this.id,this.idMax,true) + comparacion(this.where + "where",this.id,this.idMin,false) + this.groupBy + this.order).replaceAll("  "," ").replaceAll("  "," "),this.numRegistrosPorPagina,this.atributosQuery);
            } catch(java.lang.ArrayIndexOutOfBoundsException aiex) {
                    return null;
            } catch(NullPointerException ne) {
                    return null;
            } catch(Exception e) {
                    e.printStackTrace();
                    this.mostrarModal("Error al cargar la página inicial de datos. " + e.getMessage(),"fatal");
                    return null;
            }
    }

    @SuppressWarnings("unchecked")
    protected List<T> getTodosDatos() throws Exception {
            return this.dataSource.consultarLista((this.select + this.from + this.where + this.groupBy + this.order).replaceAll("  "," ").replaceAll("  "," "),this.atributosQuery);
    }

    protected void agregarCondicion(String atributo, Object valor, String comparacion) {
            if(comparacion == null) comparacion = "=";
            boolean exacto = !comparacion.equals("LIKE");
            if(valor != null && !valor.toString().equals("")) {
                    String compCadenaValor = "";
                    if(valor instanceof String) compCadenaValor = " " + comparacion + " " + "'" + (exacto? "":"%") + (String)valor + (exacto? "":"%") + "'";
                    else if(valor instanceof Boolean) compCadenaValor = " = " + (((Boolean)valor).booleanValue()?"true":"false");
                    else if(valor instanceof Integer) compCadenaValor = " " + comparacion + " " +Integer.toString((Integer) valor);
                    else if(valor instanceof Double) compCadenaValor = " " + comparacion + " " + Double.toString((Double) valor);
                    else if(valor instanceof Atributo && ((Atributo) valor).getValor() != null && !((Atributo) valor).getValor().toString().equals("")) {
                            compCadenaValor = " " + comparacion + " " + (exacto? "":"%") +":" + ((Atributo)valor).getNombre() + (exacto? "":"%");
                            this.atributosQuery.add((Atributo)valor);
                    }
                    if(!compCadenaValor.equals("")) this.where = this.where + (this.where.toLowerCase().contains("where") ? "AND " : "WHERE ") + atributo + compCadenaValor + " ";
            }
    }

    protected void agregarOpcion(String atributo, Object valor, String comparacion) {
            if(comparacion == null) comparacion = "=";
            boolean exacto = !comparacion.equals("LIKE");
            if(valor != null && !valor.toString().equals("")) {
                    String compCadenaValor = "";
                    if(valor instanceof String) compCadenaValor = " " + comparacion + " " + "'" + (exacto? "":"%") + (String)valor + (exacto? "":"%") + "'";
                    else if(valor instanceof Boolean) compCadenaValor = " = " + (((Boolean)valor).booleanValue()?"true":"false");
                    else if(valor instanceof Integer) compCadenaValor = " " + comparacion + " " +Integer.toString((Integer) valor);
                    else if(valor instanceof Double) compCadenaValor = " " + comparacion + " " + Double.toString((Double) valor);
                    else if(valor instanceof Atributo && ((Atributo) valor).getValor() != null && !((Atributo) valor).getValor().toString().equals("")) {
                            compCadenaValor = " " + comparacion + " " + (exacto? "":"%") +":" + ((Atributo)valor).getNombre() + (exacto? "":"%");
                            this.atributosQuery.add((Atributo)valor);
                    }
                    if(!compCadenaValor.equals("")) this.where = this.where + (this.where.toLowerCase().contains("where") ? "OR " : "WHERE ") + atributo + compCadenaValor + " ";
            }
    }

    public void setPaginaFinal(int paginaFinal) {
            this.paginaFinal = paginaFinal;
    }

    protected String comparacion(String where, String id, int valor, boolean max) {
            return " " + (where.toLowerCase().contains("where")? "AND":"WHERE") + " " + id + (max ? "<= " : ">= ") + valor + " ";
    }

    protected String ordenInverso(String id, String orden) {
            return " ORDER BY " + id + (order.toLowerCase().contains("desc") ? "ASC" : "DESC");
    }

    protected void limpiarQuery() {
            while(this.select.contains("  ")) this.select = this.select.replace("  "," ");
            while(this.from.contains("  ")) this.from = this.from.replace("  "," ");
            while(this.where.contains("  ")) this.where = this.where.replace("  "," ");
            while(this.groupBy.contains("  ")) this.groupBy = this.groupBy.replace("  "," ");
            while(this.order.contains("  ")) this.order = this.order.replace("  "," ");
    }

    @SuppressWarnings("unchecked")
    public void asignarPaginaSiguiente() {
            List<Integer> lista;
            try {
                    if(!this.order.toLowerCase().contains("desc")) {
                            this.idMin = this.idMax + 1;
                            lista = (List<Integer>)this.dataSource.consultarLista("SELECT " + this.id + this.from + this.where + comparacion(this.where,this.id,this.idMin,false) + this.groupBy + this.order,numRegistrosPorPagina,this.atributosQuery);
                            this.idMax = lista.get(lista.size() - 1);
                    } else {
                            this.idMax = this.idMin - 1;
                            lista = (List<Integer>)this.dataSource.consultarLista("SELECT " + this.id + this.from + this.where + comparacion(this.where,this.id,this.idMax,true) + this.groupBy + this.order,numRegistrosPorPagina,this.atributosQuery);
                            this.idMin = lista.get(lista.size() - 1);
                    }
                    this.paginaActual += 1;
            } catch(Exception e) {
                    this.mostrarModal("Error al cargar la página siguiente. " + e.getMessage(),"fatal");
            }
    }

    @SuppressWarnings("unchecked")
    public void asignarPaginaAnterior() {
            List<Integer> lista;
            try {
                    if(!this.order.toLowerCase().contains("desc")) {
                            this.idMax = this.idMin - 1;
                            lista = (List<Integer>)this.dataSource.consultarLista("SELECT " + this.id + this.from + this.where + comparacion(this.where,this.id,this.idMax,true) + this.groupBy + ordenInverso(this.id,this.order),numRegistrosPorPagina,this.atributosQuery);
                            this.idMin = lista.get(lista.size() - 1);
                    } else {
                            this.idMin = this.idMax + 1;
                            lista = (List<Integer>)this.dataSource.consultarLista("SELECT " + this.id + this.from + this.where + comparacion(this.where,this.id,this.idMin,false) + this.groupBy + ordenInverso(this.id,this.order),numRegistrosPorPagina,this.atributosQuery);
                            this.idMax = lista.get(lista.size() - 1);
                    }
                    this.paginaActual -= 1;
            } catch(Exception e) {
                    this.mostrarModal("Error al cargar la página anterior. " + e.getMessage(),"fatal");
            }
    }

    public void asignarPrimeraPagina() {
            this.idMax = -1;
            this.idMin = -1;
            this.paginaActual = 1;
    }

    @SuppressWarnings("unchecked")
    public void asignarUltimaPagina() {
            int numRegistrosUltimaPagina = this.numRegistros % this.numRegistrosPorPagina;
            List<Integer> lista;
            try {
                    if(!this.order.toLowerCase().contains("desc")) {
                            this.idMax = (Integer)this.dataSource.consultarObjeto("SELECT MAX(" + this.id + ") " + this.from + this.where + this.groupBy);
                            lista = (List<Integer>)this.dataSource.consultarLista("SELECT " + this.id + this.from + this.where + comparacion(this.where,this.id,this.idMax,true) + this.groupBy + ordenInverso(this.id,this.order),numRegistrosUltimaPagina,this.atributosQuery);
                            this.idMin = lista.get(lista.size() - 1);
                    } else {
                            this.idMin = (Integer)this.dataSource.consultarObjeto("SELECT MIN(" + this.id + ") " + this.from + this.where + this.groupBy);
                            lista = (List<Integer>)this.dataSource.consultarLista("SELECT " + this.id + this.from + this.where + this.groupBy + comparacion(this.where,this.id,this.idMin,false) + ordenInverso(this.id,this.order),numRegistrosUltimaPagina,this.atributosQuery);
                            this.idMax = lista.get(lista.size() - 1);
                    }
                    this.paginaActual = this.paginaFinal;
            } catch(Exception e) {
                    this.mostrarModal("Error al cargar la última página. " + e.getMessage(),"fatal");
            }
    }

    protected void hacerBackupQuery() {
            this.select_bk = this.select;
            this.from_bk = this.from;
            this.where_bk = this.where;
            this.groupBy_bk = this.groupBy;
            this.order_bk = this.order;
            this.datosCargados = true;
    }

    public void consultar() {
            this.reestablecerQuery();
            this.asignarParametrosConsulta();
    }

    public void asignarParametrosConsulta() {
    }

    public T getObjBusqueda() {
            return objBusqueda;
    }

    public void setObjBusqueda(T objBusqueda) {
            this.objBusqueda = objBusqueda;
    }

    public void crearObjetoBusqueda() {
    }

    public void limpiar() {
            this.crearObjetoBusqueda();
    }

    protected void imprimirQuery() {
            System.out.println(this.select + this.from + this.where + this.groupBy + this.order);
    }

    public int getNumRegistros() {
            try {
            	System.out.println("SDADASDASDSADASDASDSDSASSSSSSSSSSSSSSSSSSSSSWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWQQQQQQQQQQQQQQQs");
            	System.out.println("SELECT COUNT(*) " + this.from + this.where + this.groupBy);
            	this.numRegistros = this.dataSource.contar("SELECT COUNT(*) " + this.from + this.where + this.groupBy, this.atributosQuery);
            } catch(Exception e) {
                    this.mostrarModal("No se pudo calcular el número de registros de la consulta","fatal");
            }
            return numRegistros;
    }

    public void setNumRegistros(int numRegistros) {
            this.numRegistros = numRegistros;
    }

    public Date getFechaCreacionInicio() {
            return this.fechaCreacionInicio;
    }

    public void setFechaCreacionInicio(Date fechaCreacionInicio) {
            this.fechaCreacionInicio = fechaCreacionInicio;
    }

    public Date getFechaCreacionFin() {
            return this.fechaCreacionFin;
    }

    public void setFechaCreacionFin(Date fechaCreacionFin) {
            this.fechaCreacionFin = fechaCreacionFin;
    }

    protected void agregarCondicionFechaCreacion(String atributo){
            this.agregarCondicion("CONVERT(DATE," + atributo + ")",new Atributo("fechaCreacionInicio",this.fechaCreacionInicio),">=");
    this.agregarCondicion("CONVERT(DATE," + atributo + ")",new Atributo("fechaCreacionInicio",this.fechaCreacionFin),"<=");
    }
}
