package fr.univlyon1.mif37.dex.work;

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
        System.out.println("lol");
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
        System.out.println(this.stratum.size());
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

    public ArrayList<Relation> evalPartition(ArrayList<Tgd> partition, ArrayList<Relation> edb) {
        ArrayList<Relation> tmp = edb;
        HashSet<String> names = new HashSet();
        HashMap<String, HashSet<String>> adom = new HashMap();
        String tmpString;
        HashSet<String> tmpSet;

        for (Tgd tgd : partition) {
            for (Literal lit : tgd.getLeft()) {
                for (int i = 0; i < lit.getAtom().getVars().size(); i++) {

                    tmpString = lit.getAtom().getVars().toArray()[i].toString();

                    if (adom.containsKey(tmpString)) {
                        tmpSet = adom.get(tmpString);
                    } else {
                        tmpSet = new HashSet();
                    }

                    for (Relation rel : tmp) {
                        if (lit.getAtom().getName().equals(rel.getName())) {
                            String lol = rel.getAttributes()[i];
                            tmpSet.add(lol);
                        }
                    }
                    adom.put(tmpString, tmpSet);
                }
            }
           /*tmp.forEach((rel)->{
               if(names.contains(rel.getName())){
                   adom.put(rel.getName(), );
               }
           });*/
        }
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

    public void megaEval(ArrayList<Relation> edb) {


    }

    public ArrayList<Relation> eval(Tgd clause, ArrayList<Relation> edb) {
        HashMap<String, HashMap<String, ArrayList<String>>> result = new HashMap<>();
        clause.getLeft().forEach(literal -> {
            Atom atom = literal.getAtom();
            Object[] args = atom.getVars().toArray();
            ArrayList<ArrayList<String>> param = getParam(atom.getName(), edb);
            result.put(atom.getName(), new HashMap<>());
            System.out.println(param);

            for (int i = 0; i < param.size(); i++) {
                Variable temp = (Variable) args[i];
                result.get(atom.getName()).put(temp.getName(), param.get(i));
            }

        });
        ArrayList<String> vals = new ArrayList<>();
        for (Map.Entry<String, HashMap<String, ArrayList<String>>> entry : result.entrySet()) {
            vals.addAll(entry.getValue().entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList()));

        }
        HashMap<String, ArrayList<String>> last = new HashMap<>();
        vals.forEach(s -> {
            ArrayList<ArrayList<String>> temp = merge(result, s);
            ArrayList<String> variable = combine(temp);
            last.put(s, variable);
        });
        Atom head = clause.getRight();
        ArrayList<Variable> vars = (ArrayList<Variable>) head.getVars();
        ArrayList<Relation> end = new ArrayList<>();
        int i = 0;

        for (Map.Entry<String, ArrayList<String>> entry : last.entrySet()) {

            if (vars.get(i).getName().equals(entry.getKey())) {
                int it = 0;
                for (String s : entry.getValue()) {

                    if (i == 0) {
                        ArrayList<String> temp = new ArrayList<>();
                        temp.add(s);
                        end.add(new Relation(head.getName(), temp));
                    } else {
                        ArrayList<String> temp = (ArrayList<String>) Arrays.asList(end.get(it).getAttributes());
                        temp.add(s);
                        String[] res = (String[]) temp.toArray();
                        end.get(it).setAttributes(res);
                        it++;
                    }

                }
            }
        }
        edb.addAll(end);
        System.out.println("Result");
        ArrayList<ArrayList<String>> disp = this.getParam(head.getName(), edb);
        System.out.println(disp.get(0));
        return edb;
    }

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

    public ArrayList<String> combine(ArrayList<ArrayList<String>> data) {
        ArrayList<String> res = new ArrayList<>();
        int check = data.size();
        int it = data.get(0).size();
        int nb = 1;
        String val = "";
        while (!data.get(0).isEmpty()) {
            val = data.get(0).get(0);

            for (int j = 1; j < check; j++) {
                for (int u = 0; u < it; u++) {
                    if (data.get(j).get(u) == val) {
                        nb++;
                        data.get(j).remove(u);
                        break;
                    }
                }

            }
            if (nb == check) {
                nb = 1;
                res.add(data.get(0).get(0));
                data.get(0).remove(0);
            }
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
            removeIt(toDel,entry.getValue(),var);
            toDel.clear();
        }

    }

    public HashMap<String, ArrayList<String>> removeIt(ArrayList<String> todel, HashMap<String, ArrayList<String>> data, String var) {
        int index;
        for (String del : todel) {
            index = getIndex(data, var, del);
            for (Map.Entry<String, ArrayList<String>> entry : data.entrySet()) {
                entry.getValue().remove(index);
            }
        }
        return data;
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

    public void test() {
        this.evalPartition(this.partitions.get(1), (ArrayList) this.m.getEDB());
    }
}
