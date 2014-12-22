public class Item
{
   private String lhs;
   private String rhs;
   private int dotPosition;

   public Item(String lhs, String rhs, int dot)
   {
      this.lhs = lhs;
      this.rhs = rhs;
      this.dotPosition = dot;
   }

   public String getLhs()
   {
      return lhs;
   }

   public String getRhs()
   {
      return rhs;
   }

   public int getDot()
   {
      return dotPosition;
   }
}
