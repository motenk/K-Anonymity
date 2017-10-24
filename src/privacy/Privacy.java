package privacy;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.*;

public class Privacy {
    private Map<String, List<Integer>> classes;
    private Map<Integer, Integer> attributes;
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
            sb.append("[");
            for (int j = 0; j < data[i].length - 1; j++) {
                sb.append(data[i][j]);
                if (j < data[i].length - 2)
                    sb.append(", ");
            }
            sb.append("]");
            h[i] = sb.toString();

            int x = Integer.parseInt(data[i][data[i].length - 1]);
            if (!attributes.containsKey(x))
                attributes.put(x, x);
            s[i] = x;
        }

        for (int i = 0; i < data.length; i++) {
            if (classes.containsKey(h[i]))
                classes.get(h[i]).add(s[i]);
            else {
                List<Integer> cls = new ArrayList<>();
                cls.add(s[i]);
                classes.put(h[i], cls);
            }
        }

        for (String cls : classes.keySet()) {
            double p1 = classes.get(cls).size() / (double) data.length;
            probability1.put(cls, p1);
            Map<Integer, Integer> counts = new HashMap<>();
            for (int x : attributes.values()) {
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
        if (x == 0)
            return 0;
        return Math.log(x) / Math.log(2);
    }

    public String[] getClasses() {
        return classes.keySet().toArray(new String[classes.size()]);
    }

    public double entropy() {
        double entropy = 0;
        for (String cls : classes.keySet()) {
            double partial = 0;
            for (int x : attributes.values()) {
                double p = probability2.get(cls).get(x);
                partial += p * log2(p);
            }
            entropy -= probability1.get(cls) * partial;
        }
        return entropy;
    }

    public double[] dynamicProgramming(int[] epsilons, String cls) {
        //Get x[] and p[]
        int[] x = new int[attributes.size()];
        double[] p = new double[attributes.size()];

        Integer[] temp = attributes.values().toArray(new Integer[attributes.size()]);
        for (int i = 0; i < temp.length; i++) {
            x[i] = temp[i];
        }
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

        return dynamicProgramming(epsilons, x, p);
    }

    private double[] dynamicProgramming(int[] epsilons, int[] x, double[] p) {
        double[][] h = new double[epsilons.length][x.length + 1];
        double[] he = new double[h.length];
        if (p.length < 2) {
            return he;
        }

        for (int k = 0; k < epsilons.length; k++) {
            int e = epsilons[k];
            if (e == 0) {
                for (int i = 0; i < x.length; i++) {
                    he[k] -= p[i] * log2(p[i]);
                }
            } else {
                h[k][0] = 0;
                h[k][1] = -p[0] * log2(p[0]);
                for (int i = 1; i < x.length; i++) {
                    int j = i;
                    double partial = 0;
                    h[k][i + 1] = h[k][i];
                    while (x[i] - x[j] <= e && j != 0) {
                        partial += p[j];
                        double temp = h[k][j] - partial * log2(partial);
                        if (temp < h[k][i + 1])
                            h[k][i + 1] = temp;
                        j--;
                    }
                }
                he[k] = h[k][h[k].length - 1];
            }
        }
        return he;
    }

    public static void main(String[] args) {
        int k = -1;
        try {
            k = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.format("NumberFormatException: %s%n", e);
        }

        List<String[]> temp = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(FileSystems.getDefault().getPath(args[1]),
                Charset.forName("UTF-8"))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                temp.add(line.split(",\\s?"));
            }
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }

        if (temp.size() != 0) {
            String[] fields = temp.get(0);
            String[][] data = new String[temp.size() - 1][];
            for (int i = 1; i < temp.size(); i++) {
                data[i - 1] = temp.get(i);
            }

            System.out.println("k: " + k);
            System.out.println("fields: " + Arrays.toString(fields));

            Privacy privacy = new Privacy(data);
            System.out.println("entropy: " + privacy.entropy());
            int[] epsilons = new int[] { 0, 1, 2 };
            double[] h = privacy.dynamicProgramming(epsilons, "[39, State-gov, Bachelors, Never-married, Adm-clerical, Not-in-family, White, Male, 40, United-States]");
            for (int i = 0; i < h.length; i++) {
                System.out.println(epsilons[i] + ": " + h[i]);
            }
        }
    }
}
