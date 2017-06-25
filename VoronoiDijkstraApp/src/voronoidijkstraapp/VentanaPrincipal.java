package voronoidijkstraapp;

import java.io.File;
import java.io.IOException;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JSlider;
import voronoi.Punto;

/**
 * @author MariaOB
 */

public class VentanaPrincipal extends javax.swing.JFrame {
    private final Lienzo lienzo;
   
    public VentanaPrincipal() {
        initComponents();
        lienzo = new Lienzo(this);
        PanelAtras.add(lienzo, java.awt.BorderLayout.CENTER);
    }
    
    public void cambiaCoordenadas(String coordenadas){
        this.Coordenadas.setText(coordenadas);
    }    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupMode = new javax.swing.ButtonGroup();
        PanelAtras = new javax.swing.JPanel();
        PanelHerramientas = new javax.swing.JPanel();
        PanelModos = new javax.swing.JPanel();
        VoronoiMode = new javax.swing.JRadioButton();
        DijkstraMode = new javax.swing.JRadioButton();
        PanelAniadeInfo = new javax.swing.JPanel();
        CheckBoxDelaunay = new javax.swing.JCheckBox();
        CheckBoxCircunferencias = new javax.swing.JCheckBox();
        PanelZoom = new javax.swing.JPanel();
        SliderZoom = new javax.swing.JSlider();
        PanelPunto = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        TextFieldPuntoX = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        TextFieldPuntoY = new javax.swing.JTextField();
        ButtonAniadePunto = new javax.swing.JButton();
        PanelCoordenadas = new javax.swing.JPanel();
        Coordenadas = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuArchivo = new javax.swing.JMenu();
        MenuItemLimpiar = new javax.swing.JMenuItem();
        MenuItemAbrir = new javax.swing.JMenuItem();
        MenuItemGuardar = new javax.swing.JMenuItem();
        MenuItemExportar = new javax.swing.JMenuItem();
        MenuVer = new javax.swing.JMenu();
        CheckBoxMenuItemVerBarraHerramientas = new javax.swing.JCheckBoxMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Voronoi");
        setBackground(new java.awt.Color(255, 255, 255));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setMinimumSize(new java.awt.Dimension(1000, 1000));

        PanelAtras.setLayout(new java.awt.BorderLayout());

        PanelHerramientas.setLayout(new javax.swing.BoxLayout(PanelHerramientas, javax.swing.BoxLayout.LINE_AXIS));

        PanelModos.setBorder(javax.swing.BorderFactory.createTitledBorder("Modos"));

        buttonGroupMode.add(VoronoiMode);
        VoronoiMode.setSelected(true);
        VoronoiMode.setText("Voronoi ");
        VoronoiMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VoronoiModeActionPerformed(evt);
            }
        });
        PanelModos.add(VoronoiMode);

        buttonGroupMode.add(DijkstraMode);
        DijkstraMode.setText("Dijkstra");
        DijkstraMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DijkstraModeActionPerformed(evt);
            }
        });
        PanelModos.add(DijkstraMode);

        PanelHerramientas.add(PanelModos);

        PanelAniadeInfo.setBorder(javax.swing.BorderFactory.createTitledBorder("Más Información"));

        CheckBoxDelaunay.setText("Delaunay");
        CheckBoxDelaunay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CheckBoxDelaunayActionPerformed(evt);
            }
        });
        PanelAniadeInfo.add(CheckBoxDelaunay);

        CheckBoxCircunferencias.setText("Circunferencias");
        CheckBoxCircunferencias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CheckBoxCircunferenciasActionPerformed(evt);
            }
        });
        PanelAniadeInfo.add(CheckBoxCircunferencias);

        PanelHerramientas.add(PanelAniadeInfo);

        PanelZoom.setBorder(javax.swing.BorderFactory.createTitledBorder("Zoom"));

        SliderZoom.setMaximum(10);
        SliderZoom.setMinimum(4);
        SliderZoom.setMinorTickSpacing(1);
        SliderZoom.setPaintTicks(true);
        SliderZoom.setToolTipText("Slider for Zoom");
        SliderZoom.setValue(7);
        SliderZoom.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                SliderZoomStateChanged(evt);
            }
        });
        PanelZoom.add(SliderZoom);

        PanelHerramientas.add(PanelZoom);

        PanelPunto.setBorder(javax.swing.BorderFactory.createTitledBorder("Añadir Punto"));

        jLabel1.setText("X:");
        PanelPunto.add(jLabel1);

        TextFieldPuntoX.setToolTipText("");
        TextFieldPuntoX.setMaximumSize(new java.awt.Dimension(70, 30));
        TextFieldPuntoX.setMinimumSize(new java.awt.Dimension(70, 30));
        TextFieldPuntoX.setName(""); // NOI18N
        TextFieldPuntoX.setPreferredSize(new java.awt.Dimension(70, 30));
        PanelPunto.add(TextFieldPuntoX);

        jLabel2.setText("Y:");
        PanelPunto.add(jLabel2);

        TextFieldPuntoY.setMaximumSize(new java.awt.Dimension(70, 30));
        TextFieldPuntoY.setMinimumSize(new java.awt.Dimension(70, 30));
        TextFieldPuntoY.setPreferredSize(new java.awt.Dimension(70, 30));
        PanelPunto.add(TextFieldPuntoY);

        ButtonAniadePunto.setText("Añadir");
        ButtonAniadePunto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonAniadePuntoActionPerformed(evt);
            }
        });
        PanelPunto.add(ButtonAniadePunto);

        PanelHerramientas.add(PanelPunto);

        PanelAtras.add(PanelHerramientas, java.awt.BorderLayout.NORTH);

        Coordenadas.setText("x:0000 , y: 0000");
        Coordenadas.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        PanelCoordenadas.add(Coordenadas);

        PanelAtras.add(PanelCoordenadas, java.awt.BorderLayout.SOUTH);

        getContentPane().add(PanelAtras, java.awt.BorderLayout.CENTER);

        jMenuArchivo.setText("Archivo");

        MenuItemLimpiar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.ALT_MASK));
        MenuItemLimpiar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/nuevo.png"))); // NOI18N
        MenuItemLimpiar.setText("Limpiar");
        MenuItemLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemLimpiarActionPerformed(evt);
            }
        });
        jMenuArchivo.add(MenuItemLimpiar);

        MenuItemAbrir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_MASK));
        MenuItemAbrir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/abrir.png"))); // NOI18N
        MenuItemAbrir.setText("Abrir");
        MenuItemAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemAbrirActionPerformed(evt);
            }
        });
        jMenuArchivo.add(MenuItemAbrir);

        MenuItemGuardar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.ALT_MASK));
        MenuItemGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/guardar.png"))); // NOI18N
        MenuItemGuardar.setText("Guardar");
        MenuItemGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemGuardarActionPerformed(evt);
            }
        });
        jMenuArchivo.add(MenuItemGuardar);

        MenuItemExportar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.ALT_MASK));
        MenuItemExportar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/exportar.png"))); // NOI18N
        MenuItemExportar.setText("Exportar");
        MenuItemExportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemExportarActionPerformed(evt);
            }
        });
        jMenuArchivo.add(MenuItemExportar);

        jMenuBar1.add(jMenuArchivo);

        MenuVer.setText("Ver");

        CheckBoxMenuItemVerBarraHerramientas.setSelected(true);
        CheckBoxMenuItemVerBarraHerramientas.setText("Barra Herramientas");
        CheckBoxMenuItemVerBarraHerramientas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CheckBoxMenuItemVerBarraHerramientasActionPerformed(evt);
            }
        });
        MenuVer.add(CheckBoxMenuItemVerBarraHerramientas);

        jMenuBar1.add(MenuVer);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void VoronoiModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VoronoiModeActionPerformed
        lienzo.setModo(0);
        lienzo.repaint();
    }//GEN-LAST:event_VoronoiModeActionPerformed

    private void DijkstraModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DijkstraModeActionPerformed
        lienzo.setModo(1);
        lienzo.repaint();
    }//GEN-LAST:event_DijkstraModeActionPerformed

    private void CheckBoxDelaunayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CheckBoxDelaunayActionPerformed
        JCheckBox cb;
        cb = (JCheckBox) evt.getSource();
        if (cb.isSelected()) {
            lienzo.setPintaDelaunay(true);
        } else {
            lienzo.setPintaDelaunay(false);
        }
        lienzo.repaint();
    }//GEN-LAST:event_CheckBoxDelaunayActionPerformed

    private void CheckBoxCircunferenciasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CheckBoxCircunferenciasActionPerformed
        JCheckBox cb;
        cb = (JCheckBox) evt.getSource();
        if (cb.isSelected()) {
            lienzo.setPintaCircunferencias(true);
        } else {
            lienzo.setPintaCircunferencias(false);
        }
        lienzo.repaint();
    }//GEN-LAST:event_CheckBoxCircunferenciasActionPerformed

    private void SliderZoomStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_SliderZoomStateChanged
        JSlider sl;
        sl = (JSlider) evt.getSource();
        lienzo.cambiaZoom((double) sl.getValue());
    }//GEN-LAST:event_SliderZoomStateChanged

    private void MenuItemAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemAbrirActionPerformed
        JFileChooser dlg = new JFileChooser();
        int resp = dlg.showOpenDialog(this);
        if( resp == JFileChooser.APPROVE_OPTION) {
            try{
                File f = dlg.getSelectedFile();
                String filename = f.getAbsolutePath();
                lienzo.aniadeDesdeArchivo(filename);
                }
            catch(IOException ex){
                System.err.println("Error al leer archivo");
            }
        }
    }//GEN-LAST:event_MenuItemAbrirActionPerformed

    private void MenuItemGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemGuardarActionPerformed
        JFileChooser dlg = new JFileChooser();
        dlg.setAcceptAllFileFilterUsed(false);
        int resp = dlg.showSaveDialog(this);
        if (resp == JFileChooser.APPROVE_OPTION) {
            try {
                File f = dlg.getSelectedFile();
                String filename = f.getAbsolutePath();
                lienzo.guardarEnArchivo(filename);
            }catch (Exception ex) {
                System.err.println("Error al guardar la imagen");
            }
        }
    }//GEN-LAST:event_MenuItemGuardarActionPerformed

    private void MenuItemLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemLimpiarActionPerformed
        lienzo.clear();
        lienzo.setModo(0);
        this.VoronoiMode.setSelected(true);
    }//GEN-LAST:event_MenuItemLimpiarActionPerformed

    private void CheckBoxMenuItemVerBarraHerramientasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CheckBoxMenuItemVerBarraHerramientasActionPerformed
        JCheckBoxMenuItem cb;
        cb = (JCheckBoxMenuItem) evt.getSource();
        if (cb.isSelected()) {
            this.PanelHerramientas.setVisible(true);
        } else {
            this.PanelHerramientas.setVisible(false);
        }
    }//GEN-LAST:event_CheckBoxMenuItemVerBarraHerramientasActionPerformed

    private void MenuItemExportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemExportarActionPerformed
        JFileChooser dlg = new JFileChooser();
        dlg.setAcceptAllFileFilterUsed(false);
        int resp = dlg.showSaveDialog(this);
        if (resp == JFileChooser.APPROVE_OPTION) {
            try {
                File f = dlg.getSelectedFile();
                String filename = f.getAbsolutePath();
                lienzo.exportarEnArchivo(filename);
            }catch (Exception ex) {
                System.err.println("Error al guardar la imagen");
            }
        }
    }//GEN-LAST:event_MenuItemExportarActionPerformed

    private void ButtonAniadePuntoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonAniadePuntoActionPerformed
        try{   
            double x = Double.parseDouble(this.TextFieldPuntoX.getText());
            double y = Double.parseDouble(this.TextFieldPuntoY.getText());
            lienzo.dt.aniadePuntoDelaunay(new Punto(x,y));
            lienzo.repaint();
        }
        catch(NumberFormatException nfe){}
    }//GEN-LAST:event_ButtonAniadePuntoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ButtonAniadePunto;
    private javax.swing.JCheckBox CheckBoxCircunferencias;
    private javax.swing.JCheckBox CheckBoxDelaunay;
    private javax.swing.JCheckBoxMenuItem CheckBoxMenuItemVerBarraHerramientas;
    private javax.swing.JLabel Coordenadas;
    private javax.swing.JRadioButton DijkstraMode;
    private javax.swing.JMenuItem MenuItemAbrir;
    private javax.swing.JMenuItem MenuItemExportar;
    private javax.swing.JMenuItem MenuItemGuardar;
    private javax.swing.JMenuItem MenuItemLimpiar;
    private javax.swing.JMenu MenuVer;
    private javax.swing.JPanel PanelAniadeInfo;
    private javax.swing.JPanel PanelAtras;
    private javax.swing.JPanel PanelCoordenadas;
    private javax.swing.JPanel PanelHerramientas;
    private javax.swing.JPanel PanelModos;
    private javax.swing.JPanel PanelPunto;
    private javax.swing.JPanel PanelZoom;
    private javax.swing.JSlider SliderZoom;
    private javax.swing.JTextField TextFieldPuntoX;
    private javax.swing.JTextField TextFieldPuntoY;
    private javax.swing.JRadioButton VoronoiMode;
    private javax.swing.ButtonGroup buttonGroupMode;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenuArchivo;
    private javax.swing.JMenuBar jMenuBar1;
    // End of variables declaration//GEN-END:variables
}
