package fr.univlyon1.mif37.dex.mapping;

import java.util.List;

public class AbstractRelation {

    private String name;
    private AbstractArgument[] attributes;

    public AbstractRelation(String name, List<AbstractArgument> attributes) {
        this.name = name;
        this.attributes = attributes.toArray(new AbstractArgument[attributes.size()]);
    }

    public String getName() {
        return name;
    }

    public AbstractArgument[] getAttributes() {
        return attributes;
    }
}
