package mx.com.juan.camacho.controladores.otros;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import mx.com.juan.camacho.entidadesdb.TiempoOtro;
import mx.com.juan.camacho.utilitaria.Utilitaria;

@ManagedBean
@ViewScoped
public class DiferenciasBean extends mx.com.juan.camacho.beans.GeneralVistaBean {

  private TiempoOtro tiempoOtro;
  private int numDiferenciasEncontradas;

  public int getNumDiferenciasEncontradas() {
    return this.numDiferenciasEncontradas;
  }

  public void setNumDiferenciasEncontradas(int numDiferenciasEncontradas) {
    if(numDiferenciasEncontradas == this.numDiferenciasEncontradas + 1)
			this.numDiferenciasEncontradas = numDiferenciasEncontradas;
		else {
			this.numDiferenciasEncontradas = -1;
			this.mostrarModal("Estás haciendo trampa. Debes volver a cargar la página","fatal");
		}
  }

  public TiempoOtro getTiempoOtro() {
    return this.tiempoOtro;
  }

  public void setTiempoOtro(TiempoOtro tiempoOtro) {
    this.tiempoOtro = tiempoOtro;
  }

  public DiferenciasBean() {
    this.numDiferenciasEncontradas = 0;
  }


  @javax.annotation.PostConstruct
	public void init() {
		try {
			this.tiempoOtro = (TiempoOtro)this.dataSource.consultarObjeto("SELECT tiempoOtro FROM TiempoOtro tiempoOtro WHERE tiempoOtro.prueba = 'DIFERENCIAS' AND tiempoOtro.userapp.id = " + this.usuarioBean.getUserappSession().getId());
		} catch(Exception e) {}
	}

  public void iniciarPrueba() {
		this.tiempoOtro = new TiempoOtro();
		this.tiempoOtro.setPrueba("DIFERENCIAS");
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

  public void finalizarPrueba() {
		try {
			if(this.numDiferenciasEncontradas >= 7) {
				this.tiempoOtro.setFFin(new java.util.Date());
				this.dataSource.actualizar(this.tiempoOtro);
				this.mostrarModal("Has terminado exitosamente! El tiempo total de la prueba ha sido de: " + Utilitaria.tiempoDiferencia(Utilitaria.diferencia(this.tiempoOtro.getFInicio(),this.tiempoOtro.getFFin())), "info");
			} else {
				this.mostrarModal("Has encontrado " + this.numDiferenciasEncontradas + " de 7", "error");
			}
		} catch(Exception e) {
			this.mostrarModal("No se puede finalizar la prueba. " + e.getMessage(),"fatal");
		}
	}

}
