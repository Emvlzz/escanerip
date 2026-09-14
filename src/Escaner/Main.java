package Escaner;

import Vista.VentanaEscaner;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Ejecución en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            try {
                // Aplica el estilo visual del sistema operativo
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.out.println("No se pudo cargar el tema nativo.");
            }
            
            VentanaEscaner ventana = new VentanaEscaner();
            ventana.setVisible(true);
        });
    }
}
