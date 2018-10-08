package mx.com.juan.camacho.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.PrimeFaces;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import javax.faces.component.UIComponent;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import mx.com.juan.camacho.entidades.MensajeRespuestaBean;
import mx.com.juan.camacho.hibernate.DataSource;
import mx.com.juan.camacho.controladores.login.UsuarioBean;
import mx.com.juan.camacho.utilitaria.GestorArchivos;
import mx.com.juan.camacho.utilitaria.Utilitaria;

@ManagedBean
public class GeneralBean implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value="#{dataSource}")
	protected DataSource dataSource;

	@ManagedProperty(value="#{usuarioBean}")
	protected UsuarioBean usuarioBean;

	public UsuarioBean getUsuarioBean() {
		return this.usuarioBean;
	}

	public void setUsuarioBean(UsuarioBean usuarioBean) {
		this.usuarioBean = usuarioBean;
	}

	protected DefaultStreamedContent archivoDescarga;

	protected static String faces = "faces/vista/";

	public StreamedContent getArchivoDescarga() {
            return archivoDescarga;
	}

	public String getMensajeSeleccion() {
            return "Seleccione...";
	}

	public String getMensajeSinRegistros() {
            return "Sin registros";
	}

	public DataSource getDataSource() {
            return this.dataSource;
	}

	public void setDataSource(DataSource dataSource) {
            this.dataSource = dataSource;
	}

	public int getCantidad() {
		try {
			return this.dataSource.contarQ("SELECT COUNT(*) FROM BLOG");
		} catch(Exception e) {
			System.out.println("FALLA2 " + e.getMessage());
			return 0;
		}
	}


    /**
     * Permite mostrar una respuesta a un
     * @param boton
     * @param mensaje
     * @param tipo
     */
    protected void enviarMensaje(UIComponent boton, String mensaje, String tipo) {
        FacesContext context = FacesContext.getCurrentInstance();
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
        context.addMessage(boton == null ? null : boton.getClientId(context), new FacesMessage(s, mensaje + ".", mensaje + "."));
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


    public void mostrarModal(String tituloMensaje, String mensaje, String tipo) {
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
    	RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(s, tituloMensaje, mensaje));
    }

    /**
     * Permite redireccionar a una página web
     * @param nombrePagina
     * @throws Exception
     */
    protected void mostrarPagina(String rutaPagina) throws Exception {
    	try {
    		FacesContext.getCurrentInstance().getExternalContext().redirect(getSaltosDirec() + (rutaPagina.contains(".xhtml") ? rutaPagina : (rutaPagina + ".xhtml")));
    	} catch(IOException ioe) {
    		throw new Exception("No se localiza la página " + rutaPagina + ".xhtml");
    	}
    }

    /**
     * Permite redireccionar a una página web especificando parámetros para ésta
     * @param nombrePagina
     * @param parametros
     * @throws Exception
     */
    protected void mostrarPagina(String rutaPagina, Map<String,String[]> parametros) throws Exception {
    	String urlParametros = Utilitaria.urlParametros(parametros);
    	try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(getSaltosDirec() + (rutaPagina.contains(".xhtml") ? rutaPagina : (rutaPagina + ".xhtml")) + (urlParametros.equals("") ? "" :  "?" + urlParametros));
    	} catch(IOException ioe) {
            throw new Exception("No se localiza la página " + rutaPagina + ".xhtml?" + urlParametros);
    	}
    }

    protected void mostrarPagina(String rutaPagina,String urlParametros) throws Exception {
    	try {
    		FacesContext.getCurrentInstance().getExternalContext().redirect(getSaltosDirec() + (rutaPagina.contains(".xhtml") ? rutaPagina : (rutaPagina + ".xhtml")) + (urlParametros.equals("") ? "" :  "?" + urlParametros));
    	} catch(IOException ioe) {
    		throw new Exception("No se localiza la página " + rutaPagina + ".xhtml?" + urlParametros);
    	}
    }





    protected String obtenerTransaccionQueSolicita() {
    	return ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getRequestURL().toString().replaceAll("(.*)" + faces + "(.*).xhtml(.*)","$2");
    }

    protected String obtenerParametrosTransaccionQueSolicita() {
            String parametros = "";
            try {
                    Map<String, String[]> mapa = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameterMap();
                    for (String name: mapa.keySet()){
                String[] valor = mapa.get(name);
                String valores = "";
                for(int i = 0; i < valor.length; i++)
                    valores += valor[i];
                parametros = parametros + name.toString() + "=" + valores + "&";
            }
                    if(!parametros.equals("")) parametros = parametros.substring(0,parametros.length() - 1);
            } catch(Exception e){}
            return parametros;
    }


    public String getSaltosDirec() {
        String url = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getRequestURL().toString();
        int numSaltosDirec = Utilitaria.dividir(url.substring(url.lastIndexOf(faces) + faces.length()),"/").length - 1;
        String salto = "";
        int i = 0;
        while(i++ < numSaltosDirec) salto += "../";
        return salto;
    }


    /**
     * Permite ejecutar una función javascript, sin parámetros, que se encuentre dentro de la página en la que actualmente se encuentra el usuario
     * @param funcion
     */
    protected void ejecutarJS(String funcion) {
        RequestContext.getCurrentInstance().execute(funcion + "()");
    }

    protected void ejecutarJS2(String funcion) {
    	PrimeFaces.current().executeScript(funcion);
    }

    /**
     * Permite ejecutar una función javascript, sin parámetros, que se encuentre dentro de la página en la que actualmente se encuentra el usuario
     * Cada parámetro está compuesto de 3 partes: [tipo][dos puntos][dato], por ejemplo "n::2", teniendo en cuenta que para cadenas se usa el tipo c.
     * Otro ejemplo para llamar a esta función correctamente es: "c::esta es una cadena"
     * @param funcion
     * @param arreglo
     */
    protected void ejecutarJS(String funcion, String... arreglo) {
        String parametros = "";
        String[] aux;
        for (int i = 0; i < arreglo.length; i++) {
            aux = Utilitaria.dividir(arreglo[i],"=>");
            parametros += aux[0].equals("c") ? "'" + aux[1] + "'," : aux[1] + ",";
        }
        parametros = parametros.substring(0, parametros.length() - 1);
        RequestContext.getCurrentInstance().execute(funcion + "(" + parametros + ")");
    }


    protected void expandirArbol(TreeNode nodo) {
        try{
        	nodo.setExpanded(true);
            List<TreeNode> nodos = nodo.getChildren();
            int t = nodos.size();
            for (int i = 0; i < t; i++)
            	expandirArbol(nodos.get(i));
        }catch(Exception e){}
    }

    public void actualizar(){
    }

    /**
     * Es útil cuando la cantidad de información a mostrar es demasiado larga y se sustituye parte de esta información
     * con puntos suspensivos.
     * @param cadena
     * @param numCaracteres
     * @return
     */
    public String subCadena(String cadena, int numCaracteres) {
        int t = cadena.length();
        if (t > numCaracteres + 3) return cadena.substring(0, numCaracteres) + "...";
        return cadena;
    }

    public void imprimirMensaje(String mensajeRecibido) {
    	try {

    	} catch(Exception e) {
    		System.out.println("ERROR AL CONSULTAR POLIZAS_AUTOS " + e.getMessage());
    	}
    	System.out.println("Mensaje recibido del cliente: " + mensajeRecibido);
    }

    /**
     * Devuelve el mapa de la sesión; donde se almacenan las variables de ésta.
     * @return
     */
    public Map<String,Object> getMapaSesion() {
    	return FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
    }

    /**
     * Construye un archivo para ser descargado por el usuario a partir de su localización
     * recibida como parámetro
     */
    protected DefaultStreamedContent getArchivo(String ruta) throws Exception {
    	try {
	        File archivo = new File(ruta);
	        return new DefaultStreamedContent(new FileInputStream(archivo), FacesContext.getCurrentInstance().getExternalContext().getMimeType(archivo.getName()), archivo.getName());
    	} catch(FileNotFoundException e) {
    		throw new Exception("El archivo (" + ruta + ") no fue encontrado.");
    	} catch(Exception e) {
    		throw new Exception("No se localiza el archivo solicitado (" + ruta + "). " + e.getMessage());
    	}
    }

    /**
     * Implementado para eliminar los archivos alojados en la carpeta exports, cuyo objetivo es
     * almacenar los archivos descargados por los clientes en forma temporal. Por esta raz�n se
     * eliminan los archivos desde una petici�n autom�tica originada por JavaScript una vez
     * el archivo es entregado al cliente.
     */
    public void eliminarArchivoDescarga() {
    	try {
    		new File(GestorArchivos.exports + this.archivoDescarga.getName()).delete();
    	} catch(Exception e) {
    	}
    }

	protected void descargar(String nombreArchivo, String transaccion, Map<String,String[]> parametros) throws Exception {
		try {
			this.archivoDescarga = new DefaultStreamedContent(new FileInputStream(GestorArchivos.exports + nombreArchivo),null,nombreArchivo);
		} catch(Exception e) {
			this.mostrarPagina(transaccion,parametros);
			throw new Exception("Archivo no encontrado (" + GestorArchivos.exports + nombreArchivo +"). " + e.getMessage());
		}
	}

	protected void descargar(String nombreArchivo, String rutaArchivo, String transaccion, Map<String,String[]> parametros) throws Exception {
		try {
			this.archivoDescarga = new DefaultStreamedContent(new FileInputStream(rutaArchivo + nombreArchivo),null,nombreArchivo);
		} catch(Exception e) {
			this.mostrarPagina(transaccion,parametros);
			throw new Exception("Archivo no encontrado. " + e.getMessage());
		}
	}

	protected void eliminarArchivo(String nombreArchivo,String rutaArchivo) throws Exception {
		try {
			File archivo = new File(rutaArchivo + nombreArchivo);
			if(!archivo.exists()) throw new Exception("No se encuentra el archivo");
			if(!archivo.delete()) throw new Exception("No se cuenta con los permisos necesarios para eliminar el archivo o se encuentra en uso");
		} catch(Exception e) {
			throw new Exception("No se pudo eliminar el archivo. " + e.getMessage());
		}
	}

	public String tipoArchivo(String nombreArchivo) {
		switch(GestorArchivos.extension(nombreArchivo).toLowerCase()) {
			case "doc":
			case "docx":
				return "WORD";
			case "xls":
			case "xlsx":
				return "EXCEL";
			case "pdf":
				return "PDF";
			case "png":
			case "jpg":
			case "tiff":
			case "tif":
			case "gif":
			case "bmp":
				return "IMG";
			case "wav":
			case "mp3":
			case "ogg":
			case "midi":
				return "AUDIO";
		}
		return "DESCONOCIDO";
	}

	public String formatearFecha(Date fecha) {
		try {
			return Utilitaria.formatearDate(fecha,"dd/MM/yyyy HH:mm");	
		} catch(Exception e) {
			return null;
		}
		
	}

	public String precio(BigDecimal numero) {
		return Utilitaria.precio(numero.doubleValue(),2);
	}

	public String precio(Double numero) {
		return Utilitaria.precio(numero,2);
	}

	public String precio(double numero) {
		return Utilitaria.precio(numero,2);
	}

	public String primeraMayus(String cadena) {
		return Utilitaria.primeraMayus(cadena);
	}

	protected String getDireccionIP() {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		HttpServletRequest xc = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		String dirIP = request.getHeader("X-FORWARDED-FOR");
		if (dirIP == null) dirIP = request.getRemoteAddr();
		return dirIP;
	}
	
	public String tiempoEntre(Date a, Date b) {
		try {
			return Utilitaria.tiempoDiferencia(Utilitaria.diferencia(a,b));	
		} catch(Exception e) {
			return null;
		}
	}

	public List barajar(List lista) {
		Collections.shuffle(lista);
		return lista;
	}
	
	public List barajar(Set set) {
		List lista = new ArrayList(set);
		Collections.shuffle(lista);
		return lista;
	}
	
	public String concatenar(int id, String content) {
		return content.replaceFirst("\\<div style=\"(.*)\"\\>\\<font face","<div style=\"$1\"><span>" + Integer.toString(id) + ". </span>\\<font face");
	}
}
