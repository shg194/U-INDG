package myshgs.Experiments;

public class AlgorithmResult {
    String algorithmName;
    double queryTime;
    long IO;
    long DT;
    int dataSize;
    int dimension;
    int SL;

    public AlgorithmResult(String algorithmName, double queryTime, long DT, long IO,
                           int dataSize, int dimension, int SL) {
        this.algorithmName = algorithmName;
        this.queryTime = queryTime;
        this.IO = IO;
        this.DT = DT;
        this.dataSize = dataSize;
        this.dimension = dimension;
        this.SL = SL;
    }

    @Override
    public String toString() {
        return algorithmName + "," + queryTime + "," + IO + "," + DT + "," +
                dataSize + "," + dimension + "," + SL;
    }
}
