package semicolon.com.seniordesignapp.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import semicolon.com.seniordesignapp.R;

/**
 * After the cadence analysis service has run, this class communicates the
 * values it receives weith the UI thread
 */
public class CadenceReceiver extends BroadcastReceiver {

    public static final String BROADCAST_ID = "cadence_send";
    public static final String VALUE_ID = "cadence_value";

    private TextView textView;
    private ImageView imageView;

    public CadenceReceiver(TextView textView, ImageView imageView) {

        this.textView = textView;
        this.imageView = imageView;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onReceive(Context context, @NotNull Intent intent) {

        String action = intent.getAction();

        if (action == null)
            return;

        if (action.equals(BROADCAST_ID)) {

            float cadenceValue = intent.getFloatExtra(VALUE_ID, 0.0f);
            String cadenceString = String.format("%.2f", cadenceValue);

            textView.setText(cadenceString);

            float percentDiff = (cadenceValue - 80.0f) / 80.0f;

            if (percentDiff > 0.05f)
                imageView.setImageResource(R.drawable.red_down_arrow);

            else if (percentDiff < -0.05f)
                imageView.setImageResource(R.drawable.red_up_arrow);

            else imageView.setImageResource(R.drawable.green_dash);
        }
    }
}
