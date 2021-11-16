package com.MPI;

import mpi.*;


public class PracticeTwo {
    public static void run(String[] args) {
        int TAG = 1;
        int[] message = new int[1];

        MPI.Init(args);
        int myrank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        if (myrank == 0) {
            MPI.COMM_WORLD.Sendrecv_replace(message, 0, message.length, MPI.INT, 1, TAG, size - 1, TAG);
            System.out.println("Received:from rank " + (myrank - 1 + size) % size + " to rank " + myrank + " sum: " + message[0]);
        } else {
            MPI.COMM_WORLD.Recv(message, 0, message.length, MPI.INT, (myrank - 1 + size) % size, TAG);
            System.out.println("Received:from rank " + (myrank - 1 + size) % size + " to rank " + myrank + " sum: " + message[0]);
            message[0] += myrank;
            MPI.COMM_WORLD.Send(message, 0, message.length, MPI.INT, (myrank + 1) % size, TAG);
        }
        MPI.Finalize();
    }
}
