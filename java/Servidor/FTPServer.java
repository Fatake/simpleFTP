import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Clase FTPServer
 */
public class FTPServer {
	/**
	 * Main
	 * @param arg
	 */
	public static void main (String[] arg) {
		ServerSocket socket_servidor;
		Socket socket_cliente;
		try{			
			int puerto = 5972;
			socket_servidor = new ServerSocket(puerto);
			do {
				try {
					socket_cliente = socket_servidor.accept();
				} catch (IOException e) {
					socket_servidor.close();
					break;
				}
				Thread t = new HiloCliente(socket_cliente);
				t.start();
			}while(true);

		} catch (Exception e) {
			System.out.println("Interrupcion del hilo principal");
		}
		System.out.println("Saliendo del servidor\n\n");
	}
}










