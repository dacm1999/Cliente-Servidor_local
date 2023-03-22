package tasks;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class HiloCliente extends Thread {
    private boolean estado;

    private ServerSocket servidor;
    private Socket socket;
    List<Transferencia> lista;
    ObjectInputStream entrada;
    private ObjectOutputStream salida;
    private ArrayList<Transferencia> listaDescargas;
    private File ficheroSeleccionado;
    private FileInputStream lectorFichero;


    public HiloCliente(Socket cliente, ArrayList<Transferencia> listaDescargas) {
        this.socket = cliente;
        this.listaDescargas = listaDescargas;
    }


    @Override
    public void run() {
        try {
            salida = new ObjectOutputStream(socket.getOutputStream());
            salida.writeObject(listaDescargas);
            entrada = new ObjectInputStream(socket.getInputStream());
            do{
                Transferencia t = (Transferencia) entrada.readObject();
                System.out.println("NOMBRE DEL FICHERO SELECCIONADO: " + t.getNombre() + " CLASE HILO CLIENTE");
                ficheroSeleccionado = t.getFichero();
                long fileSize = ficheroSeleccionado.length();
                System.out.println("Archivos enviados" + " DESDE CLASE HILO CLIENTE");

                salida.writeObject(fileSize);
                salida.flush();

                lectorFichero = new FileInputStream(ficheroSeleccionado);
                byte[] buffer = new byte[1024];

                int bytesLeidos = 0;
                long totalEscrito = 0;
                while((bytesLeidos = lectorFichero.read(buffer)) > 0){
                    salida.write(buffer, 0, bytesLeidos);
                    salida.flush();
                    totalEscrito += bytesLeidos;
                }
                System.out.println("Tamaño ficheros escritos " + totalEscrito);
                lectorFichero.close();
            }while(!socket.isClosed());

            entrada.close();
            salida.close();
        } catch (SocketException ex) {
            System.out.println("CLIENTE DESCONECTADO " + socket.getInetAddress().getHostAddress());
        } catch (NullPointerException e) {
            System.out.println("NO DEBE SER NULL");
        } catch (IOException ex) {
            System.out.println("ERROR EN EL BUFFER");
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Clase no encontrada");
        } catch (Exception e) {
        }
    }
}
