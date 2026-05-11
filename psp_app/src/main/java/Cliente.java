import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Cliente {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static void main(String[] args) {

        Scanner teclado = new Scanner(System.in);

        String host = "127.0.0.1";
        int puerto = 5000;

        System.out.print("Introduce tu nombre: ");
        String nombre = teclado.nextLine();

        String nombreArchivo = "sesion_" + nombre + ".txt";

        try (
                Socket socket = new Socket(host, puerto);
                DataOutputStream salida = new DataOutputStream(socket.getOutputStream());
                DataInputStream entrada = new DataInputStream(socket.getInputStream());
                BufferedWriter escritor = new BufferedWriter(new FileWriter(nombreArchivo, true))
        ) {
            escribirLinea(escritor, "INICIO DE SESIÓN DE DENUNCIA");
            escribirLinea(escritor, "Usuario: " + nombre);
            escribirLinea(escritor, "---------------------------------");

            salida.writeUTF(nombre);

            String bienvenida = entrada.readUTF();
            System.out.println("Servidor: " + bienvenida);
            escribirLinea(escritor, "SERVIDOR: " + bienvenida);

            String mensaje = "";

            while (!mensaje.equalsIgnoreCase("salir")) {
                System.out.print("Tú: ");
                mensaje = teclado.nextLine();

                salida.writeUTF(mensaje);
                escribirLinea(escritor, "USUARIO: " + mensaje);

                String respuesta = entrada.readUTF();
                System.out.println("Servidor: " + respuesta);
                escribirLinea(escritor, "SERVIDOR: " + respuesta);
            }

            escribirLinea(escritor, "FIN DE SESIÓN");
            escribirLinea(escritor, "----------------------------------");

            System.out.println("Comprobante guardado en: " + nombreArchivo);

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void escribirLinea(BufferedWriter escritor, String texto) throws IOException {
        escritor.write("[" + obtenerFechaHora() + "] " + texto);
        escritor.newLine();
        escritor.flush();
    }

    private static String obtenerFechaHora() {
        return LocalDateTime.now().format(FORMATO_FECHA);
    }
}