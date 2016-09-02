package gorjan.exam.statistics;

/**
 * This class gathers the statistics for individual and group upload. It extends the StatisticsTools class
 * 
 * @author Liquid Sun
 *
 */
public class Statistics extends StatisticsTools {
    private double fileTransferTime;
    private double fileTransferRate;

    public static int numberOfFiles;
    public static double totalTransferTime;
    public static double totalAvgTransferRate;

    // Constructor
    public Statistics() {
	super();
	this.fileTransferTime = 0;
	this.fileTransferRate = 0;
    }

    /**
     * setFileTransferTime() saves the current file transfer time and adds it to the total transfer time variable.
     * @param fileTransferTime (Long) is the transfer time it took to upload a file.
     */
    public void setFileTransferTime(double fileTransferTime) {
	this.fileTransferTime = fileTransferTime;
	addToTotalTime(fileTransferTime);
	this.numberOfFiles += 1;
    }
    
    /**
     * setFileTransferRate() saves the current file transfer rate and adds it to the total transfer rate.
     * @param fileTransferRate (double) is the speed at which the transfer was done.
     */
    public void setFileTransferRate(double fileTransferRate) {
	fileTransferRate=Math.round(fileTransferRate * 100d) / 100d;
	this.fileTransferRate = fileTransferRate;
	addToAvgSpeedForAllDownloads(fileTransferRate);
    }
    
    //Getter
    public double getFileTransferTime() {
	return fileTransferTime;
    }
    //Getter
    public double getFileTransferRate() {
	return fileTransferRate;
    }
    //Getter
    public static long getNumberOfFiles() {
	return numberOfFiles;
    }

}
