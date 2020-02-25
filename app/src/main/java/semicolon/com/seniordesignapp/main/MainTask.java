package semicolon.com.seniordesignapp.main;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import org.jetbrains.annotations.NotNull;

import semicolon.com.seniordesignapp.bluetooth.BleAdapter;
import semicolon.com.seniordesignapp.service.CadenceService;

public class MainTask extends AsyncTask<Context, Void, Void> {

    private boolean running = true;

    private BleAdapter bleAdapter;

    MainTask (BleAdapter bleAdapter) {
        this.bleAdapter = bleAdapter;
    }

    @Override
    protected Void doInBackground(@NotNull Context[] context) {

        Intent cadenceService = new Intent();

        cadenceService.setClass(context[0], CadenceService.class);

        while (running) {

            Float data = bleAdapter.getNextGattValue();

            if (data != null)
                cadenceService.putExtra(CadenceService.BLE_VALUE_ID, data);

            context[0].startService(cadenceService);

            try {
                Thread.sleep(0, 500000);
            } catch (Exception ignored) {}
        }
        return null;
    }

    public void stopTask() {

        running = false;
        cancel(true);
    }
}
