package semicolon.com.seniordesignapp.main;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.SeekBar;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import semicolon.com.seniordesignapp.service.BluetoothService;
import semicolon.com.seniordesignapp.service.CadenceService;

public class MainReceiver extends BroadcastReceiver {

    private TextView view;
    private SeekBar seekBar;

    public MainReceiver(TextView view, SeekBar seekBar) {

        this.view = view;
        this.seekBar = seekBar;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onReceive(Context context, @NotNull Intent intent) {

        String action = intent.getAction();

        if (action == null) return;

        if (action.equals(CadenceService.BROADCAST_ID)) {

            double cadenceValue = intent.getDoubleExtra(CadenceService.VALUE_ID, 0.0) * 30.0;
            String cadenceString = String.format("%.2f", cadenceValue);

            view.setText(cadenceString);
            seekBar.setProgress((int)cadenceValue - 40, true);
        }

        else if (action.equals(BluetoothService.BROADCAST_ID)) {

            // TODO
        }
    }
}
