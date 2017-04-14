package fr.univlyon1.mif37.dex.mapping;

public class Literal {

  private Atom a;
  private Boolean flag;

  public Literal(Atom a, Boolean flag) {
    this.a = a;
    this.flag = flag;
  }

  public Boolean getFlag() {
    return flag;
  }
  
  public Atom getAtom() {
    return a;
  }

  public boolean containsVariable(Variable v) {
    return a.getVars().contains(v);
  }  
}
