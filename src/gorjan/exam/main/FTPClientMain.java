package gorjan.exam.main;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;


import gorjan.exam.connection.ConnectionSetup;
import gorjan.exam.connection.communication.FileUploader;
import gorjan.exam.statistics.Statistics;
import gorjan.exam.statistics.StatisticsTools;
import gorjan.exam.transferData.TransferFile;
import gorjan.exam.transferData.TransferFilesTools;

import static gorjan.helperTools.FTPlogger.*;


/**
 * Main program
 * 
 * @author Liquid Sun
 *
 */

public class FTPClientMain {


    public static void main(String[] args) throws IOException, InterruptedException {
        // Initialize Logger
        configureLogger();
        Logger.getLogger("FTPlogger").info("In memory logging started.");
        
	// Initialize the List of Files
	List<String> fileListWithPaths = TransferFilesTools.ListOfFiles(args);
        ConnectionSetup initial = new ConnectionSetup(args);
	// Method to initialize the threads with connections.
	System.out.println("Upload is starting...");
	

        ExecutorService executor = Executors.newFixedThreadPool(5);
	for (String fileWithPath : fileListWithPaths) {
	    Runnable worker = new FileUploader((new TransferFile(fileWithPath)), initial);
	    executor.execute(worker);
	}
	
        executor.shutdown();
	while (!executor.isTerminated()) {
	    Thread.sleep(1000);
	}


   
	System.out.println("Finished all uploads");
	// Displays the general information for the RUN of this upload session.
	System.out.println("\nTotal Upload Time is:  " + StatisticsTools.secondToMinutes(Statistics.totalTransferTime));
	System.out.println("Average upload speed for all downloads is: "
		+ String.format("%.2f", StatisticsTools.getTotalAvgSpeed()) + " KB/s");

    }

}
