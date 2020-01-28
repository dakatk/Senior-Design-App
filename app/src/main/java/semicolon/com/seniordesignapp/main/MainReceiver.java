package semicolon.com.seniordesignapp.main;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import semicolon.com.seniordesignapp.service.BluetoothService;
import semicolon.com.seniordesignapp.service.CadenceService;

public class MainReceiver extends BroadcastReceiver {

    private TextView view;

    public MainReceiver(TextView view) {

        this.view = view;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onReceive(Context context, @NotNull Intent intent) {

        String action = intent.getAction();

        if (action == null) return;

        if (action.equals(CadenceService.BROADCAST_ID)) {

            double cadenceValue = intent.getDoubleExtra(CadenceService.VALUE_ID, 0.0) * 60.0;
            String cadenceString = String.format("%.2f SPM/F", cadenceValue / 2.0);

            view.setText(cadenceString);
        }

        else if (action.equals(BluetoothService.BROADCAST_ID)) {

            // TODO
        }
    }
}
