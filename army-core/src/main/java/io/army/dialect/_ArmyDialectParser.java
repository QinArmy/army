package io.army.dialect;

public abstract class _ArmyDialectParser extends ArmyParser {

   protected _ArmyDialectParser(DialectEnv dialectEnv, Dialect dialect) {
       super(dialectEnv, dialect);
       assert this.getClass().getPackage().getName().startsWith("io.army.dialect");
   }


}
