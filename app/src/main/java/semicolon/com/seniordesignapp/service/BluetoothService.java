package semicolon.com.seniordesignapp.service;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

public class BluetoothService extends IntentService {

    public static final String BROADCAST_ID = "bluetooth_send";
    public static final String VALUE_ID = "bluetooth_value";

    /**
     * Creates an IntentService. Invoked by your subclass's constructor.
     */
    public BluetoothService() {

        super("Bluetooth Service");


    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Intent sendIntent = new Intent();
        sendIntent.setAction(BROADCAST_ID);

        sendBroadcast(sendIntent);
    }
}
