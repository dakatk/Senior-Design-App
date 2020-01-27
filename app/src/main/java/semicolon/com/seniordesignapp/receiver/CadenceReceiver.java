package semicolon.com.seniordesignapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import semicolon.com.seniordesignapp.service.CadenceService;

public class CadenceReceiver extends BroadcastReceiver {

    private TextView view;

    public CadenceReceiver(TextView view) {
        this.view = view;
    }

    @Override
    public void onReceive(Context context, @NotNull Intent intent) {

        double cadence = intent.getDoubleExtra(CadenceService.VALUE_ID, 0.0);

        this.view.setText(String.valueOf(cadence));
    }
}
