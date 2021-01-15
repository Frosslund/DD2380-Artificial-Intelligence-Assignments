public class hmm0 {
    public static void main(String[] args) {
        Kattio io = new Kattio(System.in, System.out);
        Model hmm = new Model();
        MatrixMultiplication matrix = new MatrixMultiplication();
        int rows, cols;
        double sum = 0;
        double[] inputArray;
        double[][] A, B, Pi, Pre, Dist;

        // Get input data from Kattio
        rows = io.getInt();
        cols = io.getInt();
        inputArray = new double[rows * cols];

        for (int i = 0; i < inputArray.length; i++) {
            inputArray[i] = io.getDouble();
        }
        // Set the transformation matrix (A)
        A = hmm.createTransformationMatrix(rows, cols, inputArray);

        // Get input data from Kattio
        rows = io.getInt();
        cols = io.getInt();
        inputArray = new double[rows * cols];

        for (int i = 0; i < inputArray.length; i++) {
            inputArray[i] = io.getDouble();
        }
        // Set observation matrix (B)
        B = hmm.createObservationMatrix(rows, cols, inputArray);

        // Get input data from kattio
        rows = io.getInt();
        cols = io.getInt();
        inputArray = new double[rows * cols];

        for (int i = 0; i < inputArray.length; i++) {
            inputArray[i] = io.getDouble();
        }

        // Set initial state probability distribution (Pi)
        Pi = hmm.createISPD(rows, cols, inputArray);

        // Multiply Pi and A.
        Pre = matrix.multiplyMatrices(Pi, A);

        // Output dimensions of next observation matrix
        System.out.print(Pre.length + " ");
        System.out.print(B[0].length + " ");

        // Multiply Pre and B
        Dist = matrix.multiplyMatrices(Pre, B);

        // Output next observation distribution

        for (int i = 0; i < Dist[0].length; i++) {
            System.out.print(Dist[0][i] + " ");
        }

        io.close();
    }
}