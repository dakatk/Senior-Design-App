package semicolon.com.seniordesignapp.service;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;
import semicolon.com.seniordesignapp.fft.FFT;

/**
 * Service that's run from the main background task whose sole purpose is to use
 * the runner's most recent motion data read from the bluetooth device to calculate
 * the FFT so that the runner can see their current cadence
 */
public class CadenceService extends IntentService {

    // ID's to filter the extra data sent with each service, and
    // make sure our receiver knows that the service it's sent is valid
    public static final String BROADCAST_ID = "cadence_send";
    public static final String VALUE_ID = "cadence_value";
    public static final String BLE_VALUE_ID = "ble_value";

    public static boolean ready = false;

    /**
     * The new intent that sends data from this service to the receiver
     */
    private final Intent sendIntent = new Intent(BROADCAST_ID);

    /**
     * Creates an IntentService. Invoked by your subclass's constructor.
     */
    public CadenceService() {

        // This does a thing
        super("Cadence Service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent == null)
            return;

        // Extract the data sent from the instance that ran this service
        float[] bleValues = intent.getFloatArrayExtra(BLE_VALUE_ID);

        if (bleValues == null)
            return;

        ready = false;
        sendIntent.putExtra(VALUE_ID, FFT.centerFrequency(bleValues)  * 30.0f);
        ready = true;

        sendBroadcast(sendIntent);
    }
}
