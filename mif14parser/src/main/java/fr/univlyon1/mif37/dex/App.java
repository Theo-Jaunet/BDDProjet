package fr.univlyon1.mif37.dex;

import fr.univlyon1.mif37.dex.mapping.Literal;
import fr.univlyon1.mif37.dex.mapping.Mapping;
import fr.univlyon1.mif37.dex.mapping.Relation;
import fr.univlyon1.mif37.dex.mapping.Tgd;
import fr.univlyon1.mif37.dex.parser.MappingParser;
import fr.univlyon1.mif37.dex.parser.ParseException;
import fr.univlyon1.mif37.dex.work.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);
    public static void main(String[] args) throws ParseException, IOException {

        Reader input = new InputStreamReader(new FileInputStream(new File("sample-mapping.txt")));
        MappingParser parser = new MappingParser(input);
        Mapping m = parser.mapping();
        // MappingParser mp = new MappingParser(System.in);
        //Mapping mapping = mp.mapping();
        LOG.info("Parsed {} edb(s), {} idb(s) and {} tgd(s).",
                m.getEDB().size(),
                m.getIDB().size(),
                m.getTgds().size());
        //System.out.println(m.getEDB().size());

        Object[] canard = m.getTgds().toArray();
        Tgd temp = (Tgd) canard[4];
        for (Literal lit: temp.getLeft()) {
            //System.out.println(lit.getAtom().getName());
            //System.out.println(lit.getFlag());
        }
        Work w = new Work(m);
       // w.eval((Tgd) canard[0], (ArrayList<Relation>) m.getEDB());
        w.megaEval((ArrayList<Relation>)m.getEDB());

    }
}
