package privacy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Calculates a variety of privacy metrics for a given data set.
 *
 * @author Nicolas Blarasin
 */
public class Privacy {
    /**
     * Equivalence classes.
     */
    private Map<String, List<Integer>> classes;
    /**
     * Confidential attributes.
     */
    private Map<String, Set<Integer>> attributes;
    /**
     * Probability of each class.
     */
    private Map<String, Double> probability1;
    /**
     * Probability of confidential attribute in class.
     */
    private Map<String, Map<Integer, Double>> probability2;
    /**
     * Size of the smallest equivalence class.
     */
    private int k;

    /**
     * @param data data set with confidential attributes in last column
     */
    public Privacy(String[][] data) {
        classes = new HashMap<>();
        attributes = new HashMap<>();
        probability1 = new HashMap<>();
        probability2 = new HashMap<>();
        k = Integer.MAX_VALUE;

        //Parse data
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

        //Find equivalence classes
        for (int i = 0; i < data.length; i++) {
            if (!classes.containsKey(h[i]))
                classes.put(h[i], new ArrayList<>());
            classes.get(h[i]).add(s[i]);
        }

        //Calculate k and probabilities
        for (String cls : classes.keySet()) {
            int size = classes.get(cls).size();
            double p1 = size / (double) data.length;
            if (size < k)
                k = size;
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

    private double log2(double x) {
        return Math.log(x) / Math.log(2);
    }

    /**
     * Returns the value for k.
     */
    public int k() {
        return k;
    }

    /**
     * Returns the entropy of the whole data set.
     */
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

    /**
     * Returns a sorted array of attributes in the specified equivalence class.
     */
    private int[] getAttributes(String cls) {
        Integer[] temp = attributes.get(cls).toArray(new Integer[attributes.get(cls).size()]);
        int[] x = new int[temp.length];
        for (int i = 0; i < temp.length; i++) {
            x[i] = temp[i];
        }
        return x;
    }

    /**
     * Returns an array of epsilon values.
     */
    private int[] getEpsilons(int[] x) {
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

    /**
     * Returns the average CAE of all equivalence classes.
     */
    public double dynamicProgramming() {
        double area = 0;
        for (String cls : classes.keySet()) {
            //area += probability1.get(cls) * dynamicProgramming(cls);
            area += dynamicProgramming(cls);
        }
        //return area;
        return area / (double)classes.keySet().size();
    }

    /**
     * Returns the CAE of an equivalence class.
     */
    private double dynamicProgramming(String cls) {
        //Get x[] and p[]
        int[] x = getAttributes(cls);
        double[] p = new double[x.length];

        for (int i = 0; i < x.length; i++) {
            p[i] = probability2.get(cls).get(x[i]);
        }

        //Sort x[] and p[]
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

        int[] epsilons = getEpsilons(x);
        double[] h = dynamicProgramming(epsilons, x, p);

        // Calculate Area
        double area = 0;
        if (h != null) {
            area = epsilons[0] * h[0];
            for (int i = 1; i < h.length - 1; i++) {
                int range = Math.abs(epsilons[i] - epsilons[i - 1]);
                area += range * h[i];
            }
        }
        return area;
    }

    /**
     * Returns the CAE of a given set of attributes.
     *
     * @param epsilons array of epsilons
     * @param x        ordered array of confidential attributes
     * @param p        array of each p(x_i)
     */
    private double[] dynamicProgramming(int[] epsilons, int[] x, double[] p) {
        if (p.length < 2)
            return null;

        double[] he = new double[epsilons.length + 1];

        //Calculate H(0)
        double h0 = 0;
        for (int i = 0; i < p.length; i++) {
            h0 += p[i] * log2(1 / p[i]);
        }
        he[0] = h0;

        //Calculate H(epsilon)
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
                    h[e][i] = h[e - 1][i];
                while (j != 0 && x[i - 1] - x[j - 1] <= epsilons[e]) {
                    partial += p[j - 1];
                    double temp = h[e][j - 1] + partial * log2(1 / partial);
                    if (temp < h[e][i])
                        h[e][i] = temp;
                    j = j - 1;
                }
            }
            he[e + 1] = h[e][h[e].length - 1];
        }
        return he;
    }

    /**
     * @param args data set file name
     */
    public static void main(String[] args) {
        if (args.length < 1)
            System.exit(1);

        //Read data
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

        String[][] data = new String[temp.size()][];
        for (int i = 0; i < temp.size(); i++) {
            data[i] = temp.get(i);
        }

        //Calculate privacy
        Privacy privacy = new Privacy(data);
        StringBuilder sb = new StringBuilder();
        DecimalFormat df = new DecimalFormat("#.00");
        sb.append("k,H,A\n");
        sb.append(privacy.k()).append(",");
        sb.append(df.format(privacy.entropy())).append(",");
        sb.append(df.format(privacy.dynamicProgramming()));

        //Write results
        try (BufferedWriter writer = Files.newBufferedWriter(FileSystems.getDefault().getPath("privacy-" + args[0]),
                Charset.forName("UTF-8"))) {
            writer.write(sb.toString());
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
            System.exit(1);
        }
    }
}
