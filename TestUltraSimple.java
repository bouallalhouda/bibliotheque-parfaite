import java.sql.*;

public class TestUltraSimple {
    public static void main(String[] args) {
        System.out.println("🧪 Test MySQL ULTRA SIMPLE");
        
        try {
            // Charge driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("1. Driver OK");
            
            // Essaie plusieurs combos
            String[][] combos = {
                {"localhost", ""},
                {"localhost", "root"},
                {"127.0.0.1", ""},
                {"127.0.0.1", "root"}
            };
            
            for (String[] combo : combos) {
                String host = combo[0];
                String pass = combo[1];
                
                System.out.println("\nEssai -> host:" + host + " pass:'" + pass + "'");
                
                try {
                    Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://" + host + ":3306/bibliotheque",
                        "root",
                        pass
                    );
                    
                    System.out.println("✅ RÉUSSITE !");
                    System.out.println("📊 Info base: " + conn.getCatalog());
                    
                    // Test table
                    Statement stmt = conn.createStatement();
                    stmt.execute("CREATE TABLE IF NOT EXISTS test_table (id INT)");
                    System.out.println("✅ Table test créée");
                    
                    conn.close();
                    return; // Stop au premier succès
                    
                } catch (SQLException e) {
                    System.out.println("❌ Échec: " + e.getMessage());
                }
            }
            
            System.err.println("\n💥 AUCUNE CONFIGURATION NE FONCTIONNE !");
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver manquant ! JAR dans lib/ ?");
        }
    }
}