package semicolon.com.seniordesignapp.service;

import android.app.IntentService;
import android.content.Intent;

import java.util.ArrayList;

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

    /**
     * The new intent that sends data from this service to the receiver
     */
    private final Intent sendIntent = new Intent(BROADCAST_ID);

    private static ArrayList<Double> fftBuffer = new ArrayList<>();
    private static FFT fft = new FFT();

    /**
     * Creates an IntentService. Invoked by your subclass's constructor.
     */
    public CadenceService() {

        // This does a thing
        super("Cadence Service");

       // sendIntent = new Intent(BROADCAST_ID);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent == null)
            return;

        // Extract the data sent from the instance that ran this service
        float bleValue = intent.getFloatExtra(BLE_VALUE_ID, 0.0f);

        fftBuffer.add((double)bleValue);

        // If we have enough values, calculate the FFT and send it to the receiver
        if (fftBuffer.size() >= 256) {

            sendIntent.putExtra(VALUE_ID, fft.centerFrequency(fftBuffer));
            fftBuffer.clear();

            sendBroadcast(sendIntent);
        }
    }
}
