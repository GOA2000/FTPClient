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
 * Main program for uploading of files via FTP protocol.
 * 
 * @author Liquid Sun
 *
 */

public class FTPClientMain {

    public static void main(String[] args)  {
	
	// Initialize Logger
	configureLogger();
	Logger.getLogger("FTPlogger").info("In memory logging started.");
	// Initialize the List of Files for upload
	List<String> fileListWithPaths = TransferFilesTools.ListOfFiles(args);

	// Initialize the Connection configuration without the upload port.
	ConnectionSetup initial = new ConnectionSetup(args);

	System.out.println("Upload is starting...");
	// Method to initialize the threads with connections pool of 5 threads.
	ExecutorService executor = Executors.newFixedThreadPool(5);
	for (String fileWithPath : fileListWithPaths) {
	    // Sending the files to the File Uploader which handles the uploads.
	    Runnable worker = new FileUploader((new TransferFile(fileWithPath)), initial);
	    executor.execute(worker);
	}
	executor.shutdown();
	while (!executor.isTerminated()) {
	    // Wait for termination of running executors.
	    try {
		Thread.sleep(1000);
	    } catch (InterruptedException e) {
		Logger.getLogger("FTPlogger").warning("Thread sleep interuption.");
		Logger.getLogger("FTPlogger").warning(e.toString());
	    }
	}

	System.out.println("\nFinished all uploads");
	// Displays the general information for the RUN of this upload session.
	System.out
		.println(String.format("\n*****  Total upload Time is: %03.0f seconds.", Statistics.totalTransferTime));
	System.out.println("*****  Average upload speed for all downloads is: "
		+ String.format("~%.2f", StatisticsTools.getTotalAvgSpeed()) + " KB/s");

    }

}
