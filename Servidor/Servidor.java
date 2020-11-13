import java.io.*;
import java.net.*;
import java.util.ArrayList;


/**
 * Clase principal del Servidor
 */
public class Servidor {
	public static int PUERTO = 5612;
	// Lista de Usuarios
	private ArrayList<Usuario> usuarios = new ArrayList<Usuario>();

	/*
	 * Main
	 */
	public static void main(String[] args) {
		// Variables auxiliares
		Servidor aux = new Servidor();

		// Lee la base de datos de los usuarios
		LectorArchivo lec = new LectorArchivo();

		// Socket Servidor
		ServerSocket socketServer = null;
		// Socket Cliente
		Socket socketDespachador = null;

		// Lee la base de datos de los usuarios
		// Si no exsite, entonces sale
		if ((aux.usuarios = lec.procesaArchivo("usuarios.dat")) == null) {
			System.out.println("Error al leer usuarios.dat");
			System.exit(-1);
		}

		// imprime a los usuarios
		aux.imprimeUsuarios();

		// Intenta la coneccion con el socker servidor
		try {
			// Puerto
			socketServer = new ServerSocket(PUERTO);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		// Inicia el servicio de escucha		
		System.out.println("Escuchando: " + socketServer);
		
		// Inicia el proceso de atender peticiones
		while(true){
			try {
				// Se crea un socket despachador
				socketDespachador = socketServer.accept();
				System.out.println("Nueva conexion aceptada: " + socketDespachador);
				// Se crea un Hilo para esa peticion
				new GestorPeticion(socketDespachador,aux.usuarios).start();

				//Volvemos a esperar
				socketDespachador = null;
			} catch (IOException e) {
				e.printStackTrace();
				try {
					socketServer.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				System.exit(-1);
			}
		}	
	}

	/**
	 * funcion que imprime usuarios de la Base de datos
	 */
	private void imprimeUsuarios(){
		System.out.println("Usuarios en el Servidor\n<------------------------>");
		for (Usuario usuario : usuarios) {
			System.out.println(usuario.toString());
		}
		System.out.println("<------------------------>\n");
	}
}


