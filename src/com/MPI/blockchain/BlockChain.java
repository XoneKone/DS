package com.MPI.blockchain;

import mpi.MPI;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import com.google.gson.*;
import mpi.Status;
import org.jetbrains.annotations.NotNull;

import javax.imageio.plugins.bmp.BMPImageWriteParam;

public class BlockChain {
    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static HashMap<String, TransactionOutputs> UTXOs = new HashMap<String, TransactionOutputs>();

    public static int difficulty = 4;
    public static float minimumTransaction = 0.1f;
    public static Block genesis;
    public static Wallet walletA;
    public static Wallet walletB;
    public static Transaction genesisTransaction;

    public BlockChain() {
    }

    public void setUp() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); //Setup Bouncey castle as a Security Provider


        walletA = new Wallet();
        walletB = new Wallet();
        Wallet coinbase = new Wallet();


        genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 1000f, null);
        genesisTransaction.generateSignature(coinbase.privateKey);
        genesisTransaction.transactionId = "0";
        genesisTransaction.outputs.add(new TransactionOutputs(genesisTransaction.reciepient, genesisTransaction.value, genesisTransaction.transactionId));
        UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        System.out.println("Creating and Mining Genesis block... ");
        genesis = new Block("Genesis", "0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);
    }

    public void non_par() {


    }

    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        HashMap<String, TransactionOutputs> tempUTXOs = new HashMap<String, TransactionOutputs>(); //a temporary working list of unspent transactions at a given block state.
        tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        //loop through blockchain to check hashes:
        for (int i = 1; i < blockchain.size(); i++) {

            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);
            //compare registered hash and calculated hash:
            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                System.out.println("#Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                System.out.println("#Previous Hashes not equal");
                return false;
            }
            //check if hash is solved
            if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
                System.out.println("#This block hasn't been mined");
                return false;
            }

            //loop thru blockchains transactions:
            TransactionOutputs tempOutput;
            for (int t = 0; t < currentBlock.transactions.size(); t++) {
                Transaction currentTransaction = currentBlock.transactions.get(t);

                if (!currentTransaction.verifySignature()) {
                    System.out.println("#Signature on Transaction(" + t + ") is Invalid");
                    return false;
                }
                if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
                    return false;
                }

                for (TransactionInput input : currentTransaction.inputs) {
                    tempOutput = tempUTXOs.get(input.transactionOutputId);

                    if (tempOutput == null) {
                        System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
                        return false;
                    }

                    if (input.UTXO.value != tempOutput.value) {
                        System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
                        return false;
                    }

                    tempUTXOs.remove(input.transactionOutputId);
                }

                for (TransactionOutputs output : currentTransaction.outputs) {
                    tempUTXOs.put(output.id, output);
                }

                if (currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient) {
                    System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
                    return false;
                }
                if (currentTransaction.outputs.get(1).reciepient != currentTransaction.sender) {
                    System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
                    return false;
                }

            }

        }
        System.out.println("Blockchain is valid");
        return true;
    }

    public void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }

    public void run(String[] args) {
        double time = 0;
        int number = 0;
        int count = 1;
        int source;
        Status st;
        char[] message;
        char[] prevHash;
        String prev;

        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();


        MPI.COMM_WORLD.Barrier();


        if (rank == 0) {
            time = System.currentTimeMillis();

            for (int i = 1; i < size; i++) {
                st = MPI.COMM_WORLD.Probe(i, 0);
                number = st.Get_count(MPI.CHAR);
                message = new char[number];
                MPI.COMM_WORLD.Recv(message, 0, message.length, MPI.CHAR, i, 0);
                if (blockchain.isEmpty())
                    setUp();

                prev = blockchain.get(blockchain.size() - 1).hash;
                Block block = new Block("Block number:" + count + " from rank: " + i, prev);
                count++;

                if (String.valueOf(message).equals("From")) {
                    System.out.println("\nWalletA is Attempting to send funds (400) to WalletB...");
                    block.addTransaction(walletA.sendFunds(walletB.publicKey, 400f));

                } else {
                    System.out.println("\nWalletB is Attempting to send funds (500) to WalletA...");
                    block.addTransaction(walletB.sendFunds(walletA.publicKey, 400f));
                }

                addBlock(block);

            }

        } else {
            if (rank % 2 == 0) {
                message = "To".toCharArray();
            } else {
                message = "From".toCharArray();
            }
            MPI.COMM_WORLD.Send(message, 0, message.length, MPI.CHAR, 0, 0);
        }


        if (rank == 0) {
            isChainValid();
            System.out.println("\nWalletA's balance is: " + walletA.getBalance());
            System.out.println("WalletB's balance is: " + walletB.getBalance());
            System.out.println("Time is: " + (System.currentTimeMillis() - time));
            System.out.println("\nThe block chain: ");
            for (Block b : blockchain) {
                System.out.println(b + "\n");

            }
        }
        MPI.Finalize();
    }
}
