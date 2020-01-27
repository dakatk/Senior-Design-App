package semicolon.com.seniordesignapp.service;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;
import semicolon.com.seniordesignapp.fft.TestFFT;

public class CadenceService extends IntentService {

    public static final String BROADCAST_ID = "cadence_send";
    public static final String VALUE_ID = "cadence_value";

    private TestFFT testFFT;

    /**
     * Creates an IntentService. Invoked by your subclass's constructor.
     */
    public CadenceService() {

        super("Cadence Service");

        testFFT = new TestFFT();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Intent sendIntent = new Intent();
        sendIntent.setAction(BROADCAST_ID);

        while (true) {

            testFFT.shiftAndCycleNext();

            try {
                Thread.sleep(100);
            } catch (Exception e) {}

            double testFreq = testFFT.getFrequency();

            sendIntent.putExtra(VALUE_ID, testFreq);
            sendBroadcast(sendIntent);
        }
    }
}
