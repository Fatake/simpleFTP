import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.FormatterClosedException;
import java.util.NoSuchElementException;

public class Bitacora {
    public String bitacoranombre;

    public Bitacora(){ };

	public void crear_bitacora(String log){
		Formatter archivo = null;
		try {				
			System.out.println("creando bitacora");
			SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy_hhmmss");
			Date curDate = new Date();
			String strDate = sdf.format(curDate);
			bitacoranombre = "./bitacora/" + log + "_bitacora_" + strDate + ".txt";
			archivo = new Formatter(new BufferedWriter(new FileWriter(bitacoranombre, true)));
		} catch (IOException ioException){
			ioException.printStackTrace();
		} finally{
			if(archivo != null){
				archivo.close();
			}
		}
	}
	
	public void escribir_bitacora(String log, String operacion) {
		Formatter archivo = null;
		try{
			archivo = new Formatter(new BufferedWriter(new FileWriter(bitacoranombre, true)));
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy_hhmmss");
				Date curDate = new Date();
				archivo.format("%s %s %s\n", log, operacion, curDate);
			}
			catch (FormatterClosedException formatterClosedException) {
				System.err.println("error escribiendo en el archivo");
				return;
			}
			catch (NoSuchElementException elementException){
				System.err.println("entrada invalida");
			}
		}
		catch (IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			if(archivo != null){
				archivo.close();
				//System.out.println("cerrando...");
			}
		}
	}
}
				