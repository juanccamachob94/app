package mx.com.juan.camacho.controladores.blog;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import mx.com.juan.camacho.entidadesdb.Blog;

@ManagedBean
@ViewScoped
public class MisBlogsBean extends mx.com.juan.camacho.beans.DBBean<Blog> {
  public MisBlogsBean() {
    this.select = "SELECT blog ";
    this.from = "FROM Blog blog ";
    this.order = "ORDER BY blog.FCreate DESC ";
    this.id = "blog.id ";
  }
  @PostConstruct
  public void init() {
	  this.where = "WHERE blog.userapp.id = " + this.usuarioBean.getUserappSession().getId() + " ";
  }
}
