import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

class QuizMode {

    /**
     Picks a random integer between 2 and 5 to determine the dimension of a vector for the quiz.
     @return the randomly selected integer representing the dimension of the vector
     */
    private static int pickDimensions() {
        Random random = new Random();
        int dimension = random.nextInt(4) + 2; // Randomly select a dimension between 2 and 5
        return dimension;
    }

    /**
     Generates a random vector with components consisting of whole parts and fractions.
     @param dimension The dimension of the vector to be generated.
     @return A randomly generated Vector object with the specified dimension and components.
     */
    private static Vector generateRandomVector(int dimension) {
        Random random = new Random();

        Fraction[] components = new Fraction[dimension];
        for (int i = 0; i < dimension; i++) {
            int wholePart = random.nextInt(20) - 10; // Random whole part between -10 and 10
            int numerator = random.nextInt(10) + 1; // Random numerator between 1 and 10
            int denominator = random.nextInt(10) + 1; // Random denominator between 1 and 10

            Fraction fraction = new Fraction(numerator, denominator);
            components[i] = new Fraction(wholePart).add(fraction); // Combine whole part and fraction
        }

        return new Vector(List.of(components));
    }

    /**
     Returns a string representation of a vector, with brackets replaced by parentheses.
     @param vector the vector to convert to a string
     @return a string representation of the vector with parentheses instead of brackets
     */
    private static String createStringOfVector(Vector vector) {
        String str = vector.toString()
                .replace("[", "(")
                .replace("]", ")");
        return str;
    }

    /**
     Randomly selects and returns one of the four basic arithmetic operators: addition, subtraction, dot product, or cross product.
     @return A string representing the randomly selected operator.
     */
    private static String pickOperator(){
        Random random = new Random();
        String[] operators = {"+", "-", "*", "x"};
        String operator = operators[random.nextInt(operators.length)];
        return operator;
    }

    /**
     Generates a random expression containing two vectors and an operator.
     The operator can be either +, -, * (dot product), or x (Cross product). If the operator is 'x', the
     dimension of vectors must be at least 3, otherwise a new set of vectors and operator
     will be generated.
     @return a String array representing the math expression, where index 0 represents the first vector,
     index 1 represents the operator, and index 2 represents the second vector.
     */
    private static String[] createExpression() {
        int dimensions = pickDimensions();
        Vector v1 = generateRandomVector(dimensions);
        Vector v2 = generateRandomVector(dimensions);
        String operator = pickOperator();
        while (true){
            if (operator.equals("x") && dimensions < 3) {
                dimensions = pickDimensions();
                v1 = generateRandomVector(dimensions);
                v2 = generateRandomVector(dimensions);

            } else {
                break;
            }
        }

        String[] equation = {createStringOfVector(v1), operator,createStringOfVector(v2)};
        return equation;
    }

    /**
     Solves a quiz question by performing the operation specified by the given operator on the two input vectors.
     @param v1 the first input vector
     @param operator the operator specifying the operation to be performed (can be "+", "-", "*", or "x")
     @param v2 the second input vector
     @return the result of the operation performed on the input vectors, as a String
     */
    private static String solveQuizQuestion(VectorType v1, String operator, VectorType v2) {
        String result = "";
        VectorType tmpResult;
        switch(operator) {
            case "+":
                tmpResult = v1.add(v2);
                result = tmpResult.toString();
                break;
            case "-":
                tmpResult = v1.subtract(v2);
                result = tmpResult.toString();
                break;
            case "*":
                Fraction fracResult = v1.dotProduct(v2);
                result = fracResult.toString();
                break;
            default:
                tmpResult = v1.crossProduct(v2);
                result = tmpResult.toString();
        }
        return result;
    }

    /**
     Displays the quiz question in the console.
     @param equation A string array containing the vector expressions and the operator.
     */
    private static void displayQuestion(String[] equation){
        System.out.println("This is your expression here, try it out: ");
        System.out.println(equation[0] + " " + equation[1] + " " + equation[2]);
    }

    /**
     Prompts the user to enter their answer and returns the user's input as a string.
     @return a string representing the user's input.
     */
    private static String getUserInput() {
        Scanner sc = new Scanner(System.in);
        System.out.println("");
        System.out.println("Please write your answer in proper format here: ");
        String userInput = sc.nextLine();
        return userInput;
    }

    /**
     Checks if the user's answer matches the computer-generated answer.
     @param userAnswer the user's answer to be checked
     @param computer the computer-generated answer
     @return true if the user's answer matches the computer-generated answer, false otherwise
     */
    private static Boolean checkAnswer(String userAnswer, String computer) {
        if (userAnswer.equals(computer)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     Runs a quiz in which the user is presented with randomly generated vectors and asked to solve an expression involving them.
     The user is prompted to enter their answer in proper format. The function compares the user's answer to the
     computer's calculated answer and prints a message indicating whether the user's answer is correct or not.
     To exit the quiz mode and return to the main menu, the user can type "E" as their answer.
     */
    public static void runQuiz(){
        System.out.println("Now entered quiz mode, to exit type E to exit and return to main menu");
        while (true){
            String[] expression = createExpression();
            displayQuestion(expression);

            VectorType v1 = VectorType.valueOf(expression[0]);
            String operator = expression[1];
            VectorType v2 = VectorType.valueOf(expression[2]);

            String solvedQuestionComputer = solveQuizQuestion(v1, operator, v2);
            String userAnswer = getUserInput();
            if (userAnswer.equals("E")){
                break;
            } else if (checkAnswer(userAnswer,solvedQuestionComputer)){
                System.out.println("You got it right!");
            } else {
                System.out.println("Sorry, wrong answer, try again.");
            }
        }
    }

    public static void main(String[] args) {
        runQuiz();
    }

}
