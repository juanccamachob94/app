package mx.com.juan.camacho.controladores.login;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.bean.ManagedProperty;
import mx.com.juan.camacho.entidadesdb.Userapp;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.context.RequestContext;

import mx.com.juan.camacho.hibernate.DataSource;
import mx.com.juan.camacho.utilitaria.Utilitaria;

@ManagedBean
@SessionScoped
public class UsuarioBean implements java.io.Serializable {

  private static final String faces = "faces/vista/";
  private static final long serialVersionUID = 1L;

  private Userapp userapp;
  private Userapp userappSession;
  private int numberIntents;

  @ManagedProperty(value="#{dataSource}")
	protected DataSource dataSource;


  public Userapp getUserapp() {
    return this.userapp;
  }

  public void setUserapp(Userapp userapp) {
    this.userapp = userapp;
  }

  public Userapp getUserappSession() {
    return this.userappSession;
  }

  public void setUserappSession(Userapp userapp) {
    this.userappSession = userapp;
  }

  public DataSource getDataSource() {
    return this.dataSource;
  }

  public void setDataSource(DataSource dataSource) {
	  this.dataSource = dataSource;
  }

  public UsuarioBean() {
    this.numberIntents = 0;
  }

  public void cargarUsuario() {
    if(this.numberIntents == 0) this.userapp = new Userapp();
  }

  public Map<String,Object> getSessionCustomMap() {
    return FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
  }

  public boolean isSessionStarted() {
    try {
      return this.userappSession.getUserappname() != null && this.userappSession.getPassword() != null && this.getSessionCustomMap().get("userapp") != null;
    } catch(Exception e) {
      return false;
    }
  }

  public String encriptarMD5(String password) throws Exception{
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] passBytes = password.getBytes();
      md.reset();
      byte[] digested = md.digest(passBytes);
      StringBuffer sb = new StringBuffer();
      for(int i=0;i<digested.length;i++) sb.append(Integer.toHexString(0xff & digested[i]));
      return sb.toString();
    } catch (Exception ex) {
      throw new Exception("No se pudo encriptar la contrase�a. " + ex.getMessage());
    }
  }

  public void startSessionData(Userapp userapp) throws Exception {
    this.userapp = null;
    this.userappSession = userapp;
    this.getSessionCustomMap().put("userapp",userapp);
    this.showPage("inicio/inicioSistema");
  }

  public void startSession() {
    try {
      this.userapp.setUserappname(this.userapp.getUserappname().toLowerCase().trim());
      Userapp userFound = (Userapp) this.dataSource.consultarObjeto("SELECT userapp FROM Userapp userapp WHERE userapp.userappname = '" + this.userapp.getUserappname() + "'");
      if(userFound != null) {
        if(!userFound.getPassword().equals(this.encriptarMD5(this.userapp.getPassword()))) {
          this.userapp.setPassword(null);
          this.sendMessage(null,"Clave incorrecta","error");
        } else startSessionData(userFound);
      } else {
        this.userapp.setPassword(null);
        this.sendMessage(null,"Usuario no encontrado","error");
      }
    } catch(Exception e) {
      if(!this.isSessionStarted()) this.userapp.setPassword(null);
      this.mostrarModal("No se puede iniciar sesión. " + e.getMessage(),"fatal");
    }
  }

  public void closeSession() {
    try {
      FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
      this.showPage("login/login");
    } catch(Exception e) {
      this.sendMessage(null,"No se puede cerrar la sesión. " + e.getMessage(),"fatal");
    }
  }

  /**
   * Permite redireccionar a una página web
   * @param nombrePagina
   * @throws java.lang.Exception
   */
  protected void showPage(String rutaTransaccion) throws Exception {
    try {
      FacesContext.getCurrentInstance().getExternalContext().redirect(getSaltosDirec() + (rutaTransaccion.contains(".xhtml") ? rutaTransaccion : (rutaTransaccion + ".xhtml")));
    } catch(IOException ioe) {
      throw new Exception("No se localiza la página " + rutaTransaccion + ".xhtml");
    }
  }

  public String getSaltosDirec() {
	String url = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getRequestURL().toString();
	int numSaltosDirec = Utilitaria.dividir(url.substring(url.lastIndexOf(faces) + faces.length()),"/").length - 1;
	String salto = "";
	int i = 0;
	while(i++ < numSaltosDirec) salto += "../";
	return salto;
  }

  public void mostrarModal(String mensaje, String tipo) {
    Severity s = null;
      switch (tipo) {
          case "info":
              s = FacesMessage.SEVERITY_INFO;
              break;
          case "warn":
              s = FacesMessage.SEVERITY_WARN;
              break;
          case "error":
              s = FacesMessage.SEVERITY_ERROR;
              break;
          case "fatal":
              s = FacesMessage.SEVERITY_FATAL;
              break;
      }
    RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(s, "Mensaje", mensaje));
  }

  /**
     * Permite mostrar una respuesta en la parte superior derecha del navegador
     * @param boton
     * @param mensaje
     * @param tipo
     */
    protected void sendMessage(UIComponent boton, String mensaje, String tipo) {
        FacesContext context = FacesContext.getCurrentInstance();
        Severity s = null;
        System.out.println("Mensaje enviado al usuario(" + tipo + "): " + mensaje);
        switch (tipo) {
            case "info":
                s = FacesMessage.SEVERITY_INFO;
                break;
            case "warn":
                s = FacesMessage.SEVERITY_WARN;
                break;
            case "error":
                s = FacesMessage.SEVERITY_ERROR;
                break;
            case "fatal":
                s = FacesMessage.SEVERITY_FATAL;
                break;
        }
        context.addMessage(boton == null ? null : boton.getClientId(context), new FacesMessage(s, mensaje + ".", mensaje + "."));
    }

}
