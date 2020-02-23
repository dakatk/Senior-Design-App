package semicolon.com.seniordesignapp.service;

import android.content.Intent;

import androidx.annotation.Nullable;
import semicolon.com.seniordesignapp.fft.ShiftInFFT;

public class CadenceService extends SendIntentService {

    // public static boolean running = false;

    public static final String BROADCAST_ID = "cadence_send";
    public static final String VALUE_ID = "cadence_value";
    public static final String BLE_VALUE_ID = "ble_value";

    private ShiftInFFT shiftInFFT = new ShiftInFFT();

    private double prevFFTValue;

    /**
     * Creates an IntentService. Invoked by your subclass's constructor.
     */
    public CadenceService() {

        super("Cadence Service", BROADCAST_ID);

        prevFFTValue = -1.0;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent == null)
            return;

        float bleValue = intent.getFloatExtra(BLE_VALUE_ID, 0.0f);

        if (shiftInFFT.shiftInAndUpdate((double)bleValue)) {

            System.out.println("UPDATE");

            if (prevFFTValue == -1.0)
                prevFFTValue = shiftInFFT.getFrequency();

            else
                prevFFTValue = (prevFFTValue + shiftInFFT.getFrequency()) / 2.0;

            getSendIntent().putExtra(VALUE_ID, prevFFTValue);
            super.onHandleIntent(intent);
        }
    }
}
