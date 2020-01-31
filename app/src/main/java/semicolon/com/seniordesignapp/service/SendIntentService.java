package semicolon.com.seniordesignapp.service;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import org.jetbrains.annotations.Nullable;

@SuppressLint("Registered")
class SendIntentService extends IntentService {

    private Intent sendIntent;

    SendIntentService (String name, String broadcastID) {

        super(name);

        this.sendIntent = new Intent();
        this.sendIntent.setAction(broadcastID);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        sendBroadcast(this.sendIntent);
    }

    protected Intent getSendIntent() {

        return this.sendIntent;
    }
}
