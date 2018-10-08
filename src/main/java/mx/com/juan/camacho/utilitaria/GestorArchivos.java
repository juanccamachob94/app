package mx.com.juan.camacho.utilitaria;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import org.primefaces.model.UploadedFile;


public class GestorArchivos {
	public static final String path = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("");
	public static final String imagenes = path + carpeta("resources","imgs");
	public static final String exports = path + carpeta("exports");

	public static String carpeta(String... nombres) {
		String ruta = "";
		for(int i = 0; i < nombres.length; i++)
			ruta += nombres[i] + File.separator;
		return ruta;
	}

	public static String extension(String nombreArchivo) {
		String[] partesNombre = Utilitaria.dividir(nombreArchivo,".");
		if(partesNombre.length < 2) return "";
		return partesNombre[partesNombre.length - 1];
	}

	/**
     * Crea un archivo en disco en la ruta de File con la información de UploadedFile
     * @param archivo
     * @param archivoSubido
     * @throws Exception
     */
    public static void crearArchivo(File archivo, UploadedFile archivoSubido) throws Exception {
        try {
            InputStream in = archivoSubido.getInputstream();
            OutputStream out = new FileOutputStream(archivo);
            int reader = 0;
            byte[] bytes = new byte[(int) archivoSubido.getSize()];
            while ((reader = in.read(bytes)) != -1)
                out.write(bytes, 0, reader);
            in.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            throw new Exception("Error al crear el archivo " + archivoSubido.getFileName() + ". " + e.getMessage());
        }
    }
}
