import java.util.*;

public class Table
{
   //Height and width of table
   private int stateCount;

   private int symbolCount;
   private ArrayList<String> terminals;
   private ArrayList<String> nonTerminals;
   private ArrayList<ArrayList<String>> columns;

   public Table(ArrayList<String> term, ArrayList<String> nonTerm)
   {
      stateCount = 0;
      terminals = term;
      nonTerminals = nonTerm;
      columns = new ArrayList<ArrayList<String>>();

      //Create the correct number of columns (# of term + # of non terminals)
      for (int i = 0; i < term.size() + nonTerm.size(); i++)
      {
         columns.add(new ArrayList<String>());
      }
   }

   public void setTermEntry(String colSymbol, int stateNumber, String entry)
   {
      int col = terminals.indexOf(colSymbol);
      columns.get(col).ensureCapacity(stateNumber + 1);
      columns.get(col).set(stateNumber, entry);
   }

   public void setNonTermEntry(String colSymbol, int stateNumber, String entry)
   {
      int col = terminals.size() + nonTerminals.indexOf(colSymbol);
      columns.get(col).ensureCapacity(stateNumber + 1);
      columns.get(col).set(stateNumber, entry);
   }
}
