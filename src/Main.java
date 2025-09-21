//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import config.DatabaseSetup;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        System.out.println("Iniciando setup");

        //Crear base de datos
        DatabaseSetup.createDatabase();

        //Crear tablas
        DatabaseSetup.createTables();

        //agregar datos de prueba
        DatabaseSetup.insertSampleData();

        System.out.println("Listo");
    }
}