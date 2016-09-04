package gorjan.exam.connection.communication;


import gorjan.exam.connection.ConnectionSetup;
import gorjan.exam.transferData.TransferFile;


/**
 * FileUploader class extends the ftpCommunication class
 * The class prepares all the parameters which will be used for the upload of file
 * and proceeds to execute the file transfer.
 * @author Liquid Sun
 *
 */
public class FileUploader  implements Runnable {
    private TransferFile uploadFile;
    private FtpCommunication ftpCommunication;
    
    /**
     * FileUploader constructor set the parameters necessary for the execution of the thread.
     * @param transferFile (TransferFile)is the file which will be used for the Upload
     * @param args String Main method parameters which will be used to setup a configuration of the connection.
     */
    public FileUploader(TransferFile transferFile, ConnectionSetup initial) {
        

	this.ftpCommunication=new FtpCommunication(initial);
	this.uploadFile = transferFile;
    }

    /*
     * Run method activates all the necessary processes for each thread of the
     * socket and initiates the file transfer, after which it disconnects from
     * the server.
     */
    @Override
    public void run() {

	ftpCommunication.connect();
	ftpCommunication.setPasiveMode();
	ftpCommunication.setBinaryMode();
	ftpCommunication.sendFile(uploadFile);
	ftpCommunication.disconnect();
    }

}
