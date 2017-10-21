package kanonymity.privacy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Privacy {
    private Map<String, List<Integer>> classes;
    private Set<Integer> attributes;
    private Map<String, Double> probability1;
    private Map<String, Map<Integer, Double>> probability2;

    public Privacy(String[][] data) {
        classes = new HashMap<>();
        attributes = new HashSet<>();
        probability1 = new HashMap<>();
        probability2 = new HashMap<>();

        String[] h = new String[data.length];
        int[] s = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < data[i].length - 1; j++) {
                sb.append("[").append(data[i][j]).append("]");
                if (j < data[i].length - 2)
                    sb.append(", ");
            }
            h[i] = sb.toString();

            int x = Integer.parseInt(data[i][data[i].length - 1]);
            attributes.add(x);
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
            double p1 = classes.get(cls).size() / (double)data.length;
            probability1.put(cls, p1);
            Map<Integer, Integer> counts = new HashMap<>();
            for (int x : attributes) {
                counts.put(x, 0);
            }
            for (int x : classes.get(cls)) {
                counts.put(x, counts.get(x) + 1);
            }
            for (int x : counts.keySet()) {
                double p2 = counts.get(x) / (double)classes.get(cls).size();
                probability2.get(cls).put(x, p2);
            }
        }
    }

    public String[] getClasses() {
        return classes.keySet().toArray(new String[classes.size()]);
    }

    public double entropy() {
        double entropy = 0;
        for (String cls : classes.keySet()) {
            double partial = 0;
            for (int x : attributes) {
                double p = probability2.get(cls).get(x);
                partial += p * log2(p);
            }
            partial *= -probability1.get(cls);
        }
        return entropy;
    }

    public double[][] dynamicProgramming(int epsilon, String cls) {
        //Get x[] and p[]
        int[] x = new int[attributes.size()];
        double[] p = new double[attributes.size()];

        Integer[] temp = attributes.toArray(new Integer[attributes.size()]);
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

        return dynamicProgramming(epsilon, x, p);
    }

    private double[][] dynamicProgramming(int epsilon, int[] x, double[] p) {
        double[][] h = new double[epsilon + 1][attributes.size() + 1];
        for (int e = 1; e <= epsilon; e++) {
            h[e][0] = 0;
            h[e][1] = -p[1] * log2(p[1]);
            for (int i = 2; i < h.length; i++) {
                int j = i;
                double partial = 0;
                h[e][i] = h[e][i - 1];
                while (x[i] - x[j] <= e && j != 0) {
                    partial += p[j];
                    double temp = h[e][j - 1] - partial * log2(partial);
                    if (temp < h[e][i])
                        h[e][i] = temp;
                }
                j -= 1;
            }
        }
        return h;
    }

    public static double log2(double x) {
        return Math.log(x) / Math.log(2);
    }
}
