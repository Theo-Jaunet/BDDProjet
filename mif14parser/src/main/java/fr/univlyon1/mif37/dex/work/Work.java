package fr.univlyon1.mif37.dex.work;

import fr.univlyon1.mif37.dex.mapping.Atom;
import fr.univlyon1.mif37.dex.mapping.Literal;
import fr.univlyon1.mif37.dex.mapping.Mapping;
import fr.univlyon1.mif37.dex.mapping.Relation;
import fr.univlyon1.mif37.dex.mapping.Tgd;
import fr.univlyon1.mif37.dex.mapping.Variable;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

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
    
    public void stratification(){
        
        Boolean change = true;
        Integer nbStratum = 1, 
                predicateCount,
                tmp, actualStratum;
        Atom p, q;
        
        for(Tgd tgd : this.m.getTgds()){
            
            for(Literal lit : tgd.getLeft()){
                this.stratum.put(lit.getAtom().getName(), 1);
            }
                this.stratum.put(tgd.getRight().getName(), 1);
        }
        predicateCount = this.stratum.size();
        
        while(change && nbStratum < predicateCount){
            change = false;
            for( Tgd tgd : m.getTgds()){
                
                p = tgd.getRight();
                actualStratum = this.stratum.get(p.getName());
                
                for (Literal lit : tgd.getLeft()){
                  
                    if(!lit.getFlag()){
                        tmp = Integer.max(this.stratum.get(lit.getAtom().getName()) + 1,
                                this.stratum.get(p.getName()));
                        if (tmp > actualStratum){
                            this.stratum.put(p.getName(), tmp);
                            change = true;
                        } 
                        
                    }else{
                        tmp = Integer.max(this.stratum.get(lit.getAtom().getName()),
                                this.stratum.get(p.getName()));
                        if (tmp > actualStratum){
                            //System.out.println(tmp);
                            this.stratum.put(p.getName(), tmp);
                            change = true;
                        } 
                    }
                    if (actualStratum > nbStratum){
                        nbStratum = actualStratum;
                    }
                }
            }
            
        }
    }
    
    public void afficher_stratum(){
        System.out.println(this.stratum.size());
        this.stratum.forEach((k,v) -> {
            String afficher = k;
            afficher += " : ";
            afficher += v.toString();
            System.out.println(afficher);
        }
        );
    }
    
    public void partitioning(){
        Integer nbpart = Collections.max(this.stratum.values());
        
        for(Integer i = 1; i<= nbpart; i++){
            for(HashMap.Entry<String, Integer> entry : this.stratum.entrySet()) {
                if(Objects.equals(entry.getValue(), i)){
                    ArrayList<Tgd> tmpTgd;
                    if(this.partitions.containsKey(i)){
                        tmpTgd = this.partitions.get(i);
                    }else{
                        tmpTgd = new ArrayList();
                    }
                    tmpTgd.addAll(this.getDefs(entry.getKey()));
                    this.partitions.put(i, tmpTgd);
                }
            }
        }     
    }
    
    public ArrayList<Tgd> getDefs(String head){
        ArrayList<Tgd> result = new ArrayList<>();
        this.stratum.forEach((key, val)->{
            if(key.equals(head)){
                m.getTgds().forEach((tgd) ->{
                    if(tgd.getRight().getName().equals(head)){
                        result.add(tgd);
                    }
                });
            }
        });
        return result;
    }
    
    public void afficher_partitions(){
        System.out.println(this.partitions.size());
        this.partitions.forEach((k,v) -> {
            String afficher = k.toString();
            afficher += " : ";
            afficher += v.toString();
            System.out.println(afficher);
        }
        );
    }
    
    public ArrayList<Relation> evalPartition(ArrayList<Tgd> partition, ArrayList<Relation> edb){
        ArrayList<Relation> tmp = edb;
        HashSet<String> names = new HashSet();
        HashMap<String, HashSet<String>> adom = new HashMap();
        String tmpString;
        HashSet<String> tmpSet;
        
        for(Tgd tgd : partition){
           for(Literal lit : tgd.getLeft()){
               for(int i = 0; i<lit.getAtom().getVars().size(); i++){
                   
                    tmpString = lit.getAtom().getVars().toArray()[i].toString();
                    
                    if(adom.containsKey(tmpString)){
                       tmpSet = adom.get(tmpString);
                    }else{
                       tmpSet = new HashSet();
                    }
                    
                    for(Relation rel : tmp){
                        if(lit.getAtom().getName().equals(rel.getName())){
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
    
    public void test(){
        this.evalPartition(this.partitions.get(1), (ArrayList)this.m.getEDB());
    }
}
