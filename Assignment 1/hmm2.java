public class hmm2 {

    public static double computeMax(double[] arr) {
        double maxValue = 0;
        for(int i = 0; i < arr.length; i++) {
            if(arr[i] > maxValue) {
                maxValue = arr[i];
            }
        }
        return maxValue;
    }

    public static int computeArgMax(double[] arr) {
        int arg = -1;
        double maxValue = -1;
        for(int i = 0; i < arr.length; i++) {
            if(arr[i] > maxValue) {
                maxValue = arr[i];
                arg = i;
            }
        }
        return arg;
    }
    
    public static void main(String[] args) {
        Kattio io = new Kattio(System.in, System.out);
        Model hmm = new Model();
        int rows, cols, numOfEmissions, currentEmission, finalMax, traceBack;
        double sum, finalSum, currentMax = 0;
        double[] inputArray, maxArray, finalDelta;
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

        // Multiply Beta by Pi to create Delta 1
        double Delta[][] = new double[numOfEmissions][A.length];
        int argMax[][] = new int[numOfEmissions][A.length];

        for(int i = 0; i < Beta[0].length ; i++) {
            Delta[0][i] = Pi[0][i] * Beta[0][i];
        }

        for(int e = 1; e < numOfEmissions; e++) {
            currentEmission = io.getInt();
            
            for(int x = 0; x < B.length; x++) {
                Beta[0][x] = B[x][currentEmission];
            }

            for(int i = 0; i < A.length; i++) {
                maxArray = new double[A.length];
                for(int j = 0; j < A.length; j++) {
                    maxArray[j] = A[j][i] * Delta[e-1][j] * Beta[0][i];
                }
                Delta[e][i] = computeMax(maxArray);
                argMax[e][i] = computeArgMax(maxArray);
            }
              
        }

        double[] finalArray = new double[A.length];
        for(int i = 0; i < Delta[0].length; i++) {
            finalArray[i] = Delta[numOfEmissions - 1][i];
        }   

        int[] traceBackArray = new int[numOfEmissions];
        traceBack = computeArgMax(finalArray);
        traceBackArray[0] = traceBack;

        for(int i = 1; i < argMax.length; i++) {
            traceBackArray[i] = argMax[argMax.length - i][traceBack];
            traceBack = traceBackArray[i];
        }

        for(int i = 0; i < traceBackArray.length; i++) {
            System.out.print(traceBackArray[traceBackArray.length - 1 - i] + " ");
        }
        
        io.close();
    }
}