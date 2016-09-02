package gorjan.exam.connection.communication;

import java.io.IOException;
import java.util.LinkedList;

import gorjan.exam.connection.ConnectionSetup;
import gorjan.exam.statistics.Statistics;
import gorjan.exam.transferData.TransferFile;
import gorjan.exam.transferData.TransferFiles;

/**
 * FileUploader class extends the ftpCommunication class
 * The class prepares all the parameters which will be used for the upload of file
 * and proceeds to execute the file transfer.
 * @author Liquid Sun
 *
 */
public class FileUploader extends FtpCommunication implements Runnable {
    private ConnectionSetup connectionSetup;
    private TransferFiles transferFiles;
    private TransferFile uploadFile;

    /**
     * FileUploader constructor set the parameters necessary for the execution of the thread.
     * @param transferFiles (TransferFiles)is a Singleton for access to the List of files which will be used for the Upload
     * @param args String Main method parameters which will be used to setup a configuration of the connection.
     */
    public FileUploader(TransferFiles transferFiles, String[] args) {

	this.transferFiles = transferFiles;
	this.connectionSetup = new ConnectionSetup(args);
	ftpInitialize(connectionSetup);
	this.uploadFile = new TransferFile(this.transferFiles.returnPopedFileName());
    }

    /*
     * Run method activates all the necessary processes for each thread of the
     * socket and initiates the file transfer, after which it disconnects from
     * the server.
     */
    @Override
    public void run() {

	try {

	    connect();
	    setPasiveMode();
	    setBinaryMode();
	    sendFile(uploadFile);
	    disconnect();
	} catch (IOException e1) {
	    e1.printStackTrace();
	} catch (InterruptedException e1) {
	    e1.printStackTrace();
	}
    }

}
