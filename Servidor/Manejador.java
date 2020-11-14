import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
/**
 * Clase manejador, que envia y resive mensajes
 */
public class Manejador{
	private final String SERVER_PATH = "./ServerFiles";
	public Manejador(){};
	
	public void enviar_mensaje(String mensaje, PrintStream salida) {
		salida.println(mensaje);
		salida.flush();
	}
	
	
	@SuppressWarnings("resource")
	public void send(Socket socket_cliente, String archivo, PrintStream salida) throws Exception {
		int archivo_len = 0;
		FileInputStream fis = null;
	    BufferedInputStream bis = null;
	    OutputStream os = null;
		os = socket_cliente.getOutputStream();	
		try {	
		    File myFile = new File(SERVER_PATH + archivo); // ENVIAR ARCHIVO
		    byte[] mybytearray = new byte[(int) myFile.length()];
		    fis = new FileInputStream(myFile);
		    bis = new BufferedInputStream(fis);
		    bis.read(mybytearray, 0, mybytearray.length);
		    os = socket_cliente.getOutputStream();
		    System.out.println("Sending...");
		    System.out.println("Sending " + SERVER_PATH+"/ArchivoServidor.txt" + "(" + mybytearray.length + " bytes)");
		    archivo_len = mybytearray.length;
		    salida.println(archivo_len);
		    salida.flush();
		    os.write(mybytearray, 0, mybytearray.length);
		    os.flush();
	    } finally {
	          if (bis != null) bis.close();
	          if (os != null) os.close();
	        }
	    //os.close();
	    System.out.println("Done.");
	}

	@SuppressWarnings("resource")
	public void receiveFile(Socket socket_cliente, int filesize, String nombre_archivo) throws Exception {
	    int bytesRead;
	    int current = 0;
	    FileOutputStream fos = null;
	    BufferedOutputStream bos = null;
	    byte[] mybytearray = new byte[filesize];
	    InputStream is = socket_cliente.getInputStream();
	    fos = new FileOutputStream(SERVER_PATH + nombre_archivo);
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
	    //bos.close();
	    System.out.println("File " + "prueba.txt" + " downloaded (" + current + " bytes read)");
	}
}
