package sat;

/*
import static org.junit.Assert.*;

import org.junit.Test;
*/

import javafx.geometry.Pos;
import sat.env.*;
import sat.formula.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class SATSolverTest {
    Literal a = PosLiteral.make("a");
    Literal b = PosLiteral.make("b");
    Literal c = PosLiteral.make("c");
    Literal na = a.getNegation();
    Literal nb = b.getNegation();
    Literal nc = c.getNegation();



	
	// TODO: add the main method that reads the .cnf file and calls SATSolver.solve to determine the satisfiability

    public static void main(String[] args) {

        BufferedReader br;
        ArrayList<String> ClauseList = new ArrayList<>();
        Formula ClauseFormula = new Formula();
        Clause IndividualClause = new Clause();

        try {

            br = new BufferedReader(new FileReader("bin/sat/unsat1.cnf"));
            String line = br.readLine();

            while (line!= null) {

                String eachLine[] = line.split(" ");

                if (! (eachLine[0].equals("c") || eachLine[0].equals("p"))) {
                    for (String eachChar : eachLine) {
                        if (eachChar.equals("0")) {
                            ClauseFormula = ClauseFormula.addClause(IndividualClause);

                            IndividualClause = new Clause();
                        } else {
                            if (eachChar.charAt(0) == '-') {
                                IndividualClause = IndividualClause.add(NegLiteral.make(eachChar.substring(1)));
                            } else {
                                IndividualClause = IndividualClause.add(PosLiteral.make(eachChar));
                            }

                        }
                    }
                }
                line = br.readLine();
            }
            br.close();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        Environment env = SATSolver.solve(ClauseFormula);

        System.out.println(env);


    }
    
	
    public void testSATSolver1(){

    	Environment e = SATSolver.solve(makeFm(makeCl(a,b))	);
/*
    	assertTrue( "one of the literals should be set to true",
    			Bool.TRUE == e.get(a.getVariable())
    			|| Bool.TRUE == e.get(b.getVariable())	);

*/
    }
    
    
    public void testSATSolver2(){
    	// (~a)
    	Environment e = SATSolver.solve(makeFm(makeCl(na)));
/*
    	assertEquals( Bool.FALSE, e.get(na.getVariable()));
*/    	
    }
    
    private static Formula makeFm(Clause... e) {
        Formula f = new Formula();
        for (Clause c : e) {
            f = f.addClause(c);
        }
        return f;
    }
    
    private static Clause makeCl(Literal... e) {
        Clause c = new Clause();
        for (Literal l : e) {
            c = c.add(l);
        }
        return c;
    }
    
    
    
}