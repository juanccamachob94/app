
package mx.com.juan.camacho.utilitaria;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Date;
import java.util.HashMap;

public class Utilitaria {

    /**
     * Cumple la misma función que split pero 4 veces más eficiente
     * @param cadena
     * @param c
     * @return
     */
    public static String[] dividir(String cadena, String c) {
        cadena += c;
        int i, k, l;
        int t = cadena.length();
        int tc = c.length();
        char cr = c.charAt(0);
        String aux;
        int u = 0;
        int j = 0;
        for (i = 0; i < t; i++)
            if (cadena.charAt(i) == cr) {
                k = i;
                for (l = 0; l < tc; l++) {
                    if (c.charAt(l) == cadena.charAt(i)) i++;
                    if (i == t) break;
                }
                if (i - k == tc) {
                    i--;
                    j++;
                } else i = k;
            }
        String[] resultado = new String[j];
        j = 0;
        for (i = 0; i < t; i++)
            if (cadena.charAt(i) == cr) {
                k = i;
                for (l = 0; l < tc; l++) {
                    if (c.charAt(l) == cadena.charAt(i)) i++;
                    if (i == t) break;
                }
                if (i - k == tc) {
                    aux = "";
                    for (l = u; l < k; l++)
                        aux += cadena.charAt(l) + "";
                    resultado[j++] = aux;
                    u = i;
                    i--;
                } else i = k;
            }
        return resultado;
    }

	public static String urlParametros(Map<String,String[]> parametros) {
		String urlParametros = "";
		for (Map.Entry<String, String[]> parametro : parametros.entrySet())
			urlParametros += "&" + parametro.getKey() + "=" + concatenarArreglo(parametro.getValue());
		return urlParametros.substring(1, urlParametros.length());
	}

	public static Map<String,String[]> mapaParametros(String parametros) {
		Map<String,String[]> mapa = new HashMap<String,String[]>();
		String[] parametro;
		String[] arregloParametros = dividir(parametros,"&");
		for(int i = 0; i < arregloParametros.length; i++) {
			parametro = dividir(arregloParametros[i],"=");
			mapa.put(parametro[0],dividir(parametro[1],","));
		}
		return mapa;
	}

	public static String concatenarArreglo(String[] arreglo) {
		String cadena = "";
		for(int i = 0; i < arreglo.length; i++) cadena = cadena + "," + arreglo[i];
		return cadena.substring(1,arreglo.length);
	}



	public static Date cadenaAFecha(String fechaString, String formato) {
		try {
			return new SimpleDateFormat(formato).parse(fechaString);
		} catch(ParseException pe) {
			return null;
		}
	}

    /**
     * Permite dar formato a la fecha. Generalmente el formato es dd/M/yyyy como par�metro
     * @param date
     * @param formato
     * @return
     */
    public static String formatearDate(Date date, String formato) {
        return new SimpleDateFormat(formato).format(new Timestamp(date.getTime()));
    }

    /**
     * Devuelve la misma cadena con la primera en may�sula y el resto en minúscula
     * @param cadena
     * @return
     */
    public static String primeraMayus(String cadena) {
    	return cadena.substring(0, 1).toUpperCase() + cadena.substring(1).toLowerCase();
    }


    public static Double StringADouble(String cadena) {
        try {
            return Double.parseDouble(cadena);
        } catch (Exception e) {
            return null;
        }
    }

    public static double StringAdouble(String cadena) {
        try {
            return Double.parseDouble(cadena);
        } catch (Exception e) {
            return 0;
        }
    }

	public static String precio(Double precio, int numDecimales) {
		String decimales = "";
		for(int i = 0; i < numDecimales; i++) decimales += "0";
		if(numDecimales > 0) decimales = "." + decimales;
        return new DecimalFormat("###,###,###" + decimales).format(precio);
    }

	@SuppressWarnings("deprecation")
	public static String obtenerNombreMes(Date fecha) {
		switch(fecha.getMonth()) {
			case 0: return "Enero";
			case 1: return "Febrero";
			case 2: return "Marzo";
			case 3: return "Abril";
			case 4: return "Mayo";
			case 5: return "Junio";
			case 6: return "Julio";
			case 7: return "Agosto";
			case 8: return "Septiembre";
			case 9: return "Octubre";
			case 10: return "Noviembre";
			case 11: return "Diciembre";
		}
		return null;
	}


  @SuppressWarnings("deprecation")
	public static String obtenerNombreMes(int id) {
		switch(id) {
			case 0: return "Enero";
			case 1: return "Febrero";
			case 2: return "Marzo";
			case 3: return "Abril";
			case 4: return "Mayo";
			case 5: return "Junio";
			case 6: return "Julio";
			case 7: return "Agosto";
			case 8: return "Septiembre";
			case 9: return "Octubre";
			case 10: return "Noviembre";
			case 11: return "Diciembre";
		}
		return null;
	}
  
  public static long diferencia(Date da, Date db) {
	    long diffMSec = 0;
	    diffMSec = db.getTime() - da.getTime();
	    return diffMSec;
	}
  
	public static String tiempoDiferencia(long diffMSec) {
	    int left = 0;
	    int ss = 0;
	    int mm = 0;
	    int hh = 0;
	    int dd = 0;
	    left = (int) (diffMSec / 1000);
	    ss = left % 60;
	    left = (int) left / 60;
	    if (left > 0) {
	        mm = left % 60;
	        left = (int) left / 60;
	        if (left > 0) {
	            hh = left % 24;
	            left = (int) left / 24;
	            if (left > 0) {
	                dd = left;
	            }
	        }
	    }
	    String diff = Integer.toString(dd) + " días, " + Integer.toString(hh) + " hora(s), "
	            + Integer.toString(mm) + " minuto(s) y " + Integer.toString(ss) + " segundo(s)";
	    return diff;

	}

}
