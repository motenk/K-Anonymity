package main;

/**
 * author: Scott Butler
 * studentID: c3165874
 * contactEmail: c3165874@uon.edu.au
 * institution: University of Newcastle, Australia
 * createdDate: 03/Nov/2017
 * projectName: K-Anonymity
 * courseCode: ...
 * classDescription:
 * ...
 * ...
 */

public class Analyze {
    public static void main(String[] argv) {
        String dataValuesFilename = argv[0];
        String dataTaxFilename = argv[1];

        KAnon kAnon = new KAnon();

        int repeats = 20;

        // k = 2^1 to 2^7
        for (int k_power = 1; k_power <= 7; k_power++) {
            int k = (int)Math.pow(2, k_power);
            System.out.println("\n\n\n** New k value of "+k);
            kAnon.runAnalysis("KAnon", dataValuesFilename, dataTaxFilename, k, repeats);
            kAnon.runAnalysis("TopDown", dataValuesFilename, dataTaxFilename, k, repeats);
        }
    }
}
