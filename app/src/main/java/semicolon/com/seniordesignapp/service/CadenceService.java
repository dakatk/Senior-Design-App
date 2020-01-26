package semicolon.com.seniordesignapp.service;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

public class CadenceService extends IntentService {

    public static final String BROADCAST_ID = "cadence_send";
    public static final String VALUE_ID = "cadence_value";

    /**
     * Creates an IntentService. Invoked by your subclass's constructor.
     */
    public CadenceService() {
        super("Cadence Service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        for (int i = 0; i < 256; i ++) {

            Intent sendIntent = new Intent();

            sendIntent.setAction(BROADCAST_ID);
            sendIntent.putExtra(VALUE_ID, (float)i);

            sendBroadcast(sendIntent);
        }
    }
}
