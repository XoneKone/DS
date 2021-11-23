package com.MPI;

import com.MPI.blockchain.Block;
import mpi.MPI;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.*;

public class PracticeNine {
    public static List<Block> blockchain = new ArrayList<Block>();
    public static int difficulty = 4;
    public static String prefixString = new String(new char[difficulty]).replace('\0', '0');

    public PracticeNine() {
        setUp();
    }

    public void setUp() {
        Block genesisBlock = new Block("Genesis Block", "0");
        System.out.println("Trying to Mine Genesis Block... ");
        genesisBlock.mineBlock(difficulty);
        blockchain.add(genesisBlock);
    }

    public void non_par() {
        blockchain.add(new Block("Yo im the second block", blockchain.get(blockchain.size() - 1).getHash()));
        System.out.println("Trying to Mine block 2... ");
        blockchain.get(1).mineBlock(difficulty);


        blockchain.add(new Block("Hey im the third block", blockchain.get(blockchain.size() - 1).getHash()));
        System.out.println("Trying to Mine block 3... ");
        blockchain.get(2).mineBlock(difficulty);
        System.out.println("\nBlockchain is Valid: " + isChainValid());

        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println("\nThe block chain: ");
        System.out.println(blockchainJson);
    }

    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;

        //loop through blockchain to check hashes:
        for(int i=1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);
            //compare registered hash and calculated hash:
            if(!currentBlock.getHash().equals(currentBlock.calculateBlockHash()) ){
                System.out.println("Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if(!previousBlock.getHash().equals(currentBlock.getPreviousHash()) ) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
        }
        return true;
    }

    public void run(String[] args) {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        String prev = blockchain.get(blockchain.size() - 1).getPreviousHash();
        char[] previousHash = prev.toCharArray();
        MPI.COMM_WORLD.Bcast(previousHash, 0, previousHash.length, MPI.CHAR, 0);


        MPI.Finalize();
    }
}
