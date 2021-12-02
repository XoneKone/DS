package com.MPI.graph;

import mpi.MPI;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Graph {
    private int[][] matrix;
    private int[] degrees;
    private int[] vertices;
    private int size;

    public Graph(String path) throws IOException {
        //fillMatrix(path);
        size = 100;
        Random r = new Random(123);
        matrix = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == j)
                    matrix[i][j] = 0;
                else
                    matrix[i][j] = r.nextDouble() >= 0.5 ? 1 : 0;
            }
        }
    }

    public void fillMatrix(String path) throws IOException {
        Scanner sc = new Scanner(new BufferedReader(new FileReader(path)));

        Stream<String> lines2 = Files.lines(Paths.get(path));
        int countOfVertices = (int) lines2.count();
        this.matrix = new int[countOfVertices][countOfVertices];
        this.vertices = new int[countOfVertices];
        this.degrees = new int[countOfVertices];
        for (int vertex = 0; vertex < countOfVertices; vertex++) {
            this.degrees[vertex] = vertex;
        }

        while (sc.hasNextLine()) {
            for (int i = 0; i < matrix.length; i++) {
                String[] line = sc.nextLine().trim().split(" ");
                for (int j = 0; j < line.length; j++) {
                    matrix[i][j] = Integer.parseInt(line[j]);
                }
            }
        }
    }

    public int vertexCount() {
        return size;
    }

    public int degree(int vertex) {
        int vertexDegree = 0;
        for (int i = 0; i < vertices.length; i++) {
            if (matrix[vertex][i] != 0)
                vertexDegree++;
        }
        return vertexDegree;
    }

    public void non_par() {


        double startTime = System.currentTimeMillis();

        int max = 0;
        int degree = 0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[i][j] != 0)
                    degree += 1;
            }
            if (max < degree)
                max = degree;
            degree = 0;
        }
        System.out.println("Answer is " + max + " & Time of MaxDegree is equal to " + ((double) (System.currentTimeMillis() - startTime)));
    }

    public void findMaxDegree(String[] args) {
        MPI.Init(args);
        int TAG = 0;
        long startTime = 0;
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        int[] result = new int[1];

        int[] portionsSize = {0};

        if (vertexCount() % size == 0) {
            portionsSize[0] = vertexCount() / size;
        } else {
            portionsSize[0] = vertexCount() / size + 1;
        }

        int[] a = new int[vertexCount() * vertexCount()];
        int[] recvA = new int[portionsSize[0] * vertexCount()];
        int additing = 0;

        if (vertexCount() % portionsSize[0] != 0) {
            additing = vertexCount() * (portionsSize[0] - vertexCount() % portionsSize[0]);
            a = new int[vertexCount() * vertexCount() + additing];
        }

        int[] array = Stream.of(matrix)
                .flatMapToInt(IntStream::of)
                .toArray();
        System.arraycopy(array, 0, a, 0, vertexCount() * vertexCount());

        if (rank == 0) {
            startTime = System.currentTimeMillis();
            System.out.println(Arrays.deepToString(matrix));

        }
        MPI.COMM_WORLD.Scatter(a, 0, portionsSize[0] * vertexCount(), MPI.INT, recvA, 0, portionsSize[0] * vertexCount(), MPI.INT, 0);
        int[] max = {0};
        int degree = 0;
        int board = recvA.length / portionsSize[0];
        for (int i = 0; i < portionsSize[0]; i++) {
            for (int j = 0; j < board; j++) {
                if (recvA[i * board + j] != 0)
                    degree += recvA[i * board + j];
            }
            if (max[0] < degree)
                max[0] = degree;
            degree = 0;

        }

        MPI.COMM_WORLD.Reduce(max, 0, result, 0, 1, MPI.INT, MPI.MAX, 0);


        if (rank == 0) {
            System.out.println("Answer is " + result[0] + " & Time of MaxDegree is equal to " + ((double) (System.currentTimeMillis() - startTime)));
        }
        MPI.Finalize();
    }

    public boolean check() {
        boolean isThorus = true;
        for (int i = 0; i < vertexCount(); i++) {
            for (int j = 0; j < vertexCount(); j++) {
                if (matrix[i][j] != matrix[vertexCount() - i - 1][vertexCount() - j - 1]) {
                    isThorus = false;
                    break;
                }
            }
        }
        return isThorus;
    }

    public void isGraphThorus(String[] args) {
        MPI.Init(args);
        int TAG = 0;
        long startTime = 0;
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        int[] result = new int[1];
        int[] verticesDegrees = new int[vertexCount()];

        int[] portionsSize = {0};

        if (vertexCount() % size == 0) {
            portionsSize[0] = vertexCount() / size;
        } else {
            portionsSize[0] = vertexCount() / size + 1;
        }

        int[] a = new int[vertexCount() * vertexCount()];
        int[] recvA = new int[portionsSize[0] * vertexCount()];
        int additing = 0;

        if (vertexCount() % portionsSize[0] != 0) {
            additing = vertexCount() * (portionsSize[0] - vertexCount() % portionsSize[0]);
            a = new int[vertexCount() * vertexCount() + additing];
        }

        int[] array = Stream.of(matrix)
                .flatMapToInt(IntStream::of)
                .toArray();

        System.arraycopy(array, 0, a, 0, vertexCount() * vertexCount());

        if (rank == 0) {
            startTime = System.currentTimeMillis();
            System.out.println(Arrays.deepToString(matrix));

        }
        MPI.COMM_WORLD.Scatter(a, 0, portionsSize[0] * vertexCount(), MPI.INT, recvA, 0, portionsSize[0] * vertexCount(), MPI.INT, 0);
        int[] degree = new int[portionsSize[0]];

        int board = recvA.length / portionsSize[0];

        for (int i = 0; i < portionsSize[0]; i++) {
            for (int j = 0; j < board; j++) {
                if (recvA[i * board + j] != 0)
                    degree[i] += recvA[i * board + j];
            }
        }

        MPI.COMM_WORLD.Gather(degree, 0, degree.length, MPI.INT, verticesDegrees, 0, degree.length, MPI.INT, 0);


        if (rank == 0) {
            boolean isThorus = true;
            for (int i : verticesDegrees) {
                if (i != 4) {
                    isThorus = false;
                    break;
                }
            }
            if (isThorus && check())
                System.out.println("Graph definitely is Thorus & Time of ThorusCheck is equal to " + ((double) (System.currentTimeMillis() - startTime)));
            else
                System.out.println("Graph definitely isn`t Thorus & Time of ThorusCheck is equal to " + ((double) (System.currentTimeMillis() - startTime)));
        }
        MPI.Finalize();
    }
}
