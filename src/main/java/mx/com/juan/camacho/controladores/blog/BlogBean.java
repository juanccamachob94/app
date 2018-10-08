package mx.com.juan.camacho.controladores.blog;

import javax.faces.bean.ManagedBean;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import javax.faces.bean.ViewScoped;
import java.util.Calendar;
import java.util.List;
import mx.com.juan.camacho.entidadesdb.Blog;
import mx.com.juan.camacho.utilitaria.Utilitaria;

@ManagedBean
@ViewScoped
public class BlogBean extends mx.com.juan.camacho.beans.DBBean<Blog> {

  private DefaultTreeNode rootMisBlogs;
  private String busqueda;

  public String getBusqueda() {
    return this.busqueda;
  }

  public void setBusqueda(String busqueda) {
    this.busqueda = busqueda;
  }

  @Override
  public void asignarParametrosConsulta() {
    this.agregarOpcion("blog.title",this.busqueda,"LIKE");
    this.agregarOpcion("blog.description",this.busqueda,"LIKE");
    this.agregarOpcion("blog.content",this.busqueda,"LIKE");
  }

  public BlogBean() {
    this.rootMisBlogs = null;
    this.select = "SELECT blog ";
    this.from = "FROM Blog blog ";
    this.order = "ORDER BY blog.id DESC ";
    this.id = "blog.id ";

  }

  private void addBlog(TreeNode nodoMonth, int year, int idMonth) throws Exception {
    List<Blog> blogs = this.dataSource.consultarLista("SELECT blog FROM Blog blog WHERE date_part('YEAR',CURRENT_DATE) = " + year + " AND date_part('MONTH',CURRENT_DATE) = " + (idMonth + 1) + " AND blog.userapp.id =" + this.usuarioBean.getUserappSession().getId() + " ORDER BY blog.FCreate DESC");
    int numBlogs = blogs.size();
    for(int i = 0; i < numBlogs; i++) new DefaultTreeNode("blog",blogs.get(i),nodoMonth);
  }

  private void addMonth(TreeNode nodoYear, int idMonth) throws Exception {
    TreeNode nodoMonth = new DefaultTreeNode("month",Utilitaria.obtenerNombreMes(idMonth),nodoYear);
    addBlog(nodoMonth,(Integer)nodoYear.getData(),idMonth);
    if(nodoMonth.getChildCount() == 0) nodoYear.getChildren().remove(nodoMonth);
  }


  private void addYear(TreeNode raiz, int year) throws Exception {
    if(this.dataSource.contar("SELECT COUNT(blog) FROM Blog blog WHERE date_part('YEAR',CURRENT_DATE) = " + year + " AND blog.userapp.id =" + this.usuarioBean.getUserappSession().getId()) > 0) {
      TreeNode nodoYear = new DefaultTreeNode("year",year,raiz);
      for(int idMonth = 0; idMonth <=11; idMonth++) addMonth(nodoYear,idMonth);
      if(nodoYear.getChildCount() == 0) raiz.getChildren().remove(nodoYear);
    }
  }

  public TreeNode getRootMisBlogs() {
	try {
      if(this.rootMisBlogs == null) {
        this.rootMisBlogs = new DefaultTreeNode("raiz","Mis blogs",null);
        int yearStart = Calendar.getInstance().get(Calendar.YEAR);
        for(int year = yearStart; year >= 2018; year--) addYear(this.rootMisBlogs,year);
      }
		} catch(Exception e) {
			this.enviarMensaje(null, "Error al cargar tus blogs. " + e.getMessage(), "fatal");
		}
		return this.rootMisBlogs;
	}



  public List<Blog> getUltimosBlogs() {
    try {
      return this.dataSource.consultarLista("SELECT blog FROM Blog blog ORDER BY blog.FCreate DESC",5);
    } catch(Exception e) {
      return null;
    }
  }

}
