package com.MPI;

import com.MPI.blockchain.BlockChain;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        int choice = 9;
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

            }
            case 6 -> {
                PracticeSix practiceSix = new PracticeSix(10000000);
                //practiceSix.run(args);
                practiceSix.non_par();
            }
            case 7 -> {
                PracticeSeven practiceSeven = new PracticeSeven();
                practiceSeven.run(args);
                //practiceSeven.non_par();
            }
            case 8 -> {
                PracticeEight practiceEight = new PracticeEight();
                practiceEight.run(args);
            }
            case 9 -> {
                BlockChain blockChain = new BlockChain();
                blockChain.run(args);

            }
        }
    }
}
