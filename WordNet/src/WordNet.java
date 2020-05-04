import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ST;

import java.util.ArrayList;
import java.util.List;

public class WordNet {
    private final ST<String, List<Integer>> synsetIds = new ST<>();
    private final ST<Integer, String> synsetItems = new ST<>();
    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null) {
            throw new IllegalArgumentException("synsets must be not null");
        }
        if (hypernyms == null) {
            throw new IllegalArgumentException("hypernyms must be not null");
        }

        final In synsetIn = new In(synsets);
        while (synsetIn.hasNextLine()) {
            final String line = synsetIn.readLine().strip();
            final String[] items = line.split(",");
            if (items.length < 2) {
                continue;
            }
            final int id = Integer.parseInt(items[0]);
            final String[] names = items[1].split(" ");
            for (String name: names) {
                if (!synsetIds.contains(name)) {
                    synsetIds.put(name, new ArrayList<>());
                }
                synsetIds.get(name).add(id);
                synsetItems.put(id, items[1]);
            }
        }
        int n = synsetItems.max() + 1;

        final Digraph graph = new Digraph(n);

        final In hypernymIn = new In(hypernyms);
        while (hypernymIn.hasNextLine()) {
            final String line = hypernymIn.readLine().strip();
            final String[] items = line.split(",");
            if (items.length < 2) {
                continue;
            }
            int v = Integer.parseInt(items[0]);
            for (int i = 1; i < items.length; i += 1) {
                int w = Integer.parseInt(items[i]);
                graph.addEdge(v, w);
            }
        }
        if (!isRootedDag(graph)) {
            throw new IllegalArgumentException("graph is not rooted DAG");
        }

        this.sap = new SAP(graph);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return synsetIds.keys();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException("word must be not null");
        }
        return synsetIds.contains(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA)) {
            throw new IllegalArgumentException("nounA must be in WordNet");
        }
        if (!isNoun(nounB)) {
            throw new IllegalArgumentException("nounB must be in WordNet");
        }
        return sap.length(synsetIds.get(nounA), synsetIds.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA)) {
            throw new IllegalArgumentException("nounA must be in WordNet");
        }
        if (!isNoun(nounB)) {
            throw new IllegalArgumentException("nounB must be in WordNet");
        }

        int ancestorId = sap.ancestor(synsetIds.get(nounA), synsetIds.get(nounB));
        assert ancestorId != -1;
        return synsetItems.get(ancestorId);
    }

    private boolean isRootedDag(Digraph graph) {
        if (graph.V() == 0) {
            return true;
        }
        final int n = graph.V();

        final int[] marked = new int[n];
        for (int i = 0; i < n; i++) {
            if (leadsToCycle(i, graph, marked)) {
                return false;
            }
        }

        int rootCandidates = 0;
        for (int i = 0; i < n; i++) {
            if (graph.outdegree(i) == 0) {
                rootCandidates += 1;
            }
        }
        return rootCandidates == 1;
    }

    private boolean leadsToCycle(int v, Digraph g, int[] marked) {
        if (marked[v] == 1) {
            return true;
        }
        if (marked[v] == 2) {
            return false;
        }
        marked[v] = 1;
        for (int w: g.adj(v)) {

            if (leadsToCycle(w, g, marked)) {
                return true;
            }
        }
        marked[v] = 2;
        return false;
    }
}