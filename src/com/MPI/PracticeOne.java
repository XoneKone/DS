package com.MPI;

import mpi.*;

public class PracticeOne {
    public static void run(String[] args) {
        int TAG = 0;
        Status status;
        MPI.Init(args);
        int myrank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int[] message = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int[] message2 = new int[10];
        if (myrank == 0) {
            MPI.COMM_WORLD.Send(message, 0, message.length, MPI.INT, 1, 1);
        } else {
            if (myrank == 1) {
                MPI.COMM_WORLD.Recv(message2, 0, 3, MPI.INT, 0, 1);
                System.out.println("Received:from rank " + myrank);
                for (int j : message2) {
                    System.out.print(j + " ");
                }
            }
        }
        MPI.Finalize();
    }
}
