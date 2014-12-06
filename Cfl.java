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

   public String first(String phrase)
   {
      return "e"; 
   }

   public String follow(String phrase)
   {
      return "e"; 
   }

   public static void main (String [] args)
   {
      Cfl lang = new Cfl("lang.txt"); 
      System.out.println(lang.term);
      System.out.println(lang.nonTerm);
   }
}
