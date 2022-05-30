package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread{

    private int port = 0;
    private ServerSocket serverSocket = null;

    public CurrencyRateData EurData = null;
    public CurrencyRateData UsdData = null;

    public ServerThread(int port) {
        this.port = port;
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Log.i(Constants.TAG, "[SERVER THREAD] Waiting for a client invocation...");
                Socket socket = serverSocket.accept();

                Log.i(Constants.TAG, "[SERVER THREAD] A connection request was received from " + socket.getInetAddress() + ":" + socket.getLocalPort());
                CommunicationThread communicationThread = new CommunicationThread(this, socket);
                communicationThread.start();
            }

        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());

        }
    }

    public void stopThread() {
        interrupt();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
            }
        }
    }
}
