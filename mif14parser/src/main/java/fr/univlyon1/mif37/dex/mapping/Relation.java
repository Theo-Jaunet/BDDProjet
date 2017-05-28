package fr.univlyon1.mif37.dex.mapping;

import java.util.List;

/**
 * Created by ecoquery on 20/05/2016.
 */
public class Relation {

    private String name;
    private String[] attributes;

    public Relation(String name, List<String> attributes) {
        this.name = name;
        this.attributes = attributes.toArray(new String[attributes.size()]);
    }

    public String getName() {
        return name;
    }

    public void setAttributes(String[] attributes) {
        this.attributes = attributes;
    }

    public String[] getAttributes() {
        return attributes;
    }

    public void show_you(){
        String mes = name+ " : ";
        for(int i =0;i< attributes.length;i++){
            mes += attributes[i]+ " | ";
        }
        System.out.println(mes);
    }
}
