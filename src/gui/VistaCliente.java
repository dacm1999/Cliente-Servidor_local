package gui;

import com.formdev.flatlaf.FlatIntelliJLaf;
import controlador.ControladorCliente;
import tasks.Transferencia;

import javax.swing.*;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VistaCliente {
    public JPanel panel1;
    private JScrollPane scroll;
    public JTextField txfIp;
    public JButton btnConectar;
    public JButton btnAbrirDescarga;
    public JButton btnDescargarFichero;
    public JProgressBar barraProgreso;
    public JPanel pnlP;
    public JTextField txfPuerto;
    public JLabel lblEstado;
    public JList listaGUI;
    private JLabel lblip;
    private JLabel lblPuerto;
    //public DefaultListModel modelo;

    private VistaCliente() {
        JFrame frame = new JFrame("Cliente");
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(550, 450);
        frame.setLocationRelativeTo(null);
        //listaGUI.setModel(modelo);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(VistaCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        new ControladorCliente(new VistaCliente());
    }
}
