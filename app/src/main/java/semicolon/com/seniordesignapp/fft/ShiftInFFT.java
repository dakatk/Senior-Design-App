package semicolon.com.seniordesignapp.fft;

import java.util.ArrayList;

// TODO probably just get rid of this class...
public class ShiftInFFT {

    private static final int SAMPLE_SIZE = 256;

    private ArrayList<Double> dataSample;
    private FFT fft;

    public ShiftInFFT() {

        dataSample = new ArrayList<>();
        fft = new FFT();
    }

    public boolean shiftInAndUpdate(double value) {

        System.out.println(dataSample.size());

        dataSample.add(value);

        if (dataSample.size() == SAMPLE_SIZE - 1)
            return true;

        if (dataSample.size() >= SAMPLE_SIZE)
            dataSample.clear();

        return false;
    }

    public double getFrequency() {

        return fft.centerFrequency(dataSample, 250);
    }
}
