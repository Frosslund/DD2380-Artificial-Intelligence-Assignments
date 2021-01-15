public class hmm1 {
    // Think about what are rows and what are
    // columns in the observation matrix. Make sure that your code handles non-square matrices.
    public static void main(String[] args) {
        Kattio io = new Kattio(System.in, System.out);
        Model hmm = new Model();
        MatrixMultiplication matrix = new MatrixMultiplication();
        int rows, cols, numOfEmissions, currentEmission;
        double sum, finalSum = 0;
        double[] inputArray;
        double[][] A, B, Pi;

        rows = io.getInt();
        cols = io.getInt();

        inputArray = new double[rows*cols];
        for(int i = 0; i < inputArray.length; i++) {
            inputArray[i] = io.getDouble();
        }

        // Set the transformation matrix (A)
        A = hmm.createTransformationMatrix(rows, cols, inputArray);

        // Get input data from Kattio
        rows = io.getInt();
        cols = io.getInt();
        inputArray = new double[rows*cols];
        
        for(int i = 0; i < inputArray.length; i++) {
            inputArray[i] = io.getDouble();
        }
        // Set observation matrix (B)
        B = hmm.createObservationMatrix(rows, cols, inputArray);

        // Get input data from kattio
        rows = io.getInt();
        cols = io.getInt();
        inputArray = new double[rows*cols];
        
        for(int i = 0; i < inputArray.length; i++) {
            inputArray[i] = io.getDouble();
        }

        // Set initial state probability distribution (Pi)
        Pi = hmm.createISPD(rows, cols, inputArray);

        numOfEmissions = io.getInt();

        currentEmission = io.getInt();
        // C array, column of B corresponding to initial observation
        double[][] Beta = new double[1][B.length];

        // Fill out Beta array
        for(int x = 0; x < B.length; x++) {
            Beta[0][x] = B[x][currentEmission];
        }

        // Multiply Beta by Pi to create alpha1
        double Alpha[][] = new double[numOfEmissions][Beta[0].length];

        for(int i = 0; i < Beta[0].length ; i++) {
            Alpha[0][i] = Pi[0][i] * Beta[0][i];
        }

        sum = 0;

        for(int e = 1; e < numOfEmissions; e++) {
            double r[][] = new double[1][A[0].length];
            currentEmission = io.getInt();

            // Multiply alpha[e] by A
            for(int j = 0; j < Alpha[0].length; j++) {
                for(int k = 0; k < A.length; k++) {
                    sum = sum + Alpha[e-1][k]*A[k][j];
                }
                r[0][j] = sum;
                sum = 0;
            }            

            for(int x = 0; x < B.length; x++) {
                Beta[0][x] = B[x][currentEmission];
            }

            for(int i = 0; i < Beta[0].length; i++) {
                Alpha[e][i] = Beta[0][i] * r[0][i];
            }

        }

        for(int i = 0; i < Alpha[0].length; i++) {
            finalSum = finalSum +  Alpha[numOfEmissions - 1][i];
        }
        System.out.println(finalSum);
    }
}