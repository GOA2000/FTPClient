package gorjan.exam.main;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import gorjan.exam.connection.communication.FileUploader;
import gorjan.exam.statistics.Statistics;
import gorjan.exam.statistics.StatisticsTools;
import gorjan.exam.transferData.TransferFiles;
import gorjan.helperTools.HelperTools;
import sun.rmi.runtime.Log;
/**
 * Main program 
 * @author Liquid Sun
 *
 */

public class FTPClientMain {

    public static void main(String[] args) throws IOException, InterruptedException {
        TransferFiles transferFiles=TransferFiles.getInstance();
        transferFiles.setTransferFiles(args);
	
        //Total size of the array
        int filelist=transferFiles.getSize();
       
        //Method to initialize the threads with connections.
        System.out.println("Upload is starting.");
	 ExecutorService executor = Executors.newFixedThreadPool(4);
	        for (int i = 0; i < filelist; i++) {
	            Runnable worker = new FileUploader(transferFiles, args);
	            executor.execute(worker);
	          }
	        executor.shutdown();
	        while (!executor.isTerminated()) {
	        }
	        System.out.println("Finished all threads");
	        
	        
	// Displays the information for the RUN of this upload session.
	System.out.println(
		"\nTotal transferTime is:  " + StatisticsTools.secondToMinutes(Statistics.totalTransferTime));
	System.out.println("Average Download speed for all downloads is: "
		+ String.format( "%.2f", StatisticsTools.getTotalAvgSpeed() ) + " KB/s");

    }

}
