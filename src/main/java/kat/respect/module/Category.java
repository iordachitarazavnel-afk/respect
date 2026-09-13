package kat.respect.module;

public enum Category {
   COMBAT("Combat"),
   MISC("Misc"),
   RENDER("Render"),
   CLIENT("Client");

   public final CharSequence name;

   Category(CharSequence name) {
      this.name = name;
   }
}
