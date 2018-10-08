package mx.com.juan.camacho.hibernate;

import org.hibernate.cfg.Configuration;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HibernateUtil implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    private final SessionFactory sessionFactory;

    public HibernateUtil() {
        try {
            Logger mongoLogger = Logger.getLogger("org.hibernate");
            mongoLogger.setLevel(Level.SEVERE);
            Configuration configuracion = new Configuration();
            configuracion.configure("hibernate.cfg.xml");
            sessionFactory = new Configuration().configure().buildSessionFactory(new StandardServiceRegistryBuilder().applySettings(configuracion.getProperties()).build());
        } catch (Throwable e) {
            System.err.println("HibernateUtil : {Ha fallado la instancia de la SessionFactory " + e + "}");
            throw new ExceptionInInitializerError(e);
        }
    }
    
    
    public HibernateUtil(String xml) {
        try {
            Logger mongoLogger = Logger.getLogger("org.hibernate");
            mongoLogger.setLevel(Level.SEVERE);
            Configuration configuracion = new Configuration();
            configuracion.configure(xml + ".xml");
            sessionFactory = new Configuration().configure().buildSessionFactory(new StandardServiceRegistryBuilder().applySettings(configuracion.getProperties()).build());
        } catch (Throwable e) {
            System.err.println("HibernateUtil : {Ha fallado la instancia de la SessionFactory " + e + "}");
            throw new ExceptionInInitializerError(e);
        }
    }
    

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

}