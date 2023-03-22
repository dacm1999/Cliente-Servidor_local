package controlador;

import gui.VistaServidor;
import tasks.HiloCliente;
import tasks.Transferencia;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ControladorServidor implements ActionListener {


    public VistaServidor vistaServidor;
    private ServerSocket servidor;
    private Socket cliente;
    private boolean estado = true;
    private File fichero;
    private Transferencia transferencia;
    public DefaultListModel modelo;
    private ArrayList<Transferencia> listaDescargas;
    private ServerSocket serverSocket;

    private JFileChooser selector;

    public ControladorServidor(VistaServidor vistaServidor) {
        this.vistaServidor = vistaServidor;
        vincularListeners(this);
        listaDescargas = new ArrayList<Transferencia>();
        modelo = new DefaultListModel();
        vistaServidor.lista.setModel(modelo);
    }

    private void vincularListeners(ActionListener listener) {
        vistaServidor.btnIniciar.addActionListener(listener);
        vistaServidor.btnAnadirDescarga.addActionListener(listener);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();

        switch (comando) {
            case "Añadir Descarga": {
                anadirDescarga();
                break;
            }
            case "Iniciar": {
                inicio2();
                break;
            }

        }
    }

    private void anadirDescarga() {
        selector = new JFileChooser();
        int respuesta = selector.showSaveDialog(null);
        if (respuesta == JFileChooser.APPROVE_OPTION) {
            fichero = selector.getSelectedFile();
        }
        transferencia = new Transferencia(fichero.getName(), new File(fichero.getAbsolutePath()));
        modelo.addElement(transferencia);
        listaDescargas.add(transferencia);
    }

    public void inicio2() {
        try {
            if (fichero == null) {
                JOptionPane.showMessageDialog(null, "Debe seleccionar un archivo para descargar", "ERROR", JOptionPane.ERROR_MESSAGE);
            } else {
                serverSocket = new ServerSocket(Integer.parseInt(vistaServidor.txfPuerto.getText()));
                vistaServidor.btnIniciar.setText("SERVIDOR INICIADO");
                estado = true;
                Thread hilo = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (estado) {
                            try {
                                cliente = serverSocket.accept();
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        vistaServidor.txtClientes.append(cliente.getInetAddress().getHostAddress() + " conectado\n");
                                        //vistaServidor.txtClientes.append("Archivos para enviar:\n");
                                        //for (Transferencia transferencia : listaDescargas) {
                                          //  vistaServidor.txtClientes.append("- " + transferencia.getNombre() + "\n");
                                        //}
                                        HiloCliente hiloCliente = new HiloCliente(cliente, listaDescargas);
                                        hiloCliente.start();
                                    }
                                });
                            } catch (IOException e) {
                                System.out.println("ERROR: " + e.getMessage());
                            }
                        }
                    }
                });
                hilo.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}