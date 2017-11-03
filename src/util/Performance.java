package util;

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

public class Performance {
    private double ncp = 0;
    private double averageClassSize = 0;
    private int measuredK = 0;
    private long runtime = 0;

    public Performance() {}

    public Performance(double _ncp, double _averageClassSize, int _measuredK, long _runtime) {
        this.ncp = _ncp;
        this.averageClassSize = _averageClassSize;
        this.measuredK = _measuredK;
        this.runtime = _runtime;
    }

    public double getNcp() {
        return ncp;
    }

    public void setNcp(double ncp) {
        this.ncp = ncp;
    }

    public double getAverageClassSize() {
        return averageClassSize;
    }

    public void setAverageClassSize(double averageClassSize) {
        this.averageClassSize = averageClassSize;
    }

    public int getMeasuredK() {
        return measuredK;
    }

    public void setMeasuredK(int measuredK) {
        this.measuredK = measuredK;
    }

    public long getRuntime() {
        return runtime;
    }

    public void setRuntime(long runtime) {
        this.runtime = runtime;
    }


    @Override public String toString() {
        return "{" +
            "\"ncp\":\"" + ncp + "\"" + ", " +
            "\"averageClassSize\":\"" + averageClassSize + "\"" + ", " +
            "\"measuredK\":\"" + measuredK + "\"" + ", " +
            "\"runtime\":\"" + runtime + "\"" +
            "}";
    }
}
