package fr.univlyon1.mif37.dex.mapping;

import java.util.Collection;

public class Mapping {

    private Collection<Relation> edb;
    private Collection<AbstractRelation> idb;
    private Collection<Tgd> tgds;

    public Mapping(Collection<Relation> edb, Collection<AbstractRelation> idb, Collection<Tgd> tgds) {
        this.edb = edb;
        this.idb = idb;
        this.tgds = tgds;
    }

    public Collection<Relation> getEDB() {
        return edb;
    }

    public Collection<AbstractRelation> getIDB() {
        return idb;
    }

    public Collection<Tgd> getTgds() {
        return tgds;
    }
}
