package views;

import javax.swing.*;

public class MenuView extends JFrame {

    private JButton btnListar;
    private JButton btnAgregar;
    private JButton btnSalir;

    public MenuView() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Menú Principal");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        btnListar = new JButton("Listar Animales");
        btnListar.setBounds(70, 20, 150, 30);
        add(btnListar);

        btnAgregar = new JButton("Agregar Animales");
        btnAgregar.setBounds(70, 60, 150, 30);
        add(btnAgregar);

        btnSalir = new JButton("Salir");
        btnSalir.setBounds(70, 100, 150, 30);
        add(btnSalir);

        btnListar.addActionListener(e -> new ListarAnimalesView().setVisible(true));

        btnAgregar.addActionListener(e -> {
            try {
                new AgregarAnimalView().setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al abrir AgregarAnimalView.");
            }
        });

        btnSalir.addActionListener(e -> System.exit(0));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuView().setVisible(true));
    }
}
