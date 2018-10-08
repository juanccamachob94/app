package mx.com.juan.camacho.controladores.userapp;

import java.util.Calendar;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import mx.com.juan.camacho.entidadesdb.Userapp;
import mx.com.juan.camacho.utilitaria.Utilitaria;
import mx.com.juan.camacho.entidadesdb.Blog;

@ManagedBean
@ViewScoped

public class ViewUserappBean extends mx.com.juan.camacho.beans.GeneralVistaBean {

  private Userapp userapp;

  private DefaultTreeNode rootBlogs;

  public void setRootBlogs(DefaultTreeNode rootBlogs) {
    this.rootBlogs = rootBlogs;
  }

  private void addBlog(TreeNode nodoMonth, int year, int idMonth) throws Exception {
    List<Blog> blogs = this.dataSource.consultarLista("SELECT blog FROM Blog blog WHERE date_part('YEAR',CURRENT_DATE) = " + year + " AND date_part('MONTH',CURRENT_DATE) = " + (idMonth + 1) + " AND blog.userapp.id =" + this.userapp.getId() + " ORDER BY blog.FCreate DESC");
    int numBlogs = blogs.size();
    for(int i = 0; i < numBlogs; i++) new DefaultTreeNode("blog",blogs.get(i),nodoMonth);
  }

  private void addMonth(TreeNode nodoYear, int idMonth) throws Exception {
    TreeNode nodoMonth = new DefaultTreeNode("month",Utilitaria.obtenerNombreMes(idMonth),nodoYear);
    addBlog(nodoMonth,(Integer)nodoYear.getData(),idMonth);
    if(nodoMonth.getChildCount() == 0) nodoYear.getChildren().remove(nodoMonth);
  }


  private void addYear(TreeNode raiz, int year) throws Exception {
    if(this.dataSource.contar("SELECT COUNT(blog) FROM Blog blog WHERE date_part('YEAR',CURRENT_DATE) = " + year + " AND blog.userapp.id =" + this.userapp.getId()) > 0) {
      TreeNode nodoYear = new DefaultTreeNode("year",year,raiz);
      for(int idMonth = 0; idMonth <=11; idMonth++) addMonth(nodoYear,idMonth);
      if(nodoYear.getChildCount() == 0) raiz.getChildren().remove(nodoYear);
    }
  }

  public TreeNode getRootBlogs() {
	try {
      if(this.rootBlogs == null) {
        this.rootBlogs = new DefaultTreeNode("raiz","Blogs",null);
        int yearStart = Calendar.getInstance().get(Calendar.YEAR);
        for(int year = yearStart; year >= 2018; year--) addYear(this.rootBlogs,year);
      }
		} catch(Exception e) {
			this.enviarMensaje(null, "Error al cargar tus blogs. " + e.getMessage(), "fatal");
		}
		return this.rootBlogs;
	}


  public ViewUserappBean() {
  }

  public void setUserapp(Userapp userapp) {
    this.userapp = userapp;
  }

  public Userapp getUserapp() {
    return this.userapp;
  }

  public void setIdUserapp(int idUserapp) {
    try{
       this.userapp = (Userapp)this.dataSource.consultarObjeto("SELECT userapp FROM Userapp userapp WHERE userapp.id = " + idUserapp);
    } catch(Exception e) {
      this.mostrarModal("No se pudo consultar el usuario " + idUserapp + ". " + e.getMessage(),"fatal");
    }
  }

  public int getIdUserapp() {
	  try {
		  return this.userapp.getId();		  
	  } catch(Exception e) {
		  return 0;
	  }
  }

}
