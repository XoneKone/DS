package com.MPI;

import mpi.MPI;
import mpi.Request;
import mpi.Status;

import java.nio.ByteBuffer;


public class PracticeFive {
    private double[] vectorA;
    private double[] vectorB;
    private int vectorLength;

    public PracticeFive(int vectorLength) {
        this.vectorLength = vectorLength;
        this.vectorA = new double[vectorLength];
        this.vectorB = new double[vectorLength];

        for (int i = 0; i < vectorLength; i++) {
            this.vectorA[i] = i + 1;
            this.vectorB[i] = i + 1;
        }

    }


    public void run(String[] args) {
        syncSend(args);
        RSend(args);
        ISend(args);
        casualSend(args);
    }

    public void casualSend(String[] args) {
        int TAG = 0;

        MPI.Init(args);
        long startTime = System.currentTimeMillis();
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        double[] result = {0};
        double[] buf = {0};


        int portionSize = vectorLength / (size - 1) + 1;
        double[] a = new double[portionSize];
        double[] b = new double[portionSize];

        if (rank == 0) {
            for (int destination = 1; destination < size; destination++) {
                fillArrays(portionSize, a, b, destination);
                MPI.COMM_WORLD.Send(a, 0, portionSize, MPI.DOUBLE, destination, TAG);
                MPI.COMM_WORLD.Send(b, 0, portionSize, MPI.DOUBLE, destination, TAG);
                MPI.COMM_WORLD.Recv(buf, 0, buf.length, MPI.DOUBLE, destination, TAG + 1);
                result[0] += buf[0];
            }
        } else {
            MPI.COMM_WORLD.Recv(a, 0, portionSize, MPI.DOUBLE, 0, TAG);
            MPI.COMM_WORLD.Recv(b, 0, portionSize, MPI.DOUBLE, 0, TAG);
            double[] sum = {0};
            for (int i = 0; i < portionSize; i++) {
                sum[0] += a[i] * b[i];

            }
            MPI.COMM_WORLD.Send(sum, 0, sum.length, MPI.DOUBLE, 0, TAG + 1);
        }

        if (rank == 0) {
            System.out.println("Answer is " + result[0] + " & Time of calc (Send) is equal to " + ((double) (System.currentTimeMillis() - startTime)));

        }
        MPI.Finalize();
    }


    public void syncSend(String[] args) {
        int TAG = 0;

        MPI.Init(args);
        long startTime = System.currentTimeMillis();
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        double[] result = {0};
        double[] buf = {0};


        int portionSize = vectorLength / (size - 1) + 1;
        double[] a = new double[portionSize];
        double[] b = new double[portionSize];

        if (rank == 0) {
            for (int destination = 1; destination < size; destination++) {
                fillArrays(portionSize, a, b, destination);
                MPI.COMM_WORLD.Ssend(a, 0, portionSize, MPI.DOUBLE, destination, TAG);
                MPI.COMM_WORLD.Ssend(b, 0, portionSize, MPI.DOUBLE, destination, TAG);
                MPI.COMM_WORLD.Recv(buf, 0, buf.length, MPI.DOUBLE, destination, TAG + 1);
                result[0] += buf[0];
            }
        } else {
            MPI.COMM_WORLD.Recv(a, 0, portionSize, MPI.DOUBLE, 0, TAG);
            MPI.COMM_WORLD.Recv(b, 0, portionSize, MPI.DOUBLE, 0, TAG);
            double[] sum = {0};
            for (int i = 0; i < portionSize; i++) {
                sum[0] += a[i] * b[i];

            }
            MPI.COMM_WORLD.Ssend(sum, 0, sum.length, MPI.DOUBLE, 0, TAG + 1);
        }

        if (rank == 0) {
            System.out.println("Answer is " + result[0] + " & Time of calc (Ssend) is equal to " + ((double) (System.currentTimeMillis() - startTime)));
        }
        MPI.Finalize();
    }

    public void RSend(String[] args) {
        MPI.Init(args);
        int TAG = 0;
        long startTime = System.currentTimeMillis();
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        double[] result = {0};
        double[] buf = {0};


        int portionSize = vectorLength / (size - 1) + 1;
        double[] a = new double[portionSize];
        double[] b = new double[portionSize];

        if (rank == 0) {
            for (int destination = 1; destination < size; destination++) {
                fillArrays(portionSize, a, b, destination);
                MPI.COMM_WORLD.Rsend(a, 0, portionSize, MPI.DOUBLE, destination, TAG);
                MPI.COMM_WORLD.Rsend(b, 0, portionSize, MPI.DOUBLE, destination, TAG);
                MPI.COMM_WORLD.Recv(buf, 0, buf.length, MPI.DOUBLE, destination, TAG + 1);
                result[0] += buf[0];
            }
        } else {
            MPI.COMM_WORLD.Recv(a, 0, portionSize, MPI.DOUBLE, 0, TAG);
            MPI.COMM_WORLD.Recv(b, 0, portionSize, MPI.DOUBLE, 0, TAG);
            double[] sum = {0};
            for (int i = 0; i < portionSize; i++) {
                sum[0] += a[i] * b[i];

            }
            MPI.COMM_WORLD.Rsend(sum, 0, sum.length, MPI.DOUBLE, 0, TAG + 1);
        }

        if (rank == 0) {
            System.out.println("Answer is " + result[0] + " & Time of calc (Rsend) is equal to " + ((double) (System.currentTimeMillis() - startTime)));
        }
        MPI.Finalize();
    }

    public void ISend(String[] args) {
        MPI.Init(args);
        int TAG = 0;
        long startTime = System.currentTimeMillis();
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        double[] result = {0};
        double[] buf = {0};

        Request[] sendRequests = new Request[size];
        Request[] recvRequests = new Request[size];


        int portionSize = vectorLength / (size - 1) + 1;
        double[] a = new double[portionSize];
        double[] b = new double[portionSize];

        if (rank == 0) {
            for (int destination = 1; destination < size; destination++) {
                fillArrays(portionSize, a, b, destination);
                sendRequests[destination] = MPI.COMM_WORLD.Isend(a, 0, portionSize, MPI.DOUBLE, destination, TAG);
                sendRequests[destination] = MPI.COMM_WORLD.Isend(b, 0, portionSize, MPI.DOUBLE, destination, TAG);
                Request.Waitall(sendRequests);
                recvRequests[destination] = MPI.COMM_WORLD.Irecv(buf, 0, buf.length, MPI.DOUBLE, destination, TAG + 1);
                Request.Waitall(recvRequests);

                result[0] += buf[0];
            }
        } else {
            recvRequests[0] = MPI.COMM_WORLD.Irecv(a, 0, portionSize, MPI.DOUBLE, 0, TAG);
            recvRequests[1] = MPI.COMM_WORLD.Irecv(b, 0, portionSize, MPI.DOUBLE, 0, TAG);
            Request.Waitall(recvRequests);

            double[] sum = {0};
            for (int i = 0; i < portionSize; i++) {
                sum[0] += a[i] * b[i];

            }
            sendRequests[0] = MPI.COMM_WORLD.Isend(sum, 0, sum.length, MPI.DOUBLE, 0, TAG + 1);
            Request.Waitall(sendRequests);
        }

        if (rank == 0) {
            System.out.println("Answer is " + result[0] + " & Time of calc (Isend) is equal to " + ((double) (System.currentTimeMillis() - startTime)));
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
