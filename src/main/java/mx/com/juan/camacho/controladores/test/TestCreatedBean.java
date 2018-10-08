package mx.com.juan.camacho.controladores.test;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import mx.com.juan.camacho.entidadesdb.Test;
import javax.annotation.PostConstruct;

@ManagedBean
@ViewScoped
public class TestCreatedBean extends mx.com.juan.camacho.beans.DBBean<Test> {

    

    public TestCreatedBean() {
    this.select = "SELECT test ";
    this.from = "FROM Test test ";
    this.order = "ORDER BY test.id DESC";
    this.id = "test.id ";
  }

  @PostConstruct
  public void init() {
    this.where = "WHERE test.blog.userapp.id = " + this.usuarioBean.getUserappSession().getId() + " ";
  }



}
