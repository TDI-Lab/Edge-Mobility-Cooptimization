package EdgeEPOS.Utility;

/**
 *
 * @author rooyesh
 *
 * This class contains the functions that are written for filling a arrays with
 * random numbers, according to certain distributions.
 */
public class ArrayFiller {

    /**
     * Given an array, it assigns as the elements of the array the probabilities
     * of a probability density function (PDF). The sum of elements will add up
     * to 1
     *
     * @param input
     */
    public static void fillRandomPDFInArray(double[] input) {
        int[] weight = new int[input.length];
        double sum = 0;
        for (int a = 0; a < input.length; a++) {
            weight[a] = (int) RandomGenerator.genUniformRandomBetween(10, 100);
            sum += weight[a];
        }
        for (int a = 0; a < input.length; a++) {
            input[a] = (double) weight[a] / sum;
        }
    }

    /**
     * This function fills random numbers in a 2D array, in a specified
     * range
     *
     * @param array the input array
     * @param rangeLow the low range of the random numbers generated
     * @param rangehigh the high range of the random numbers generated
     */
    public static void fill2DArrayRandomlyInRange(double[][] array, double rangeLow, double rangehigh) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                array[i][j] = RandomGenerator.genUniformRandomBetween(rangeLow, rangehigh);
            }
        }
    }

    /**
     * This function fills random numbers in a 2D array, in a specified
     * range
     *
     * @param array the input array
     * @param rangeLow the low range of the random numbers generated
     * @param rangehigh the high range of the random numbers generated
     */
    public static void fill2DArrayRandomlyInRange(double[][] array, int lowrowindex, int highrowindex, int lowcolindex, int highcolindex, double rangeLow, double rangehigh) {
        
    	for (int i = lowrowindex; i < highrowindex; i++) {
            for (int j = lowcolindex; j < highcolindex; j++) {
            	array[i][j] = RandomGenerator.genUniformRandomBetween(rangeLow, rangehigh);
            	
            }
        }
    }

    /**
     * This function fills random numbers in a 1D array, in a specified
     * range
     *
     * @param array the input array
     * @param rangeLow the low range of the random numbers generated
     * @param rangehigh the high range of the random numbers generated
     */
    public static void fill1DArrayRandomlyInRange(double[] array, int lowindex, int highindex, double rangeLow, double rangehigh) {
        for (int i = lowindex; i <= highindex; i++) {
            array[i] = RandomGenerator.genUniformRandomBetween(rangeLow, rangehigh);
        }
    }

    /**
     * This function fills random numbers in a 1D array, in a specified
     * range
     *
     * @param array the input array
     * @param rangeLow the low range of the random numbers generated
     * @param rangehigh the high range of the random numbers generated
     */
    
    public static void fill1DArrayRandomlyInRange(int[] array, int rangeLow, int rangehigh) {
        for (int i = 0; i < array.length; i++) {
            array[i] = RandomGenerator.getivalue(rangeLow, rangehigh);
        }
    }
    /**
     * This function fills random numbers in a 1D array, in a specified
     * range
     *
     * @param array the input array
     * @param rangeLow the low range of the random numbers generated
     * @param rangehigh the high range of the random numbers generated
     */
    public static void fill1DArrayRandomlyInRange(double[] array, double rangeLow, double rangehigh) {
        for (int i = 0; i < array.length; i++) {
            array[i] = RandomGenerator.genUniformRandomBetween(rangeLow, rangehigh);
        }
    }

    /**
     * This function fills random numbers in a 1D array, with specified mean
     * and variance
     *
     * @param array the input array
     * @param mean the mean of the distribution
     * @param variance the variance of the distribution
     */
    public static void fill2DArrayRandomlyWithMeanVariance(double[] array, double mean, double variance) {
        for (int i = 0; i < array.length; i++) {
            do {
                array[i] = RandomGenerator.genNormalRandomMeanVariance(mean, variance);
            } while (array[i] < 0);
        }
    }

    /**
     * This function fills random numbers in a 2D array, in a specified
     * range
     *
     * @param array the input array
     * @param rangeLow the low range of the random numbers generated
     * @param rangehigh the high range of the random numbers generated
     */
    public static void fill2DArrayWithConstantNumber(int[][] array, int value) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                array[i][j] = value;
            }
        }
    }

    public static void fill2DArrayWithConstantNumber(double[][] array, double value) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                array[i][j] = value;
            }
        }
    }

    public static void fill2DArrayWithConstantNumber(int[][] array, int lowrowindex, int highrowindex, int lowcolindex, int highcolindex, int value) {
    	for (int i = lowrowindex; i < highrowindex; i++) {
            for (int j = lowcolindex; j < highcolindex; j++) {
                  array[i][j] = value;
            }
        }
    }
    	
    public static void fill2DArrayWithConstantNumber(double[][] array, int lowrowindex, int highrowindex, int lowcolindex, int highcolindex, double value) {
    	for (int i = lowrowindex; i < highrowindex; i++) {
            for (int j = lowcolindex; j < highcolindex; j++) {
                  array[i][j] = value;
            }
        }
    }
    	
    /**
     * This function fills a 2D array, with a constant value
     * @param array the input array
     * @param value the constant value
     */
    public static <T extends Comparable<? super T>> void fill2DArrayWithConstantNumber(T[][] array, T value) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                array[i][j] = value;
            }
        }
    }

    /**
     * This function fills a 1D array, with a constant value
     * @param array the input array
     * @param d the constant value
     */
    /*
    public static <T extends Comparable<? super T>> void fill1DArrayWithConstantNumber(T[] array, T value) {
        for (int i = 0; i < array.length; i++) {
            array[i] = value;
        }
    }
    */
    public static void fill1DArrayWithConstantNumber(double[] array, double d) {
        for (int i = 0; i < array.length; i++) {
            array[i] = d;
        }
    }
    
    public static void fill1DArrayWithConstantNumber(double[] array, int lowindex, int highindex, double d) {
        for (int i = lowindex; i < highindex; i++) {
            array[i] = d;
        }
    }
    
    public static void fill1DArrayWithConstantNumber(int[] array, int d) {
        for (int i = 0; i < array.length; i++) {
            array[i] = d;
        }
    }
    
    public static void fill1DArrayWithConstantNumber(int[] array, int lowindex, int highindex, int d) {
        for (int i = lowindex; i < highindex; i++) {
            array[i] = d;
        }
    }
    
    public static void fill1DArrayWithConstantNumber(long[] array, int lowindex, int highindex, long d) {
        for (int i = lowindex; i < highindex; i++) {
            array[i] = d;
        }
    }
    /**
     * Converts a Integer 2D array to an int 2D array
     * @param input the input 2D array (Integer type)
     * @return 
     */
    public static int[][] convertIntegerToInt2DArray(Integer[][] input){
        int[][] output;
        int rows = input.length;
        int cols = input[0].length;
        output = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                output[i][j] = input[i][j];
            }
        }
        return output;
    }

	public static void fill1DArrayRandomlyInRange(long[] array, int lowindex, int highindex, double rangeLow,
			double rangehigh) {
		for (int i = lowindex; i <= highindex; i++) {
            array[i] = (long) RandomGenerator.genUniformRandomBetween(rangeLow, rangehigh);
        }
	}

	public static void fill1DArrayRandomlyInRange(int[] array, int lowindex, int highindex, double rangeLow,
			double rangehigh) {
		for (int i = lowindex; i <= highindex; i++) {
            array[i] = (int) RandomGenerator.genUniformRandomBetween(rangeLow, rangehigh);
        }
		
	}

	public static void fill1DArrayRandomlyInRange(long[] array, int l, int m) {
		for (int i = 0; i < array.length; i++) {
            array[i] = RandomGenerator.genUniformRandomBetween(l, m);
        }
	}

}
