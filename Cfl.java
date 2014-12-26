import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;

public class Cfl
{
   public Set<String> nonTerm;
   public Set<String> term;
   public ArrayList<String> rhs;
   public ArrayList<String> lhs;

   public Cfl(String fileName)
   {
      //Initialize attributes
      nonTerm = new HashSet<String>();
      term = new HashSet<String>(); 
      lhs = new ArrayList<String>();
      rhs = new ArrayList<String>();

      BufferedReader br = null;

      //Open the language file
      try
      {
         String currentProduction;
         br = new BufferedReader(new FileReader(fileName));

         //Regex for processing each production
         String productionRegex = "(\\w):(.*)";
         Pattern productionPattern = Pattern.compile(productionRegex);

         //Loop over every line, each with a production of the form A:w
         while ((currentProduction = br.readLine()) != null)
         {
            Matcher productionMatcher = productionPattern.matcher(currentProduction);
            if (productionMatcher.find())
            {
               //Add terminals and non terminals
               nonTerm.add(productionMatcher.group(1));
               term.addAll(Arrays.asList(Arrays.copyOfRange(productionMatcher.group(2).split(""), 1, productionMatcher.group(2).split("").length)));

               //Add productions
               lhs.add(productionMatcher.group(1));
               rhs.add(productionMatcher.group(2));
            }

            else
            {
               System.out.println("Found a non-production!");
            }
         }

         //Ensure that the non terminal and terminal sets are disjoint
         //If a symbol is a lhs then it must be a non terminal and cannot be in the terminal set
         term.removeAll(nonTerm);

         //Remove the reserved e symbol (epsilon) from non terminal set
         term.remove("e");
      }

      //In case the file couldn't open
      catch (IOException e)
      {
         e.printStackTrace();
      }

      //Close the file
      finally
      {
         try
         {
            if (br != null) br.close();
         }

         catch (IOException ex)
         {
            ex.printStackTrace();
         }
      }
   }

   public Set<String> first(String phrase)
   {
      Set<String> retSet = new HashSet<String>(); 

      //Rule FIRST(w) = {   }
      for (int i = 0; i < phrase.length(); i++)
      {
         //If symbol being looked at is a terminal then the first set has been computed
         if (term.contains(String.valueOf(phrase.charAt(i))))
         {
            retSet.add(String.valueOf(phrase.charAt(i)));
            continue;
         }

         //If symbol being looked at is a non terminal, find its first set and union with retSet
         if (nonTerm.contains(String.valueOf(phrase.charAt(i))))
         {
            Set<String> subSet = firstOfNonTerm(String.valueOf(phrase.charAt(i)), new HashSet<Integer>());
            retSet.addAll(subSet);

            //If the current symbol is non nullable then the first set has been computed
            if (!subSet.contains("e"))
            {
               continue;
            }
         }

         else
         {
            System.out.println("Unrecognized symbol!");
         }
      }
      return retSet;
   }

   private Set<String> firstOfNonTerm(String phrase, Set<Integer> traversedProductions)
   {
      Set<String> retSet = new HashSet<String>();

      //Loop through each production to find ones with lhs that match the given non terminal
      for (int i = 0; i < lhs.size(); i++)
      {
         if (lhs.get(i).equals(phrase) && !traversedProductions.contains(i))
         {
            traversedProductions.add(i);

            //Loop through rhs left to right of the current production
            for (int j = 0; j < rhs.get(i).length(); j++)
            {
               //If the non terminal is nullable, mark it as so
               if (rhs.get(i).equals("e"))
               {
                  retSet.add("e");
                  break;
               }

               //If symbol being looked at is a terminal then the subset of the first set from this production has been computed
               if (term.contains(String.valueOf(rhs.get(i).charAt(j))))
               {
                  retSet.add(String.valueOf(rhs.get(i).charAt(j)));
                  break;
               }

               //If symbol being looked at is a non terminal, find its first set and union with retSet
               if (nonTerm.contains(String.valueOf(rhs.get(i).charAt(j))))
               {
                  Set<String> subSet = firstOfNonTerm(String.valueOf(rhs.get(i).charAt(j)), traversedProductions);
                  retSet.addAll(subSet);

                  //If the current symbol is non nullable then the first set from this production has been computed
                  if (!subSet.contains("e"))
                  {
                     //Move on to the next production
                     break;
                  }

                  //If we are looking at the last symbol and it is nullable (implied from above), then the non terminal is nullable
                  if (j == rhs.get(i).length() - 1)
                  {
                     //Mark the non terminal as nullable
                     retSet.add("e");
                  }
               }

               else
               {
                  System.out.println("Unrecognized symbol: " + rhs.get(i).charAt(j));
               }
            }
         }
      }
      return retSet;
   }

   public Set<String> follow(String phrase)
   {
      Set<String> retSet = new HashSet<String>(); 

      //Given string must be a non terminal
      if(!nonTerm.contains(phrase))
      {
         System.out.println("Not a non terminal!");
         return retSet;
      }

      return followOfNonTerm(phrase, new HashSet<Integer>());
   }

   private Set<String> followOfNonTerm(String phrase, Set<Integer> traversedProductions)
   {
      Set<String> retSet = new HashSet<String>();

      //Loop through all productions
      for (int i = 0; i < lhs.size(); i++)
      {
         //Only analyze productions that use the given non terminal
         if (!traversedProductions.contains(i) && rhs.get(i).contains(phrase))
         {
            traversedProductions.add(i);
            //If there is a production B->yAw then FOLLOW(A) = U FIRST(w)
            for (int j = 0; j < rhs.get(i).length(); j++)
            {
               if (String.valueOf(rhs.get(i).charAt(j)).equals(phrase))
               {
                  Set<String> firstW = first(rhs.get(i).substring(j+1));
                  retSet.addAll(firstW);
                  retSet.remove("e");

                  if (first(rhs.get(i).substring(j+1)).contains("e") || j == rhs.get(i).length() - 1)
                  {
                     retSet.addAll(followOfNonTerm(lhs.get(i), traversedProductions));
                  }
               }
            }
         }
      }
      return retSet;
   }

   public Set<Item> closure(Item item)
   {
      System.out.println(item);
      Set<Item> retSet = new HashSet<Item>();

      //If the dot precedes a terminal then the closure set is empty or given a finished item
      if (item.getDot() == item.getRhs().length() || term.contains(String.valueOf(item.getRhs().charAt(item.getDot()))))
      {
         return retSet;
      }

      //If the dot precedes a non terminal, then add all initial items of that non terminal
      else if (nonTerm.contains(String.valueOf(item.getRhs().charAt(item.getDot()))))
      {
         Set<Item> initialSet = initialItems(String.valueOf(item.getRhs().charAt(item.getDot())));
         retSet.addAll(initialSet);

         Item initialArray[] = initialSet.toArray(new Item[initialSet.size()]);

         //Add the closure sets of all the initial items added
         for (int i = 0; i < initialSet.size(); i++)
         {
            retSet.addAll(closure(initialArray[i], new HashSet<String>()));
         }
      }
      return retSet;
   }

   private Set<Item> closure(Item item, Set<String> traversedNonterminals)
   {
      Set<Item> retSet = new HashSet<Item>();

      //If the dot precedes a terminal then the closure set is empty or given a finished item
      if (item.getDot() == item.getRhs().length() || term.contains(String.valueOf(item.getRhs().charAt(item.getDot()))))
      {
         return retSet;
      }

      //If the dot precedes a non terminal, then add all initial items of that non terminal
      else if (nonTerm.contains(String.valueOf(item.getRhs().charAt(item.getDot()))) &&
            !traversedNonterminals.contains(String.valueOf(item.getRhs().charAt(item.getDot()))))
      {
         traversedNonterminals.add(String.valueOf(item.getRhs().charAt(item.getDot())));
         Set<Item> initialSet = initialItems(String.valueOf(item.getRhs().charAt(item.getDot())));
         retSet.addAll(initialSet);

         Item initialArray[] = initialSet.toArray(new Item[initialSet.size()]);

         //Add the closure sets of all the initial items added
         for (int i = 0; i < initialSet.size(); i++)
         {
            retSet.addAll(closure(initialArray[i], traversedNonterminals));
         }
      }
      return retSet;
   }

   private Set<Item> initialItems(String nonTerminal)
   {
      Set<Item> retSet = new HashSet<Item>();

      for (int i = 0; i < lhs.size(); i++)
      {
         if (lhs.get(i).equals(nonTerminal))
         {
            if (rhs.get(i).equals("e"))
            {
               Item initial = new Item(lhs.get(i), "", 0);
               retSet.add(initial);
            }
            else
            {
               Item initial = new Item(lhs.get(i), rhs.get(i), 0);
               retSet.add(initial);
            }
         }
      }
      return retSet;
   }

   public void generateLrTable(Table table)
   {

   }

   public static void main (String [] args)
   {
      Cfl lang = new Cfl("lang.txt"); 
      System.out.println("Terminals: " + lang.term);
      System.out.println("Non terminals: " + lang.nonTerm);
      System.out.println("Left hand sides: " + lang.lhs);
      System.out.println("Right hand sides: " + lang.rhs);
      System.out.println("First(E) = " + lang.first("E"));
      System.out.println("First(D) = " + lang.first("D"));
      System.out.println("First(T) = " + lang.first("T"));
      System.out.println("First(S) = " + lang.first("S"));

      System.out.println("Follow(E) = " + lang.follow("E"));
      System.out.println("Follow(D) = " + lang.follow("D"));
      System.out.println("Follow(T) = " + lang.follow("T"));
      System.out.println("Follow(S) = " + lang.follow("S"));
      System.out.println("Follow(F) = " + lang.follow("F"));
      System.out.println("Follow(F) = " + lang.follow("F"));

      System.out.println(lang.closure(new Item("E", "TD", 0)));
   }
}
