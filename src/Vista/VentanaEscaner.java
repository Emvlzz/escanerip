package Vista;

import Escaner.ValidarIP;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class VentanaEscaner extends JFrame {

    private JTextField txtIpInicio, txtIpFin, txtTimeout;
    private JButton btnEscanear, btnLimpiar, btnGuardar;
    private JTable tablaResultados;
    private DefaultTableModel modeloTabla;
    private JProgressBar barraProgreso;
    private JLabel lblTotalActivos;
    private SwingWorker<Void, Object[]> worker;

    public VentanaEscaner() {
        setTitle("Herramienta de Escaneo de Red - Módulo Ping");
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // Panel de configuración (Norte)
        JPanel panelConfig = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        txtIpInicio = new JTextField("192.168.1.1", 10);
        txtIpFin = new JTextField("192.168.1.20", 10);
        txtTimeout = new JTextField("1000", 5);

        btnEscanear = new JButton("Iniciar Escaneo");
        btnLimpiar = new JButton("Limpiar Pantalla");
        btnGuardar = new JButton("Guardar Resultados");

        panelConfig.add(new JLabel("IP Inicio:"));
        panelConfig.add(txtIpInicio);
        panelConfig.add(new JLabel("IP Fin:"));
        panelConfig.add(txtIpFin);
        panelConfig.add(new JLabel("Timeout (ms):"));
        panelConfig.add(txtTimeout);
        panelConfig.add(btnEscanear);
        panelConfig.add(btnLimpiar);
        panelConfig.add(btnGuardar);

        // Tabla de Resultados (Centro)
        String[] columnas = {"Dirección IP", "Nombre del Equipo", "Estado", "Tiempo"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Evita que se edite la tabla a mano
            }
        };
        tablaResultados = new JTable(modeloTabla);
        
        // Permite ordenar las columnas haciendo clic en el título
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modeloTabla);
        tablaResultados.setRowSorter(sorter);

        // Barra inferior (Sur)
        JPanel panelEstado = new JPanel(new BorderLayout());
        barraProgreso = new JProgressBar();
        barraProgreso.setStringPainted(true);
        lblTotalActivos = new JLabel(" Equipos activos: 0 ");
        
        panelEstado.add(barraProgreso, BorderLayout.CENTER);
        panelEstado.add(lblTotalActivos, BorderLayout.EAST);

        // Agregando todo a la ventana
        add(panelConfig, BorderLayout.NORTH);
        add(new JScrollPane(tablaResultados), BorderLayout.CENTER);
        add(panelEstado, BorderLayout.SOUTH);

        // Configuración de Eventos
        btnLimpiar.addActionListener(e -> limpiarTabla());
        
        btnGuardar.addActionListener(e -> {
            // MÓDULO OMITIDO PARA MANTENER EL 63%
            JOptionPane.showMessageDialog(this, 
                "El módulo de exportación CSV está pendiente de implementación en esta versión.", 
                "Aviso", JOptionPane.INFORMATION_MESSAGE);
        });

        btnEscanear.addActionListener(e -> iniciarEscaneo());
    }

    private void limpiarTabla() {
        modeloTabla.setRowCount(0);
        barraProgreso.setValue(0);
        lblTotalActivos.setText(" Equipos activos: 0 ");
    }

    private void iniciarEscaneo() {
        String ipIn = txtIpInicio.getText().trim();
        String ipFi = txtIpFin.getText().trim();

        if (!ValidarIP.esValida(ipIn) || !ValidarIP.esValida(ipFi)) {
            JOptionPane.showMessageDialog(this, "Por favor revise el formato de las direcciones IP.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
            return;
        }

        long start = ValidarIP.ipToLong(ipIn);
        long end = ValidarIP.ipToLong(ipFi);

        if (start > end) {
            JOptionPane.showMessageDialog(this, "La IP de inicio debe ser menor o igual a la IP final.", "Error de Rango", JOptionPane.ERROR_MESSAGE);
            return;
        }

        limpiarTabla();
        btnEscanear.setEnabled(false);
        int totalIps = (int) (end - start + 1);
        barraProgreso.setMaximum(totalIps);

        // SwingWorker para ejecutar pings sin congelar la ventana
        worker = new SwingWorker<Void, Object[]>() {
            int activos = 0;
            int procesadas = 0;

            @Override
            protected Void doInBackground() throws Exception {
                boolean isWin = System.getProperty("os.name").toLowerCase().contains("win");
                String timeout = txtTimeout.getText().trim();

                for (long i = start; i <= end; i++) {
                    String ipActual = ValidarIP.longToIp(i);
                    String estado = "No Conectado";
                    String tiempo = "-";
                    String nombreEquipo = "Pendiente DNS"; // MÓDULO DNS OMITIDO (63%)

                    try {
                        String[] comando = isWin ? 
                            new String[]{"ping", "-n", "1", "-w", timeout, ipActual} : 
                            new String[]{"ping", "-c", "1", "-W", "1", ipActual};

                        Process process = new ProcessBuilder(comando).start();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String linea;
                        boolean respondio = false;

                        while ((linea = reader.readLine()) != null) {
                            if (linea.toLowerCase().contains("ttl=")) {
                                respondio = true;
                                estado = "Conectado";
                                tiempo = "< 1ms"; // Simplificado
                                break;
                            }
                        }
                        process.waitFor();
                        
                    } catch (Exception ex) {
                        estado = "Error comando";
                    }

                    publish(new Object[]{ipActual, nombreEquipo, estado, tiempo});
                    
                    procesadas++;
                    setProgress((procesadas * 100) / totalIps);
                }
                return null;
            }

            @Override
            protected void process(List<Object[]> chunks) {
                for (Object[] fila : chunks) {
                    modeloTabla.addRow(fila);
                    if ("Conectado".equals(fila[2])) {
                        activos++;
                        lblTotalActivos.setText(" Equipos activos: " + activos + " ");
                    }
                    barraProgreso.setValue(procesadas);
                }
            }

            @Override
            protected void done() {
                btnEscanear.setEnabled(true);
                JOptionPane.showMessageDialog(VentanaEscaner.this, "Escaneo finalizado correctamente.");
            }
        };

        worker.execute();
    }
}