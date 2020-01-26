package semicolon.com.seniordesignapp.main;

import androidx.appcompat.app.AppCompatActivity;
import semicolon.com.seniordesignapp.R;
import semicolon.com.seniordesignapp.receiver.CadenceReceiver;
import semicolon.com.seniordesignapp.service.CadenceService;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Intent cadenceIntent;
    private CadenceReceiver cadenceReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView cadenceView = findViewById(R.id.ShowCadence);
        cadenceReceiver = new CadenceReceiver(cadenceView);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CadenceService.BROADCAST_ID);

        registerReceiver(cadenceReceiver, intentFilter);
    }

    @Override
    public void onStart() {

        super.onStart();

        cadenceIntent = new Intent();
        cadenceIntent.setClass(this, CadenceService.class);

        startService(cadenceIntent);
    }

    @Override
    public void onStop() {

        super.onStop();

        if (cadenceIntent != null)
            stopService(cadenceIntent);

        unregisterReceiver(cadenceReceiver);
    }
}
