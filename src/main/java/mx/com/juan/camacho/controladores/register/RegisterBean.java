package mx.com.juan.camacho.controladores.register;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import mx.com.juan.camacho.entidadesdb.Userapp;
import java.util.regex.Pattern;
import org.primefaces.model.UploadedFile;
import mx.com.juan.camacho.utilitaria.GestorArchivos;

import java.io.File;
import java.security.MessageDigest;
import java.util.Calendar;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

@ManagedBean
@ViewScoped
public class RegisterBean extends mx.com.juan.camacho.beans.GeneralVistaBean {

  private Userapp newUserapp;
  private String confirmPassword;
  private UploadedFile archivoImagen;
  
  private UploadedFile file;
  
  public UploadedFile getFile() {
      return file;
  }

  public void setFile(UploadedFile file) {
      this.file = file;
  }
   
  public void upload() {
      if(file != null) System.out.println("NO nulo");
      else System.out.println("SI ES NULO");
  }
  
  
  public UploadedFile getArchivoImagen() {
	  return this.archivoImagen;
  }
  
  public void setArchivoImagen(UploadedFile archivoImagen) {
	  this.archivoImagen = archivoImagen;
  }

  public String getConfirmPassword() {
    return this.confirmPassword;
  }

  public void setConfirmPassword(String password) {
    this.confirmPassword = password;
  }

  public RegisterBean() {
    this.newUserapp = new Userapp();
  }

  public void setNewUserapp(Userapp userapp) {
    this.newUserapp = userapp;
  }

  public Userapp getNewUserapp() {
    return this.newUserapp;
  }

  public String encriptarMD5(String password) throws Exception {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] passBytes = password.getBytes();
      md.reset();
      byte[] digested = md.digest(passBytes);
      StringBuffer sb = new StringBuffer();
      for(int i=0;i<digested.length;i++) sb.append(Integer.toHexString(0xff & digested[i]));
      return sb.toString();
    } catch (Exception ex) {
      throw new Exception("No se pudo encriptar la contraseña. " + ex.getMessage());
    }
  }

  public void register() {
    try {
      this.newUserapp.setUserappname(this.newUserapp.getUserappname().toLowerCase().trim());
      this.newUserapp.setEmail(this.newUserapp.getEmail().trim());      
      if(this.dataSource.contar("SELECT COUNT(userapp) FROM Userapp userapp WHERE userapp.userappname = '" + this.newUserapp.getUserappname() + "'") > 0)
        this.enviarMensaje(null,"El usuario ya existe","error");
      else {
    	 if(!(Pattern.compile("^[a-zA-Z0-9]+((_|\\-)?[a-zA-Z0-9]+)*(\\.([a-zA-Z0-9]+|([a-zA-Z0-9]+(_|\\-)?[a-zA-Z0-9]+)+))*@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|([a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)+))$").matcher(this.newUserapp.getEmail()).matches()))
          this.enviarMensaje(null,"El email no es válido","error");
        else {
          if(!this.confirmPassword.equals(this.newUserapp.getPassword()))
            this.enviarMensaje(null,"Las contraseñas no coinciden","error");
          else {
        	  if(this.archivoImagen != null) {
                  String extension = GestorArchivos.extension(archivoImagen.getFileName());
                  if(!extension.equals("")) {
                    Calendar calendario = Calendar.getInstance();
                    String nombreArchivo = "img-" + calendario.getTimeInMillis() +  (extension.equals("")? "" : "." + extension);
                    String rutaImgs = (((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/")).replace("\\", "/") + "/" + "resources/imgs/users";
                    GestorArchivos.crearArchivo(new File(rutaImgs + "/" + nombreArchivo),archivoImagen);
                    try {
                    	GestorArchivos.crearArchivo(new File("E:/WINDOWS_UNIDAD_ALMACENAMIENTO/ProyectosProgramas/EclipseWS/ComprensionLectora/WebContent/resources/imgs/users/" + nombreArchivo), archivoImagen);
                    } catch(Exception ex) {}
                    this.newUserapp.setPicture(nombreArchivo);
                  }        		  
        	  }
        	  this.newUserapp.setPassword(encriptarMD5(this.newUserapp.getPassword()));
        	  this.dataSource.insertar(this.newUserapp);
        	  this.dataSource.recargar(this.newUserapp);
        	  this.usuarioBean.setUserappSession(this.newUserapp);
        	  this.usuarioBean.startSessionData(this.newUserapp);
        	  this.newUserapp = null;
        	  this.mostrarPagina("inicio/inicioSistema");
          }
        }
      }
    } catch(Exception e) {
      this.enviarMensaje(null,"No se pudo registar el usuario. " + e.getMessage(),"fatal");
    }
    this.ejecutarJS2("ocultarCargando('bloqueoPgRegister')");
  }


}
