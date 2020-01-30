package semicolon.com.seniordesignapp.main;

import androidx.appcompat.app.AppCompatActivity;
import semicolon.com.seniordesignapp.R;
import semicolon.com.seniordesignapp.service.BluetoothService;
import semicolon.com.seniordesignapp.service.CadenceService;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MainReceiver mainReceiver;
    private Button playbackButton;

    private Thread serviceThread;

    private boolean running = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playbackButton = findViewById(R.id.playback_button);
        playbackButton.setOnClickListener(this);

        TextView cadenceView = findViewById(R.id.show_cadence);
        SeekBar cadenceDiff = findViewById(R.id.difference_seekbar);

        mainReceiver = new MainReceiver(cadenceView, cadenceDiff);

        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(CadenceService.BROADCAST_ID);
        intentFilter.addAction(BluetoothService.BROADCAST_ID);

        registerReceiver(mainReceiver, intentFilter);

        serviceThread = new Thread(new Runnable() {

            @Override
            public void run() {

                Intent bluetoothService = new Intent();
                Intent cadenceService = new Intent();

                bluetoothService.setClass(MainActivity.this, BluetoothService.class);
                cadenceService.setClass(MainActivity.this, CadenceService.class);

                while (true) {

                    if (!MainActivity.this.running)
                        continue;

                    startService(bluetoothService);
                    startService(cadenceService);

                    try {
                        Thread.sleep(500);
                    } catch (Exception ignored) {}
                }
            }
        });

        serviceThread.start();
    }

    @Override
    public void onPause() {

        super.onPause();

        running = false;
        playbackButton.setText(R.string.button_run);
    }

    @Override
    public void onStop() {

        super.onStop();

        running = false;
        playbackButton.setText(R.string.button_run);
    }

    @Override
    public void onDestroy() {

        System.out.println("Stopping");

        super.onDestroy();

        try {
            serviceThread.join();
        } catch (Exception ignored) {}

        unregisterReceiver(mainReceiver);
    }

    @Override
    public void onClick(View v) {

        running = !running;

        if (running)
            playbackButton.setText(R.string.button_stop);

        else playbackButton.setText(R.string.button_run);
    }
}
