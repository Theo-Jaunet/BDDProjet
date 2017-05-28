package fr.univlyon1.mif37.dex.work;

import com.sun.javafx.runtime.async.AsyncOperationListener;
import fr.univlyon1.mif37.dex.mapping.Atom;
import fr.univlyon1.mif37.dex.mapping.Literal;
import fr.univlyon1.mif37.dex.mapping.Mapping;
import fr.univlyon1.mif37.dex.mapping.Relation;
import fr.univlyon1.mif37.dex.mapping.Tgd;
import fr.univlyon1.mif37.dex.mapping.Variable;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by theo on 4/21/17.
 */
public final class Work {

    Mapping m;
    String type;
    HashMap<String, Integer> stratum;
    HashMap<Integer, ArrayList<Tgd>> partitions;
    ArrayList<Relation> final_edb;

    public ArrayList<Relation> getFinal_edb() {
        return final_edb;
    }

    public Mapping getM() {
        return m;
    }

    public String getType() {
        return type;
    }

    public HashMap<String, Integer> getStratum() {
        return stratum;
    }

    public Work(Mapping m) {
        this.stratum = new HashMap<>();
        this.partitions = new HashMap();
        this.m = m;
        this.type = characterize();
        this.stratification();
        this.partitioning();
        this.final_edb = this.megaEval((ArrayList<Relation>)m.getEDB());
    }

    private String characterize() {
        if (allSafe()) {
            if (this.strat()) {
                return "stratified";
            } else if (this.semi()) {
                return "semi";
            } else {
                return "pos";
            }
        }
        return "";
    }

    public Boolean allSafe() {

        for (Tgd tgd : m.getTgds()) {
            if (!tgd.isSafe()) {
                return false;
            }
        }
        return true;
    }

    public Boolean strat() {

        HashSet<String> list = new HashSet<>();
        m.getIDB().forEach((R) -> {
            list.add(R.getName());
        });

        for (Tgd tgd : m.getTgds()) {
            for (Literal lit : tgd.getLeft()) {
                String name = lit.getAtom().getName();
                Boolean flag = lit.getFlag();

                // pb du contains
                if (!flag && list.contains(name) && this.safeExtend()) {
                    return true;
                }
            }

        }
        return false;
    }

    public Boolean semi() {

        HashSet<String> list = new HashSet<>();
        m.getEDB().forEach((R) -> {
            list.add(R.getName());
        });

        for (Tgd tgd : m.getTgds()) {
            for (Literal lit : tgd.getLeft()) {
                String name = lit.getAtom().getName();
                Boolean flag = lit.getFlag();

                //System.out.println(lit.getAtom().getName());
                //System.out.println(lit.getFlag());
                if (!flag && list.contains(name)) {

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

            if (lit.getFlag() && neg.contains(lit.getAtom().getName())) {
                neg.remove(lit.getAtom().getName());
            }
        }
        if (neg.size() > 0) {
            return false;
        } else
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

    public void stratification() {

        Boolean change = true;
        Integer nbStratum = 1,
                predicateCount,
                tmp, actualStratum;
        Atom p, q;

        for (Tgd tgd : this.m.getTgds()) {

            for (Literal lit : tgd.getLeft()) {
                this.stratum.put(lit.getAtom().getName(), 1);
            }
            this.stratum.put(tgd.getRight().getName(), 1);
        }
        predicateCount = this.stratum.size();

        while (change && nbStratum < predicateCount) {
            change = false;
            for (Tgd tgd : m.getTgds()) {

                p = tgd.getRight();
                actualStratum = this.stratum.get(p.getName());

                for (Literal lit : tgd.getLeft()) {

                    if (!lit.getFlag()) {
                        tmp = Integer.max(this.stratum.get(lit.getAtom().getName()) + 1,
                                this.stratum.get(p.getName()));
                        if (tmp > actualStratum) {
                            this.stratum.put(p.getName(), tmp);
                            change = true;
                        }

                    } else {
                        tmp = Integer.max(this.stratum.get(lit.getAtom().getName()),
                                this.stratum.get(p.getName()));
                        if (tmp > actualStratum) {
                            //System.out.println(tmp);
                            this.stratum.put(p.getName(), tmp);
                            change = true;
                        }
                    }
                    if (actualStratum > nbStratum) {
                        nbStratum = actualStratum;
                    }
                }
            }

        }
    }

    public void afficher_stratum() {
        this.stratum.forEach((k, v) -> {
                    String afficher = k;
                    afficher += " : ";
                    afficher += v.toString();
                    System.out.println(afficher);
                }
        );
    }

    public void partitioning() {
        Integer nbpart = Collections.max(this.stratum.values());

        for (Integer i = 1; i <= nbpart; i++) {
            for (HashMap.Entry<String, Integer> entry : this.stratum.entrySet()) {
                if (Objects.equals(entry.getValue(), i)) {
                    ArrayList<Tgd> tmpTgd;
                    if (this.partitions.containsKey(i)) {
                        tmpTgd = this.partitions.get(i);
                    } else {
                        tmpTgd = new ArrayList();
                    }
                    tmpTgd.addAll(this.getDefs(entry.getKey()));
                    this.partitions.put(i, tmpTgd);
                }
            }
        }
    }

    public ArrayList<Tgd> getDefs(String head) {
        ArrayList<Tgd> result = new ArrayList<>();
        this.stratum.forEach((key, val) -> {
            if (key.equals(head)) {
                m.getTgds().forEach((tgd) -> {
                    if (tgd.getRight().getName().equals(head)) {
                        result.add(tgd);
                    }
                });
            }
        });
        return result;
    }

    public void afficher_partitions() {
        System.out.println(this.partitions.size());
        this.partitions.forEach((k, v) -> {
                    String afficher = k.toString();
                    afficher += " : ";
                    afficher += v.toString();
                    System.out.println(afficher);
                }
        );
    }

    public ArrayList<Relation> megaEval(ArrayList<Relation> edb) {
        ArrayList<Relation> oldEdb = new ArrayList<>(edb);
        Boolean change = true;
        for (Map.Entry<Integer, ArrayList<Tgd>> entry : this.partitions.entrySet()) {
            do {
                for (Tgd clause : entry.getValue()) {
                    edb = this.eval(clause, edb);
                }
                if (edb.size() == oldEdb.size()) {
                    change = false;
                } else {
                    oldEdb = edb;
                }

            } while (change);
            System.out.println("Changement de Partition");
        }
        affiche_Edb(edb);
        return edb;
    }

    public ArrayList<ArrayList<String>> getParam(String name, ArrayList<Relation> edb) {
        ArrayList<ArrayList<String>> actual = new ArrayList<>();
        edb.forEach(relation -> {
            if (relation.getName().equals(name)) {
                String[] attri = relation.getAttributes();
                for (int i = 0; i < attri.length; i++) {

                    if (actual.size() < attri.length) {
                        actual.add(new ArrayList<>());
                    }
                    actual.get(i).add(attri[i]);
                }
            }

        });
        return actual;
    }

    public void affiche_Edb(ArrayList<Relation> edb) {
        System.out.println("------------EDB-------------");
        for (Relation r : edb) {
            r.show_you();
        }
    }

    public ArrayList<Relation> eval(Tgd clause, ArrayList<Relation> edb) {
        //initialisation du résultat
        HashMap<String, ArrayList<Boolean>> flags = new HashMap<>();
        HashMap<String, HashMap<String, ArrayList<String>>> result = new HashMap<>();

        ArrayList<String> vals = new ArrayList<>();


        //pour chaque litteraux
        for (Literal literal : clause.getLeft()) {
            //on récupères les nom des variables
            Atom atom = literal.getAtom();

            Object[] args = atom.getVars().toArray();
            //on récupère la liste des faits correspondants pour cette relation
            ArrayList<ArrayList<String>> param = getParam(atom.getName(), edb);
            //pour chaque relation
            if (!result.containsKey(atom.getName())) {
                result.put(atom.getName(), new HashMap<>());
            }
            //on assigne les faits possibles aux différentes variables correspondantes
            for (int i = 0; i < param.size(); i++) {
                Variable temp = (Variable) args[i];
                result.get(atom.getName()).put(temp.getName(), param.get(i));
            }
        }

        //on récupère le nom toutes les variables de la clause
        for (Map.Entry<String, HashMap<String, ArrayList<String>>> entry : result.entrySet()) {

            vals.addAll(entry.getValue().entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList()));
        }


        HashSet<String> va = new HashSet<>(vals);
        HashMap<String, ArrayList<String>> last = new HashMap<>();
        //pour chaque variables
        ArrayList<Boolean> tempo = new ArrayList<>();
        for (String s : va) {
            tempo.clear();
            for (Literal literal : clause.getLeft()) {
                for (Variable v : literal.getAtom().getVars()) {
                    if (v.getName().equals(s)) {
                        tempo.add(literal.getFlag());
                        break;
                    }
                }
            }
            //on récupère toutes les valeurs possible pour chaque relation
            ArrayList<ArrayList<String>> temp = merge(result, s);
            //on ne garde que celles qui correspondent
            ArrayList<String> variable = combine(temp, tempo);
            // System.out.println(variable +" de "+ hh);
            clear(result, variable, s);
            //on les ajoute au résultats
            last.put(s, variable);
        }
        //  System.out.println(result);
        //  System.out.println("3" + last);

        Atom head = clause.getRight();
        ArrayList<Variable> vars = (ArrayList<Variable>) head.getVars();
        ArrayList<Relation> end = new ArrayList<>();

        last = this.generateAndTest(edb, last, new ArrayList<>(clause.getLeft()));

        int nb = last.get(vars.get(0).getName()).size();
        ArrayList<String> temp = new ArrayList<>();

        for (int y = 0; y < nb; y++) {
            for (Variable var : vars) {
                // System.out.println(var.getName() +" : "+last.get(var.getName()));
                temp.add(last.get(var.getName()).get(y));
            }
            Relation toAdd = new Relation(head.getName(), temp);
            if (!this.isInEdb(edb, toAdd) && !this.isInEdb(end, toAdd)) {
                end.add(toAdd);
            }
            temp.clear();
        }

       /* ArrayList<String> attributs = new ArrayList<>();
        ArrayList<ArrayList<String>> ok = new ArrayList<>();
        ok.addAll(last.values());
        ArrayList<String> keys = new ArrayList<>();
        keys.addAll(last.keySet());

        for (Map.Entry<String, ArrayList<String>> entry : last.entrySet()) {
            for (int w = 0; w < entry.getValue().size(); w++) {
                for (int p = 0; p < keys.size(); p++) {
                    attributs.add(ok.get(p).get(w));
                }
            }
        }*/
        edb.addAll(end);
        return edb;
    }

    //creation d'une "matrice" correspondant à une variable, chaque ligne contient
    //les valeurs possibles de cette variable pour une relation qui l'utilise
    public ArrayList<ArrayList<String>> merge(HashMap<String, HashMap<String, ArrayList<String>>> data, String var) {
        ArrayList<ArrayList<String>> temp = new ArrayList<>();
        for (Map.Entry<String, HashMap<String, ArrayList<String>>> entry : data.entrySet()) {
            for (Map.Entry<String, ArrayList<String>> entry2 : entry.getValue().entrySet()) {
                if (entry2.getKey().equals(var)) {
                    temp.add(entry2.getValue());
                }
            }
        }
        return temp;
    }

    //à partir de la matrice précédente, on ne garde que les valeurs contenues dans chaque ligne
    public ArrayList<String> combine(ArrayList<ArrayList<String>> data, ArrayList<Boolean> flags) {
        ArrayList<String> res = new ArrayList<>();
        int check = data.size();
        int it = 0;
        int nb = 1;
        int ref = 0;
        Boolean arrayFlag;
        String val = "";
        //on recherche un terme positif de la clause
        for (int j = 0; j < flags.size(); j++) {
            if (flags.get(j)) {
                ref = j;
                break;
            }
        }
        while (!data.get(ref).isEmpty()) {
            nb = 1;
            //on selectionne une valeur arbitraire
            val = data.get(ref).get(0);
            for (int j = 0; j < check; j++) {
                if (j != ref) {
                    arrayFlag = flags.get(j);
                    // && arrayFlag) || (!data.get(j).contains(val) && !arrayFlag)
                    if (data.get(j).contains(val)) {

                        if (arrayFlag) {
                            nb++;
                            data.get(j).remove(val);
                            break;
                        }
                    } else {
                        if (!arrayFlag) {
                            data.get(j).remove(val);
                            nb++;
                            break;
                        }
                    }
                }
            }
            //si on a trouver cette valeur dans toutes les lignes, on l'ajoute au resultat
            if (nb == check && !flags.contains(false)) {
                res.add(data.get(ref).get(0));
            } else {
                if (!res.contains(data.get(ref).get(0))) {
                    res.add(data.get(ref).get(0));
                }
            }

            //on l'enlève de la colonne.
            data.get(ref).remove(0);
        }
        return res;
    }

    public void clear(HashMap<String, HashMap<String, ArrayList<String>>> data, ArrayList<String> checked, String var) {
        ArrayList<String> toDel = new ArrayList<>();
        for (Map.Entry<String, HashMap<String, ArrayList<String>>> entry : data.entrySet()) {
            for (Map.Entry<String, ArrayList<String>> entry2 : entry.getValue().entrySet()) {
                if (entry2.getKey().equals(var)) {
                    entry2.getValue().forEach(s -> {
                        if (!checked.contains(s)) {
                            toDel.add(entry2.getValue().get(entry2.getValue().indexOf(s)));
                            //removeIt(entry2.getValue().indexOf(s),entry.getValue());
                        }
                    });
                }
            }
            removeIt(toDel, entry.getValue(), var);
            toDel.clear();
        }
    }

    public void removeIt(ArrayList<String> todel, HashMap<String, ArrayList<String>> data, String var) {
        int index;
        for (String del : todel) {
            index = getIndex(data, var, del);
            for (Map.Entry<String, ArrayList<String>> entry : data.entrySet()) {
                entry.getValue().remove(index);

            }
        }
    }

    public int getIndex(HashMap<String, ArrayList<String>> data, String var, String del) {
        int res = 0;
        for (Map.Entry<String, ArrayList<String>> entry : data.entrySet()) {
            if (entry.getKey().equals(var)) {
                res = entry.getValue().indexOf(del);
            }
        }
        return res;
    }

    public boolean isok(HashMap<String, ArrayList<String>> data) {
        int size = 0;
        int temp = 0;
        for (Map.Entry<String, ArrayList<String>> entry : data.entrySet()) {

            temp = entry.getValue().size();
            if (size == 0) {
                size = temp;
            }
            if (temp != size) {
                return false;
            }
        }
        return true;
    }

    public HashMap<String, ArrayList<String>> generateAndTest(ArrayList<Relation> edb, HashMap<String, ArrayList<String>> data, ArrayList<Literal> all) {
        int size = -1;
        int temp;
        int it = 0;


        HashMap<String, Integer> vars = new HashMap<>();
        HashMap<String, ArrayList<String>> result = new HashMap<>();
        String min = "";
        ArrayList<ArrayList<String>> heu = new ArrayList<>();
        for (Map.Entry<String, ArrayList<String>> entry : data.entrySet()) {

            temp = entry.getValue().size();
            if (size == -1) {
                size = temp;
            }
            if (temp < size) {
                min = entry.getKey();
                size = temp;
            }
            heu.add(entry.getValue());
            vars.put(entry.getKey(), it);
            result.put(entry.getKey(), new ArrayList<>());
            it++;
        }

        heu = cartesianProduct(heu);
        ArrayList<String> op;
        ArrayList<Relation> end = new ArrayList<>();
        ArrayList<Variable> varst;
        int tot = 0;
        for (int u = 0; u < heu.size(); u++) {
            op = heu.get(u);
            tot = 0;
            for (Literal lit : all) {
                varst = (ArrayList<Variable>) lit.getAtom().getVars();
                ArrayList<String> argus = new ArrayList<>();

                for (Variable v : varst) {
                    argus.add(op.get(vars.get(v.getName())));
                }

                Relation toTest = new Relation(lit.getAtom().getName(), argus);
                if (all.size() == 3) {
                    //System.out.println(op);
                }
                if (isInEdb(edb, toTest) && lit.getFlag()) {
                    tot++;
                    if (!isInEdb(end, toTest)) {
                        end.add(toTest);
                    }
                } else if (!lit.getFlag() && !isInEdb(edb, toTest)) {
                    tot++;
                    toTest.show_you();
                    if (!isInEdb(end, toTest)) {
                        end.add(toTest);
                    }
                }
            }
            if (tot == all.size()) {
                for (Map.Entry<String, Integer> entry : vars.entrySet()) {
                    result.get(entry.getKey()).add(op.get(entry.getValue()));
                }
            }
            //affiche_Edb(end);
        }
        return result;
    }

    public boolean isInEdb(ArrayList<Relation> edb, Relation test) {

        for (int i = 0; i < edb.size(); i++) {
            if (edb.get(i).getName().equals(test.getName()) && sameOrder(new ArrayList<>(Arrays.asList(edb.get(i).getAttributes())), new ArrayList<>(Arrays.asList(test.getAttributes())))) {
                return true;
            }
        }
        return false;
    }


    public boolean sameOrder(ArrayList<String> s1, ArrayList<String> s2) {
        int i = 0;
        if (s1.size() == s2.size()) {
            for (String s : s1) {
                if (!s.equals(s2.get(i))) {
                    return false;
                }
                i++;
            }
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<ArrayList<String>> cartesianProduct(ArrayList<ArrayList<String>> lists) {
        ArrayList<ArrayList<String>> resultLists = new ArrayList<>();
        if (lists.size() == 0) {
            resultLists.add(new ArrayList<>());
            return resultLists;
        } else {
            ArrayList<String> firstList = lists.get(0);
            ArrayList<ArrayList<String>> remainingLists = cartesianProduct(new ArrayList<>(lists.subList(1, lists.size())));
            for (String condition : firstList) {
                for (ArrayList<String> remainingList : remainingLists) {
                    ArrayList<String> resultList = new ArrayList<>();
                    resultList.add(condition);
                    resultList.addAll(remainingList);
                    resultLists.add(resultList);
                }
            }
        }
        return resultLists;
    }


}
