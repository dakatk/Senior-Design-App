package semicolon.com.seniordesignapp.main;

import androidx.appcompat.app.AppCompatActivity;
import semicolon.com.seniordesignapp.R;
import semicolon.com.seniordesignapp.bluetooth.BleAdapter;
import semicolon.com.seniordesignapp.receiver.CadenceReceiver;
import semicolon.com.seniordesignapp.service.CadenceService;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private CadenceReceiver cadenceReceiver;

    private BluetoothAdapter bluetoothAdapter;
    private Button playbackButton;

    private BleAdapter bleAdapter;
    private MainTask mainTask;

    private boolean running = false;
    private boolean paused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playbackButton = findViewById(R.id.playback_button);
        playbackButton.setOnClickListener(this);

        TextView cadenceView = findViewById(R.id.show_cadence);
        SeekBar cadenceDiff = findViewById(R.id.difference_seekbar);

        cadenceReceiver = new CadenceReceiver(cadenceView, cadenceDiff);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CadenceService.BROADCAST_ID);

        registerReceiver(cadenceReceiver, intentFilter);

        enableLocationServices();
        enableBluetoothServices();

        bleAdapter = new BleAdapter(this, bluetoothAdapter);
        mainTask = new MainTask(bleAdapter);
    }

    private void enableBluetoothServices () {

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {

            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        BluetoothManager bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);

        if (bluetoothManager != null)
            bluetoothAdapter = bluetoothManager.getAdapter();
    }

    private void enableLocationServices () {

        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(R.string.loc_access_alert);
            builder.setMessage(R.string.loc_access_message);

            builder.setPositiveButton(android.R.string.ok, null);

            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
            builder.show();
        }
    }

    public boolean isPaused() {
        return paused;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {

        if (requestCode != PERMISSION_REQUEST_COARSE_LOCATION)
            return;

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            System.out.println("Coarse location permission granted");

        else {

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(R.string.loc_fail_alert);
            builder.setMessage(R.string.loc_fail_message);

            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {}
            });

            builder.show();
        }
    }

    @Override
    public void onResume() {

        super.onResume();

        if (!paused)
            return;

        paused = false;

        enableLocationServices();
        enableBluetoothServices();

        if (bleAdapter != null)
            bleAdapter.enableNotifications();
    }

    @Override
    public void onPause() {

        super.onPause();

        paused = true;

        playbackButton.setText(R.string.button_run);

        if (bleAdapter != null)
            bleAdapter.disableNotifications();
    }

    @Override
    public void onStop() {

        super.onStop();

        playbackButton.setText(R.string.button_run);

        if (bleAdapter != null)
            bleAdapter.disableNotifications();
    }

    @Override
    public void onDestroy() {

        System.out.println("Stopping");

        super.onDestroy();

        bleAdapter.disableNotifications();

        unregisterReceiver(cadenceReceiver);
    }

    @Override
    public void onClick(View v) {

        running = !running;

        if (running) {

            mainTask.execute(this);
            bleAdapter.enableNotifications();
        }

        else {

            mainTask.stopTask();
            bleAdapter.disableNotifications();
        }
    }
}
