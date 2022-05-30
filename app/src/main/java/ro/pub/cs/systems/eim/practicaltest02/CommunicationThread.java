package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }

        try {

            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type!");
            String request = bufferedReader.readLine();
            String response = "EUR OR USD";

            HttpClient httpClient = new DefaultHttpClient();

            if (request.startsWith("USD")) {
                if (serverThread.UsdData == null || Duration.between(serverThread.UsdData.reqTime, LocalDateTime.now()).toMinutes() > 1) {
                    Log.i(Constants.TAG, "[COMMUNICATION THREAD] New USD Request");
                    HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS + "USD.json");
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    String requestResponse = EntityUtils.toString(httpEntity);

                    JSONObject content = new JSONObject(requestResponse);
                    response = content.getJSONObject("bpi").getJSONObject("USD").getString("rate");
                    serverThread.UsdData = new CurrencyRateData(response, LocalDateTime.now());
                } else {
                    response = serverThread.UsdData.rate;
                }

            }
            if (request.startsWith("EUR")) {
                if (serverThread.EurData == null || Duration.between(serverThread.EurData.reqTime, LocalDateTime.now()).toMinutes() > 1) {
                    Log.i(Constants.TAG, "[COMMUNICATION THREAD] New EUR Request");
                    HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS + "EUR.json");
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    String requestResponse = EntityUtils.toString(httpEntity);

                    JSONObject content = new JSONObject(requestResponse);
                    response = content.getJSONObject("bpi").getJSONObject("EUR").getString("rate");
                    serverThread.EurData = new CurrencyRateData(response, LocalDateTime.now());

                } else {
                    response = serverThread.EurData.rate;
                }

            }

            printWriter.println(response);
            printWriter.flush();
        } catch (Exception e) {
            Log.e(Constants.TAG, "Exception in comm thread " + e.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                }
            }
        }
    }
}
