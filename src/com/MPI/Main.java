package com.MPI;

import com.MPI.graph.Graph;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        int choice = 6;
        switch (choice) {
            case 1 -> {
                PracticeOne.run(args);
            }
            case 2 -> {
                PracticeTwoAlt.run(args);
            }
            case 3 -> {
                PracticeThree practiceThree = new PracticeThree();
                practiceThree.run(args);
            }
            case 4 -> {
                PracticeFour.run(args);
            }
            case 5 -> {
                PracticeFive practiceFive1 = new PracticeFive(10);
                practiceFive1.casualSend(args);
                practiceFive1.syncSend(args);
                practiceFive1.RSend(args);
                practiceFive1.ISend(args);
//                practiceFive1.non_par();
//                PracticeFive practiceFive2 = new PracticeFive(100000);
////                practiceFive2.casualSend(args);
////                practiceFive2.syncSend(args);
////                practiceFive2.RSend(args);
////                practiceFive2.ISend(args);
//                practiceFive2.non_par();
//                PracticeFive practiceFive3 = new PracticeFive(1000000);
////                practiceFive3.casualSend(args);
////                practiceFive3.syncSend(args);
////                practiceFive3.RSend(args);
////                practiceFive3.ISend(args);
//                practiceFive3.non_par();
//                PracticeFive practiceFive4 = new PracticeFive(10000000);
////                practiceFive4.casualSend(args);
////                practiceFive4.syncSend(args);
////                practiceFive4.RSend(args);
////                practiceFive4.ISend(args);
//                practiceFive4.non_par();
            }
            case 6 -> {
                PracticeSix practiceSix = new PracticeSix(10000000);
                //practiceSix.run(args);
                practiceSix.non_par();
            }
            case 7 -> {
                PracticeSeven practiceSeven = new PracticeSeven();
                practiceSeven.run(args);
            }
        }
    }
}
