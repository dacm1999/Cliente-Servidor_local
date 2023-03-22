package gui;

import com.formdev.flatlaf.FlatIntelliJLaf;
import controlador.ControladorServidor;
import tasks.Transferencia;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VistaServidor {
    public JPanel pnlPrincipal;
    public JButton btnIniciar;
    public JLabel lbl2;
    public JLabel lbl3;
    public JList lista;
    public JButton btnAnadirDescarga;
    private JLabel lbl1;
    public JTextField txfPuerto;
    public JTextArea txtClientes;
    //public DefaultListModel modelo;
    //public ArrayList<Transferencia> listaDescargas;

    private VistaServidor() {
        JFrame frame = new JFrame("Servidor");
        frame.setContentPane(pnlPrincipal);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Definir tamano inicial
        frame.setSize(new Dimension(350,450));
        //centrar ventana inicial
        frame.setLocationRelativeTo(null);
        //modelo = new DefaultListModel<Transferencia>();
        //lista.setModel(modelo);
        //listaDescargas = new ArrayList<>();

        frame.setVisible(true);
    }
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(VistaServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
        new ControladorServidor(new VistaServidor());
    }
}
