package com.MPI;

import mpi.*;

import java.util.Arrays;
import java.util.Random;


public class PracticeThree {

    private final Random random;

    public PracticeThree() {
        this.random = new Random();
    }

    public int log2(int N) {
        return (int) (Math.log(N) / Math.log(2));
    }


    public void run(String[] args) {
        // ***** Initial part ***** //
        MPI.Init(args);
        final int ODD_TAG = 0;
        final int EVEN_TAG = 1;

        int[] odd_number = new int[1];
        int[] even_number = new int[1];

        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        int localSize = (int) Math.round((size - 3) / 2.0);
        int k;

        int[] buf = new int[1];
        int[] buf_odd = new int[localSize];
        int[] buf1 = new int[localSize];
        int[] buf2 = new int[localSize];
        int[] buf_even = new int[localSize];
        int[] result = new int[2 * localSize];

        Request[] sendRequests = new Request[size];
        Request[] recvRequests = new Request[size];

        Status[] sendStatuses = new Status[size];
        Status[] recvStatuses = new Status[size];
        // ************************************** //

        if (rank == 0) {
            recvRequests[size / 2] = MPI.COMM_WORLD.Irecv(buf1, 0, buf1.length, MPI.INT, size / 2, ODD_TAG);
            recvRequests[size - 1] = MPI.COMM_WORLD.Irecv(buf2, 0, buf2.length, MPI.INT, size - 1, EVEN_TAG);
            Request.Waitall(recvRequests);

            // ***** Sorting **** //
            for (int i = 0; i < localSize; i++) {
                if (buf1[i] < buf2[i]) {
                    result[2 * i] = buf1[i];
                    result[2 * i + 1] = buf2[i];
                } else {
                    result[2 * i] = buf2[i];
                    result[2 * i + 1] = buf1[i];
                }
                k = 2 * i;
                while (k > 0 && result[k - 1] > result[k]) {
                    int tmp = result[k - 1];
                    result[k - 1] = result[k];
                    result[k] = tmp;
                    k--;
                }
            }
            // ****************** //

            // ***** Print array ***** //
            for (int i : result) {
                if (i != 0)
                    System.out.print(i + " ");
            }
            // ***** ********** ***** //

        } else if (rank != size / 2 && rank != size - 1) {
            if (rank < size / 2) {
                odd_number[0] = initOdd();
                sendRequests[rank] = MPI.COMM_WORLD.Isend(odd_number, 0, odd_number.length, MPI.INT, size / 2, ODD_TAG);
            } else {
                even_number[0] = initEven();
                sendRequests[rank] = MPI.COMM_WORLD.Isend(even_number, 0, even_number.length, MPI.INT, size - 1, EVEN_TAG);
            }
            Request.Waitall(sendRequests);
        } else if (rank == size / 2 || rank == size - 1) {
            if (rank == size / 2) {
                for (int source = 1; source < size / 2; source++) {
                    if (source != rank) {
                        recvRequests[source] = MPI.COMM_WORLD.Irecv(buf, 0, 1, MPI.INT, source, ODD_TAG);
                        recvStatuses[source] = recvRequests[source].Wait();
                        buf_odd[source % localSize] = buf[0];
                    }
                }
                Arrays.sort(buf_odd);
                sendRequests[rank] = MPI.COMM_WORLD.Isend(buf_odd, 0, buf_odd.length, MPI.INT, 0, ODD_TAG);
            } else {
                for (int source = size / 2 + 1; source < size - 1; source++) {
                    if (source != size - 1) {
                        recvRequests[rank] = MPI.COMM_WORLD.Irecv(buf, 0, 1, MPI.INT, MPI.ANY_SOURCE, EVEN_TAG);
                        recvStatuses[source] = recvRequests[rank].Wait();
                        buf_even[source % localSize] = buf[0];
                    }
                }
                Arrays.sort(buf_even);
                sendRequests[rank] = MPI.COMM_WORLD.Isend(buf_even, 0, buf_even.length, MPI.INT, 0, EVEN_TAG);
            }
            Request.Waitall(sendRequests);
        }
        MPI.Finalize();

    }

    private int initOdd() {

        return random.nextInt(50) * 2 + 1;
    }

    private int initEven() {
        return random.nextInt(50) * 2;
    }
}
