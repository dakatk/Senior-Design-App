package semicolon.com.seniordesignapp.fft;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import semicolon.com.seniordesignapp.fft.maths.Complex;

class FFT {

    // Bit reversal algorithm, altered for use with FFT
    @Contract(pure = true)
    private int bitReverse(int n, int bits) {

        int reversed = n;
        int count = bits - 1;

        for (n >>= 1; n > 0; n >>= 1){

            reversed <<= 1;
            reversed |= (n & 1);

            count --;
        }
        return ((reversed << count) & ((1 << bits) - 1));
    }

    private void swap(@NotNull List<Complex> buffer, int i, int j) {

        Complex temp = buffer.get(j);

        buffer.set(j, buffer.get(i).clone());
        buffer.set(i, temp);
    }

    @NotNull
    private ArrayList<Complex> compute(List<Double> values) {

        ArrayList<Complex> buffer = Complex.fromScalars(values);

        int bits = (int)(Math.log(buffer.size()) / Math.log(2));

        // Bit-reversed indices used to combine FFT stages
        for (int j = 1; j < buffer.size() / 2; j ++) {

            int swapPos = bitReverse(j, bits);

            swap(buffer, swapPos, j);
        }

        // Radix-2 Cooley-Tukey in-place formula
        for (int n = 2; n <= buffer.size(); n <<= 1) {

            for (int i = 0; i < buffer.size(); i += n) {

                for (int k = 0; k < n / 2; k ++) {

                    int evenIndex = i + k;
                    int oddIndex = i + k + (n / 2);

                    Complex even = buffer.get(evenIndex);
                    Complex odd = buffer.get(oddIndex);

                    double term = (-2 * Math.PI * k) / (double)n;

                    // Identity: e^(j * x) = cos(x) + (j * sin(x))
                    Complex exp = new Complex(Math.cos(term), Math.sin(term)).mul(odd);

                    buffer.set(evenIndex, even.add(exp));
                    buffer.set(oddIndex, even.sub(exp));
                }
            }
        }
        return buffer;
    }

    // Formula: for max magnitude at indices n0 and N - n0, Fsin = (Fs * n0) / N
    double centerFrequency(@NotNull List<Double> values, double samplingFreq) {

        final int n = values.size();

        List<Complex> fft = this.compute(values);
        final List<Double> magnitudes = new ArrayList<>();

        fft.forEach(new Consumer<Complex>() {

            @Override
            public void accept(Complex value) {
                magnitudes.add(value.magnitude());
            }
        });

        Double localMax = Collections.max(magnitudes.subList(0, n / 2));
        int maxIndex = magnitudes.indexOf(localMax);

        return (samplingFreq * maxIndex) / n;
    }
}
