import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;

public class SAP {
    private final Digraph g;
    private final int n;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        g = new Digraph(G);
        n = G.V();
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        return length(new ArrayList<>(Arrays.asList(v)), new ArrayList<>(Arrays.asList(w)));
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w)  {
        return ancestor(new ArrayList<>(Arrays.asList(v)), new ArrayList<>(Arrays.asList(w)));
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        return findAncestor(v, w).dist;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        return findAncestor(v, w).vertex;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }

    private int[] bfs(Iterable<Integer> vertices) {
        if (vertices == null) {
            throw new IllegalArgumentException("given null vertices");
        }

        final int[] dist   = new int[n];
        final boolean[] marked = new boolean[n];
        final Queue<Integer> queue = new Queue<>();

        for (int i = 0; i < n; i++) {
            dist[i] = n + 1;
        }

        for (Integer v: vertices) {
            if (v == null || v < 0 || v >= n) {
                throw new IllegalArgumentException("an illegal vertex");
            }

            dist[v] = 0;
            if (!marked[v]) {
                queue.enqueue(v);
            }
            marked[v] = true;
        }
        while (!queue.isEmpty()) {
            int v = queue.dequeue();
            for (int w: g.adj(v)) {
                if (!marked[w]) {
                    dist[w] = dist[v] + 1;
                    queue.enqueue(w);
                    marked[w] = true;
                }
            }
        }
        return dist;
    }

    private Ancestor findAncestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (n == 0) {
            return new Ancestor(-1, -1);
        }

        int[] distV = bfs(v);
        int[] distW = bfs(w);

        int minDist = distV[0] + distW[0];
        int minVertex = 0;
        for (int i = 1; i < n; i++) {
            int dist = distV[i] + distW[i];
            if (dist < minDist) {
                minDist   = dist;
                minVertex = i;
            }
        }
        if (minDist >= n) {
            return new Ancestor(-1, -1);
        }
        return new Ancestor(minVertex, minDist);
    }

    private static class Ancestor {
        private final int vertex;
        private final int dist;

        public Ancestor(int vertex, int dist) {
            this.vertex = vertex;
            this.dist = dist;
        }
    }
}