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
            //System.out.println(currentProduction);
            Matcher productionMatcher = productionPattern.matcher(currentProduction);
            if (productionMatcher.find())
            {
               //System.out.println(productionMatcher.group(1));
               //System.out.println(productionMatcher.group(2));

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
            return retSet;
         }

         //If symbol being looked at is a non terminal, find its first set and union with retSet
         if (nonTerm.contains(String.valueOf(phrase.charAt(i))))
         {
            Set<String> subSet = firstOfNonTerm(String.valueOf(phrase.charAt(i)), new HashSet<Integer>());
            retSet.addAll(subSet);

            //If the current symbol is non nullable then the first set has been computed
            if (!subSet.contains("e"))
            {
               return retSet;
            }
         }

         else
         {
            System.out.println("Unrecognized symbol!");
         }
      }
      return retSet;
   }

   public Set<String> firstOfNonTerm(String phrase, Set<Integer> traversedProductions)
   {
      Set<String> retSet = new HashSet<String>();

      //Loop through each production to find ones with lhs that match the given non terminal
      for (int i = 0; i < lhs.size(); i++)
      {
         if (lhs.get(i) == phrase && !traversedProductions.contains(i))
         {
            traversedProductions.add(i);

            //Loop through rhs left to right of the current production
            for (int j = 0; j < rhs.get(i).length(); j++)
            {
               //If symbol being looked at is a terminal then the first set has been computed
               if (term.contains(String.valueOf(rhs.get(i).charAt(j))))
               {
                  retSet.add(String.valueOf(rhs.get(i).charAt(j)));
                  return retSet;
               }

               //If symbol being looked at is a non terminal, find its first set and union with retSet
               if (nonTerm.contains(String.valueOf(rhs.get(i).charAt(j))))
               {
                  Set<String> subSet = firstOfNonTerm(String.valueOf(rhs.get(i).charAt(j)), traversedProductions);
                  retSet.addAll(subSet);

                  //If the current symbol is non nullable then the first set has been computed
                  if (!subSet.contains("e"))
                  {
                     return retSet;
                  }
               }

               else
               {
                  System.out.println("Unrecognized symbol!");
               }
            }
         }
      }
      return retSet;
   }

   public Set<String> follow(String phrase)
   {
      Set<String> retSet = new HashSet<String>(); 
      return retSet;
   }

   public static void main (String [] args)
   {
      Cfl lang = new Cfl("lang.txt"); 
      System.out.println(lang.term);
      System.out.println(lang.nonTerm);
   }
}
