package com.MPI;

import mpi.MPI;

public class PracticeSix {
    private double[] vectorA;
    private double[] vectorB;
    private int vectorLength;

    public PracticeSix(int vectorLength) {
        this.vectorLength = vectorLength;
        this.vectorA = new double[vectorLength];
        this.vectorB = new double[vectorLength];

        for (int i = 0; i < vectorLength; i++) {
            this.vectorA[i] = i + 1;
            this.vectorB[i] = i + 1;
        }

    }


    public void run(String[] args) {
        broadcastSend(args);
        scatterSend(args);
    }

    public void broadcastSend(String[] args) {
        MPI.Init(args);
        int TAG = 0;
        long startTime = 0;
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();


        int[] portionsSize = {0};
        if (vectorLength % (size - 1) == 0) {
            portionsSize[0] = vectorLength / (size - 1);
        } else {
            portionsSize[0] = vectorLength / (size - 1) + 1;
        }
        double[] a = new double[portionsSize[0]];
        double[] b = new double[portionsSize[0]];
        MPI.COMM_WORLD.Bcast(portionsSize, 0, portionsSize.length, MPI.INT, 0);

        if (rank == 0) {
            startTime = System.currentTimeMillis();
            for (int destination = 1; destination < size; destination++) {
                fillArrays(portionsSize[0], a, b, destination);
                MPI.COMM_WORLD.Send(a, 0, portionsSize[0], MPI.DOUBLE, destination, TAG);
                MPI.COMM_WORLD.Send(b, 0, portionsSize[0], MPI.DOUBLE, destination, TAG);
            }
            portionsSize[0] = vectorLength - portionsSize[0] * (size - 1);
        } else {
            MPI.COMM_WORLD.Recv(a, 0, portionsSize[0], MPI.DOUBLE, 0, TAG);
            MPI.COMM_WORLD.Recv(b, 0, portionsSize[0], MPI.DOUBLE, 0, TAG);
        }

        double[] sum = {0};
        for (int i = 0; i < portionsSize[0]; i++) {
            sum[0] += a[i] * b[i];
        }

        double[] result = {0};

        MPI.COMM_WORLD.Reduce(sum, 0, result, 0, 1, MPI.DOUBLE, MPI.SUM, 0);

        if (rank == 0) {
            System.out.println("Answer is " + result[0] + " & Time of Bcast&Reduce is equal to " + ((double) (System.currentTimeMillis() - startTime)));

        }
        MPI.Finalize();
    }

    public void scatterSend(String[] args) {
        MPI.Init(args);
        int TAG = 0;
        long startTime = 0;
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        double[] result = new double[size];


        int[] portionsSize = {0};
        if (vectorLength % size == 0) {
            portionsSize[0] = vectorLength / size;
        } else {
            portionsSize[0] = vectorLength / size + 1;
        }
        double[] a = new double[vectorLength];
        double[] recvA = new double[portionsSize[0]];
        double[] b = new double[vectorLength];
        double[] recvB = new double[portionsSize[0]];
        int additing = 0;
        if (vectorLength % portionsSize[0] != 0) {
            additing = (portionsSize[0] - vectorLength % portionsSize[0]);
            a = new double[vectorLength + additing];
            b = new double[vectorLength + additing];
        }
        System.arraycopy(vectorA, 0, a, 0, vectorLength);
        System.arraycopy(vectorB, 0, b, 0, vectorLength);


        if (rank == 0) {
            startTime = System.currentTimeMillis();
        }

        MPI.COMM_WORLD.Scatter(a, 0, portionsSize[0], MPI.DOUBLE, recvA, 0, portionsSize[0], MPI.DOUBLE, 0);
        MPI.COMM_WORLD.Scatter(b, 0, portionsSize[0], MPI.DOUBLE, recvB, 0, portionsSize[0], MPI.DOUBLE, 0);

        double[] sum = {0};
        for (int i = 0; i < portionsSize[0]; i++) {
            sum[0] += recvA[i] * recvB[i];
        }

        MPI.COMM_WORLD.Gather(sum, 0, 1, MPI.DOUBLE, result, 0, 1, MPI.DOUBLE, 0);

        if (rank == 0) {
            double r = 0;
            for (double i : result) {
                r += i;
            }
            System.out.println("Answer is " + r + " & Time of ScatterGather is equal to " + ((double) (System.currentTimeMillis() - startTime)));

        }
        MPI.Finalize();
    }


    private void fillArrays(int portionSize, double[] a, double[] b, int destination) {
        for (int i = 0; i < portionSize; i++) {
            if ((portionSize * (destination - 1) + i) < vectorLength) {
                a[i] = vectorA[portionSize * (destination - 1) + i];
                b[i] = vectorB[portionSize * (destination - 1) + i];
            } else {
                a[i] = 0;
                b[i] = 0;
            }
        }
    }

    public void non_par() {
        double s = 0.0;
        long st = System.currentTimeMillis();
        for (int i = 0; i < vectorLength; i++) {
            s += vectorA[i] * vectorB[i];
        }
        System.out.println("Answer is " + s + " & Time of calc is equal to " + ((double) (System.currentTimeMillis() - st)));
    }
}
