package semicolon.com.seniordesignapp.service;

import android.app.IntentService;
import android.content.Intent;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import semicolon.com.seniordesignapp.fft.FFT;

public class CadenceService extends IntentService {

    public static final String BROADCAST_ID = "cadence_send";
    public static final String VALUE_ID = "cadence_value";
    public static final String BLE_VALUE_ID = "ble_value";

    private Intent sendIntent;
    private FFT fft;

    private ArrayList<Double> fftBuffer;

    private double prevFFTValue;

    /**
     * Creates an IntentService. Invoked by your subclass's constructor.
     */
    public CadenceService() {

        super("Cadence Service");

        fftBuffer = new ArrayList<>();
        fft = new FFT();

        sendIntent = new Intent();
        sendIntent.setAction(BROADCAST_ID);

        prevFFTValue = -1.0;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent == null)
            return;

        float bleValue = intent.getFloatExtra(BLE_VALUE_ID, 0.0f);

        fftBuffer.add((double)bleValue);

        if (fftBuffer.size() >= 256) {

            double fftCalc = fft.centerFrequency(fftBuffer, 250);
            fftBuffer.clear();

            if (prevFFTValue == -1.0)
                prevFFTValue = fftCalc;

            else
                prevFFTValue = (prevFFTValue + fftCalc) / 2.0;

            sendIntent.putExtra(VALUE_ID, prevFFTValue);
            sendBroadcast(sendIntent);
        }
    }
}
