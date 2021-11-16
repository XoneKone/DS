package com.MPI;

import com.MPI.graph.Graph;

import java.io.FileNotFoundException;
import java.io.IOException;

public class PracticeSeven {
    private Graph graph;

    PracticeSeven() throws IOException {
        graph = new Graph("C:\\Users\\Костя\\IdeaProjects\\test\\src\\com\\MPI\\graph\\graph2");
    }

    public void run(String[] args) {
        graph.findMaxDegree(args);
    }


}
