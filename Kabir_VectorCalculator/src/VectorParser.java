import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class VectorParser {

    /**
     Counts the number of dimensions of vectors in a string expression.
     Vectors must be enclosed in parentheses and separated by commas.
     @param expression a string expression containing vectors
     @return the number of dimensions of vectors in the expression
     @throws IllegalArgumentException if the expression has less than 1 dimension
     **/
    private static int countVectorDimensions(String expression) {
        int numDimensions = 0;
        Pattern pattern = Pattern.compile("\\((.*?)\\)"); // Matches anything in parentheses
        Matcher matcher = pattern.matcher(expression);
        while (matcher.find()) {
            String vector = matcher.group(1);
            String[] components = vector.split(",\\s*"); // Split components by comma
            if (numDimensions == 0) { // Set the number of dimensions to the first vector's number of components
                numDimensions = components.length;
            } else if (numDimensions != components.length) { // Check if subsequent vectors have the same number of components
                throw new IllegalArgumentException("All vectors must have the same number of components");
            }
        }
        if (numDimensions == 0) {
            throw new IllegalArgumentException("Expression must contain at least one dimension");
        }
        return numDimensions;
    }

    /**
     Returns a regular expression string representing the operators supported in the vector expressions.
     The returned string includes the plus sign and the minus sign, separated by spaces.
     @return a regular expression string representing the operators supported in the vector expressions
     */
    private static String operators() {
        return "( \\+ )|( - )";
    }

    /**
     Returns a regex pattern for a vector of n dimensions. A vector is represented by a string that starts with an optional
     sign (+ or -), followed by a fraction (or integer), then zero or more spaces, followed by a comma, then zero or more
     spaces, and so on for each dimension. The final dimension is followed by a closing parenthesis. The format of each
     fraction is determined by the {@link Fraction#fractionPattern()} method.
     @param n the number of dimensions of the vector
     @return the regex pattern for a vector of n dimensions
     */
    private static String vectorType(int n) {
        return String.format("(%s){0,1} {0,1}(%s)", Fraction.fractionPattern(), VectorType.formatVector(n));
    }

    /**
     Returns the format for a fraction multiplied by a vector
     with n dimensions.
     @param n the number of dimensions in the vector
     @return the coefficient format
     */
    private static String coefficientFormat(int n) {
        return String.format("(%s)(%s)", Fraction.fractionPattern(), VectorType.formatVector(n));
    }

    /**
     Returns a regular expression string that matches an equation of vectors of dimension n.
     @param n the dimension of the vectors in the equation
     @return a regular expression string that matches an equation of vectors of dimension n
     */
    private static String equationFormat(int n) {
        return String.format("(%s)((%s)(%s)){0,}", vectorType(n), operators(), vectorType(n));
    }

    /**
     Parses a term in an equation and distributes a scalar multiple to a vector if applicable.
     @param term the term to be parsed
     @return a VectorType that may have been multiplied by a scalar
     @throws IllegalArgumentException if the term is not a valid vector or scalar multiple of a vector
     */
    private static VectorType distributeScalar(String term) {
        if (term.matches(coefficientFormat(countVectorDimensions(term)))) {
            String vectorStr = "(" + term.split("\\(")[1];
            String scalarMultipleStr = term.split("\\(")[0];

            VectorType vector = VectorType.valueOf(vectorStr);
            return vector.distributeScalar(Fraction.valueOf(scalarMultipleStr));
        } else {
            return VectorType.valueOf(term);
        }
    }


    /**
     Parses an equation in vector form and returns the resulting vector equation.
     The equation should be in vector or fraction form separated by "+" or "-" operators.
     The vector components and scalar factors must be in fraction form and enclosed in
     parentheses. The equation must have at least one vector term. All vectors in the equation
     must have the same number of components.
     @param equation the vector equation to parse
     @return the resulting vector equation
     @throws IllegalArgumentException if the equation is not in the correct format
     */
    public static VectorType parseEquation(String equation) throws IllegalArgumentException {

        if (equation.matches(equationFormat(countVectorDimensions(equation)))) {

            String[] totalEquationParts = equation.split(operators());
            String initial = totalEquationParts[0];
            VectorType vectorEquationTotal = distributeScalar(initial);
            equation = equation.substring(initial.length());

            while (!equation.isEmpty()) {
                int count = countVectorDimensions(equation);
                String operator = equation.split(vectorType(count))[0].trim();
                String followingTerm = equation.split(operators())[1];
                VectorType followingVector = distributeScalar(followingTerm);

                switch (operator) {
                    case "+":
                        vectorEquationTotal = vectorEquationTotal.add(followingVector);
                        break;
                    case "-":
                        vectorEquationTotal = vectorEquationTotal.subtract(followingVector);
                        break;
                    default:
                        // Handle invalid operator
                        break;
                }
                equation = equation.substring(operator.length() + 2 + followingTerm.length());
            }

            return vectorEquationTotal;

        } else {
            throw new IllegalArgumentException("Illegal format");
        }
    }


    /**
     * test cases for parser
     */
    private static void test() {
        System.out.println(VectorParser.parseEquation("(1, 2, 3) + (4, 5, 6)")); // (5, 7, 9)
        System.out.println(VectorParser.parseEquation("(3/4, 1/3, -1/2) - (-1/3, 2/3, 3/4)")); // (5/6, -1, -5/4)
        System.out.println(VectorParser.parseEquation("-1(2, 1/4, -1/5)")); // (-2, -1/4, 1/5)
        System.out.println(VectorParser.parseEquation("(4 3/4, 2 2/3, -5/7) + (7, -1 1/7, 3/4)")); // (11 3/4, 1 5/21, -1/28)
        System.out.println(VectorParser.parseEquation("(-2 1/4, 5/-8, 8) - (-5 1/2, -1/2, 2 2/5)")); // (3 1/4, -4 1/8, 11 3/5)
        System.out.println(VectorParser.parseEquation("1 1/2(3, -4, 5/10)")); // (4 1/2, -6, 1 1/2)
        System.out.println(VectorParser.parseEquation("(-15, 1/2, 7/6) - (-20/3, -6/5, -12/5)")); // (10 2/3, 11/10, 49/30)
        System.out.println(VectorParser.parseEquation("(2, 3/2, 4) + 3(1/2, -1/4, 5) - (4, 1, 3)")); // (-1/2, 1/4, 18)
        System.out.println(VectorParser.parseEquation("(1, -3, 9 3/4) - 2(4, 7/8, 10)")); // (-7, -17 3/8, -10 1/4)
        System.out.println(VectorParser.parseEquation("1 1/5(1/2, 3/8, -29) - 1 1/4(2, -3, 4) + 2/3(5, -1/3, -1/6)")); // (-2 37/60, 11/120, -30 413/180)
        System.out.println(VectorParser.parseEquation("(2, 4, 6) + (1, -1, -1)")); // (3, 3, 5)
        System.out.println(VectorParser.parseEquation("(6, -3, 2) - (2, 1, -3)")); // (4, -4, 5)
        System.out.println(VectorParser.parseEquation("2(1, -1, 2)")); // (2, -2, 4)
        System.out.println(VectorParser.parseEquation("(1 1/4, 2 1/3, -1/2) + (1/4, -2 1/3, 1/2)")); // (1 1/2, 0, 0)


        System.out.println(VectorParser.parseEquation("(2/3, -1/4, 1/2) + (-1/3, 1/4, -1/2)"));
        System.out.println(VectorParser.parseEquation("(3 1/2, -2 1/3, 1/5) - (1 1/2, 2/3, 1/10)"));
        System.out.println(VectorParser.parseEquation("2/3(4, -2, 1) - 1/2(-2, 1, -1)"));
        System.out.println(VectorParser.parseEquation("(5/6, -1/4, 3/5) - (-1/3, 2/5, 4/5) + 3/4(1/6, -1/5, -3/10)"));
        System.out.println(VectorParser.parseEquation("(2 1/2, 3/4, -4 1/3) + (-1 1/2, -3/4, 4 1/3)"));
        System.out.println(VectorParser.parseEquation("5(1/2, 1/3, -1/4) - 3(1/3, 1/2, 1/4)"));
        System.out.println(VectorParser.parseEquation("(2/3, -3/4, 1/2) + (1/3, 1/4, -1/2) - 1/6(2, -1, 1)"));
        System.out.println(VectorParser.parseEquation("(-3 1/4, 2 2/3, -5/7) - (-1/4, 1/3, 1/7) + 2 3/4(7, -1 1/7, 3/4)"));
        System.out.println(VectorParser.parseEquation("1/2(1, -2, 3) + 1/3(-2, 4, -6) - 1/6(3, -6, 9)"));
        System.out.println(VectorParser.parseEquation("(1, 2, 3, 4) - (4, 3, 2, 1)"));
        System.out.println(VectorParser.parseEquation("(1/2, 1/3, 1/4, 1/5) + (1/5, 1/4, 1/3, 1/2)"));
        System.out.println(VectorParser.parseEquation("2(1, -1, 2, -2) - 3(-2, 2, -1, 1)"));
        System.out.println(VectorParser.parseEquation("(1, 2, 3, 4, 5) + (-5, -4, -3, -2, -1)"));
        System.out.println(VectorParser.parseEquation("(1/2, 1/3, 1/4, 1/5, 1/6) - (1/6, 1/5, 1/4, 1/3, 1/2)"));

    }

    /**
     Presents the user with a menu to input an equation, write a quiz, or exit the program. If an equation is entered,
     it attempts to parse the equation and output the resulting vector. If the equation is not valid, it prints "Invalid
     Equation". The method uses a while loop to continuously prompt the user for input until the user enters "X" to exit
     the program.
     */
    private static void menu() {
        Scanner input = new Scanner(System.in);
        String equation;
        while (true) {
        System.out.println("Enter an equation or enter Q to write a quiz or type X to exit program: ");
            equation = input.nextLine();
            switch (equation) {
                case "X":
                    input.close();
                    return;
                case "Q":
                    QuizMode.runQuiz();
                    break;
                default:
                    try {
                        System.out.println(VectorParser.parseEquation(equation));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid Equation");
                    }
                    break;
            }
        }
    }

    public static void main (String[]args){
        //test();
        menu();
    }
}









