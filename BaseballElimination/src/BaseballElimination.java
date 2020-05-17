import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.FlowEdge;

import java.util.ArrayList;
import java.util.List;

public class BaseballElimination {
    private final List<String> teams;
    private final int[] w;
    private final int[] l;
    private final int[] r;

    private final List<List<String>> certificateCache;

    private int[][] g;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        int n;

        final In in = new In(filename);
        n = in.readInt();

        w = new int[n];
        l = new int[n];
        r = new int[n];
        g = new int[n][n];
        certificateCache = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            certificateCache.add(null);
        }


        final List<String> teams = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            final String team = in.readString();
            teams.add(team);

            w[i] = in.readInt();
            l[i] = in.readInt();
            r[i] = in.readInt();

            for (int j = 0; j < n; j++) {
                g[i][j] = in.readInt();
            }
        }

        this.teams = teams;
    }

    // number of teams
    public int numberOfTeams() {
        return teams.size();
    }

    // all teams
    public Iterable<String> teams() {
        return new ArrayList<>(teams);
    }

    // number of wins for given team
    public int wins(String team) {
        int idx = getIndex(team);
        return w[idx];
    }

    // number of losses for given team
    public int losses(String team) {
        int idx = getIndex(team);
        return l[idx];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        int idx = getIndex(team);
        return r[idx];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        int idx1 = getIndex(team1);
        int idx2 = getIndex(team2);
        return g[idx1][idx2];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        return certificateOfElimination(team) != null;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        int x = getIndex(team);

        final List<String> certificate = (certificateCache.get(x) != null) ? certificateCache.get(x)
                : findCertificateOfElimination(x);

        certificateCache.set(x, certificate);
        if (certificate.size() == 0) {
            return null;
        }
        return new ArrayList<>(certificate);
    }

    public static void main(String[] args) {
        final BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            } else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }

    private List<String> findCertificateOfElimination(int x) {
        int n = teams.size();

        for (int i = 0; i < n; i++) {
            if (w[i] > w[x] + r[x]) {
                final ArrayList<String> certificate = new ArrayList<>();
                certificate.add(teams.get(i));
                return certificate;
            }
        }

        int gameVertices = n * (n - 1) / 2;
        int teamVertices =  n;
        int s = gameVertices + teamVertices;
        int t = gameVertices + teamVertices + 1;
        final FlowNetwork network = new FlowNetwork(gameVertices + teamVertices + 2);


        int gameId = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                int teamI = gameVertices + i;
                int teamJ = gameVertices + j;

                if (g[i][j] > 0) {
                    network.addEdge(new FlowEdge(s, gameId, g[i][j]));
                    network.addEdge(new FlowEdge(gameId, teamI, Double.POSITIVE_INFINITY));
                    network.addEdge(new FlowEdge(gameId, teamJ, Double.POSITIVE_INFINITY));
                }

                gameId += 1;
            }
        }

        long targetValue = 0;
        for (int i = 0; i < n; i++) {
            int teamI = gameVertices + i;
            int capacity = w[x] + r[x] - w[i];
            targetValue += capacity;
            network.addEdge(new FlowEdge(teamI, t, w[x] + r[x] - w[i]));
        }

        final FordFulkerson fordFulkerson = new FordFulkerson(network, s, t);
        if (fordFulkerson.value() >= targetValue + 0.1) {
            return new ArrayList<>();
        }

        final ArrayList<String> certificate = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (i == x) {
                continue;
            }

            int teamI = gameVertices + i;
            if (fordFulkerson.inCut(teamI)) {
                certificate.add(teams.get(i));
            }
        }
        return certificate;
    }

    private int getIndex(String team) {
        if (!teams.contains(team)) {
            throw new IllegalArgumentException("team " + team + " not found");
        }
        return teams.indexOf(team);
    }
}
