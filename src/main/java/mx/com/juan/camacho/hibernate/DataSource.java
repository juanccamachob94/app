package mx.com.juan.camacho.hibernate;

import java.math.BigInteger;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import mx.com.juan.camacho.entidades.Atributo;

@ManagedBean
@SessionScoped
public class DataSource implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    private static DataSource instancia;
    private HibernateUtil hibernateUtil;
    private Transaction transaccion;
    private Session sesion;
    private boolean transaccionIniciada;
    private Boolean unicaTransaccion;
    private boolean mismaSesion;

    public boolean transaccionEnProceso() {
    	return this.transaccionIniciada;
    }

    public DataSource() {
        this.transaccionIniciada = false;
        this.unicaTransaccion = null;
        this.transaccion = null;
        this.sesion = null;
        this.mismaSesion = true;
    }

    protected DataSource(boolean mismaSesion) {
        this.transaccionIniciada = false;
        this.unicaTransaccion = null;
        this.transaccion = null;
        this.sesion = null;
        this.mismaSesion = mismaSesion;
    }

    protected DataSource(boolean mismaSesion, boolean sqlNativo) {
        this.transaccionIniciada = false;
        this.unicaTransaccion = null;
        this.transaccion = null;
        this.sesion = null;
        this.mismaSesion = mismaSesion;
    }

    public static DataSource getInstancia() {
        if(instancia == null) instancia = new DataSource(true);
        return instancia;
    }

    public static DataSource getInstancia(boolean mismaSesion) {
        if(instancia == null) instancia = new DataSource(mismaSesion);
        return instancia;
    }

    public static DataSource getInstancia(boolean mismaSesion, boolean sqlNativo) {
        if(instancia == null) instancia = new DataSource(mismaSesion,sqlNativo);
        return instancia;
    }

    public void iniciarTransaccion() throws Exception {
        this.transaccionIniciada = true;
        this.unicaTransaccion = false;
        this.comenzarTransaccion();
    }

    public void recargar(Object objeto) throws Exception {
        try {
            if(this.transaccionEnProceso()) this.sesion.flush();
            this.sesion.refresh(objeto);
        } catch(Exception e) {
        String mensaje = e.getMessage();
        try {
            mensaje = e.getCause().getMessage();
        } catch(Exception ex) {}
            throw new Exception("No se pudo recargar el objeto " + objeto.toString() + ". " + mensaje);
        }
    }

    public void cargarHibernateUtil(String xml) throws Exception {
        this.hibernateUtil = new HibernateUtil(xml);
    }
    
    
    private void comenzarTransaccion() throws Exception {
        try {
            this.sesion = this.sesion == null || !this.mismaSesion ? getHibernateUtil().getSessionFactory().openSession() : this.sesion;
            this.sesion.clear();
            this.transaccion = this.sesion.beginTransaction();
        }catch(Exception e) {
            String mensaje = e.getMessage();
            try {
                    mensaje = e.getCause().getMessage();
            } catch(Exception ex) {}
            throw new Exception("No se puede iniciar la transacción. " + mensaje);
        }
    }

    public void revertirTransaccion() throws Exception {
        try {
            this.transaccionIniciada = false;
            this.unicaTransaccion = null;
            this.transaccion.rollback();
            this.sesion.clear();
            if(!this.mismaSesion) if(sesion.isConnected()) this.sesion.close();
            this.hibernateUtil = null;
        }catch(Exception e) {
            String mensaje = e.getMessage();
            try {
                mensaje = e.getCause().getMessage();
            } catch(Exception ex) {}
            throw new Exception("No se puede revertir la transacción. " + mensaje);
        }
    }

    public void finalizarTransaccion() throws Exception {
        try {
            this.transaccionIniciada = false;
            this.unicaTransaccion = null;
            this.transaccion.commit();
            if(!this.mismaSesion)if(sesion.isConnected()) this.sesion.close();
            this.hibernateUtil = null;
        } catch(ConstraintViolationException cve) {
        	throw new Exception(cve.getCause().getMessage());
        } catch(NullPointerException e) {
        	throw new Exception("La transacción está vacía");
        }catch(Exception e) {
        	String mensaje = e.getMessage();
        	try {
                    mensaje = e.getCause().getMessage();
        	} catch(Exception ex) {}
            throw new Exception("No se puede finalizar la transacción. " + mensaje);
        }
    }

    private void validarTransaccionIniciada() throws Exception {
        if(!this.transaccionIniciada || esUnicaTransaccion()) throw new Exception("No ha iniciado la transacción correctamente");
    }

    private boolean esUnicaTransaccion() {
        if(this.unicaTransaccion == null) return true;
        return this.unicaTransaccion;
    }

    public int contar(String query) throws Exception {
        try {
            if(this.esUnicaTransaccion()) this.sesion = this.sesion == null || !this.mismaSesion ? getHibernateUtil().getSessionFactory().openSession() : this.sesion;
            return ((Long)this.sesion.createQuery(query).uniqueResult()).intValue();
        }catch(Exception e) {
        	if(!this.esUnicaTransaccion()) this.revertirTransaccion();
        	String mensaje = e.getMessage();
        	try {
                    mensaje = e.getCause().getMessage();
        	} catch(Exception ex) {}
            throw new Exception("Error fatal al contar la cantidad de objetos. " + mensaje);
        }
    }

    public int contarQ(String query) throws Exception {
        try {
            if(this.esUnicaTransaccion()) this.sesion = this.sesion == null || !this.mismaSesion ? getHibernateUtil().getSessionFactory().openSession() : this.sesion;
            return ((BigInteger)this.sesion.createSQLQuery(query).uniqueResult()).intValue();
        }catch(Exception e) {
        	if(!this.esUnicaTransaccion()) this.revertirTransaccion();
        	String mensaje = e.getMessage();
        	try {
                    mensaje = e.getCause().getMessage();
        	} catch(Exception ex) {}
            throw new Exception("Error fatal al contar la cantidad de objetos. " + mensaje);
        }
    }


    public int contar(String query, List<Atributo> atributos) throws Exception {
        try {
            if(this.esUnicaTransaccion()) this.sesion = this.sesion == null || !this.mismaSesion ? getHibernateUtil().getSessionFactory().openSession() : this.sesion;
            Query miQuery = this.sesion.createQuery(query);
            for(Atributo atr : atributos) miQuery.setParameter(atr.getNombre(),atr.getValor());
            return ((Long)miQuery.uniqueResult()).intValue();
        }catch(Exception e) {
        	if(!this.esUnicaTransaccion()) this.revertirTransaccion();
        	String mensaje = e.getMessage();
        	try {
                    mensaje = e.getCause().getMessage();
        	} catch(Exception ex) {}
            throw new Exception("Error fatal al contar la cantidad de objetos. " + mensaje);
        }
    }

    public int contarQ(String query, List<Atributo> atributos) throws Exception {
        try {
            if(this.esUnicaTransaccion()) this.sesion = this.sesion == null || !this.mismaSesion ? getHibernateUtil().getSessionFactory().openSession() : this.sesion;
            Query miQuery = this.sesion.createSQLQuery(query);
            for(Atributo atr : atributos) miQuery.setParameter(atr.getNombre(),atr.getValor());
            return ((BigInteger)miQuery.uniqueResult()).intValue();
        }catch(Exception e) {
        	if(!this.esUnicaTransaccion()) this.revertirTransaccion();
        	String mensaje = e.getMessage();
        	try {
                    mensaje = e.getCause().getMessage();
        	} catch(Exception ex) {}
            throw new Exception("Error fatal al contar la cantidad de objetos. " + mensaje);
        }
    }


    public Object consultarObjeto(int idObjeto, Class<? extends Object> clase) throws Exception {
        try {
            if(this.esUnicaTransaccion()) this.sesion = this.sesion == null || !this.mismaSesion ? getHibernateUtil().getSessionFactory().openSession() : this.sesion;
            return this.sesion.get(clase, idObjeto);
        }catch(Exception e) {
        	if(!this.esUnicaTransaccion()) this.revertirTransaccion();
        	String mensaje = e.getMessage();
        	try {
                    mensaje = e.getCause().getMessage();
        	} catch(Exception ex) {}
            throw new Exception("Error fatal al consultar el objeto. " + mensaje);
        }
    }

    public Object consultarObjeto(String query) throws Exception {
        try {
            if(this.esUnicaTransaccion()) this.sesion = this.sesion == null || !this.mismaSesion ? getHibernateUtil().getSessionFactory().openSession() : this.sesion;
            return this.sesion.createQuery(query).uniqueResult();
        }catch(Exception e) {
        	if(!this.esUnicaTransaccion()) this.revertirTransaccion();
        	String mensaje = e.getMessage();
        	try {
                    mensaje = e.getCause().getMessage();
        	} catch(Exception ex) {}
            throw new Exception("Error fatal al consultar el objeto. " + mensaje);
        }
    }

    public Object consultarObjeto(String query, List<Atributo> atributos) throws Exception {
        try {
            if(this.esUnicaTransaccion()) this.sesion = this.sesion == null || !this.mismaSesion ? getHibernateUtil().getSessionFactory().openSession() : this.sesion;
            Query miQuery = this.sesion.createQuery(query);
            for(Atributo atr : atributos) miQuery.setParameter(atr.getNombre(),atr.getValor());
            return miQuery.uniqueResult();
        }catch(Exception e) {
        	if(!this.esUnicaTransaccion()) this.revertirTransaccion();
        	String mensaje = e.getMessage();
        	try {
                    mensaje = e.getCause().getMessage();
        	} catch(Exception ex) {}
            throw new Exception("Error fatal al consultar el objeto. " + mensaje);
        }
    }

    public Object consultarObjetoQ(String query) throws Exception {
        try {
            if(this.esUnicaTransaccion()) this.sesion = this.sesion == null || !this.mismaSesion ? getHibernateUtil().getSessionFactory().openSession() : this.sesion;
            return this.sesion.createSQLQuery(query).uniqueResult();
        }catch(Exception e) {
        	if(!this.esUnicaTransaccion()) this.revertirTransaccion();
        	String mensaje = e.getMessage();
        	try {
                    mensaje = e.getCause().getMessage();
        	} catch(Exception ex) {}
            throw new Exception("Error fatal al consultar el objeto. " + mensaje);
        }
    }

    public Object consultarObjetoQ(String query, List<Atributo> atributos) throws Exception {
        try {
            if(this.esUnicaTransaccion()) this.sesion = this.sesion == null || !this.mismaSesion ? getHibernateUtil().getSessionFactory().openSession() : this.sesion;
            Query miQuery = this.sesion.createSQLQuery(query);
            for(Atributo atr : atributos) miQuery.setParameter(atr.getNombre(),atr.getValor());
            return miQuery.uniqueResult();
        }catch(Exception e) {
        	if(!this.esUnicaTransaccion()) this.revertirTransaccion();
        	String mensaje = e.getMessage();
        	try {
                    mensaje = e.getCause().getMessage();
        	} catch(Exception ex) {}
            throw new Exception("Error fatal al consultar el objeto. " + mensaje);
        }
    }

    @SuppressWarnings("rawtypes")
    public List consultarLista(String query, int numFilas) throws Exception {
        try {
            if(this.esUnicaTransaccion()) this.sesion = this.sesion == null || !this.mismaSesion ? getHibernateUtil().getSessionFactory().openSession() : this.sesion;
            Query q = this.sesion.createQuery(query);
            q.setFirstResult(0);
            q.setMaxResults(numFilas);
            return q.list();
        }catch(Exception e) {
                if(!this.esUnicaTransaccion()) this.revertirTransaccion();
                String mensaje = e.getMessage();
                try {
                    mensaje = e.getCause().getMessage();
                } catch(Exception ex) {}
            throw new Exception("Error fatal al consultar la lista. " + mensaje);
        }
    }

    @SuppressWarnings("rawtypes")
	public List consultarListaQ(String query, int numFilas) throws Exception {
        try {
            if(this.esUnicaTransaccion()) this.sesion = this.sesion == null || !this.mismaSesion ? getHibernateUtil().getSessionFactory().openSession() : this.sesion;
            Query q = this.sesion.createSQLQuery(query);
            q.setFirstResult(0);
            q.setMaxResults(numFilas);
            return q.list();
        }catch(Exception e) {
        	if(!this.esUnicaTransaccion()) this.revertirTransaccion();
        	String mensaje = e.getMessage();
        	try {
                    mensaje = e.getCause().getMessage();
        	} catch(Exception ex) {}
            throw new Exception("Error fatal al consultar la lista. " + mensaje);
        }
    }

    @SuppressWarnings("rawtypes")
    public List consultarLista(String query, int numFilas, List<Atributo> atributos) throws Exception {
    try {
        if(this.esUnicaTransaccion()) this.sesion = this.sesion == null || !this.mismaSesion ? getHibernateUtil().getSessionFactory().openSession() : this.sesion;
        Query q = this.sesion.createQuery(query);
        q.setFirstResult(0);
        q.setMaxResults(numFilas);
        for(Atributo atr : atributos) q.setParameter(atr.getNombre(),atr.getValor());
        return q.list();
    }catch(Exception e) {
            if(!this.esUnicaTransaccion()) this.revertirTransaccion();
            String mensaje = e.getMessage();
            try {
                mensaje = e.getCause().getMessage();
            } catch(Exception ex) {}
        throw new Exception("Error fatal al consultar la lista. " + mensaje);
    }
}

    @SuppressWarnings("rawtypes")
	public List consultarListaQ(String query, int numFilas, List<Atributo> atributos) throws Exception {
        try {
            if(this.esUnicaTransaccion()) this.sesion = this.sesion == null || !this.mismaSesion ? getHibernateUtil().getSessionFactory().openSession() : this.sesion;
            Query q = this.sesion.createSQLQuery(query);
            q.setFirstResult(0);
            q.setMaxResults(numFilas);
            for(Atributo atr : atributos) q.setParameter(atr.getNombre(),atr.getValor());
            return q.list();
        }catch(Exception e) {
        	if(!this.esUnicaTransaccion()) this.revertirTransaccion();
        	String mensaje = e.getMessage();
        	try {
                    mensaje = e.getCause().getMessage();
        	} catch(Exception ex) {}
            throw new Exception("Error fatal al consultar la lista. " + mensaje);
        }
    }

    @SuppressWarnings("rawtypes")
	public List consultarLista(String query) throws Exception {
        try {
            if(this.esUnicaTransaccion()) this.sesion = this.sesion == null || !this.mismaSesion ? getHibernateUtil().getSessionFactory().openSession() : this.sesion;
            return this.sesion.createQuery(query).list();
        }catch(Exception e) {
        	if(!this.esUnicaTransaccion()) this.revertirTransaccion();
        	String mensaje = e.getMessage();
        	try {
                    mensaje = e.getCause().getMessage();
        	} catch(Exception ex) {}
            throw new Exception("Error fatal al consultar la lista. " + mensaje);
        }
    }

    @SuppressWarnings("rawtypes")
	public List consultarListaQ(String query) throws Exception {
        try {
            if(this.esUnicaTransaccion()) this.sesion = this.sesion == null || !this.mismaSesion ? getHibernateUtil().getSessionFactory().openSession() : this.sesion;
            return this.sesion.createSQLQuery(query).list();
        }catch(Exception e) {
        	if(!this.esUnicaTransaccion()) this.revertirTransaccion();
        	String mensaje = e.getMessage();
        	try {
                    mensaje = e.getCause().getMessage();
        	} catch(Exception ex) {}
            throw new Exception("Error fatal al consultar la lista. " + mensaje);
        }
    }

    @SuppressWarnings("rawtypes")
    public List consultarLista(String query, List<Atributo> atributos) throws Exception {
        try {
            if(this.esUnicaTransaccion()) this.sesion = this.sesion == null || !this.mismaSesion ? getHibernateUtil().getSessionFactory().openSession() : this.sesion;
            Query miQuery = this.sesion.createQuery(query);
            for(Atributo atr : atributos) miQuery.setParameter(atr.getNombre(),atr.getValor());
            return miQuery.list();
        }catch(Exception e) {
                if(!this.esUnicaTransaccion()) this.revertirTransaccion();
                String mensaje = e.getMessage();
                try {
                    mensaje = e.getCause().getMessage();
                } catch(Exception ex) {}
            throw new Exception("Error fatal al consultar la lista. " + mensaje);
        }
    }

    @SuppressWarnings("rawtypes")
    public List consultarListaQ(String query, List<Atributo> atributos) throws Exception {
        try {
            if(this.esUnicaTransaccion()) this.sesion = this.sesion == null || !this.mismaSesion ? getHibernateUtil().getSessionFactory().openSession() : this.sesion;
            Query miQuery = this.sesion.createSQLQuery(query);
            for(Atributo atr : atributos) miQuery.setParameter(atr.getNombre(),atr.getValor());
            return miQuery.list();
        }catch(Exception e) {
        	if(!this.esUnicaTransaccion()) this.revertirTransaccion();
        	String mensaje = e.getMessage();
        	try {
                    mensaje = e.getCause().getMessage();
        	} catch(Exception ex) {}
            throw new Exception("Error fatal al consultar la lista. " + mensaje);
        }
    }


    public void insertar(Object objeto) throws Exception {
        try {
            if(this.esUnicaTransaccion()) {
                this.comenzarTransaccion();
                this.sesion.save(objeto);
                this.finalizarTransaccion();
            }else {
                this.validarTransaccionIniciada();
                this.sesion.save(objeto);
            }
        }catch(Exception e) {
        	this.revertirTransaccion();
        	String mensaje = e.getMessage();
        	try {
                    mensaje = e.getCause().getMessage();
        	} catch(Exception ex) {}
            throw new Exception("Error fatal al insertar el objeto. " + mensaje);
        }
    }

    @SuppressWarnings("rawtypes")
    public void insertar(List lista) throws Exception {
        try {
            if(this.esUnicaTransaccion()) {
                this.comenzarTransaccion();
                int t = lista.size();
                for(int i = 0; i < t; i++)
                    this.sesion.save(lista.get(i));
                this.finalizarTransaccion();
            }else {
                this.validarTransaccionIniciada();
                int t = lista.size();
                for(int i = 0; i < t; i++)
                    this.sesion.save(lista.get(i));
            }
        }catch(Exception e) {
                this.revertirTransaccion();
                String mensaje = e.getMessage();
                try {
                    mensaje = e.getCause().getMessage();
                } catch(Exception ex) {}
            throw new Exception("Error fatal al insertar la lista. " + mensaje);
        }
    }

    public void actualizar(String query) throws Exception {
        try {
            if(this.esUnicaTransaccion()) {
                this.comenzarTransaccion();
                this.sesion.createQuery(query).executeUpdate();
                this.finalizarTransaccion();
            }else {
                this.validarTransaccionIniciada();
                this.sesion.createQuery(query).executeUpdate();
            }
        }catch(Exception e) {
        	this.revertirTransaccion();
        	String mensaje = e.getMessage();
        	try {
                    mensaje = e.getCause().getMessage();
        	} catch(Exception ex) {}
            throw new Exception("Error fatal al actualizar el objeto. " + mensaje);
        }
    }

    public void actualizarQ(String query) throws Exception {
        try {
            if(this.esUnicaTransaccion()) {
                this.comenzarTransaccion();
                this.sesion.createSQLQuery(query).executeUpdate();
                this.finalizarTransaccion();
            }else {
                this.validarTransaccionIniciada();
                this.sesion.createSQLQuery(query).executeUpdate();
            }
        }catch(Exception e) {
        	this.revertirTransaccion();
        	String mensaje = e.getMessage();
        	try {
                    mensaje = e.getCause().getMessage();
        	} catch(Exception ex) {}
            throw new Exception("Error fatal al actualizar el objeto. " + mensaje);
        }
    }

    public void actualizar(Object objeto) throws Exception {
        try {
            if(this.esUnicaTransaccion()) {
                this.comenzarTransaccion();
                this.sesion.update(objeto);
                this.finalizarTransaccion();
            }else {
                this.validarTransaccionIniciada();
                this.sesion.update(objeto);
            }
        }catch(Exception e) {
        	this.revertirTransaccion();
        	String mensaje = e.getMessage();
        	try {
                    mensaje = e.getCause().getMessage();
        	} catch(Exception ex) {}
            throw new Exception("Error fatal al actualizar el objeto. " + mensaje);
        }
    }

    @SuppressWarnings("rawtypes")
    public void actualizar(List lista) throws Exception {
        try {
            if(this.esUnicaTransaccion()) {
                this.comenzarTransaccion();
                int t = lista.size();
                for(int i = 0; i < t; i++)
                    this.sesion.update(lista.get(i));
                this.finalizarTransaccion();
            }else {
                this.validarTransaccionIniciada();
                int t = lista.size();
                for(int i = 0; i < t; i++)
                    this.sesion.update(lista.get(i));
            }
        }catch(Exception e) {
                this.revertirTransaccion();
                String mensaje = e.getMessage();
                try {
                    mensaje = e.getCause().getMessage();
                } catch(Exception ex) {}
            throw new Exception("Error fatal al actualizar la lista. " + mensaje);
        }
    }

    public void eliminar(String query) throws Exception {
        try {
            if(this.esUnicaTransaccion()) {
                this.comenzarTransaccion();
                this.sesion.createQuery(query).executeUpdate();
                this.finalizarTransaccion();
            }else {
                this.validarTransaccionIniciada();
                this.sesion.createQuery(query).executeUpdate();
            }
        }catch(Exception e) {
        	this.revertirTransaccion();
        	String mensaje = e.getMessage();
        	try {
                    mensaje = e.getCause().getMessage();
        	} catch(Exception ex) {}
            throw new Exception("Error fatal al eliminar el objeto. " + mensaje);
        }
    }

    public void eliminarQ(String query) throws Exception {
        try {
            if(this.esUnicaTransaccion()) {
                this.comenzarTransaccion();
                this.sesion.createSQLQuery(query).executeUpdate();
                this.finalizarTransaccion();
            }else {
                this.validarTransaccionIniciada();
                this.sesion.createSQLQuery(query).executeUpdate();
            }
        }catch(Exception e) {
        	this.revertirTransaccion();
        	String mensaje = e.getMessage();
        	try {
                    mensaje = e.getCause().getMessage();
        	} catch(Exception ex) {}
            throw new Exception("Error fatal al eliminar el objeto. " + mensaje);
        }
    }

    public void eliminar(Object objeto) throws Exception {
        try {
            if(this.esUnicaTransaccion()) {
                this.comenzarTransaccion();
                this.sesion.delete(objeto);
                this.finalizarTransaccion();
            }else {
                this.validarTransaccionIniciada();
                this.sesion.delete(objeto);
            }
        }catch(Exception e) {
        	this.revertirTransaccion();
        	String mensaje = e.getMessage();
        	try {
                    mensaje = e.getCause().getMessage();
        	} catch(Exception ex) {}
            throw new Exception("Error fatal al eliminar el objeto. " + mensaje);
        }
    }

    @SuppressWarnings("rawtypes")
    public void eliminar(List lista) throws Exception {
        try {
            if(this.esUnicaTransaccion()) {
                this.comenzarTransaccion();
                int t = lista.size();
                for(int i = 0; i < t; i++)
                    this.sesion.delete(lista.get(i));
                this.finalizarTransaccion();
            } else {
                this.validarTransaccionIniciada();
                int t = lista.size();
                for(int i = 0; i < t; i++)
                    this.sesion.delete(lista.get(i));
            }
        }catch(Exception e) {
                this.revertirTransaccion();
                String mensaje = e.getMessage();
                try {
                    mensaje = e.getCause().getMessage();
                } catch(Exception ex) {}
            throw new Exception("Error fatal al eliminar la lista. " + mensaje);
        }
    }

    public HibernateUtil getHibernateUtil() {
        if(this.hibernateUtil == null) this.hibernateUtil = new HibernateUtil();
        return hibernateUtil;
    }

}
