
class Fraction {
    private int numerator;
    private int denominator;

    /**
     * Describes a Fraction
     * @param numerator Represents numerator - an integer.
     * @param denominator Represents denominator - an integer.
     * @throws IllegalArgumentException
     */

    public Fraction(int numerator, int denominator) throws IllegalArgumentException{
        if (denominator == 0){
            throw new IllegalArgumentException("Denominator cannot be zero");
        }
        // reducing
        this.numerator = reduceNum(numerator, denominator);
        this.denominator = reduceDen(numerator, denominator);   
    }

    /**
     * Constructor for a mixed fraction.
     * @param whole
     * @param numerator
     * @param denominator
     * @throws IllegalArgumentException
     */
    public Fraction(int whole, int numerator, int denominator) throws IllegalArgumentException{
        if (denominator == 0){
            throw new IllegalArgumentException("Denominator cannot be zero");
        }
        // converting from mixed to improper
        int num = Math.abs(whole) * Math.abs(denominator) + Math.abs(numerator);
        int den = Math.abs(denominator);
        
        // reducing
        this.numerator = reduceNum(num, den);
        this.denominator = reduceDen(num, den);

        // assigning the negative sign
        if (whole * numerator * denominator < 0){
            this.numerator = -1 * this.numerator;
        }
    }

    public Fraction(int whole){
        this.numerator = whole;
        this.denominator = 1;
    }

    /**
     * @param numVal
     * @param denVal
     * @return
     */
    private static int reduceNum(int numVal, int denVal){
        if (numVal == 0){
            return 0;
        }
        // check if they are the same sign
        else if (numVal < 0 && denVal < 0 || numVal > 0 && denVal > 0){
            return Math.abs(numVal / gcd(numVal, denVal));
        }
        return -1 * (Math.abs(numVal) / gcd(numVal, denVal));     
    }

    private static int reduceDen(int numVal, int denVal){
        if (numVal == 0){
            return 1;
        }
        return Math.abs(denVal / gcd(numVal, denVal));
    }

    private static int gcd(int a, int b){
        int min = Math.min(Math.abs(a), Math.abs(b));
        if (min == 0 || min == 1){
            return 1;
        }
        while (Math.abs(a) % min != 0 || Math.abs(b) % min != 0){
            min --;
        }
        return min;
    }

    public int getNumerator() {
        return numerator;
    }

    public int getDenominator(){
        return denominator;
    }

    public void setNumerator(int newNum){
        numerator = newNum;
    }

    public void setDenominator(int newDen){
        denominator = newDen;
    }

    public boolean equals(Fraction other){
        return numerator == other.numerator && denominator == other.denominator;
    }

    private int getWhole(){
        if (numerator < 0 && denominator < 0 || numerator > 0 && denominator > 0){
            return numerator / denominator;
        }
        else{
            return -1 * Math.abs(numerator) / Math.abs(denominator);
        }  
    }

    public String toString(){
        // whole
        if (denominator == 1){
            return Integer.toString(numerator);
        }
        // proper
        else if (getWhole() == 0){
            return String.format("%d/%d", numerator, denominator);
        }
        // mixed
        else{
            return String.format("%d %d/%d", getWhole(), Math.abs(numerator % denominator), Math.abs(denominator));
        }    
    }

    public Fraction clone() throws IllegalArgumentException{
        return new Fraction(numerator, denominator);
    }

    public Fraction add(Fraction other){
        // a/b + c/d
        // a*d + c*b
        // b * d
        return new Fraction(numerator * other.denominator + other.numerator * denominator, denominator * other.denominator);
    }

    public Fraction subtract(Fraction other){
        return this.add(new Fraction(-1 * other.numerator, other.denominator));
    }

    public Fraction multiply(Fraction other){
        return new Fraction(this.numerator * other.numerator, this.denominator * other.denominator);
    }

    public Fraction divide(Fraction other) throws IllegalArgumentException{
        if (other.numerator == 0){
            throw new IllegalArgumentException("Cannot divide by zero");
        }
        return this.multiply(new Fraction(other.denominator, other.numerator));
    }

    public static Fraction valueOf(String fraction) throws IllegalArgumentException{
        if (fraction.matches(wholeNumberPattern())){
            return new Fraction(Integer.valueOf(fraction));
        }
        else if (fraction.matches(properFractionPattern())){
            return new Fraction(Integer.valueOf(fraction.split("/")[0]), Integer.valueOf(fraction.split("/")[1]));
        }
        else if (fraction.matches(mixedFractionPattern())){
            return new Fraction(Integer.valueOf(fraction.split(" ")[0]), Integer.valueOf(fraction.split(" ")[1].split("/")[0]), Integer.valueOf(fraction.split(" ")[1].split("/")[1]));
        }
        else{
            throw new IllegalArgumentException("This cannot be a fraction");
        }
    }

    private static String wholeNumberPattern(){
        return "-{0,1}\\d+";
    }

    private static String properFractionPattern(){
        return String.format("%s/%s", wholeNumberPattern(), wholeNumberPattern());
    }

    private static String mixedFractionPattern(){
        return String.format("%s %s", wholeNumberPattern(), properFractionPattern());
    }

    public static String fractionPattern(){
        return String.format("(%s)|(%s)|(%s)",  mixedFractionPattern(), properFractionPattern(), wholeNumberPattern());
    }

    private static int randomValue(){
        return (int)(Math.random() * 10 + 1);
    }

    public static Fraction randomFraction(){
        int type = (int)(Math.random() * 3);
        if (type == 0){
            return new Fraction(randomValue());
        }
        else if (type == 1){
            return new Fraction(randomValue(), randomValue());
        }
        else{
            return new Fraction(randomValue(), randomValue(), randomValue());
        }
    }

    public double parseDouble(){
        return Double.valueOf(numerator) / Double.valueOf(denominator);
    }

    private static void test(){
        System.out.println(Fraction.valueOf("1/2")); // 1/2
        System.out.println(Fraction.valueOf("10/-12")); // -5/6
        System.out.println(Fraction.valueOf("-120/2")); // -60
        System.out.println(Fraction.valueOf("-51/4")); // -12 3/4
        System.out.println(Fraction.valueOf("-1204/120")); //-10 1/30
        System.out.println(Fraction.valueOf("-2 4/18")); // -2 2/9
        System.out.println(Fraction.valueOf("-2")); // -2
        System.out.println(Fraction.valueOf("-2 110/18")); // -8 1/9
        System.out.println(Fraction.valueOf("0/5")); // 0
        System.out.println(Fraction.valueOf("-0/5")); // 0
        System.out.println(Fraction.valueOf("-4/2")); // -2

        System.out.println(new Fraction(-11, 5).add(new Fraction(-2, 4))); // -2 7/10
        System.out.println(new Fraction(-11, 5).subtract(new Fraction( -2, 4))); // -1 7/10
        System.out.println(new Fraction(-11, 5).multiply(new Fraction(-2, 4))); // 1 1/10
        System.out.println(new Fraction(-11, 5).divide(new Fraction(-2, 4))); // 4 2/5

        System.out.println(new Fraction(-1, 11, 5).add(new Fraction(-2, 3, 4))); // -5 19/20
        System.out.println(new Fraction(-1, 11, 5).subtract(new Fraction(-2, 3, 4))); // -9/20
        System.out.println(new Fraction(-1, 11, 5).multiply(new Fraction(-2, 3, 4))); // 8 4/5
        System.out.println(new Fraction(-1, 11, 5).divide(new Fraction(-2, 3, 4))); // 1 9/55

        System.out.println(new Fraction(-1).add(new Fraction(-2))); // -3
        System.out.println(new Fraction(-1).subtract(new Fraction(-2))); // 1
        System.out.println(new Fraction(-1).multiply(new Fraction(-2))); // 2
        System.out.println(new Fraction(-1).divide(new Fraction(-2))); // 1/2

        System.out.println(new Fraction(-1).add(new Fraction(0))); // -1
        System.out.println(new Fraction(-1).subtract(new Fraction(0))); // -1
        System.out.println(new Fraction(-1).multiply(new Fraction(0))); // 0
        System.out.println(new Fraction(-1).divide(new Fraction(1))); // -1
    }
      
 
    public static void main(String[] args) {
        test();  
    }


 
}




