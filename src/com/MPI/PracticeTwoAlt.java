package com.MPI;

import mpi.MPI;
import mpi.Request;
import mpi.Status;

public class PracticeTwoAlt {
    public static void run(String[] args) {
        int TAG = 1;
        int[] message = new int[1];

        MPI.Init(args);
        int myrank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        Request[] requestsSend = new Request[size];
        Request[] requestsRecv = new Request[size];
        Status[] statusesSend = new Status[size];
        Status[] statusesRecv = new Status[size];

        if (myrank == 0) {
            requestsSend[myrank] = MPI.COMM_WORLD.Isend(message, 0, message.length, MPI.INT, 1, TAG);
            statusesSend[myrank] = requestsSend[myrank].Wait();
            if (requestsSend[myrank].Is_null()) {
                System.out.println("Isend to " + (myrank) + " complete");
            }

            requestsRecv[myrank] = MPI.COMM_WORLD.Irecv(message, 0, message.length, MPI.INT, size - 1, TAG);
            statusesRecv[myrank] = requestsRecv[myrank].Wait();
            if (requestsRecv[myrank].Is_null()) {
                System.out.println("Irecv to " + (size - 1) + " complete");
                System.out.println("Received:from rank " + (myrank - 1 + size) % size + " to rank " + myrank + " sum: " + message[0]);
            }

        } else {
            requestsRecv[myrank] = MPI.COMM_WORLD.Irecv(message, 0, message.length, MPI.INT, (myrank - 1 + size) % size, TAG);
            statusesRecv[myrank] = requestsRecv[myrank].Wait();
            if (requestsRecv[myrank].Is_null()) {
                System.out.println("Irecv to " + (myrank - 1 + size) % size + " complete");
                System.out.println("Received:from rank " + (myrank - 1 + size) % size + " to rank " + myrank + " sum: " + message[0]);
            }

            message[0] += myrank;
            requestsSend[myrank] = MPI.COMM_WORLD.Isend(message, 0, message.length, MPI.INT, (myrank + 1) % size, TAG);
            statusesSend[myrank] = requestsSend[myrank].Wait();
            if (requestsSend[myrank].Is_null()) {
                System.out.println("Isend to " + (myrank) + " complete");
            }

        }
        MPI.Finalize();
    }
}
