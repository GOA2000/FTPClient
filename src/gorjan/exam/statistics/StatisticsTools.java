package gorjan.exam.statistics;

/**
 * StatisticTools class are the tools for maintaining and helping to do
 * calculations and conversion for the statistics
 * 
 * @author Liquid Sun
 *
 */
public class StatisticsTools {

    public synchronized double calculateMilliSecondsFileTransfer(double timerOut, double timerIn) {
	timerIn=timerIn/1000;
	timerOut=timerOut/1000;
	double transferTime = Math.round(timerOut - timerIn ) ;
	return transferTime;
    }

    /**
     * CalculateAverageSpeed() method calculates the average speed by using the fileSize
     * parameter and miliSeconds parameter it returns a variable of type double.
     * 
     * @param fileSize (Long) is the byte size of the file that has been transfered
     * @param milliSeconds(Long) are the milliseconds it took for the transfer to complete in
     * this specific thread.
     * 
     * @return trasferRate
     */
    public double calculateAverageSpeed(double fileSize, double milliSeconds) {

	// If the File or the transfer time was 0 the error control defaults
	// the values in question to 1KB
	if (fileSize == 0) {
	    return (double) 1;
	}
	if (milliSeconds == 0) {
	    return (double) 1;
	}
	double trasferRate = fileSize / milliSeconds/1000 ;
	return trasferRate;
    }

    // Converter of Milliseconds to Minutes and Seconds.
    public static synchronized String secondToMinutes(double theSeconds) {
	int minutes = (int) (theSeconds ) / 60;
	int seconds = (int) (theSeconds ) % 60;
	return (String.format("%02d.%02d %s", minutes, seconds, "Minutes"));
    }

    /**
     * addToTotalTime() method adds the milliseconds of a single file transfer to the
     * class variable of statistics Object the total time taken by all transfers
     * 
     * @param milliseconds
     *            (Long) are the milliseconds it took for the single transfer to
     *            complete in this specific thread.
     */
    public synchronized void addToTotalTime(double milliseconds) {
	Statistics.totalTransferTime += milliseconds;
    }

    /**
     * addToAvgSpeedForAllDownloads() method adds the average transfer speed of a
     * single file transfer to the class variable of statistics Object the total
     * Average Transfer Speed taken by all transfers
     * 
     * @param transferRate
     *            (double) is the the average transfer rate it took for the
     *            download to complete in this specific thread
     */
    public synchronized void addToAvgSpeedForAllDownloads(double transferRate) {
	Statistics.totalAvgTransferRate += transferRate;
    }

    /**
     * Getter for the total average transfer rate
     * 
     * @return double
     */
    public static double getTotalAvgSpeed() {
	double avgSpeed = Statistics.totalAvgTransferRate / Statistics.numberOfFiles;
	return avgSpeed;
    }

    /**
     * ConsolePrintout() prints out the statistics for the specific file
     * transfer.
     * 
     * @param fileName (String) Name of the File
     * @param statistics (Statistics) The Statistics Object for the specific file transfer.
     */
    public static void ConsolePrintout(String fileName, Statistics statistics) {
	System.out.println("Transfer stats for file: " + fileName + " With speed: " + statistics.getFileTransferRate()
		+ " KB" + " and the download time of: " + secondToMinutes(statistics.getFileTransferTime())
		+ ".");
    }
}
