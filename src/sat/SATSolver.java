package sat;

import immutable.ImList;
import immutable.NonEmptyImList;
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
    /**
     * Solve the problem using a simple version of DPLL with backtracking and
     * unit propagation. The returned environment binds literals of class
     * bool.Variable rather than the special literals used in clausification of
     * class clausal.Literal, so that clients can more readily use it.
     * 
     * @return an environment for which the problem evaluates to Bool.TRUE, or
     *         null if no such environment exists.
     */

    //preservation variable
    private static Variable x;

    public static Environment solve(Formula formula) {
        Environment env = new Environment();
        return solve(formula.getClauses(),env);
    }

    /**
     * Takes a partial assignment of variables to values, and recursively
     * searches for a complete satisfying assignment.
     * 
     * @param clauses
     *            formula in conjunctive normal form
     * @param env
     *            assignment of some or all variables in clauses to true or
     *            false values.
     * @return an environment for which all the clauses evaluate to Bool.TRUE,
     *         or null if no such environment exists.
     */
    private static Environment solve(ImList<Clause> clauses, Environment env) {

        if (clauses.size() == 1 && clauses.first().size() == 0) {
            return env;
        } else {

            Clause smallest = clauses.first();

            for(Clause each: clauses) {


                if (each.isEmpty()) {
                    //Failure, start backtrack
                    return null;

                } else if (each.isUnit()) {

                    //This clause MUST be true, substitute the literal in the other clauses
                    if (each.chooseLiteral() instanceof PosLiteral) {
                        env = env.putTrue(each.chooseLiteral().getVariable());
                    } else {
                        env = env.putFalse(each.chooseLiteral().getVariable());
                    }

                    clauses = substitute(clauses,each.chooseLiteral());

                    try {
                        clauses = clauses.remove(each);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }


                } else {

                    //Finding the smallest clause
                    if (smallest.size() > each.size()) {
                        smallest = each;
                    }
                }
            }


            //the idea is to choose the first literal from the smallest clause and evaluate to true
            x = smallest.chooseLiteral().getVariable();

            if (smallest.chooseLiteral() instanceof PosLiteral) {
                env = env.putTrue(smallest.chooseLiteral().getVariable());
            } else {
                env = env.putFalse(smallest.chooseLiteral().getVariable());
            }

            clauses = substitute(clauses,smallest.chooseLiteral());

            Environment temp = solve(clauses,env);

            if (temp == null) {
                if (env.get(x) == Bool.FALSE) {
                    env.putTrue(x);
                } else {
                    env.putFalse(x);
                }

                return solve(clauses,env);

            } else {
                return temp;
            }
        }


    }

    /**
     * given a clause list and literal, produce a new list resulting from
     * setting that literal to true
     * 
     * @param clauses
     *            , a list of clauses
     * @param l
     *            , a literal to set to true
     * @return a new list of clauses resulting from setting l to true
     */
    private static ImList<Clause> substitute(ImList<Clause> clauses,
            Literal l) {

        Clause c = clauses.first().reduce(l);
        ImList<Clause> newClauseList = null;
        if (c != null) {
            newClauseList = new NonEmptyImList<>(c);
        }

        for (Clause each: clauses) {
            if (! (each.equals(clauses.first()))) {

                c = each.reduce(l);

                if (c != null && newClauseList==null) {
                    newClauseList = new NonEmptyImList<>(c);
                }

                if (c != null) {
                    newClauseList = newClauseList.add(c);

                }
            }
        }
        return newClauseList;
    }

}
