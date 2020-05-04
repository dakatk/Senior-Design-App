package semicolon.com.seniordesignapp.main;

import android.content.Intent;
import android.os.AsyncTask;

import org.jetbrains.annotations.NotNull;

import semicolon.com.seniordesignapp.bluetooth.BleAdapter;
import semicolon.com.seniordesignapp.receiver.CadenceReceiver;
// import semicolon.com.seniordesignapp.service.CadenceService;

/**
 * Runs and manages the main update loop of the application
 */
public class MainTask extends AsyncTask<MainActivity, Void, Void> {

    private static final float SECONDS_PER_UNIT = 0.075f;

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

        MainActivity mainActivity = mainActivities[0];

        float cadence = 0.0f;

        int valueIndex = 0;
        int unitsPerStep = 1;

        int sign = -1;

        while (running) {

            if (mainActivity.isPaused() || !bleAdapter.hasPairedDevice()) {
                continue;
            }

            Float nextGattValue = bleAdapter.getNextGattValue();

            if (nextGattValue == null) {
                continue;
            }

            unitsPerStep ++;

            if (unitsPerStep > 250) {

                cadence = 0.0f;

                valueIndex = 1;
                unitsPerStep = 1;

                Intent sendIntent = new Intent("cadence_send");
                sendIntent.putExtra("cadence_value", cadence);

                mainActivity.sendBroadcast(sendIntent);
            }

            else if ((nextGattValue < 0.0f && sign == 1) || (nextGattValue >= 0.0f && sign == -1)) {

                valueIndex ++;
                sign = -sign;
            }

            if (valueIndex >= 3) {

                float newCadence = 60.0f / (unitsPerStep * SECONDS_PER_UNIT);

                valueIndex = 1;
                unitsPerStep = 1;

                if (cadence <= 0.01f) {
                    cadence = newCadence;
                }

                else {
                    cadence = (cadence + newCadence) / 2.0f;
                }

                Intent sendIntent = new Intent(CadenceReceiver.BROADCAST_ID);
                sendIntent.putExtra(CadenceReceiver.VALUE_ID, cadence);

                mainActivity.sendBroadcast(sendIntent);
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
