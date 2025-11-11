
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set modern system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getLookAndFeel());
            
            // Modern UI improvements
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 5);
            UIManager.put("ScrollBar.width", 12);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Run the application on EDT
        SwingUtilities.invokeLater(() -> {
            new MainFrame();
        });
    }
}