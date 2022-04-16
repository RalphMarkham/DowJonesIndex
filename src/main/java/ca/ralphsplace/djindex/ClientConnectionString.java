package ca.ralphsplace.djindex;

public class ClientConnectionString {

    private ClientConnectionString(){}

    private static final ThreadLocal<String> clientConnectionStr = new ThreadLocal<>();

    public static String getClientConnectionStr() {
        return clientConnectionStr.get();
    }

    public static void setClientConnectionStr(String connectionString) {
        clientConnectionStr.set(connectionString);
    }

    public static void clear() {
        clientConnectionStr.remove();
    }
}
