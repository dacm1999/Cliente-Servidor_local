package controlador;

import gui.VistaCliente;
import tasks.CopiaWorker;
import tasks.Transferencia;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ControladorCliente implements ActionListener {


    public VistaCliente vistaCliente;
    public Transferencia t;
    private Socket socket;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    private boolean estado;
    private DefaultListModel modelo;
    ArrayList<Transferencia> listaDescargas;
    public File destino;
    private CopiaWorker descarga;

    public ControladorCliente(VistaCliente vistaCliente) {
        this.vistaCliente = vistaCliente;
        vincularListener(this);

        listaDescargas = new ArrayList<>();
        modelo = new DefaultListModel();
        vistaCliente.listaGUI.setModel(modelo);
    }

    private void vincularListener(ActionListener listener) {

        vistaCliente.btnAbrirDescarga.addActionListener(listener);
        vistaCliente.btnDescargarFichero.addActionListener(listener);
        vistaCliente.btnConectar.addActionListener(listener);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
        switch (comando) {
            case "Conectar": {
                conectar();
                break;
            }
            case "Descargar Fichero": {
                descargar();
                break;
            }
            case "Directorio": {
                directorio();
                break;
            }
        }
    }


    private void directorio() {
        String projectDir = Paths.get("").toAbsolutePath().toString();
        //System.out.println("Opening Project Directory: " + projectDir);
        try {
            File directory = new File(projectDir);
            Desktop.getDesktop().open(directory);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void conectar() {
        try {
            socket = new Socket(vistaCliente.txfIp.getText(), Integer.parseInt(vistaCliente.txfPuerto.getText()));
            entrada = new ObjectInputStream(socket.getInputStream());
            salida = new ObjectOutputStream(socket.getOutputStream());
            estado = true;
            System.out.println("Cliente conectado" + socket.getInetAddress().getHostAddress());

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {

                        System.out.println("ESTOY EN EL EDT: " + SwingUtilities.isEventDispatchThread());
                        listaDescargas = (ArrayList<Transferencia>) entrada.readObject();
                        vistaCliente.btnConectar.setText("CONECTADO");
                        for (int i = 0; i < listaDescargas.size(); i++) {
                            modelo.addElement(listaDescargas.get(i));
                        }

                        for (Transferencia t : listaDescargas) {
                            System.out.println("FICHEROS RECIBIDOS: " + t.getNombre());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        estado = false;
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            System.out.println("HOST NOT FOUND");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "No se pudo conectar con el servidor", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void descargar() {
        try {
            t = (Transferencia) vistaCliente.listaGUI.getSelectedValue();
            //t.setNombre("copia_" + t.getNombre());
            System.out.println("FICHERO SELECCIONADO : " + t.getNombre() + " enviado al servidor");
            salida.writeObject(t);
            descarga = new CopiaWorker(socket, t, vistaCliente, entrada);
            descarga.execute();
        } catch (ArrayIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(null, "DEBES SELECCIONAR UN FICHERO", "ERROR", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
