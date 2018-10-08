package mx.com.juan.camacho.controladores.blog;

import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

import mx.com.juan.camacho.controladores.diagram.DiagramBean;
import mx.com.juan.camacho.entidadesdb.Blog;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
@ManagedBean
@ViewScoped
public class ViewBlogBean extends mx.com.juan.camacho.beans.GeneralVistaBean {

  private Blog blog;
  private String textoSeleccionado;
  private String content;
  private List<String> concepts;
  @ManagedProperty(value="#{diagramBean}")
  private DiagramBean diagramBean;
  
  
  public List<String> getConcepts() {
	  return this.concepts;
  }
  
  public void setConcepts(List<String> concepts) {
	  this.concepts = concepts;
  }
  
  public DiagramBean getDiagramBean() {
	  return this.diagramBean;
  }
  
  public void setDiagramBean(DiagramBean diagramBean) {
	  this.diagramBean = diagramBean;
  }
  
  public String getContent() {
	  return this.content;
  }
  
  public void setContent(String content) {
	  this.content = content;
  }

  public void seleccionarTexto() {
	  this.textoSeleccionado = this.textoSeleccionado.trim();
	  if(this.textoSeleccionado != null && !this.textoSeleccionado.trim().equals("") && !this.textoSeleccionado.contains("style=\"background-color:yellow\"") || (this.textoSeleccionado.contains("style=\"background-color:yellow\"") && !this.textoSeleccionado.contains("</span>"))) {
		  Matcher mTexto = Pattern.compile("([^\\>\\<]+(?=((\\<[^\\>\\<]+\\>)|(\\<\\/[^\\>\\<]+\\>))))|(?<=\\>)(^.)*(?=\\<)").matcher(this.content);
		  Matcher mEtiquetas = Pattern.compile("(\\<[^\\>\\<]+\\>)|(\\<\\/[^\\>\\<]+\\>)").matcher(this.content);
		  List<String> matchesTexto = new ArrayList<String>();
		  List<String> matchesEtiquetas = new ArrayList<String>();
		  while (mTexto.find()) matchesTexto.add(mTexto.group());
		  while (mEtiquetas.find()) matchesEtiquetas.add(mEtiquetas.group());
		  int tx = matchesTexto.size();
		  int tl = matchesEtiquetas.size();
		  int min = tx <= tl ? tx : tl;
		  String cadena = "";
		  for(int x = 0; x < min; x++)
			  cadena = cadena + matchesEtiquetas.get(x) + matchesTexto.get(x).replace(this.textoSeleccionado,"<span style=\"background-color:yellow\">" + this.textoSeleccionado + "</span>");
		  if(tx <= tl) cadena = cadena + matchesEtiquetas.get(tl - 1);
		  else cadena = cadena + matchesTexto.get(tx -1);
		  this.content = cadena;
		  boolean encontrado = false;
		  for(String concept : concepts)
			  if(concept.equals(this.textoSeleccionado)) {
				  encontrado = true;
				  break;
			  }
		  if(!encontrado) this.concepts.add(this.textoSeleccionado);
	  }
		  
  }
  
  public void eliminarConcepto(String concepto) {
	  try {
		  this.content = this.content.replace("<span style=\"background-color:yellow\">" + concepto + "</span>", concepto);
		  this.concepts.remove(concepto);
	  } catch(Exception e) {}
  }
  
  public void solicitarDiagrama() {
	  this.diagramBean.setConcepts(this.concepts);
  }
  
  public String getTextoSeleccionado() {
	  return this.textoSeleccionado;
  }
  
  public void setTextoSeleccionado(String textoSeleccionado) {
	  this.textoSeleccionado = textoSeleccionado;
  }

  public ViewBlogBean() {
	  this.concepts = new ArrayList<String>();
  }

  public void setIdBlog(int idBlog) {
    try {
      this.blog = (Blog)this.dataSource.consultarObjeto("SELECT blog FROM Blog blog WHERE blog.id =" + idBlog);
      this.content = this.blog.getContent();
    } catch(Exception e) {
      this.mostrarModal("No se pudo consultar el blog " + idBlog + ". " + e.getMessage(),"fatal");
    }
  }

  public int getIdBlog() {
	  try {
		  return this.blog.getId();		  
	  } catch(Exception e) {
		  return 0;
	  }
    
  }

  public void setBlog(Blog blog) {
    this.blog = blog;
  }

  public Blog getBlog() {
    return this.blog;
  }

}
