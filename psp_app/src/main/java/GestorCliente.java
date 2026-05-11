import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class GestorCliente implements Runnable {

    private Socket socket;

    private static Scanner tecladoServidor = new Scanner(System.in);

    public GestorCliente(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try (
                DataInputStream entrada = new DataInputStream(socket.getInputStream());
                DataOutputStream salida = new DataOutputStream(socket.getOutputStream())
        ) {
            String nombre = entrada.readUTF();

            System.out.println();
            System.out.println("[INFO] " + nombre + " se ha conectado.");

            salida.writeUTF("Bienvenido/a " + nombre + ". Escribe tu denuncia o 'salir' para terminar.");

            boolean activo = true;

            while (activo) {
                String mensaje = entrada.readUTF();

                if (mensaje.equalsIgnoreCase("salir")) {
                    salida.writeUTF("Chat finalizado. Gracias por usar el canal de ayuda.");
                    System.out.println("[INFO] " + nombre + " ha salido.");
                    activo = false;
                } else {
                    System.out.println();
                    System.out.println("------------------------------------");
                    System.out.println("Mensaje de: " + nombre);
                    System.out.println("Denuncia: " + mensaje);
                    System.out.println("------------------------------------");

                    String respuesta;

                    synchronized (tecladoServidor) {
                        System.out.print("Respuesta para " + nombre + ": ");
                        respuesta = tecladoServidor.nextLine();
                    }

                    guardarMensaje(nombre, mensaje, respuesta);

                    salida.writeUTF(respuesta);
                }
            }

        } catch (IOException e) {
            System.out.println("Conexión perdida con cliente.");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error al cerrar el socket.");
            }
        }
    }

    private void guardarMensaje(String nombre, String mensaje, String respuesta) {
        String sql = "INSERT INTO mensajes (nombre, mensaje, respuesta) VALUES (?, ?, ?)";

        try (
                Connection conexion = ConexionBD.conectar();
                PreparedStatement ps = conexion.prepareStatement(sql)
        ) {
            ps.setString(1, nombre);
            ps.setString(2, mensaje);
            ps.setString(3, respuesta);

            ps.executeUpdate();

            System.out.println("Mensaje guardado en MySQL.");

        } catch (SQLException e) {
            System.out.println("Error al guardar en MySQL: " + e.getMessage());
        }
    }
}