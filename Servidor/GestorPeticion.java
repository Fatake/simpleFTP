import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;

/*
 * Clase Gestor de peticiones
 * Se crea un hilo por cada enlace nuevo
 */
public class GestorPeticion extends Thread {
	private ArrayList<Usuario> usuarios = new ArrayList<Usuario>();
	BufferedReader entrada = null;
	PrintWriter salida = null;
	Socket socket;

	// Constructor sin nombre
	public GestorPeticion(Socket socket,ArrayList<Usuario> usuarios){
		this.socket = socket;
		this.usuarios = usuarios;
	}

	// Constructor con nombre
	public GestorPeticion(String nombre, Socket socket,ArrayList<Usuario> usuarios){
		super(nombre);
		this.socket = socket;
		this.usuarios = usuarios;
	}

	/*
	 * Instruccion de ejecucion
	 */
	public void run(){
		System.out.print("\033[H\033[2J");  
		System.out.flush();
		System.out.println("\n\n<----------------->"); 
		Usuario user = null;
		int indexUser = 0;
		String textoAleatorio = "";
		String textoMezclado  = "";
		try{
			entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			salida = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
			while (true){
				//Lee lo que se reciba en el Socket
				String str = desencriptar(entrada.readLine());
				System.out.println("-> " + str);
				//Separa lo que se lee
				String aux[] = str.split(",");
				if (aux[0].startsWith("us")) {//Recibe Usuaro
					indexUser = buscaUsuario(aux[1]);

					if (indexUser == -1) {//Si no se encuentra el usuario
						System.out.println("Usuario no Encontrado");
						salida.println(encriptar("un,"+"null"));

						System.out.println("Cerrando Coneccion");
						break;
					}else{//Si lo encuentra
						Mezclador mes = new Mezclador();
						user = usuarios.get(indexUser);
						System.out.println("Usuario Encontrado");

						//Gerenando texto Aleatorio
						textoAleatorio = generaTexto();
						System.out.println("Texto Generado:\n"+textoAleatorio+"\n");
						salida.println(encriptar("ms,"+textoAleatorio));

						//Texto aletorio mezclado
						textoMezclado = mes.mezcla(textoAleatorio, user.getPass());
						System.out.println("Texto Mezclado:\n"+textoMezclado+"\n");
					}
				}else if (aux[0].startsWith("md")) {//Recibe md
					MD5 gen = new MD5();
					String md5cli = aux[1];
					String md5ser = gen.getMD5(textoMezclado);
					System.out.println("MD5Cli:\n"+md5cli+"\n");
					System.out.println("MD5Ser:\n"+md5ser+"\n");
					
					if (md5ser.equals(md5cli)) {
						System.out.println("Contraseña Correcta");
						salida.println(encriptar("cn"));
						// Si la contraseña es correcta
						// SE cambia el nombre del hilo al del usuario
						try {
							this.setName(user.getName());
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}else{
						System.out.println("Contraseña Incorrecta");
						salida.println(encriptar("nn"));
					}
				}

				if(str.equals("fn")){
					System.out.println("Cerrando Coneccion");
					break;
				}
			}
			
			salida.close();
			entrada.close();
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	/*
	 * Encriptador
	 */
	private String encriptar(String s) throws UnsupportedEncodingException{
        return Base64.getEncoder().encodeToString(s.getBytes("utf-8"));
    }
	
	/*
	 * Desincriptador
	 */
    private String desencriptar(String s) throws UnsupportedEncodingException{
        byte[] decode = Base64.getDecoder().decode(s.getBytes());
        return new String(decode, "utf-8");
    }

	/*
	 * Busca ujsuarios en Base de Datos
	 */
	private int buscaUsuario(String userName){
		int posicion = -1;
		for (int i = 0; i < usuarios.size(); i++) {
			Usuario user = usuarios.get(i);
			String nombre = user.getName();
			if (nombre.equals(userName)) {
				posicion = i;
				break;
			}
		}
		return posicion;
	}

	/*
	 * Generador de texto aleatorio
	 */
	private String generaTexto(){
		SecureRandom random = new SecureRandom();
 		String text = new BigInteger(586, random).toString(32);
 		return text;
	}
}