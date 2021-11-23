package com.MPI;

import com.MPI.graph.Graph;

import java.io.IOException;

public class PracticeEight {
    private Graph graph;

    PracticeEight() throws IOException {
        graph = new Graph("C:\\Users\\Костя\\IdeaProjects\\test\\src\\com\\MPI\\graph\\graphThorus");
    }

    public void run(String[] args) {
        graph.isGraphThorus(args);
    }
}
