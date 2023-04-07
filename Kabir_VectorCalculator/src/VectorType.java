import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class VectorType {
    private Fraction[] dimensions;

    /**
     Constructor for a VectorType object that takes in an array of Fractions as its dimensions.
     @param dimensions an array of Fractions representing the dimensions of the vector
     */
    public VectorType(Fraction... dimensions) {
        this.dimensions = dimensions;
    }

    /**
     Returns a string format for a vector with n dimensions.
     @param n an integer representing the number of dimensions of the vector.
     @return a string format that can be used to parse and format vectors with n dimensions.
     */
    public static String formatVector(int n) {
        String[] coordPatterns = new String[n];
        Arrays.fill(coordPatterns, Fraction.fractionPattern());
        return String.format("\\((%s)\\)", String.join("), (", coordPatterns));
    }

    /**
     Creates a new VectorType object from a string in the format "(a, b, c, ...)" where a, b, c, ...
     represent the individual components of the vector.
     @param vector a string representation of a vector
     @return a VectorType object created from the input string
     @throws IllegalArgumentException if the input string is not in the correct format
     */

    public static VectorType valueOf(String vector) throws IllegalArgumentException {
        Matcher matcher = Pattern.compile("\\((.*)\\)").matcher(vector);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid Format");
        }
        String[] dimensionStrings = matcher.group(1).split(",\\s*");
        Fraction[] dimensions = new Fraction[dimensionStrings.length];
        for (int i = 0; i < dimensions.length; i++) {
            dimensions[i] = Fraction.valueOf(dimensionStrings[i]);
        }
        return new VectorType(dimensions);
    }

    /**
     * Adds this vector to another vector and returns the resulting vector.
     *
     * @param other the vector to add to this vector
     * @return the vector sum of this vector and the other vector
     * @throws IllegalArgumentException if the dimensions of the two vectors are different
     */
    public VectorType add(VectorType other) throws IllegalArgumentException {
        if (dimensions.length != other.dimensions.length) {
            throw new IllegalArgumentException("Cannot add vectors of different dimensions");
        }
        Fraction[] resultDimensions = new Fraction[dimensions.length];
        for (int i = 0; i < dimensions.length; i++) {
            resultDimensions[i] = dimensions[i].add(other.dimensions[i]);
        }
        return new VectorType(resultDimensions);
    }

    /**
     Returns a new VectorType object that represents the subtraction of the input vector from the current vector.
     @param other the VectorType object to subtract from the current vector
     @return a new VectorType object that represents the subtraction of the input vector from the current vector
     @throws IllegalArgumentException if the dimensions of the input vector do not match the dimensions of the current vector
     */
    public VectorType subtract(VectorType other) throws IllegalArgumentException {
        if (dimensions.length != other.dimensions.length) {
            throw new IllegalArgumentException("Cannot subtract vectors of different dimensions");
        }
        Fraction[] resultDimensions = new Fraction[dimensions.length];
        for (int i = 0; i < dimensions.length; i++) {
            resultDimensions[i] = dimensions[i].subtract(other.dimensions[i]);
        }
        return new VectorType(resultDimensions);
    }


    /**
     Calculates the cross product of this vector with another vector.
     Both vectors must have the same number of dimensions and dimensions greater or equal to 3.
     The cross product of two vectors is defined as a vector orthogonal to both of them.
     @param other the other vector to calculate the cross product with
     @return a new vector representing the cross product of this vector and the other vector
     @throws IllegalArgumentException if the vectors have different dimensions or have less than 3 dimensions
     */
    public VectorType crossProduct(VectorType other) throws IllegalArgumentException {
        int n = dimensions.length;
        int m = other.dimensions.length;
        if (n != m || n < 3 || m < 3) {
            throw new IllegalArgumentException("Vectors must have the same number of dimensions and dimensions greater or equal to 3");
        }

        Fraction[] result = new Fraction[n];
        for (int i = 0; i < n; i++) {
            int j = (i + 1) % n;
            int k = (i + 2) % n;
            result[j] = dimensions[i].multiply(other.dimensions[k]).subtract(dimensions[k].multiply(other.dimensions[i]));
        }

        return new VectorType(result);
    }

    /**
     * Computes the dot product of this vector with the given vector.
     *
     * @param other the other vector to compute the dot product with
     * @return the dot product of this vector and the given vector
     * @throws IllegalArgumentException if the dimensions of this vector and the given vector are different
     */
    public Fraction dotProduct(VectorType other) throws IllegalArgumentException {
        if (dimensions.length != other.dimensions.length) {
            throw new IllegalArgumentException("Cannot compute dot product of vectors of different dimensions");
        }
        Fraction sum = new Fraction(0,0,1);
        for (int i = 0; i < dimensions.length; i++) {
            sum = sum.add(dimensions[i].multiply(other.dimensions[i]));
        }
        return sum;
    }

    /**
     Returns a new vector where each dimension is multiplied by the given scalar.
     @param scalar the scalar value to distribute
     @return a new VectorType with each dimension multiplied by the scalar
     */
    public VectorType distributeScalar(Fraction scalar) {
        Fraction[] resultCoords = new Fraction[dimensions.length];
        for (int i = 0; i < dimensions.length; i++) {
            resultCoords[i] = dimensions[i].multiply(scalar);
        }
        return new VectorType(resultCoords);
    }

    /**
     Returns a string representation of this vector in the format "(d1, d2, ..., dn)",
     where d1, d2, ..., dn are the coordinates of the vector.
     @return a string representation of this vector
     */

    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < dimensions.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(dimensions[i]);
        }
        sb.append(")");
        return sb.toString();
    }

    private static void test(){
        VectorType a = new VectorType(new Fraction(2),new Fraction(2) ,new Fraction(-8), new Fraction(4), new Fraction(5));
        VectorType b = new VectorType(new Fraction(3),new Fraction(3) ,new Fraction(4), new Fraction(3));
        VectorType c = new VectorType(new Fraction(2),new Fraction(1) ,new Fraction(5), new Fraction(2));
        System.out.println(b.crossProduct(c));
        System.out.println(b.add(c));
        System.out.println(b.subtract(c));
        System.out.println(b.dotProduct(c));
        System.out.println(VectorType.valueOf("(2, 43, 4, 22)"));
        System.out.println(formatVector(3));

    }
    public static void main(String[] args) {
        test();
    }

}
