import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Clase Hilo Cliente, atiende peticiones
 */
class HiloCliente extends Thread {
    private final String SERVER_PATH = "./ServerFiles";
    private Socket cliente;
    private Bitacora reg = new Bitacora();

    private InputStream flujoentrada;
	private BufferedReader entrada;
	private OutputStream flujosalida;
	private PrintStream salida;
	private Manejador flujos;
	private boolean finalizar = false;

	private  AutentificaUsuario usuario;
	private String log;
	private String clave;
	
    //static String entrada;
    static String existe;
    static String mensaje;
	static String[] palabra;
	static String nombre_archivo_subir;
	static String nombre_archivo_bajar;
    static int archivo_len;
    
	/**
     * Constructor 
     * @param socket_cliente
     */
	HiloCliente(Socket socket_cliente) {
		this.cliente = socket_cliente;
		System.out.println("Accepted connection : " + socket_cliente);
		try{
			this.flujoentrada = cliente.getInputStream();
			this.entrada = new BufferedReader(new InputStreamReader(this.flujoentrada));
			this.flujosalida = cliente.getOutputStream();
			this.salida = new PrintStream (this.flujosalida);
			this.flujos = new Manejador();
		} catch (Exception e) {
            e.printStackTrace();
        }
	}
	/**
     * Run
     */
	public void run() {
		try{			
			do{
                flujos.enviar_mensaje("conexion aceptada", salida);
                // Analisa la coneccion
				mensaje = entrada.readLine();
				palabra = mensaje.split(" ");
				this.log = palabra[1];
                this.clave = palabra[2];

                // Autentifica el usuario
                usuario = new AutentificaUsuario(this.log, this.clave);
                
				existe = this.usuario.Autenticar(this.log, this.clave);
				if(palabra[0].equals("1")){
                    if(existe.equals("si")){// EL USUARIO SI EXISTE
                        this.reg.crear_bitacora(this.log);
                        this.reg.escribir_bitacora(this.log, "autenticado");
                        flujos.enviar_mensaje("Usuario Autenticado. Hola " + this.log, salida);
                        while(!finalizar){
                            System.out.println("esperando comando");
                            mensaje = entrada.readLine();
                            while(true){
                                if(mensaje.equals("ls")){
                                    System.out.println("listando directorio");
                                    this.reg.escribir_bitacora(this.log, "listar directorio servidor");
                                    flujos.enviar_mensaje(listDir(), salida);
                                    break;
                                } else if (mensaje.equals("down")){ // Descargar Archivo
                                    nombre_archivo_subir = entrada.readLine(); // espera el nombre del archivo
                                    this.reg.escribir_bitacora(this.log, "bajando archivo: " + nombre_archivo_subir);
                                    System.out.println("enviando archivo...");
                                    this.flujos.send(cliente, nombre_archivo_subir, salida);
                                    break;
                                } else if (mensaje.equals("up")){ // Cargar Archivo
                                    nombre_archivo_bajar = entrada.readLine(); // espera el nombre del archivo
                                    reg.escribir_bitacora(this.log, "subiendo archivo: " + nombre_archivo_bajar);
                                    System.out.println("Resiviendo...");
                                    mensaje = entrada.readLine();
                                    archivo_len = Integer.parseInt(mensaje);
                                    System.out.println("Size archivo: " + mensaje);
                                    flujos.receiveFile(cliente, archivo_len, nombre_archivo_bajar);
                                    break;
                                } else if (mensaje.equals("fin")){ //Terminar coneccion
                                    System.out.println("cerrando socket del cliente");
                                    finalizar = true;
                                    break;    
                                }
                            }
                        }
                    } else {
                    flujos.enviar_mensaje("Usuario no existente", salida);
                    break;
                    }
				} else { // No existe
                    if(existe.equals("no")){
                        flujos.enviar_mensaje("registrando usuario " + this.log, salida);
                        usuario.Registrar(this.log, this.clave);
                    } else {
                        flujos.enviar_mensaje("Usuario ya existente. Hola " + this.log, salida); // registrando usuario ya existente
                    }
				}
				System.out.println("finalizando");
				this.flujosalida.close();
				this.salida.close();
				this.flujoentrada.close();
				this.entrada.close();
				this.cliente.close();
			} while(true);
		} catch (Exception e){e.printStackTrace();}
		System.out.println("Sale de hilo");	
		Thread.currentThread().interrupt();//preserve the message
		return;
    }
    /**
     * Funcion que lista el directorio del servidor
     * @return
     */
    private String listDir () {
        File folder = new File(SERVER_PATH);
        if (!folder.exists()) { // Si no existe el directorio
            folder.mkdir();
        }
		File[] listOfFiles = folder.listFiles();
		String str = "";
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		        System.out.println(file.getName());
		    	str = str + file.getName() + "&";
		    }
		}
		return str;
	}
} 
