public class hmm3 {
    public static void main(String[] args) {
        Kattio io = new Kattio(System.in, System.out);
        Model hmm = new Model();
        int rows, cols, numOfEmissions, currentEmission;
        double[][] A, B, Pi, Gamma, E, Alpha, Beta;
        double[][][] Digamma;
        double denom, numer;
        double sum = 0;
        double[] inputArray, scaleFactors;
        int[] emissionsArray;
        rows = io.getInt();
        cols = io.getInt();
        int iters = 0;
        int maxIters = 30;
        double logProb = -100000000;
        double oldLogProb = logProb-1;
        boolean completed = false;

        inputArray = new double[rows*cols];
        for(int i = 0; i < inputArray.length; i++) {
            inputArray[i] = io.getDouble();
        }

        A = hmm.createTransformationMatrix(rows, cols, inputArray);

        rows = io.getInt();
        cols = io.getInt();
        inputArray = new double[rows*cols];
        
        for(int i = 0; i < inputArray.length; i++) {
            inputArray[i] = io.getDouble();
        }
        
        B = hmm.createObservationMatrix(rows, cols, inputArray);

        rows = io.getInt();
        cols = io.getInt();
        inputArray = new double[rows*cols];
        
        for(int i = 0; i < inputArray.length; i++) {
            inputArray[i] = io.getDouble();
        }

        Pi = hmm.createISPD(rows, cols, inputArray);

        numOfEmissions = io.getInt();
        emissionsArray = new int[numOfEmissions];
        for(int i = 0; i < numOfEmissions; i++) {
            emissionsArray[i] = io.getInt();
        }

        Alpha = new double[numOfEmissions][A.length];
        scaleFactors = new double[numOfEmissions];
        Beta = new double[numOfEmissions][A.length];
        Digamma = new double[numOfEmissions][A.length][A.length];
        Gamma = new double[numOfEmissions][A.length];

        //Här börjar while-loopen
        while(!completed && iters < maxIters) {
            scaleFactors[0] = 0;

            // Computing Alpha 0
            for(int i = 0; i < A.length ; i++) {
                Alpha[0][i] = Pi[0][i] * B[i][emissionsArray[0]];
                scaleFactors[0] += Alpha[0][i];
            }

            // Scaling Alpha 0
            scaleFactors[0] = 1/scaleFactors[0];
            for(int i = 0; i < A.length; i++) {
                Alpha[0][i] = scaleFactors[0] * Alpha[0][i];
            }

            // Computing and scaling Alpha t
            // fortsätta lusläsa stamp
            for(int e = 1; e < numOfEmissions; e++) {
                scaleFactors[e] = 0;

                for(int i = 0; i < A.length; i++) {
                    Alpha[e][i] = 0;
                    for(int j = 0; j < A.length; j++) {
                        Alpha[e][i] += Alpha[e-1][j]*A[j][i];
                    }
                    Alpha[e][i] = Alpha[e][i] * B[i][emissionsArray[e]];
                    scaleFactors[e] += Alpha[e][i];
                }

                scaleFactors[e] = 1/scaleFactors[e];

                for(int i = 0; i < A.length; i++) {
                    Alpha[e][i] = scaleFactors[e] * Alpha[e][i];
                }  
            }
            /* Alpha pass finished */
            /* Beta pass starting */

            // Set initial Betas to 1, scaled by scaling
            for(int i = 0; i < A.length; i++) {
                Beta[numOfEmissions-1][i] = scaleFactors[numOfEmissions-1];
            }

            for(int e = numOfEmissions-2; e >= 0; e--) {

                for(int i = 0; i < A.length; i++) {
                    Beta[e][i] = 0;

                    for(int j = 0; j < A.length; j++) {
                        Beta[e][i] += Beta[e+1][j] * A[i][j] * B[j][emissionsArray[e+1]];
                    }

                    Beta[e][i] = scaleFactors[e] * Beta[e][i];
                }
            }

            /* for(int e = 2; e <= numOfEmissions; e++) {
                for(int i = 0; i < A.length; i++) {
                    Beta[numOfEmissions-e][i] = 0;

                    for(int j = 0; j < A.length; j++) {
                        Beta[numOfEmissions-e][i] += Beta[numOfEmissions-e+1][j] * A[i][j] * B[j][emissionsArray[numOfEmissions-e+1]];
                    }

                    Beta[numOfEmissions-e][i] = scaleFactors[numOfEmissions-e] * Beta[numOfEmissions-e][i];
                }
            } */
            /* Beta pass finished */
            /* Time to compute gammas */

            for(int e = 0; e < numOfEmissions - 1; e++) {

                for(int i = 0; i < A.length; i++) {
                    Gamma[e][i] = 0;
                    for(int j = 0; j < A.length; j++) {
                        Digamma[e][i][j] = Alpha[e][i] * A[i][j] * Beta[e+1][j] * B[j][emissionsArray[e+1]];
                        Gamma[e][i] += Digamma[e][i][j];
                    }
                }
            }

            for(int i = 0; i < A.length; i++) {
                Gamma[numOfEmissions - 1][i] = Alpha[numOfEmissions - 1][i];
            }

            /* Gamma and Digamma finished */
            /* Re-estimation of model */

            // re-estimate Pi
            for(int i = 0; i < A.length; i++) {
                Pi[0][i] = Gamma[0][i];
            }

            // re-estimate A
            for(int i = 0; i < A.length; i++) {
                denom = 0;
                for(int e = 0; e < numOfEmissions - 1; e++) {
                    denom += Gamma[e][i];
                }
                for(int j = 0; j < A.length; j++) {
                    numer = 0;
                    for(int t = 0; t < numOfEmissions - 1; t++) {
                        numer += Digamma[t][i][j];
                    }
                    A[i][j] = numer / denom;
                }
            }

            // re-estimate B
            for(int i = 0; i < A.length; i++) {
                denom = 0;
                for(int e = 0; e < numOfEmissions; e++) {
                    denom += Gamma[e][i];
                }
                for(int j = 0; j < B[0].length; j++) {
                    numer = 0;
                    for(int t = 0; t < numOfEmissions; t++) {
                        if(emissionsArray[t] == j) {
                            numer += Gamma[t][i];
                        }
                    }
                    B[i][j] = numer / denom;
                }
            }

            // Compute logs
            logProb = 0;
            for(int i = 0; i < numOfEmissions; i++) {
                logProb += Math.log(scaleFactors[i]);
            }
            logProb = -logProb;
            
            iters++;

            if(iters < maxIters && logProb > oldLogProb) {
                oldLogProb = logProb;
            } else {
                completed = true;
                System.out.print(A.length + " ");
                System.out.print(A[0].length + " ");
                for(int i = 0; i < A.length; i++) {
                    for(int j = 0; j < A[0].length; j++) {
                        System.out.print(A[i][j] + " ");
                    }
                }
                System.out.println();
                System.out.print(B.length + " ");
                System.out.print(B[0].length + " ");
                for(int i = 0; i < B.length; i++) {
                    for(int j = 0; j < B[0].length; j++) {
                        System.out.print(B[i][j] + " ");
                    }
                }
            }
        }
        io.close();
    }
}