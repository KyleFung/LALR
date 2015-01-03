import java.util.ArrayList;

public class State
{
   public int index;
   private ArrayList<Item> items;
   private ArrayList<String> terminals;
   private ArrayList<String> nonTerminals;

   private String[] entries;

   public State(int number, ArrayList<String> term, ArrayList<String> nonTerm)
   {
      index = number;
      items = new ArrayList<Item>();
      terminals = term;
      nonTerminals = nonTerm;

      entries = new String[terminals.size() + nonTerminals.size()];
   }
}
