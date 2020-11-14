import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Formatter;
import java.util.FormatterClosedException;
import java.util.NoSuchElementException;
/**
 * CLase Autentificador
 */
public class AutentificaUsuario {
	
	private final String USERS = "users.dat";
	private String exito;

	//Constructor
	public AutentificaUsuario(String log, String clave){
		exito = "no";
	}
	/**
	 * Autentifica un usuario existente
	 * @param log
	 * @param clave
	 * @return
	 */
	public String Autenticar(String log, String clave) {
		BufferedReader archivo = null;
		try{
			String lectura_linea;
			archivo = new BufferedReader(new FileReader(USERS));
			while ((lectura_linea = archivo.readLine()) != null) {
				String[] palabra = lectura_linea.split(" ");
				if(log.equals(palabra[0]) && clave.equals(palabra[1])){
					exito = "si";
					break;
				} 				
			}
			if (exito=="no"){
				System.out.println("usuario " + log + " no existente");
				exito = "no";
			} else {
				System.out.println("hola " + log);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (archivo != null)archivo.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		System.out.println(exito);
		return exito;
	}

	/**
	 * Registrar nuevo Usuario
	 * @param log
	 * @param clave
	 */
	public void Registrar(String log, String clave) {
		Formatter archivo = null;
		try {				
			archivo = new Formatter(new BufferedWriter(new FileWriter(USERS, true)));
			try{
				archivo.format("%s %s\n", log, clave);
			} catch (FormatterClosedException formatterClosedException) {
				System.err.println("error escribiendo en el archivo");
				return;
			} catch (NoSuchElementException elementException){
				System.err.println("entrada invalida");
			}
		}catch (IOException ioException){
			ioException.printStackTrace();
		} finally{
			if(archivo != null){
				archivo.close();
				System.out.println("cerrando...");
			}
		}
	}
}


