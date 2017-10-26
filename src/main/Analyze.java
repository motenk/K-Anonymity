package main;

/**
 * author: Scott Butler
 * studentID: c3165874
 * contactEmail: c3165874@uon.edu.au
 * institution: University of Newcastle, Australia
 * createdDate: 26/Oct/2017
 * projectName: K-Anonymity
 * courseCode: ...
 * classDescription:
 * ...
 * ...
 */

public class Analyze {
    public static void main(String[] argv) {
        KAnon kAnon = new KAnon();
        kAnon.runAnalysis(argv[0]);
    }
}
