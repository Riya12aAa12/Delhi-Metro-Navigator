
import java.util.*;

public class Graph_M {

    public class Vertex {

        HashMap<String, Integer> nbrs = new HashMap<>();
    }

    static HashMap<String, Vertex> vtces;

    public Graph_M() {
        vtces = new HashMap<>();
    }

    public int numVertex() {
        return vtces.size();
    }

    public boolean containsVertex(String vname) {
        return vtces.containsKey(vname);
    }

    public void addVertex(String vname) {
        Vertex vtx = new Vertex();
        vtces.put(vname, vtx);
    }

    public void removeVertex(String vname) {
        Vertex vtx = vtces.get(vname);
        ArrayList<String> keys = new ArrayList<>(vtx.nbrs.keySet());

        for (String key : keys) {
            Vertex nbrVtx = vtces.get(key);
            nbrVtx.nbrs.remove(vname);
        }

        vtces.remove(vname);
    }

    public int numEdges() {
        int count = 0;
        for (Vertex vtx : vtces.values()) {
            count += vtx.nbrs.size();
        }
        return count / 2;
    }

    public boolean containsEdge(String vname1, String vname2) {
        Vertex vtx1 = vtces.get(vname1);
        Vertex vtx2 = vtces.get(vname2);

        return vtx1 != null && vtx2 != null && vtx1.nbrs.containsKey(vname2);
    }

    public void addEdge(String vname1, String vname2, int value) {
        Vertex vtx1 = vtces.get(vname1);
        Vertex vtx2 = vtces.get(vname2);

        if (vtx1 == null || vtx2 == null || vtx1.nbrs.containsKey(vname2)) {
            return;
        }

        vtx1.nbrs.put(vname2, value);
        vtx2.nbrs.put(vname1, value);
    }

    public void removeEdge(String vname1, String vname2) {
        Vertex vtx1 = vtces.get(vname1);
        Vertex vtx2 = vtces.get(vname2);

        if (vtx1 == null || vtx2 == null || !vtx1.nbrs.containsKey(vname2)) {
            return;
        }

        vtx1.nbrs.remove(vname2);
        vtx2.nbrs.remove(vname1);
    }

    public void displayMap() {
        System.out.println("\t Delhi Metro Map");
        System.out.println("\t------------------");
        System.out.println("----------------------------------------------------\n");

        for (String key : vtces.keySet()) {
            StringBuilder str = new StringBuilder(key + " =>\n");
            Vertex vtx = vtces.get(key);

            for (String nbr : vtx.nbrs.keySet()) {
                str.append("\t").append(nbr).append("\t");

                if (nbr.length() < 16) {
                    str.append("\t");
                }
                if (nbr.length() < 8) {
                    str.append("\t");
                }

                str.append(vtx.nbrs.get(nbr)).append("\n");
            }
            System.out.println(str);
        }
        System.out.println("\t------------------");
        System.out.println("---------------------------------------------------\n");
    }

    public void displayStations() {
        System.out.println("\n***********************************************************************\n");
        int i = 1;
        for (String key : vtces.keySet()) {
            System.out.println(i + ". " + key);
            i++;
        }
        System.out.println("\n***********************************************************************\n");
    }

    public boolean hasPath(String vname1, String vname2, HashMap<String, Boolean> processed) {
        if (containsEdge(vname1, vname2)) {
            return true;
        }

        processed.put(vname1, true);

        Vertex vtx = vtces.get(vname1);
        for (String nbr : vtx.nbrs.keySet()) {
            if (!processed.containsKey(nbr)) {
                if (hasPath(nbr, vname2, processed)) {
                    return true;
                }
            }
        }
        return false;
    }

    private class DijkstraPair implements Comparable<DijkstraPair> {

        String vname;
        String psf;
        int cost;

        @Override
        public int compareTo(DijkstraPair o) {
            return this.cost - o.cost;
        }
    }

    public int dijkstra(String src, String des, boolean nan) {
        int val = 0;
        ArrayList<String> ans = new ArrayList<>();
        HashMap<String, DijkstraPair> map = new HashMap<>();

        Heap<DijkstraPair> heap = new Heap<>();

        for (String key : vtces.keySet()) {
            DijkstraPair np = new DijkstraPair();
            np.vname = key;
            np.cost = Integer.MAX_VALUE;

            if (key.equals(src)) {
                np.cost = 0;
                np.psf = key;
            }

            heap.add(np);
            map.put(key, np);
        }

        while (!heap.isEmpty()) {
            DijkstraPair rp = heap.remove();

            if (rp.vname.equals(des)) {
                val = rp.cost;
                break;
            }

            map.remove(rp.vname);
            ans.add(rp.vname);

            Vertex v = vtces.get(rp.vname);
            for (String nbr : v.nbrs.keySet()) {
                if (map.containsKey(nbr)) {
                    int oc = map.get(nbr).cost;
                    int nc = nan ? rp.cost + 120 + 40 * v.nbrs.get(nbr) : rp.cost + v.nbrs.get(nbr);

                    if (nc < oc) {
                        DijkstraPair gp = map.get(nbr);
                        gp.psf = rp.psf + nbr;
                        gp.cost = nc;

                        heap.updatePriority(gp);
                    }
                }
            }
        }
        return val;
    }

    private class Pair {

        String vname;
        String psf;
        int min_dis;
        int min_time;
    }

    public String getMinimumDistance(String src, String dst) {
        int min = Integer.MAX_VALUE;
        String ans = "";
        HashMap<String, Boolean> processed = new HashMap<>();
        LinkedList<Pair> stack = new LinkedList<>();

        Pair sp = new Pair();
        sp.vname = src;
        sp.psf = src + "  ";
        sp.min_dis = 0;
        sp.min_time = 0;

        stack.addFirst(sp);

        while (!stack.isEmpty()) {
            Pair rp = stack.removeFirst();

            if (processed.containsKey(rp.vname)) {
                continue;
            }

            processed.put(rp.vname, true);

            if (rp.vname.equals(dst)) {
                int temp = rp.min_dis;
                if (temp < min) {
                    ans = rp.psf;
                    min = temp;
                }
                continue;
            }

            Vertex rpvtx = vtces.get(rp.vname);
            for (String nbr : rpvtx.nbrs.keySet()) {
                if (!processed.containsKey(nbr)) {
                    Pair np = new Pair();
                    np.vname = nbr;
                    np.psf = rp.psf + nbr + "  ";
                    np.min_dis = rp.min_dis + rpvtx.nbrs.get(nbr);

                    stack.addFirst(np);
                }
            }
        }
        return ans + min;
    }

    public String getMinimumTime(String src, String dst) {
        int min = Integer.MAX_VALUE;
        String ans = "";
        HashMap<String, Boolean> processed = new HashMap<>();
        LinkedList<Pair> stack = new LinkedList<>();

        Pair sp = new Pair();
        sp.vname = src;
        sp.psf = src + "  ";
        sp.min_dis = 0;
        sp.min_time = 0;

        stack.addFirst(sp);

        while (!stack.isEmpty()) {
            Pair rp = stack.removeFirst();

            if (processed.containsKey(rp.vname)) {
                continue;
            }

            processed.put(rp.vname, true);

            if (rp.vname.equals(dst)) {
                int temp = rp.min_time;
                if (temp < min) {
                    ans = rp.psf;
                    min = temp;
                }
                continue;
            }

            Vertex rpvtx = vtces.get(rp.vname);
            for (String nbr : rpvtx.nbrs.keySet()) {
                if (!processed.containsKey(nbr)) {
                    Pair np = new Pair();
                    np.vname = nbr;
                    np.psf = rp.psf + nbr + "  ";
                    np.min_dis = rp.min_dis + rpvtx.nbrs.get(nbr);

                    stack.addFirst(np);
                }
            }
        }
        return ans + min;
    }
}
