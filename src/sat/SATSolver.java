package sat;

import immutable.EmptyImList;
import immutable.ImList;
import javafx.geometry.Pos;
import sat.env.Bool;
import sat.env.Environment;
import sat.env.Variable;
import sat.formula.*;

import java.util.Iterator;

/**
 * A simple DPLL SAT solver. See http://en.wikipedia.org/wiki/DPLL_algorithm
 */
public class SATSolver {

    //preservation variable
    private static Variable x;
    private static Clause smallest;
    public static Environment solve(Formula formula) {
        Environment env = new Environment();
        return solve(formula.getClauses(), env);
    }

    private static Environment solve(ImList<Clause> clauses, Environment env) {

        smallest = clauses.first();


        System.out.println(clauses);
        if (clauses.size() == 0) {
            return env;
        } else if (smallest.isEmpty()) {
            //Failure, start backtrack
            return null;

        } else if (smallest.isUnit()) {
            //the idea is to choose the first literal from the smallest clause and evaluate to true
            x = smallest.chooseLiteral().getVariable();
            //System.out.println(x);
            if (smallest.chooseLiteral() instanceof PosLiteral) {
                env = env.putTrue(x);
            } else {
                env = env.putFalse(x);
            }

            clauses = substitute(clauses, smallest.chooseLiteral());

            return solve(clauses,env);
        } else {
            x = smallest.chooseLiteral().getVariable();
            //System.out.println(x);
            if (smallest.chooseLiteral() instanceof PosLiteral) {
                env = env.putFalse(x);
            } else {
                env = env.putTrue(x);
            }
            clauses = substitute(clauses, smallest.chooseLiteral());
        }
        Environment temp = solve(clauses, env);

        if (temp == null) {

            if (env.get(x) == Bool.FALSE) {
                env = env.putTrue(x);
            } else {
                env = env.putFalse(x);
            }

            clauses = substitute(clauses, smallest.chooseLiteral().getNegation());

            return solve(clauses, env);

        } else {

            return temp;

        }
    }

    private static ImList<Clause> substitute(ImList<Clause> clauses, Literal l) {
        //Take in a list of clauses. and a literal and give out a reduced list of clauses, with sorting.,
        Clause temp;
        ImList<Clause> newClauseList = new EmptyImList<>();
        Clause small = null;

        //Smallest is the smallest clause
        for (Clause each : clauses) {
            temp = each.reduce(l);
            //Create a new list, and copy the contents of the reduction into the new list\

            if (temp != null && newClauseList.size() == 0) {
                small  = temp;
                newClauseList = newClauseList.add(temp);
            } else if (temp != null) {
                if (temp.size() < small.size()) {
                    small = temp;
                }
                newClauseList = newClauseList.add(temp);
            }
        }

        newClauseList = newClauseList.remove(small);
        newClauseList = newClauseList.add(small);

        return newClauseList;
    }

}
