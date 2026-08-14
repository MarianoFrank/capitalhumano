package ar.edu.utn.frsf.capitalhumano;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@SpringBootApplication
@EnableMethodSecurity
@EnableAsync
@EnableScheduling
public class CapitalHumanoApplication {

    public static void main(String[] args) {
        cargarDotEnv();
        SpringApplication.run(CapitalHumanoApplication.class, args);
    }

    private static void cargarDotEnv() {
        // Busca el .env únicamente correspondiente a la carpeta del backend
        Path envPath = Paths.get(".env");
        if (!Files.exists(envPath)) {
            envPath = Paths.get("backend", ".env");
        }

        if (Files.exists(envPath) && Files.isRegularFile(envPath)) {
            try {
                List<String> lineas = Files.readAllLines(envPath);
                for (String linea : lineas) {
                    String texto = linea.trim();
                    if (texto.isEmpty() || texto.startsWith("#")) {
                        continue;
                    }
                    int separador = texto.indexOf('=');
                    if (separador > 0) {
                        String clave = texto.substring(0, separador).trim();
                        String valor = texto.substring(separador + 1).trim();

                        if ((valor.startsWith("\"") && valor.endsWith("\"")) ||
                            (valor.startsWith("'") && valor.endsWith("'"))) {
                            if (valor.length() >= 2) {
                                valor = valor.substring(1, valor.length() - 1);
                            }
                        }

                        if (System.getProperty(clave) == null && System.getenv(clave) == null) {
                            System.setProperty(clave, valor);
                        }
                    }
                }
                System.out.println(">>> Archivo .env del backend cargado correctamente desde: " + envPath.toAbsolutePath());
            } catch (IOException e) {
                System.err.println("Advertencia: No se pudo leer el archivo .env en " + envPath + ": " + e.getMessage());
            }
        }
    }
}
