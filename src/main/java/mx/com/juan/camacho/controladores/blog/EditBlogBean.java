package mx.com.juan.camacho.controladores.blog;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import mx.com.juan.camacho.entidadesdb.Blog;

@ManagedBean
@ViewScoped
public class EditBlogBean extends mx.com.juan.camacho.beans.GeneralVistaBean {

  private Blog editblog;

  public EditBlogBean() {
  }

  public void setIdBlog(int idBlog) {
    try {
      this.editblog = (Blog)this.dataSource.consultarObjeto("SELECT blog FROM Blog blog WHERE blog.id =" + idBlog);
      System.out.println(this.usuarioBean.getUserappSession().getId() +" +  "+ this.editblog.getUserapp().getId());
      if(this.usuarioBean.getUserappSession().getId() != this.editblog.getUserapp().getId()) {
    	  this.editblog = null;
    	  this.mostrarPagina("inicio/inicioSistema");
      }
    } catch(Exception e) {
      this.mostrarModal("No se pudo consultar el blog " + idBlog + ". " + e.getMessage(),"fatal");
    }
  }
  public int getIdBlog() {
	  try {
		  return this.editblog.getId();		  
	  } catch(Exception e) {
		  return 0;
	  }
  }

  public void setEditblog(Blog editblog) {
    this.editblog = editblog;
  }

  public Blog getEditblog() {
    return this.editblog;
  }

  public void editarBlog() {
    try {
      this.dataSource.actualizar(this.editblog);
      this.mostrarModal("Los cambios han sido realizados satisfactoriamente","info");
    } catch(Exception e) {
      this.enviarMensaje(null,"No se pudo editar el blog. " + e.getMessage(),"fatal");
    }
  }

}
