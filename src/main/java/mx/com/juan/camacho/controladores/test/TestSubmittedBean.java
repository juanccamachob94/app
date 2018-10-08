package mx.com.juan.camacho.controladores.test;

import java.math.BigDecimal;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import mx.com.juan.camacho.entidadesdb.Test;

@ManagedBean
@ViewScoped
public class TestSubmittedBean extends mx.com.juan.camacho.beans.DBBean<Test> {

  private boolean mensajeError;
  
  public TestSubmittedBean() {
    this.select = "SELECT test ";
    this.from = "FROM Test test, Question q, UserappQuestion uq, Userapp u ";
    this.where = "WHERE test.id = q.test.id AND q.id = uq.question.id AND u.id = uq.userapp.id ";
    this.groupBy = "GROUP BY test.id ";
    this.order = "ORDER BY test.id DESC ";
    this.id = "test.id ";
    this.mensajeError = false;
  }

  @PostConstruct
  public void init() {
    this.where = this.where + " AND u.id = " + this.usuarioBean.getUserappSession().getId() + " ";
  }

  public String puntosObtenidos(int idTest) {
    try {
    	BigDecimal valor = (BigDecimal)this.dataSource.consultarObjetoQ("SELECT SUM(UQ.EARNED_POINTS) FROM USERAPP_QUESTION UQ, QUESTION Q, USERAPP U, TEST T WHERE T.ID = Q.TEST_ID AND Q.ID = UQ.QUESTION_ID AND U.ID = UQ.USERAPP_ID AND T.ID = " + idTest + " AND U.ID = " + this.usuarioBean.getUserappSession().getId());
    	if(valor == null) return "-";
    	return Float.toString((valor).floatValue());
    } catch(Exception e) {
      if(!this.mensajeError) {
        this.mostrarModal("No se pudo calcular la cantidad de puntos por examen. " + e.getMessage(),"fatal");
        this.mensajeError = true;
      }
      return "-";
    }
  }


}
