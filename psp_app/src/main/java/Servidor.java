import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    public static void main(String[] args) {
        int puerto = 5000;

        try (ServerSocket servidor = new ServerSocket(puerto)) {

            System.out.println(" SERVIDOR CHAT DENUNCIAS BULLYING");
            System.out.println("--------------------------------------");
            System.out.println("Servidor iniciado en el puerto " + puerto);
            System.out.println("Esperando clientes...");

            while (true) {
                Socket socketCliente = servidor.accept();

                System.out.println("Nuevo cliente conectado.");

                GestorCliente gestor = new GestorCliente(socketCliente);
                Thread hilo = new Thread(gestor);
                hilo.start();
            }

        } catch (IOException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }
}