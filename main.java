
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class CalculadoraServer {

    public static void main(String[] args) throws IOException {

        // Crear el servidor en el puerto 8080
        HttpServer servidor = HttpServer.create(
            new InetSocketAddress(8080), 0
        );

        // Mostrar la página HTML
        servidor.createContext("/", intercambio -> {

            if (!intercambio.getRequestURI().getPath().equals("/")) {
                enviarRespuesta(intercambio, 404,
                    "{\"error\":\"Ruta no encontrada\"}");
                return;
            }

            byte[] pagina = Files.readAllBytes(
                Path.of("index.html")
            );

            intercambio.getResponseHeaders().set(
                "Content-Type", "text/html; charset=UTF-8"
            );

            intercambio.sendResponseHeaders(200, pagina.length);

            intercambio.getResponseBody().write(pagina);
            intercambio.close();
        });

        // Recibir y realizar las operaciones matemáticas
        servidor.createContext("/api/calcular", intercambio -> {

            if (!intercambio.getRequestMethod().equals("POST")) {
                enviarRespuesta(intercambio, 405,
                    "{\"error\":\"Método no permitido\"}");
                return;
            }

            try {

                // Leer los datos enviados desde HTML
                String cuerpo = new String(
                    intercambio.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8
                );

                Map<String, String> parametros =
                    obtenerParametros(cuerpo);

                // Obtener los números y la operación
                int numero1 = Integer.parseInt(
                    parametros.get("numero1")
                );

                int numero2 = Integer.parseInt(
                    parametros.get("numero2")
                );

                String operacion = parametros.get("operacion");

                // Comprobar el intervalo permitido
                if (numero1 < -99999 || numero1 > 99999 ||
                    numero2 < -99999 || numero2 > 99999) {

                    throw new IllegalArgumentException(
                        "Los números deben estar entre -99999 y 99999"
                    );
                }

                BigInteger a = BigInteger.valueOf(numero1);
                BigInteger b = BigInteger.valueOf(numero2);

                BigInteger resultado;

                // Realizar la operación seleccionada
                switch (operacion) {

                    case "suma":

                        resultado = a.add(b);
                        break;

                    case "resta":

                        resultado = a.subtract(b);
                        break;

                    case "multiplicacion":

                        resultado = a.multiply(b);
                        break;

                    case "potencia":

                        int exponente = numero2;

                        if (exponente < 0 && numero1 == 0) {
                            throw new IllegalArgumentException(
                                "No se puede elevar cero a un exponente negativo"
                            );
                        }

                        BigInteger potencia = a.pow(
                            Math.abs(exponente)
                        );

                        if (exponente < 0) {

                            String signo =
                                (numero1 < 0 && exponente % 2 != 0)
                                ? "-" : "";

                            enviarRespuesta(intercambio, 200,
                                "{\"resultado\":\"" + signo +
                                "1/" + potencia + "\"}");

                            return;
                        }

                        resultado = potencia;
                        break;

                    default:

                        throw new IllegalArgumentException(
                            "Operación no permitida"
                        );
                }

                // Enviar el resultado
                enviarRespuesta(intercambio, 200,
                    "{\"resultado\":\"" + resultado + "\"}");

            } catch (NumberFormatException e) {

                enviarRespuesta(intercambio, 400,
                    "{\"error\":\"Introduce números enteros válidos\"}");

            } catch (IllegalArgumentException e) {

                enviarRespuesta(intercambio, 400,
                    "{\"error\":\"" + e.getMessage() + "\"}");

            } catch (Exception e) {

                enviarRespuesta(intercambio, 500,
                    "{\"error\":\"Error al realizar la operación\"}");

            }

        });

        // Iniciar el servidor
        servidor.start();

        System.out.println("Calculadora iniciada.");
        System.out.println("Abre http://localhost:8080");
    }

    // Convertir los parámetros recibidos a un mapa
    private static Map<String, String> obtenerParametros(
        String cuerpo
    ) {

        Map<String, String> parametros = new HashMap<>();

        for (String parametro : cuerpo.split("&")) {

            String[] partes = parametro.split("=", 2);

            if (partes.length == 2) {

                String clave = URLDecoder.decode(
                    partes[0], StandardCharsets.UTF_8
                );

                String valor = URLDecoder.decode(
                    partes[1], StandardCharsets.UTF_8
                );

                parametros.put(clave, valor);
            }
        }

        return parametros;
    }

    // Enviar respuestas al navegador
    private static void enviarRespuesta(
        HttpExchange intercambio,
        int codigo,
        String respuesta
    ) throws IOException {

        byte[] datos = respuesta.getBytes(
            StandardCharsets.UTF_8
        );

        intercambio.getResponseHeaders().set(
            "Content-Type", "application/json; charset=UTF-8"
        );

        intercambio.sendResponseHeaders(codigo, datos.length);

        intercambio.getResponseBody().write(datos);
        intercambio.close();
    }
}
