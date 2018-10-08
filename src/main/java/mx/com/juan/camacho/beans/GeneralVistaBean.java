package mx.com.juan.camacho.beans;

import java.io.FileInputStream;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import mx.com.juan.camacho.utilitaria.GestorArchivos;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;


@ManagedBean
@ViewScoped
public class GeneralVistaBean extends GeneralBean implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    protected String nombreTransaccion;
    protected String parametrosTransaccion;
    //Sirve para parametrizar el uso de pestañas a fin de facilitar su gestión
    //de acuerdo a la respuesta del servidor a una solicitud del cliente
    protected String tabPrincipal;

    public GeneralVistaBean() {
        this.nombreTransaccion = this.obtenerTransaccionQueSolicita();
        this.parametrosTransaccion = this.obtenerParametrosTransaccionQueSolicita();
    }

    protected void descargar(String nombreArchivo) throws Exception {
        try {
            this.archivoDescarga = new DefaultStreamedContent(new FileInputStream(GestorArchivos.exports + nombreArchivo),null,nombreArchivo);
        } catch(Exception e) {
            this.mostrarPagina(this.nombreTransaccion,this.parametrosTransaccion);
            throw new Exception("Archivo no encontrado (" + GestorArchivos.exports + nombreArchivo +"). " + e.getMessage());
        }
    }

    protected void descargar(String nombreArchivo, String rutaArchivo) throws Exception {
        try {
            this.archivoDescarga = new DefaultStreamedContent(new FileInputStream(rutaArchivo + nombreArchivo),null,nombreArchivo);
        } catch(Exception e) {
            this.mostrarPagina(this.nombreTransaccion,this.parametrosTransaccion);
            throw new Exception("Archivo no encontrado. " + e.getMessage());
        }
    }

		public void permitirAcceso() {
			try {
				if(this.usuarioBean.isSessionStarted()) this.mostrarPagina("inicio/inicioSistema");
			} catch(Exception e) {
				this.enviarMensaje(null,"Error al validar el acceso [PERMITIR]","fatal");
			}
		}

		public void denegarAcceso() {
			try {
				if(!this.usuarioBean.isSessionStarted()) this.mostrarPagina("login/login");
			} catch(Exception e) {
				this.enviarMensaje(null,"Error al validar el acceso [DENEGACION]","fatal");
			}
		}
	public String getNombreTransaccion() {
            return nombreTransaccion;
	}

	public void setNombreTransaccion(String nombreTransaccion) {
            this.nombreTransaccion = nombreTransaccion;
	}

	public String getParametrosTransaccion() {
            return parametrosTransaccion;
	}

	public void setParametrosTransaccion(String parametrosTransaccion) {
            this.parametrosTransaccion = parametrosTransaccion;
	}

	public String getTabPrincipal() {
            return tabPrincipal;
	}

	public void setTabPrincipal(String tabPrincipal) {
            this.tabPrincipal = tabPrincipal;
	}

	protected void mostrarTab(int id) {
            this.ejecutarJS("mostrarTab",new String[]{"n=>" + id,"c=>" + this.tabPrincipal});
	}

}
