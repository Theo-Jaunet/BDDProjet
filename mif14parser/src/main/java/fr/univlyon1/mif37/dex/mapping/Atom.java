package fr.univlyon1.mif37.dex.mapping;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

public class Atom {

    private String name;
    private Value[] args;

    public Atom(String name, List<Value> args) {
        this.name = name;
        this.args = args.toArray(new Value[args.size()]);
    }

    public String getName() {
        return name;
    }

    public Value[] getArgs() {
        return args;
    }

    public Collection<Variable> getVars() {
      Collection<Variable> container = new ArrayList<Variable>();
      for (Value v: this.getArgs()) {
        if (v instanceof Variable) {
          container.add((Variable)v);
        }
      }
      return container;
    }
}
