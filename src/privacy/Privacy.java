package privacy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Privacy {
    private Map<String, List<Integer>> classes;
    private Map<String, Set<Integer>> attributes;
    private Map<String, Double> probability1;
    private Map<String, Map<Integer, Double>> probability2;

    public Privacy(String[][] data) {
        classes = new HashMap<>();
        attributes = new HashMap<>();
        probability1 = new HashMap<>();
        probability2 = new HashMap<>();

        String[] h = new String[data.length];
        int[] s = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < data[i].length - 1; j++) {
                sb.append(data[i][j]);
                if (j < data[i].length - 2)
                    sb.append(",");
            }
            h[i] = sb.toString();

            int x = Integer.parseInt(data[i][data[i].length - 1]);
            if (!attributes.containsKey(h[i]))
                attributes.put(h[i], new TreeSet<>());
            attributes.get(h[i]).add(x);
            s[i] = x;
        }

        for (int i = 0; i < data.length; i++) {
            if (!classes.containsKey(h[i]))
                classes.put(h[i], new ArrayList<>());
            classes.get(h[i]).add(s[i]);
        }

        for (String cls : classes.keySet()) {
            double p1 = classes.get(cls).size() / (double) data.length;
            probability1.put(cls, p1);
            Map<Integer, Integer> counts = new HashMap<>();
            for (int x : attributes.get(cls)) {
                counts.put(x, 0);
            }
            for (int x : classes.get(cls)) {
                counts.put(x, counts.get(x) + 1);
            }
            probability2.put(cls, new HashMap<>());
            for (int x : counts.keySet()) {
                double p2 = counts.get(x) / (double) classes.get(cls).size();
                probability2.get(cls).put(x, p2);
            }
        }
    }

    public static double log2(double x) {
        return Math.log(x) / Math.log(2);
    }

    public String[] getClasses() {
        return classes.keySet().toArray(new String[classes.size()]);
    }

    public double entropy(String cls) {
        double entropy = 0;
        for (int x : getAttributes(cls)) {
            double p = probability2.get(cls).get(x);
            entropy += p * log2(1 / p);
        }
        return entropy;
    }

    public double entropy() {
        double entropy = 0;
        for (String cls : classes.keySet()) {
            double partial = 0;
            for (int x : getAttributes(cls)) {
                double p = probability2.get(cls).get(x);
                partial += p * log2(1 / p);
            }
            entropy += probability1.get(cls) * partial;
        }
        return entropy;
    }

    public int[] getAttributes(String cls) {
        Integer[] temp = attributes.get(cls).toArray(new Integer[attributes.get(cls).size()]);
        int[] x = new int[temp.length];
        for (int i = 0; i < temp.length; i++) {
            x[i] = temp[i];
        }
        return x;
    }

    public int[] getEpsilons(int[] x) {
        Set<Integer> temp = new TreeSet<>();
        for (int i = 0; i < x.length - 1; i++) {
            for (int j = i + 1; j < x.length; j++) {
                int diff = Math.abs(x[j] - x[i]);
                if (diff != 0)
                    temp.add(diff);
            }
        }
        int[] epsilons = new int[temp.size()];
        int count = 0;
        for (Integer t : temp) {
            epsilons[count] = t;
            count++;
        }
        return epsilons;
    }

    public double[][] dynamicProgramming(String cls) {
        //Get x[] and p[]
        int[] x = getAttributes(cls);
        double[] p = new double[x.length];

        for (int i = 0; i < x.length; i++) {
            p[i] = probability2.get(cls).get(x[i]);
        }

        // Sort x[] and p[]
        boolean flag = true;
        while (flag) {
            flag = false;
            for (int j = 0; j < x.length - 1; j++) {
                if (x[j] > x[j + 1]) {
                    int temp1 = x[j];
                    x[j] = x[j + 1];
                    x[j + 1] = temp1;
                    double temp2 = p[j];
                    p[j] = p[j + 1];
                    p[j + 1] = temp2;
                    flag = true;
                }
            }
        }

        return dynamicProgramming(getEpsilons(x), x, p);
    }

    public double[][] dynamicProgramming(int[] epsilons, int[] x, double[] p) {
        if (p.length < 2)
            return null;

        double[][] he = new double[epsilons.length + 1][2];

        double h0 = 0;
        for (int i = 0; i < p.length; i++) {
            h0 += p[i] * log2(1 / p[i]);
        }

        he[0][0] = 0;
        he[0][1] = h0;

        double[][] h = new double[epsilons.length][x.length + 1];
        for (int e = 0; e < epsilons.length; e++) {
            h[e][0] = 0;
            h[e][1] = p[0] * log2(1 / p[0]);
            for (int i = 2; i < x.length + 1; i++) {
                int j = i;
                double partial = 0;
                if (e == 0)
                    h[e][i] = h0;
                else
                    h[e][i] = h[e - 1][i - 1];
                while (j != 0 && x[i - 1] - x[j - 1] <= epsilons[e]) {
                    partial += p[j - 1];
                    double temp = h[e][j - 1] + partial * log2(1 / partial);
                    if (temp < h[e][i])
                        h[e][i] = temp;
                    j = j - 1;
                }
            }
            he[e + 1][0] = epsilons[e];
            he[e + 1][1] = h[e][h[e].length - 1];
        }
        return he;
    }

    public static void main(String[] args) {
        if (args.length < 0)
            System.exit(1);

        List<String[]> temp = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(FileSystems.getDefault().getPath(args[0]),
                Charset.forName("UTF-8"))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                temp.add(line.split(",\\s?"));
            }
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
            System.exit(1);
        }

        if (temp.size() == 0)
            System.exit(1);

        String[][] data = new String[temp.size() - 1][];
        for (int i = 1; i < temp.size(); i++) {
            data[i - 1] = temp.get(i);
        }

        StringBuilder sb = new StringBuilder();

        Privacy privacy = new Privacy(data);

        sb.append("k,").append(privacy.getClasses().length).append("\n\n");
        sb.append("cls,H(cls)").append("\n");

        for (String cls : privacy.getClasses()) {
            sb.append("\"" + cls + "\"").append(",").append(privacy.entropy(cls)).append("\n");
        }
        sb.append("H(D),").append(privacy.entropy()).append("\n");

        for (String cls : privacy.getClasses()) {
            double[][] h = privacy.dynamicProgramming(cls);
            if (h != null) {
                sb.append("\n").append("\"" + cls + "\"").append("\n");
                sb.append("e,H(e)\n");
                for (int i = 0; i < h.length; i++) {
                    sb.append((int) h[i][0]).append(",").append(h[i][1]).append("\n");
                }
                double area = 0;
                for (int i = 0; i < h.length - 1; i++) {
                    int diff = Math.abs((int) h[i + 1][0] - (int) h[i][0]);
                    area += diff * h[i][1];
                }
                sb.append("Area,").append(area).append("\n");
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(FileSystems.getDefault().getPath("privacy-" + args[0]),
                Charset.forName("UTF-8"))) {
            writer.write(sb.toString());
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
            System.exit(1);
        }

        /*int[] x = new int[]{1, 3, 8, 9};
        double[] p = new double[]{0.15, 0.1, 0.7, 0.05};
        int[] epsilons = privacy.getEpsilons(x);
        double[][] h = privacy.dynamicProgramming(epsilons, x, p);
        for (int i = 0; i < h.length; i++) {
            System.out.println("H(" + (int) h[i][0] + "): " + h[i][1]);
        }
        double area = 0;
        for (int i = 0; i < h.length - 1; i++) {
            int diff = Math.abs((int) h[i + 1][0] - (int) h[i][0]);
            area += diff * h[i][1];
        }
        System.out.println("A = " + area);*/
    }
}
