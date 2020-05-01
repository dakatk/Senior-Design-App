package semicolon.com.seniordesignapp.fft;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import semicolon.com.seniordesignapp.fft.maths.Complex;

/**
 *
 */
public class FFT {

    private FFT() {}

    /**
     * Bit reversal algorithm, altered for use with FFT
     *
     * @param n n
     * @param bits bits
     * @return n with bits reversed
     */
    @Contract(pure = true)
    private static int bitReverse(int n, int bits) {

        int reversed = n;
        int count = bits - 1;

        for (n >>= 1; n > 0; n >>= 1){

            reversed <<= 1;
            reversed |= (n & 1);

            count --;
        }
        return ((reversed << count) & ((1 << bits) - 1));
    }

    /**
     * Swaps buffer[i] with buffer[j]
     *
     * @param buffer buffer
     * @param i i
     * @param j j
     */
    private static void swap(@NotNull List<Complex> buffer, int i, int j) {

        Complex temp = buffer.get(j);

        buffer.set(j, buffer.get(i).clone());
        buffer.set(i, temp);
    }

    /**
     * Compute the FFT values given a radix-2 sized array of data
     *
     * @param values values
     * @return computed frequencies
     */
    @NotNull
    private static ArrayList<Complex> compute(float[] values) {

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
                    Complex exp = new Complex((float)Math.cos(term), (float)Math.sin(term)).mul(odd);

                    buffer.set(evenIndex, even.add(exp));
                    buffer.set(oddIndex, even.sub(exp));
                }
            }
        }
        return buffer;
    }

    /**
     * For max magnitude at indices n0 and N - n0, Fsin = (Fs * n0) / N
     *
     * @param values values
     * @return Sinusoidal frequency of computed FFT values given a list of data
     */
    public static float centerFrequency(@NotNull float[] values) {

        final int n = values.length;

        List<Complex> fft = compute(values);

        double maxValue = fft.get(0).magnitude();
        int maxIndex = 0;

        for (int i = 1; i < n / 2; i ++) {

            Complex c = fft.get(i);

            if (c.magnitude() >= maxValue) {

                maxIndex = i;
                maxValue = c.magnitude();
            }
        }

        float sampling_freq = 250.0f;

        return (sampling_freq * maxIndex) / n;
    }
}
