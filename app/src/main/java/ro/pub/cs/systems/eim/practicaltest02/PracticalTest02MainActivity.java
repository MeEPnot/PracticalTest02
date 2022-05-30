package ro.pub.cs.systems.eim.practicaltest02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest02MainActivity extends AppCompatActivity {

    private EditText clientPort;
    private EditText serverPort;
    private EditText clientAddress;
    private EditText data;
    private Button startServer;
    private Button startClient;
    private TextView result;

    private ServerThread serverThread = null;
    private ClientThread clientThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        clientPort =  findViewById(R.id.ClientConnectPortEditText);
        serverPort = findViewById(R.id.ServerPortEditText);
        clientAddress = findViewById(R.id.ClientConnectAddressEditText);
        data = findViewById(R.id.ClientRequestDataEditText);
        result = findViewById(R.id.resultEditText);

        startServer = findViewById(R.id.StarServerButton);
        startServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String serverPortStr = serverPort.getText().toString();

                if (serverPortStr == null || serverPortStr.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                    return;
                }
                serverThread = new ServerThread(Integer.parseInt(serverPortStr));

                serverThread.start();
            }
        });

        startClient = findViewById(R.id.SendRequestButton);
        startClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String clientAddressStr = clientAddress.getText().toString();
                String clientPortStr = clientPort.getText().toString();
                String dataStr = data.getText().toString();
                clientThread = new ClientThread(clientAddressStr, Integer.parseInt(clientPortStr), dataStr, result);
                clientThread.start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }


}