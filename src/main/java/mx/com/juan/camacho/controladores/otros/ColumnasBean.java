package mx.com.juan.camacho.controladores.otros;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import mx.com.juan.camacho.entidadesdb.TiempoOtro;
import mx.com.juan.camacho.utilitaria.Utilitaria;

@ManagedBean
@ViewScoped
public class ColumnasBean extends mx.com.juan.camacho.beans.GeneralBean {


	private TiempoOtro tiempoOtro;
	private int numPalabrasEncontradas;

	public int getNumPalabrasEncontradas() {
		return this.numPalabrasEncontradas;
	}

	public void setNumPalabrasEncontradas(int numPalabrasEncontradas) {
		if(numPalabrasEncontradas == this.numPalabrasEncontradas + 1)
			this.numPalabrasEncontradas = numPalabrasEncontradas;
		else {
			this.numPalabrasEncontradas = -1;
			this.mostrarModal("Estás haciendo trampa. Debes volver a cargar la página","fatal");
		}
	}

	public ColumnasBean() {
		this.numPalabrasEncontradas = 0;
	}
	@javax.annotation.PostConstruct
	public void init() {
		try {
			this.tiempoOtro = (TiempoOtro)this.dataSource.consultarObjeto("SELECT tiempoOtro FROM TiempoOtro tiempoOtro WHERE tiempoOtro.prueba = 'COLUMNAS' AND tiempoOtro.userapp.id = " + this.usuarioBean.getUserappSession().getId());
		} catch(Exception e) {}
	}

	public void finalizarPrueba() {
		try {
			if(this.numPalabrasEncontradas >= 28) {
				this.tiempoOtro.setFFin(new java.util.Date());
				this.dataSource.actualizar(this.tiempoOtro);
				this.mostrarModal("Has terminado exitosamente! El tiempo total de la prueba ha sido de: " + Utilitaria.tiempoDiferencia(Utilitaria.diferencia(this.tiempoOtro.getFInicio(),this.tiempoOtro.getFFin())), "info");
			} else {
				this.mostrarModal("Has encontrado " + this.numPalabrasEncontradas + " de 28", "error");
			}
		} catch(Exception e) {
			this.mostrarModal("No se puede finalizar la prueba. " + e.getMessage(),"fatal");
		}
	}


	public void iniciarPrueba() {
		this.tiempoOtro = new TiempoOtro();
		this.tiempoOtro.setPrueba("COLUMNAS");
		this.tiempoOtro.setUserapp(this.usuarioBean.getUserappSession());
		this.tiempoOtro.setFInicio(new java.util.Date());
		try {
			this.dataSource.insertar(this.tiempoOtro);
		} catch(Exception e) {
			this.mostrarModal("No se puede iniciar la prueba. " + e.getMessage(),"fatal");
		}
	}

	public boolean isPruebaIniciada() {
		return this.tiempoOtro != null;
	}

	public TiempoOtro getTiempoOtro() {
		return this.tiempoOtro;
	}

	public void setTiempoOtro(TiempoOtro tiempoOtro) {
		this.tiempoOtro = tiempoOtro;
	}
}
