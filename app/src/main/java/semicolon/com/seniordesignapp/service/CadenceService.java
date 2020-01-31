package semicolon.com.seniordesignapp.service;

import android.content.Intent;

import androidx.annotation.Nullable;
import semicolon.com.seniordesignapp.fft.TestFFT;

public class CadenceService extends SendIntentService {

    // public static boolean running = false;

    public static final String BROADCAST_ID = "cadence_send";
    public static final String VALUE_ID = "cadence_value";

    private TestFFT testFFT = new TestFFT();

    /**
     * Creates an IntentService. Invoked by your subclass's constructor.
     */
    public CadenceService() {

        super("Cadence Service", BROADCAST_ID);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        testFFT.shiftAndCycleNext();
        getSendIntent().putExtra(VALUE_ID, testFFT.getFrequency());

        super.onHandleIntent(intent);
    }
}
