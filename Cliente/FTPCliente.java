import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;


/**
 * Clase FTP CLiente
 */
public class FTPCliente {
	private final String CLIENT_PAHT = "./Cliente";

	public FTPCliente(){

	};
	/**
	 * Main FTP Client
	 * @param args
	 */
	public static void main(String args[]) {
		String log, clave,comando , direccion = args[0];
		String mensaje, operacion = null;
		String fileNameUP, fileNameDown;
		FTPCliente c = new FTPCliente();
		int fileLen, puerto = Integer.parseInt(args[1]);
		

		try {
			Socket conexion = new Socket(direccion,puerto);
			InputStream flujoentrada = conexion.getInputStream();
			BufferedReader entrada = new BufferedReader(new InputStreamReader(flujoentrada));
			OutputStream flujosalida = conexion.getOutputStream();
			PrintStream salida = new PrintStream (flujosalida);

			mensaje = entrada.readLine();
			boolean bandera_operacion;
			bandera_operacion = true;

			while(bandera_operacion){
				operacion = System.console().readLine("Ingrese\n (1) autenticar usuario \n (2) para registar usuario\n$ ");
				if(operacion.equals("1")){
					System.out.println("autenticar usuario");
					bandera_operacion = false;
				} else if (operacion.equals("2")) {
					System.out.println("registrar usuario");
					bandera_operacion= false;
				} else {
					System.out.println("error");
					bandera_operacion = true;
				}
			}

			log = System.console().readLine("Usuario\n$ ");
			clave = System.console().readLine("Password\n$ ");
			mensaje = operacion + " " + log + " " + clave;
			salida.println(mensaje);
			salida.flush();
			mensaje = entrada.readLine();
			System.out.println(mensaje);
			
			if(!"Usuario no existente".equals(mensaje)){  // si el usuario si existe ejecutar codigo para transferir archivos
				boolean finalizar = false;
				while(finalizar == false)	{// Mientras no finalice
					comando = System.console().readLine("\nIntroduzca un comando: ls, ll, up, down, fin, (h para ayuda) -> ");
					
					switch(comando.toLowerCase()){
						case "ls":						
							System.out.println("\nARCHIVOS DEL SERVIDOR:");
							salida.println(comando);
							salida.flush();
							mensaje = entrada.readLine();
							c.listDirServer(mensaje);
						break;

						case "ll":
							System.out.println("\nARCHIVOS LOCALES:");
							c.listDirClient();
						break;

						case "up":
							System.out.println("\nListo para subir archivo:");
							salida.println(comando);
							salida.flush();
							fileNameUP = System.console().readLine("Seleccione archivo a subir: ");
							salida.println(fileNameUP);
							salida.flush();
							c.send(conexion, fileNameUP, salida);
						break;

						case "down":
							System.out.println("\nlisto para recibir archivo");
							salida.println(comando);
							salida.flush();
							fileNameDown = System.console().readLine("Seleccione archivo a descargar: ");
							salida.println(fileNameDown);
							salida.flush();
							mensaje = entrada.readLine();
							fileLen = Integer.parseInt(mensaje);
							System.out.println("Size File: " + mensaje);
							c.receiveFile(conexion, fileLen, fileNameDown);
						break;

						case "h":
							System.out.println("\nAyuda para comandos disponibles:\nls: listar directorio de archivos del servidor\nll: listar directorio de archivos locales\n"
							+ "up: subir archivo al servidor\ndown: bajar archivo desde el servidor\nfin: salir del programa");
						break;

						case "fin":
							System.out.println("\nFinalizando...uwu.");
							salida.println(comando);
							salida.flush();
							finalizar = true;
						break;

						default:
						System.out.println("\nSeleccione un comando:");
					}
				}
			}
			conexion.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
 
 	public void send(Socket socket_cliente, String archivo, PrintStream salida) throws Exception {
	    int fileLen = 0;
		FileInputStream fis = null;
	    BufferedInputStream bis = null;
	    OutputStream os = null;
		os = socket_cliente.getOutputStream();		
	    File myFile = new File(CLIENT_PAHT + archivo); // ENVIAR ARCHIVO
	    byte[] mybytearray = new byte[(int) myFile.length()];
	    fis = new FileInputStream(myFile);
	    bis = new BufferedInputStream(fis);
	    bis.read(mybytearray, 0, mybytearray.length);
	    os = socket_cliente.getOutputStream();
	    System.out.println("Sending...");
	    System.out.println("Sending " + "(" + mybytearray.length + " bytes)");
	    fileLen = mybytearray.length;
	    salida.println(fileLen);
	    salida.flush();
	    os.write(mybytearray, 0, mybytearray.length);
		os.flush();
		bis.close();
	    System.out.println("Done.");
	}

	public void receiveFile(Socket socket_cliente, int filesize, String nombre_archivo) throws Exception {
	    int bytesRead;
	    int current = 0;

	    FileOutputStream fos = null;
	    BufferedOutputStream bos = null;
	    byte[] mybytearray = new byte[filesize];
	    InputStream is = socket_cliente.getInputStream();
	    fos = new FileOutputStream(CLIENT_PAHT + nombre_archivo);
	    bos = new BufferedOutputStream(fos);
	    bytesRead = is.read(mybytearray, 0, mybytearray.length);
	    current = bytesRead;
	    do {
	        bytesRead =
	           is.read(mybytearray, current, (mybytearray.length-current));
	        if(bytesRead >= 0) current += bytesRead;
	    } while(current < filesize);
	    bos.write(mybytearray, 0 , current);
		bos.flush();
		bos.close();
	    System.out.println("File downloaded (" + current + " bytes read)");

	}

	public void listDirClient () {
		File folder = new File(CLIENT_PAHT);
		if (!folder.exists()) { // Si no existe el path del cliente
			folder.mkdir();
		}
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		        System.out.println(file.getName());
		    }
		}
	}

	public void listDirServer(String s){
		String[] arr = s.split("&");
		for ( String ss : arr) {
			System.out.println(ss);
		}		
	}
}


