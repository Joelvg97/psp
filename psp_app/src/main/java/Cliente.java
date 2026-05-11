import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {

    public static void main(String[] args) {

        Scanner teclado = new Scanner(System.in);

        String host = "127.0.0.1";
        int puerto = 5000;

        System.out.print("Introduce tu nombre: ");
        String nombre = teclado.nextLine();

        try (
                Socket socket = new Socket(host, puerto);
                DataOutputStream salida = new DataOutputStream(socket.getOutputStream());
                DataInputStream entrada = new DataInputStream(socket.getInputStream())
        ) {
            salida.writeUTF(nombre);

            String bienvenida = entrada.readUTF();
            System.out.println("Servidor: " + bienvenida);

            String mensaje = "";

            while (!mensaje.equalsIgnoreCase("salir")) {
                System.out.print("Tú: ");
                mensaje = teclado.nextLine();

                salida.writeUTF(mensaje);

                String respuesta = entrada.readUTF();
                System.out.println("Servidor: " + respuesta);
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}