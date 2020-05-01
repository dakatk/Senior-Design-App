package semicolon.com.seniordesignapp.fft.maths;

import android.annotation.SuppressLint;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Complex implements Comparable<Complex> {

    private float real;
    private float imag;

    public Complex(float real, float imag) {

        this.real = real;
        this.imag = imag;
    }

    @NotNull
    public static ArrayList<Complex> fromScalars(@NotNull float[] scalars) {

        final ArrayList<Complex> complexes = new ArrayList<>();

        for (float scalar : scalars) {
            complexes.add(new Complex(scalar, 0.0f));
        }

        return complexes;
    }

    public Complex add(@NotNull Complex other) {
        return new Complex(this.real + other.real, this.imag + other.imag);
    }

    public Complex sub(@NotNull Complex other) {
        return new Complex(this.real - other.real, this.imag - other.imag);
    }

    public Complex mul(@NotNull Complex other) {
        return new Complex(this.real * other.real - this.imag * other.imag,
                this.real * other.imag + this.imag * other.real);
    }

    public double magnitude() {
        return Math.sqrt((this.real * this.real) + (this.imag * this.imag));
    }

    @NotNull
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Complex clone() {
        return new Complex(this.real, this.imag);
    }

    @SuppressLint("DefaultLocale")
    @NotNull
    @Override
    public String toString() {
        return String.format("(%f, %f)", this.real, this.imag);
    }

    @Override
    public int compareTo(@NotNull Complex other) {

        double m = magnitude();
        double n = other.magnitude();

        if (m > n)
            return 1;

        else if (m < n)
            return -1;

        return 0;
    }
}
