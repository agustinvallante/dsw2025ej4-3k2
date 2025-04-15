/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package views;

import data.Persistencia;
import domain.Carnivoro;
import domain.Especie;
import domain.Herbivoro;
import domain.Pais;
import domain.Sector;
import domain.TipoAlimentacion;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.InvalidPropertiesFormatException;
import javax.swing.JOptionPane;

/**
 *
 * @author braia
 */
public class AgregarAnimalView extends javax.swing.JFrame {

    private final ListarAnimalesView padre;

    /**
     * Creates new form AgregarAnimalView
     */
    public AgregarAnimalView(ListarAnimalesView padre) {
        this.padre = padre;
        initComponents();
        loadData();
    }
    public AgregarAnimalView(){
        this(null);
    }
    
    private void loadData(){
        
        // Se agrega listeners para actualizar campos segun tipo (segun los radioButtons seleccionados)
        hervivoroRadio.addItemListener(e -> actualizarCamposSegunTipo());
        carnivoroRadio.addItemListener(e -> actualizarCamposSegunTipo());
        
        especieCombo.removeAllItems();
        
        // Se cargan los combos
        for(Especie especie : Controlador.getEspecies()) {
            // probar con getNombre aca
            especieCombo.addItem(especie.getNombre());
        }
        
        // Se agrega un listener para filtrar sectores segun la especie seleccionada
       especieCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filtrarSectores();
            }
        });
       
       // Se cargan los combobox de sectores
       filtrarSectores();
       
       // Se cargan comboboxes de paises
       paisCombo.removeAllItems();
       for (Pais pais : Persistencia.getPaises()) {
            paisCombo.addItem(pais.getNombre());
        }
        

    
    }
    private void filtrarSectores(){
        
        // Da una excepcion por que en los comboBoxes esta declarado como String y no como objeto (ultimas lineas)
        
        //Especie espSelec= (Especie) especieCombo.getSelectedItem();
        //if (espSelec == null) return;
        
        // Se soluciona haciendo que queden directamente strings
        String nombreSeleccionado = (String) especieCombo.getSelectedItem();
        if (nombreSeleccionado == null) return;

        Especie espSelec = buscarEspeciePorNombre(nombreSeleccionado);
        if (espSelec == null) return;

        
        
        sectorCombo.removeAllItems();
        
        for(Sector sector : Controlador.getSectores()){
            if (sector.getTipoAlimentacion() == espSelec.getTipoAlimentacion()) {
                // Poner siempre toString ya que no lo implementamos desde los elementos 
                sectorCombo.addItem(String.valueOf(sector.getNumero()));
            }
        }
        
        
        
        
}
    
    private Especie buscarEspeciePorNombre(String nombre) {
    for (Especie especie : Controlador.getEspecies()) {
        if (especie.getNombre().equals(nombre)) {
            return especie;
        }
    }
    return null;
}
    private Pais buscarPaisPorNombre(String nombre) {
    for (Pais pais : Persistencia.getPaises()) {
        if (pais.getNombre().equals(nombre)) {
            return pais;
        }
    }
    return null;
}

private Sector buscarSectorPorNumero(int numero) {
    for (Sector sector : Controlador.getSectores()) {
        if (sector.getNumero() == numero) {
            return sector;
        }
    }
    return null;
}

    

    private void actualizarCamposSegunTipo(){
        boolean esHerbivoro = hervivoroRadio.isSelected();
        
        valorFijoLabel.setVisible(esHerbivoro);
        valorFIjoTextField.setVisible(esHerbivoro);
        
        // También filtramos especies según el tipo seleccionado
        especieCombo.removeAllItems();
        TipoAlimentacion tipoSeleccionado = esHerbivoro ? TipoAlimentacion.HERBIVORO : TipoAlimentacion.CARNIVORO;
        
        for (Especie especie : Controlador.getEspecies()) {
            if (especie.getTipoAlimentacion() == tipoSeleccionado) {
                especieCombo.addItem(especie.getNombre());
            }
        }
        
        // Actualizar sectores después de cambiar las especies
        filtrarSectores();
    }
    
    private void guardarAnimal() {
        try {
            // Validar campos
            if (edadTextField.getText().trim().isEmpty() || pesoTextField.getText().trim().isEmpty() ||
                    (hervivoroRadio.isSelected() && valorFIjoTextField.getText().trim().isEmpty())) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int edad;
            double peso;
            double valorFijo = 0;
            
            try {
                edad = Integer.parseInt(edadTextField.getText().trim());
                peso = Double.parseDouble(pesoTextField.getText().trim());
                
                if (hervivoroRadio.isSelected()) {
                    valorFijo = Double.parseDouble(valorFIjoTextField.getText().trim());
                }
                
                if (edad <= 0 || peso <= 0 || (hervivoroRadio.isSelected() && valorFijo <= 0)) {
                    JOptionPane.showMessageDialog(this, "Los valores numéricos deben ser mayores a cero", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Los campos edad, peso y valor fijo deben ser números válidos", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Seguimos obteniendo el error de no se puede castear domain.especie ya que en el combobox obtiene especie.getNombre no objetos especie, lo mismo para 
            // los sectores y paises, por lo tanto recurrimos a la misma solucion 
            
            //Especie especie = (Especie) especieCombo.getSelectedItem();
            //Sector sector = (Sector) sectorCombo.getSelectedItem();
            //Pais pais = (Pais) paisCombo.getSelectedItem();
            
            String nombreSeleccionado = (String) especieCombo.getSelectedItem();
            Especie especie = buscarEspeciePorNombre(nombreSeleccionado);
            
            String nombrePais = (String) paisCombo.getSelectedItem();
            Pais pais = buscarPaisPorNombre(nombrePais);

            String numeroSectorStr = (String) sectorCombo.getSelectedItem();
            int numeroSector = Integer.parseInt(numeroSectorStr);
            Sector sector = buscarSectorPorNumero(numeroSector);

            
            if (especie == null || sector == null || pais == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar una especie, un sector y un país", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Crear el animal según el tipo seleccionado
            if (carnivoroRadio.isSelected()) {
                Carnivoro carnivoro = new Carnivoro(edad, peso, especie, sector, pais);
                Persistencia.agregarAnimal(carnivoro);
            } else {
                Herbivoro herbivoro = new Herbivoro(edad, peso, especie, sector, valorFijo, pais);
                Persistencia.agregarAnimal(herbivoro);
            }
            
            JOptionPane.showMessageDialog(this, "Animal agregado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
            
            if(padre != null){
                padre.listarAnimales();
            }
            
            
        } catch (InvalidPropertiesFormatException e) {
            JOptionPane.showMessageDialog(this, "Error al agregar animal: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        
    }
    
    private void limpiarCampos() {
        edadTextField.setText("");
        pesoTextField.setText("");
        valorFIjoTextField.setText("");
        carnivoroRadio.setSelected(true);
        actualizarCamposSegunTipo();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        edadLabel = new javax.swing.JLabel();
        hervivoroRadio = new javax.swing.JRadioButton();
        carnivoroRadio = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        edadTextField = new javax.swing.JTextField();
        pesoTextField = new javax.swing.JTextField();
        especieCombo = new javax.swing.JComboBox<>();
        sectorCombo = new javax.swing.JComboBox<>();
        paisCombo = new javax.swing.JComboBox<>();
        cancelarButton = new javax.swing.JButton();
        guardarButton = new javax.swing.JButton();
        valorFijoLabel = new javax.swing.JLabel();
        valorFIjoTextField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        edadLabel.setText("Edad");

        buttonGroup1.add(hervivoroRadio);
        hervivoroRadio.setText("Hervívoro");

        buttonGroup1.add(carnivoroRadio);
        carnivoroRadio.setText("Carnívoro");

        jLabel2.setText("Tipo de animal");

        jLabel3.setText("Peso");

        jLabel4.setText("Especie");

        jLabel5.setText("Sector");

        jLabel6.setText("País");

        especieCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        sectorCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        paisCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cancelarButton.setText("Cancelar");
        cancelarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelarButtonActionPerformed(evt);
            }
        });

        guardarButton.setText("Guardar");
        guardarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarButtonActionPerformed(evt);
            }
        });

        valorFijoLabel.setText("Valor Fijo");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(valorFijoLabel)
                            .addComponent(jLabel5)
                            .addComponent(jLabel3)
                            .addComponent(edadLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 204, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cancelarButton, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(carnivoroRadio)
                                .addGap(34, 34, 34)
                                .addComponent(hervivoroRadio))
                            .addComponent(edadTextField)
                            .addComponent(pesoTextField)
                            .addComponent(especieCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(sectorCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(paisCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(valorFIjoTextField))
                        .addGap(69, 69, 69))))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(62, 62, 62)
                    .addComponent(jLabel2)
                    .addContainerGap(416, Short.MAX_VALUE)))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(120, 120, 120)
                    .addComponent(guardarButton, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(339, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(carnivoroRadio)
                    .addComponent(hervivoroRadio))
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(edadLabel)
                    .addComponent(edadTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(pesoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(especieCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(sectorCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(paisCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(valorFijoLabel)
                    .addComponent(valorFIjoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addComponent(cancelarButton, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(52, 52, 52)
                    .addComponent(jLabel2)
                    .addContainerGap(307, Short.MAX_VALUE)))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addContainerGap(358, Short.MAX_VALUE)
                    .addComponent(guardarButton, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(27, 27, 27)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    private void guardarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarButtonActionPerformed
       guardarAnimal();
    }//GEN-LAST:event_guardarButtonActionPerformed

    private void cancelarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelarButtonActionPerformed
        dispose();
    }//GEN-LAST:event_cancelarButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AgregarAnimalView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AgregarAnimalView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AgregarAnimalView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AgregarAnimalView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AgregarAnimalView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton cancelarButton;
    private javax.swing.JRadioButton carnivoroRadio;
    private javax.swing.JLabel edadLabel;
    private javax.swing.JTextField edadTextField;
    private javax.swing.JComboBox<String> especieCombo;
    private javax.swing.JButton guardarButton;
    private javax.swing.JRadioButton hervivoroRadio;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JComboBox<String> paisCombo;
    private javax.swing.JTextField pesoTextField;
    private javax.swing.JComboBox<String> sectorCombo;
    private javax.swing.JTextField valorFIjoTextField;
    private javax.swing.JLabel valorFijoLabel;
    // End of variables declaration//GEN-END:variables
}
