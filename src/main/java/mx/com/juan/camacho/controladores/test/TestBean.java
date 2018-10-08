package mx.com.juan.camacho.controladores.test;

import mx.com.juan.camacho.beans.GeneralVistaBean;
import mx.com.juan.camacho.entidades.Atributo;
import mx.com.juan.camacho.entidadesdb.UserappQuestion;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.com.juan.camacho.entidadesdb.Option;
import mx.com.juan.camacho.entidadesdb.Question;
import mx.com.juan.camacho.entidadesdb.Test;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Date;

@ManagedBean
@ViewScoped
public class TestBean extends GeneralVistaBean {

  private Test test;
  private List<String> idsAtributos;
  private List<UserappQuestion> userappQuestions;
  private List<List<Atributo>> capsulaQuestions;

  public TestBean() {
  }
  
  
  public List<List<Atributo>> getCapsulaQuestions() {
	  return this.capsulaQuestions;
  }
  
  public void setCapsulaQuestions(List<List<Atributo>> capsulaQuestions) {
	  this.capsulaQuestions = capsulaQuestions;
  }
  public List<String> getIdsAtributos() {
	  return this.idsAtributos;
  }
  
  public void setIdsAtributos(List<String> idsAtributos) {
	  this.idsAtributos = idsAtributos;
  }
  
  
  public Test getTest() {
	  return this.test;
  }
  
  public boolean isTestEvaluado() {
	  try {
		  return this.dataSource.contar("SELECT COUNT(test.id) FROM Test test, Question question, UserappQuestion userappQuestion WHERE question.test.id = test.id AND test.id = " + this.test.getId() + " AND userappQuestion.question.id = question.id AND userappQuestion.userapp.id = " + this.usuarioBean.getUserappSession().getId() + " AND userappQuestion.earnedPoints IS NOT NULL") > 0;
	  } catch(Exception e) {
		  this.mostrarModal("No se pudo revisar si el test ya fue o no evaluado. " + e.getMessage(),"fatal");
		  return false;
	  }
  }
  
  public void setTest(Test test) {
	  this.test = test;
  }

  public List<UserappQuestion> getUserappQuestions() {
    return this.userappQuestions;
  }

  public void setUserappQuestions(List<UserappQuestion> questions) {
    this.userappQuestions = questions;
  }

  public void setIdTest(int idTest) {
    try {
      this.test = (Test)this.dataSource.consultarObjeto("SELECT test FROM Test test WHERE test.id = " + idTest);
      this.userappQuestions = new ArrayList();
      Set<Question> questions = (Set<Question>)test.getQuestions();
      UserappQuestion userappQuestion;
      for (Question question : questions) {
        userappQuestion = new UserappQuestion();
        userappQuestion.setUserapp(this.usuarioBean.getUserappSession());
        userappQuestion.setQuestion(question);
        this.userappQuestions.add(userappQuestion);
      }
      this.capsulaQuestions = new ArrayList<List<Atributo>>();
      this.idsAtributos = new ArrayList<String>();
      int numUserappQuestions = this.userappQuestions.size();
      List<Atributo> capsulasOptions;
      Atributo atributo;
      List<Option> options;
      int numOptions;
      for(int i = 0; i < numUserappQuestions; i++) {
    	  capsulasOptions = new ArrayList<Atributo>();
    	  options = new ArrayList(this.userappQuestions.get(i).getQuestion().getOptions());
    	  numOptions = options.size();
    	  for(int j = 0; j < numOptions; j++) {
    		  atributo = new Atributo();
    		  atributo.setNombre(Integer.toString(j));
    		  atributo.setValor(options.get(j));
    		  capsulasOptions.add(atributo);
    	  }
    	  this.idsAtributos.add(null);
    	  this.capsulaQuestions.add(capsulasOptions);
      }
    } catch(Exception e) {
      this.mostrarModal("No se pudo consultar el examen " + idTest + ". " + e.getMessage(),"fatal");
    }
  }
  
  public void setOptionQuestion(int index) {
	  try {
		  String identificadorSeleccionado = this.idsAtributos.get(index);
		  List<Atributo> capsulaQuestion = this.capsulaQuestions.get(index);
		  int num = capsulaQuestion.size();
		  for(int i = 0; i < num; i++)
			  if(identificadorSeleccionado.equals(capsulaQuestion.get(i).getNombre())) {
				  this.userappQuestions.get(index).setOption((Option)capsulaQuestion.get(i).getValor());
				  break;
			  }
	  }catch(Exception e) {
		  this.mostrarModal("No se pudo asignar la respuesta. " + e.getMessage(),"fatal");
	  }
  }

  public int getIdTest() {
    try {
      return this.test.getId();
    } catch(Exception e) {
      return 0;
    }
  }

  public boolean isExamenPresentado() {
    try {
    	return this.dataSource.contar("SELECT COUNT(test.id) FROM Test test, Question question, UserappQuestion userappQuestion WHERE question.test.id = test.id AND test.id = " + this.test.getId() + " AND userappQuestion.question.id = question.id AND userappQuestion.userapp.id = " + this.usuarioBean.getUserappSession().getId()) > 0;
    } catch(Exception e) {
      this.mostrarModal("Error en el sistema al revisar si el examen ya fue presentado. " + e.getMessage(),"fatal");
      return false;
    }
  }

  public Integer getPuntosObtenidosExamen() {
    try {
      return this.dataSource.contar("SELECT COUNT(uq.earnedPoints) FROM UserappQuestion uq WHERE uq.userapp.id = " + this.usuarioBean.getUserappSession().getId() + " AND uq.question.test.id = " + this.test.getId());
    } catch(Exception e) {
      return null;
    }
  }

  public void finalizarExamen() {
    try {
      UserappQuestion userappQuestion;
      Date fechaHoy = new Date();
      int t = this.userappQuestions.size();
      this.dataSource.iniciarTransaccion();
      for(int i = 0; i < t; i++) {
        userappQuestion = this.userappQuestions.get(i);
        userappQuestion.setDateOfGrades(fechaHoy);
        if(userappQuestion.getQuestion().getType().equals("MULTIPLE")) {
        	if(userappQuestion.getOption() == null)
        		throw new Exception("Todas las opciones deben ser seleccionadas");
        } else if(userappQuestion.getQuestion().getType().equals("OPEN")) {
        	if(userappQuestion.getAnswer() == null || userappQuestion.getAnswer().trim().equals(""))
        		throw new Exception("Todas las preguntas abiertas deben ser resueltas");
        }
        this.dataSource.insertar(userappQuestion);
      }
      this.dataSource.finalizarTransaccion();
      this.mostrarModal("Examen presentado exitosamente","info");
    } catch(Exception e) {
      this.enviarMensaje(null,"No se pudo presentar el examen. " + e.getMessage(),"fatal");
    }
  }

  public int getExamenesPresentados() {
    try {
      return this.dataSource.contar("SELECT COUNT(uq.question.test.id) FROM UserappQuestion uq, Question q WHERE q.id = uq.question.id AND uq.userapp.id = " + this.usuarioBean.getUserappSession().getId() + " GROUP BY uq.question.test.id");
    } catch(Exception e) {
      return 0;
    }
  }

  public int getPuntosObtenidos() {
    try {
      return this.dataSource.contar("SELECT COUNT(uq.earnedPoints) FROM UserappQuestion uq WHERE uq.userapp.id = " + this.usuarioBean.getUserappSession().getId());
    } catch(Exception e) {
      return 0;
    }
  }






}
