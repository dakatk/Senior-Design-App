package semicolon.com.seniordesignapp.main;

import android.content.Intent;
import android.os.AsyncTask;

import org.jetbrains.annotations.NotNull;

import semicolon.com.seniordesignapp.bluetooth.BleAdapter;
import semicolon.com.seniordesignapp.service.CadenceService;

/**
 * Runs and manages the main update loop of the application
 */
public class MainTask extends AsyncTask<MainActivity, Void, Void> {

    /**
     * This value should be held 'true' until the background task is stopped.
     * This makes sure that when the task is cancelled, it is less messy than if
     * it had to force stop an infinite loop
     */
    private static boolean running = true;

    /**
     * Reference to the BleAdapter object from our MainActivity
     */
    private BleAdapter bleAdapter;

    MainTask (BleAdapter bleAdapter) {
        this.bleAdapter = bleAdapter;
    }

    /**
     * This starts and runs our main loop outside the UI thread.
     * This kind of thing is why developing for Android sucks
     *
     * @param mainActivities mainActivities
     * @return null
     */
    @Override
    protected Void doInBackground(@NotNull MainActivity[] mainActivities) {

        Intent cadenceService = new Intent();
        MainActivity mainActivity = mainActivities[0];

        cadenceService.setClass(mainActivity, CadenceService.class);

        float[] gattValues = new float[256];
        int index = 0;

        while (running) {

            if (mainActivity.isPaused() || !bleAdapter.hasPairedDevice())
                continue;

            if (!CadenceService.ready)
                continue;

            // Record the data that was read from the bluetooth device
            gattValues[index ++] = bleAdapter.getNextGattValue();

            if (index >= gattValues.length) {

                cadenceService.putExtra(CadenceService.BLE_VALUE_ID, gattValues);
                mainActivity.startService(cadenceService);

                index = 0;
            }
        }
        return null;
    }

    /**
     * This method is for when it's time to stop everything
     */
    void stopTask() {

        running = false;
        cancel(true);
    }
}
