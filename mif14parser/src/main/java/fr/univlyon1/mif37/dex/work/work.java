package fr.univlyon1.mif37.dex.work;

import fr.univlyon1.mif37.dex.mapping.Literal;
import fr.univlyon1.mif37.dex.mapping.Mapping;
import fr.univlyon1.mif37.dex.mapping.Tgd;


import java.util.ArrayList;

/**
 * Created by theo on 4/21/17.
 */
public class work {
    Mapping m;
    String type;

    public work(Mapping m) {
        this.m = m;
        this.type = characterize();
    }

    public String characterize() {
        if (allSafe()) {
            if (this.strat()) {
                return "stratified";
            } else if (this.semi()) {

            }
        }

        return "";
    }

    public Boolean allSafe() {

        for (Tgd tgd : m.getTgds()) {
            if (tgd.isSafe()) {
                return false;
            }
        }
        return true;
    }

    public Boolean strat() {

        for (Tgd tgd : m.getTgds()) {
            for (Literal lit : tgd.getLeft()) {
                String name = lit.getAtom().getName();
                Boolean flag = lit.getFlag();
                Boolean test = false;
                System.out.println(lit.getAtom().getName());
                System.out.println(lit.getFlag());

                if (!flag && m.getIDB().contains(name) && this.safeExtend()) {
                    return true;
                }


                return false;
            }

        }
        return false;
    }

    public Boolean semi() {

        for (Tgd tgd : m.getTgds()) {
            for (Literal lit : tgd.getLeft()) {
                String name = lit.getAtom().getName();
                Boolean flag = lit.getFlag();

                System.out.println(lit.getAtom().getName());
                System.out.println(lit.getFlag());
                if (!flag && m.getEDB().contains(name)) {

                    return true;
                }
            }

        }


        return false;
    }

    public Boolean safeExtend() {


        ArrayList<String> neg = new ArrayList<>();
        ArrayList<Literal> lits = this.leftlist();


        for (Literal lit : lits) {
            if (!lit.getFlag()) {
                neg.add(lit.getAtom().getName());
            }
        }

        for (Literal lit : lits) {

            if (lit.getFlag() && neg.contains(lit.getAtom().getName()) ) {
                neg.remove(lit.getAtom().getName());

            }
        }
        if (neg.size() > 0){
            return false;
        }else
            return true;

    }


    public ArrayList<Literal> leftlist() {
        ArrayList<Literal> res = new ArrayList<>();
        for (Tgd tgd : m.getTgds()) {
            for (Literal lit : tgd.getLeft()) {
                res.add(lit);
            }
        }
        return res;
    }


}
