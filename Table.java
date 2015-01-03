import java.util.ArrayList;

public class Table
{
   //Height and width of table
   private int stateCount;
   private int symbolCount;
   private ArrayList<State> states;

   private ArrayList<String> terminals;
   private ArrayList<String> nonTerminals;

   public Table(ArrayList<String> term, ArrayList<String> nonTerm)
   {
      stateCount = 0;
      states = new ArrayList<State>();
      terminals = terminals;
      nonTerminals = nonTerm;
   }

   public void addState()
   {
      states.add(new State(stateCount, terminals, nonTerminals));
      stateCount += 1;
   }
}
