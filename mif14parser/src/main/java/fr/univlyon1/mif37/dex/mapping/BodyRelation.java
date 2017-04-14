package fr.univlyon1.mif37.dex.mapping;

import java.util.List;

public abstract class BodyRelation<T> {

    private String name;
    private T[] attributes;

    public String getName() {
        return name;
    }

    public T[] getAttributes() {
        return attributes;
    }
}
