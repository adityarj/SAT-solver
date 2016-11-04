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

    public static Environment solve(Formula formula) {
        Environment env = new Environment();
        return solve(formula.getClauses(), env);
    }

    private static Environment solve(ImList<Clause> clauses, Environment env) {

        Clause smallest = clauses.first();
        Literal x;
        Environment temp;

        if (clauses.isEmpty() || smallest == null) {
            return env;
        } else if (smallest.isEmpty()) {
            //Failure, start backtrack
            return null;

        } else if (smallest.isUnit()) {
            //the idea is to choose the first literal from the smallest clause and evaluate to true
            x = smallest.chooseLiteral();
            //System.out.println(x);
            if (x instanceof PosLiteral) {
                return solve(substitute(clauses,x),env.putTrue(x.getVariable()));
            } else {
                return solve(substitute(clauses,x),env.putFalse(x.getVariable()));
            }
        } else {
            x = smallest.chooseLiteral();
            //System.out.println(x);
            if (x instanceof PosLiteral) {
                 temp = solve(substitute(clauses,x), env.putTrue(x.getVariable()));
            } else {
                 temp = solve(substitute(clauses,x), env.putFalse(x.getVariable()));
            }
        }

        if (temp == null) {

            return solve(substitute(clauses,smallest.chooseLiteral()), env.putFalse(x.getVariable()));

        } else {
            return temp;
        }
    }

    private static ImList<Clause> substitute(ImList<Clause> clauses, Literal l) {
        //Take in a list of clauses. and a literal and give out a reduced list of clauses, with sorting.,
        ImList<Clause> newClauseList = new EmptyImList<>();
        Clause small = null;
        Clause inter;

        //Smallest is the smallest clause
        for (Clause each : clauses) {
            inter = each.reduce(l);
            //Create a new list, and copy the contents of the reduction into the new list\

            if (inter != null ) {
                if (small == null) {
                    small  = inter;
                } else {
                    if (inter.size() < small.size()) {
                        newClauseList = newClauseList.add(small);
                        small = inter;
                    } else {
                        newClauseList = newClauseList.add(inter);
                    }
                }
            }
        }

        if (small != null) {
            newClauseList = newClauseList.add(small);
        }


        return newClauseList;
    }

}
