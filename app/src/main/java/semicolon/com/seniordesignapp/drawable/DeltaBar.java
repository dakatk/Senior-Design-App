package semicolon.com.seniordesignapp.drawable;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

/**
 * Same idea as the SeekBar view, visually displays a given delta between the maximum and minimum extrema
 * That the delta should be allowed to reach
 */
public class DeltaBar extends View {

    private int delta;

    private int maximum;
    private int minimum;

    public DeltaBar(Context context, int defaulMax) {

        super(context);

        delta = 0;

        maximum = defaulMax;
        minimum = -defaulMax;
    }

    public void setDelta(int delta) {
        this.delta = delta;
    }

    public void setExtrema(int max) {

        maximum = max;
        minimum = -max;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // TODO draw the thing
    }
}
