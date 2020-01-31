package semicolon.com.seniordesignapp.service;

import android.content.Intent;

import androidx.annotation.Nullable;

public class BluetoothService extends SendIntentService {

    public static final String BROADCAST_ID = "bluetooth_send";
    public static final String VALUE_ID = "bluetooth_value";

    /**
     * Creates an IntentService. Invoked by your subclass's constructor.
     */
    public BluetoothService() {

        super("Bluetooth Service", BROADCAST_ID);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        super.onHandleIntent(intent);
    }
}
