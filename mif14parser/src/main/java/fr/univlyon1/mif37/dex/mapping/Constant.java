package fr.univlyon1.mif37.dex.mapping;

/**
 * Created by ecoquery on 20/05/2016.
 */
public class Constant implements Value {

    private int value;

    private Constant(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Constant constant = (Constant) o;

        return value == constant.value;

    }

    @Override
    public int hashCode() {
        return value;
    }

    public static Constant parse(String s) {
        return new Constant(Integer.parseInt(s));
    }
}
