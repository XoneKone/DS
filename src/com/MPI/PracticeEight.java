package com.MPI;

import com.MPI.blockchain.Block;
import mpi.MPI;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PracticeEight {
    public static List<Block> blockchain = new ArrayList<Block>();
    public static int prefix = 4;
    public static String prefixString = new String(new char[prefix]).replace('\0', '0');

    public void setUp() {
        Block genesisBlock = new Block("Genesis Block", "0", new Date().getTime());
        genesisBlock.mineBlock(prefix);
        blockchain.add(genesisBlock);
    }

    public void run(String[] args) {


        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        String prev = blockchain.get(blockchain.size() - 1).getPreviousHash();
        char[] previousHash = prev.toCharArray();
        MPI.COMM_WORLD.Bcast(previousHash,0,previousHash.length,MPI.CHAR,0);


        MPI.Finalize();
    }
}
