package tasks;

import gui.VistaCliente;
import tasks.Transferencia;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class CopiaWorker extends SwingWorker<Void, Integer> {
    private Socket socket;
    private Transferencia t;
    private boolean estado;
    private int contador;
    private long fileSize;
    private ObjectInputStream entrada;
    private VistaCliente vistaCliente;
    private FileOutputStream fileOutputStream;

    public CopiaWorker(Socket socket, Transferencia t, VistaCliente vistaCliente, ObjectInputStream entrada) {
        this.socket=socket;
        this.t=t;
        this.vistaCliente=vistaCliente;
        this.entrada=entrada;
        estado =false;
        contador=0;
    }

    @Override
    protected Void doInBackground() throws Exception {
        downloadFile(t);
        return null;
    }

    @Override
    protected void done() {
        vistaCliente.lblEstado.setText("Estado: Descarga finalizada");
    }


    @Override
    protected void process(List<Integer> valores) {
        if(!valores.isEmpty()) {
            vistaCliente.barraProgreso.setValue((Integer) valores.get(valores.size() - 1));
        }
    }

    private void downloadFile(Transferencia t) throws IOException, ClassNotFoundException {
        if (contador>0){
            while(!vistaCliente.lblEstado.getText().equals("Descarga finalizada")){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        contador++;
        vistaCliente.lblEstado.setText("Estado: Descarga en proceso");
        vistaCliente.lblEstado.setForeground(Color.black);

        fileOutputStream = new FileOutputStream(t.getNombre());

        byte[] buffer = new byte[1024];
        fileSize = (long) entrada.readObject();
        int bytesLeidos;
        long totalLeido = 0;

        estado =false;
        while(totalLeido < fileSize && (bytesLeidos = entrada.read(buffer)) > 0 ){
            while (estado){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            fileOutputStream.write(buffer, 0, bytesLeidos);
            totalLeido += bytesLeidos;
//            System.out.println(totalLeido);
            int progreso =(int) (totalLeido*100/fileSize);
            publish(progreso);
        }
        fileOutputStream.close();
    }

}
