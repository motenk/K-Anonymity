package util;

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

public class RunTime {
    private int k;
    private long runtime;
    private String filename;
    private int maxRows;
    private String method;

    public RunTime(String _method, String filename, int k, int maxRows, long runtime) {
        this.k = k;
        this.filename = filename;
        this.maxRows = maxRows;
        this.runtime = runtime;
        this.method = _method;
    }

    @Override public String toString() {
        return "{" +
            "\"method\":" + (method == null ? "null" : "\"" + method + "\"") + ", " +
            "\"k\":\"" + k + "\"" + ", " +
            "\"runtime\":\"" + runtime + "\"" + ", " +
            "\"filename\":" + (filename == null ? "null" : "\"" + filename + "\"") + ", " +
            "\"maxRows\":\"" + maxRows + "\"" +
            "}";
    }
}
