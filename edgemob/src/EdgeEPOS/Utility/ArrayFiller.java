package EdgeEPOS.Utility;

/**
 *
 * @author rooyesh
 *
 * This class contains the functions that are written for filling arrays with
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

    public static void fill2DArrayRandomlyInRangeDoubled(double[][] array, int lowrowindex, int highrowindex, int lowcolindex, int highcolindex, double rangeLow, double rangehigh) {
        
    	for (int i = lowrowindex; i < highrowindex; i++) {
            for (int j = lowcolindex; j < highcolindex; j++) {
            	array[i][j] = RandomGenerator.genUniformRandomBetween(rangeLow, rangehigh);
            	array[j][i] = array[i][j];
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

    public static void fill2DArrayWithConstantNumber(short[][] array, short value) {
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
    
    public static void fill2DArrayWithConstantNumber(short[][] array, int lowrowindex, int highrowindex, int lowcolindex, int highcolindex, short value) {
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
    public static void fill1DArrayWithConstantNumber(boolean[] array, int lowindex, int highindex, boolean d) {
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

	public static void fill1DArrayWithArrays(double[] fp, int[] fm, long[] fs, short[] ncore, double[] sC_MaxPow, double[] sC_IdlePow, int lowindex, int highindex, int[] p, int[] m, int[] s, short[] nP, double[] idlePow, int[] maxPow) {
		//first create edge node indices randomly
		int index;
		for (int i = lowindex; i <= highindex; i++) {
			index = RandomGenerator.genUniformRandomBetween(0, p.length);
            fp[i] = p[index];
            fm[i] = m[index];
            fs[i] = s[index];
            ncore[i] = nP[index];
            sC_IdlePow[i] = idlePow[index];
            sC_MaxPow[i] = maxPow[index];
            
        }
	}

	public static void fill2DArrayRandomlyInRangeDiagonal(double[][] array, int lowrowindex, int highrowindex, int lowcolindex, int highcolindex, double rangeLow, double rangehigh) {
        // TODO Auto-generated method stub
		for (int i = lowrowindex; i < highrowindex; i++) {
            for (int j = lowcolindex; j < i; j++) {
            	array[i][j] = RandomGenerator.genUniformRandomBetween(rangeLow, rangehigh);
            	array[j][i] = array[i][j];
            }
        }
		
	}

	/** updates the available resources including cpu, memory, storage based on the hard constraints. 
	 * @param fP
	 * @param fM
	 * @param fS
	 * @param fP_back
	 * @param fM_back
	 * @param fS_back
	 * @param lowindex
	 * @param highindex
	 * @param factor
	 */
	public static void adapt1DArrayWithArrays(double[] fP, int[] fM, long[] fS, double[] fP_back, int[] fM_back,
			long[] fS_back, int lowindex, int highindex, double[] fp, double[] fm, double[] fs) {
		
		for (int i = lowindex; i <= highindex; i++) {
			fP[i] = fP_back[i]*fp[i];
            fM[i] = (int) (fM_back[i]*fm[i]);
            fS[i] = (long) (fS_back[i]*fs[i]);
            
        }
	}

	/**
	 * updates the available resources including cpu, memory, storage based on the hard constraints. 
	 * @param fp
	 * @param fm
	 * @param fs
	 * @param lowindex
	 * @param highindex
	 * @param factor
	 */
	public static void adapt1DArrayWithArrays(double[] fP, int[] fM, long[] fS, double[] fP_back, int[] fM_back,
			long[] fS_back, int lowindex, int highindex, double[] factor) {
		
		for (int i = lowindex; i <= highindex; i++) {
			fP[i] = fP_back[i]*factor[i];
            fM[i] = (int) (fM_back[i]*factor[i]);
            fS[i] = (long) (fS_back[i]*factor[i]);
            
        }
	}
	

}
