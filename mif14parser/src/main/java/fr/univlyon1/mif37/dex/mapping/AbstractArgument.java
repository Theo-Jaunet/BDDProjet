package fr.univlyon1.mif37.dex.mapping;

public class AbstractArgument {
  
  private String attribute = null;
  private Variable variable = null;

  public AbstractArgument(String attribute) {
    this.attribute = attribute;
  }

  public AbstractArgument(Variable v) {
    this.variable = v;
  }
  
  public boolean isVariable() {
    return variable != null;
  }
  
  public boolean isAttribute() {
    return attribute != null;
  }

  public Variable getVar() {
    return variable;
  }
  
  public String getAtt() {
    return attribute;
  }
}
