package com.forestplus;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Component
public class TestConnection implements CommandLineRunner {

    private final DataSource dataSource;

    public TestConnection(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
        	
            System.out.println("🔍 JDBC URL: " + conn.getMetaData().getURL());
            System.out.println("🔍 JDBC USER: " + conn.getMetaData().getUserName());

            ResultSet rs = stmt.executeQuery("SELECT NOW()");
            if (rs.next()) {
                System.out.println("✅ Conexión exitosa! Hora actual DB: " + rs.getString(1));
            } else {
                System.out.println("❌ No se pudo obtener la hora de la DB");
            }

        } catch (Exception e) {
            System.err.println("❌ Error conectando a la DB: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
