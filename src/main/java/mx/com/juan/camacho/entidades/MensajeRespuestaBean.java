package mx.com.juan.camacho.entidades;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class MensajeRespuestaBean {
	
	private String mensaje;
	private String tipo;
	/**
	 * modal
	 * growl
	 */
	private String representacion;
	
	public MensajeRespuestaBean() {
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}



	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getRepresentacion() {
		return representacion;
	}

	public void setRepresentacion(String representacion) {
		this.representacion = representacion;
	}
	

}
