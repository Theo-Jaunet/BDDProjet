package fr.univlyon1.mif37.dex;

import fr.univlyon1.mif37.dex.mapping.Mapping;
import fr.univlyon1.mif37.dex.parser.MappingParser;
import fr.univlyon1.mif37.dex.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);
    public static void main(String[] args) throws ParseException, FileNotFoundException {
        Reader input = new InputStreamReader(new FileInputStream(new File("/home/theo/Documents/bdd/BDDProjet/BDDProjet/mif14parser/src/main/java/fr/univlyon1/mif37/dex/sample-mapping.txt")));
        MappingParser parser = new MappingParser(input);
        Mapping m = parser.mapping();
        // MappingParser mp = new MappingParser(System.in);
        //Mapping mapping = mp.mapping();
        LOG.info("Parsed {} edb(s), {} idb(s) and {} tgd(s).",
                m.getEDB().size(),
                m.getIDB().size(),
                m.getTgds().size());
        System.out.println(m.getEDB().size());
    }
}
