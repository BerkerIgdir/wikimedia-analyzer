package business;

import business.interfaces.WikiMediaAnalyzer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ConcurrentQuerier<T extends WikiMediaAnalyzer> implements Runnable {
    private static final int PORT = 9093;
    private final List<T> servicesToQuery;

    private final PrintWriter out;
    private final Socket clientSocket;
    private final ServerSocket serverSocket;


    public ConcurrentQuerier(List<T> servicesToQuery) {
        try {
            this.serverSocket = new ServerSocket(PORT);
            this.clientSocket = serverSocket.accept();
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.servicesToQuery = servicesToQuery;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("QUERIER COULD NOT BE CREATED");
        }
    }

    public ConcurrentQuerier(List<T> servicesToQuery, int portToBroadcast) throws IOException {
        this.serverSocket = new ServerSocket(portToBroadcast);
        this.clientSocket = serverSocket.accept();
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.servicesToQuery = servicesToQuery;
    }

    @Override
    public void run() {
        if (!Thread.currentThread().isInterrupted()) {
            servicesToQuery.forEach(wikiMediaAnalyzer -> wikiMediaAnalyzer.printInfo(out));
        }
    }
    public void close(){

        try {
            out.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            // DO NOTHING
        }

    }
}
