package Escaner;

public class ValidarIP {

    // Revisa que la IP esté bien escrita
    public static boolean esValida(String ip) {
        String regex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        return ip != null && ip.matches(regex);
    }

    // Convierte IP a número para poder hacer un ciclo (For)
    public static long ipToLong(String ip) {
        String[] partes = ip.split("\\.");
        long resultado = 0;
        for (int i = 0; i < 4; i++) {
            resultado += Long.parseLong(partes[i]) << (24 - (8 * i));
        }
        return resultado;
    }

    // Convierte el número de vuelta a texto IP
    public static String longToIp(long ip) {
        return ((ip >> 24) & 0xFF) + "." +
               ((ip >> 16) & 0xFF) + "." +
               ((ip >> 8) & 0xFF) + "." +
               (ip & 0xFF);
    }
}

