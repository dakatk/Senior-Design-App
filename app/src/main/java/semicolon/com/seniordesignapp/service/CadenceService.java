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

    private static ArrayList<Double> fftBuffer = new ArrayList<>();
    private static FFT fft = new FFT();

    private static double prevFFTValue = -1.0;
    private static float lastBleValue = 0.0f;

    /**
     * Creates an IntentService. Invoked by your subclass's constructor.
     */
    public CadenceService() {

        super("Cadence Service");

        sendIntent = new Intent();
        sendIntent.setAction(BROADCAST_ID);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent == null)
            return;

        float bleValue = intent.getFloatExtra(BLE_VALUE_ID, 0.0f);

        sendIntent.putExtra(VALUE_ID, (double)bleValue);
        sendBroadcast(sendIntent);

        /*if (bleValue == lastBleValue)
            return;

        lastBleValue = bleValue;

        fftBuffer.add((double)bleValue);
        //System.out.println(fftBuffer.size());

        if (fftBuffer.size() >= 128) {

            double fftCalc = fft.centerFrequency(fftBuffer, 250);
            //System.out.println("FFT");
            fftBuffer.clear();

            if (prevFFTValue == -1.0)
                prevFFTValue = fftCalc;

            else
                prevFFTValue = (prevFFTValue + fftCalc) / 2.0;

            sendIntent.putExtra(VALUE_ID, prevFFTValue);
            sendBroadcast(sendIntent);
        }*/
    }
}
