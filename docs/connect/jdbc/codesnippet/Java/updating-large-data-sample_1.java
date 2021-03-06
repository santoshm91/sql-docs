import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.microsoft.sqlserver.jdbc.SQLServerStatement;

public class UpdateLargeData {

    public static void main(String[] args) {

        // Create a variable for the connection string.
        String connectionUrl = "jdbc:sqlserver://<server>:<port>;databaseName=AdventureWorks;user=<user>;password=<password>";

        try (Connection con = DriverManager.getConnection(connectionUrl);
                Statement stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);) {
            // Since the summaries could be large, make sure that
            // the driver reads them incrementally from a database,
            // even though a server cursor is used for the updatable result sets.

            // The recommended way to access the Microsoft JDBC Driver for SQL Server
            // specific methods is to use the JDBC 4.0 Wrapper functionality.
            // The following code statement demonstrates how to use the
            // Statement.isWrapperFor and Statement.unwrap methods
            // to access the driver specific response buffering methods.

            if (stmt.isWrapperFor(com.microsoft.sqlserver.jdbc.SQLServerStatement.class)) {
                SQLServerStatement SQLstmt = stmt.unwrap(com.microsoft.sqlserver.jdbc.SQLServerStatement.class);

                SQLstmt.setResponseBuffering("adaptive");
                System.out.println("Response buffering mode has been set to " + SQLstmt.getResponseBuffering());
            }

            // Select all of the document summaries.
            ResultSet rs = stmt.executeQuery("SELECT Title, DocumentSummary FROM Production.Document");

            // Update each document summary.
            while (rs.next()) {

                // Retrieve the original document summary.
                Reader reader = rs.getCharacterStream("DocumentSummary");

                if (reader == null) {
                    // Update the document summary.
                    System.out.println("Updating " + rs.getString("Title"));
                    rs.updateString("DocumentSummary", "Work in progress");
                    rs.updateRow();
                }
                else {
                    // Do something with the chunk of the data that was
                    // read.
                    System.out.println("reading " + rs.getString("Title"));
                    reader.close();
                    reader = null;
                }
            }
        }
        // Handle any errors that may have occurred.
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
