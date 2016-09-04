package gorjan.exam.connection.communication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Logger;

import static gorjan.exam.connection.communication.FtpCode.*;
import gorjan.exam.connection.ConnectionSetup;
import gorjan.exam.statistics.Statistics;
import gorjan.exam.statistics.StatisticsTools;
import gorjan.exam.transferData.TransferFile;

/**
 * FtpCommunication class is the class that contains all the communication
 * methods to be used with the FTPServer
 * 
 * @author Liquid Sun
 *
 */
public class FtpCommunication {
    private ConnectionSetup connectionSetup;
    private BufferedReader reader;
    private Socket socket;
    private BufferedWriter writer;
    private BufferedInputStream bis;
    private FileInputStream fileInputStream;
    private Socket uploadDataSocket;
    private Statistics statistics;
    private StatisticsTools statTools;
    private TransferFile transferFile;

    public FtpCommunication(ConnectionSetup connectionSetup) {
	this.connectionSetup = new ConnectionSetup(connectionSetup);
	this.statistics = new Statistics();
	this.statTools = new StatisticsTools();
	this.socket = new Socket();
    }

    /**
     * ftpInitialize() sets up the necessary parameters which will be used
     * during the use of methods in this class.
     * 
     * @param connectionSetup
     *            ConnectionSetup is the forwarded setup parameters to be used
     *            during the execution
     */
    // public void ftpInitialize(ConnectionSetup connectionSetup) {
    // this.connectionSetup = connectionSetup;
    // this.statistics = new Statistics();
    // this.statTools = new StatisticsTools();
    //
    // }

    /**
     * ReadLine() connects to the pre-configured BufferedReader and identifies
     * if the the last read line contains one of the FTP response codes.
     * 
     * @return Last line of response code FTP message
     * @throws IOException
     * @throws InterruptedException
     */
    public String readLine() throws IOException {
	String strLine = null;
	String line = null;

	try {
	    while (((line = reader.readLine()) != null)) {
		for (int i = 99; i < 10069; i++) {
		    if (line.contains(i + " ")) {
			strLine = line;
			return strLine;
		    }
		}
	    }
	} catch (IOException e) {
	    Logger.getLogger("FTPlogger").warning(e.toString());
	    System.out.println("Server Replied: " + line);
	    System.out.println("There was an error in the communication with the server");
	    System.out.println("Not all Downloads have been completed please try again.");
	    System.out.println("Exiting program.");
	    System.exit(0);
	}

	return strLine;
    }

    /**
     * Sendline() sends via the buffered writer.
     * 
     * @param line
     *            String is the String we send to the FTP server.
     */
    public void sendLine(String line) {
	if (socket == null) {
	    try {
		throw new IOException("No FTPConnection");
	    } catch (IOException e) {
		Logger.getLogger("FTPlogger").warning(e.toString());
	    }
	}
	try {
	    writer.write(line + "\r\n");
	    writer.flush();
	} catch (IOException e) {
	    Logger.getLogger("FTPlogger").warning(e.toString());
	    try {
		socket.close();
	    } catch (IOException e1) {
		 Logger.getLogger("FTPlogger").warning(e1.toString());
	    }
	    socket = null;
	   
	}
    }

    /**
     * connect() method Connects to the server in question and expects the FTP
     * code for the succesfull connection. The method then proceedes to connect
     * with the provided data (Username and Password)or the default
     * preconfigured data if the data provided is incomplete.
     * 
     */
    public void connect(){
	// Configuration of socket, reader and writer.
	try {
	    socket = new Socket(connectionSetup.getServerIp(), connectionSetup.getPort());
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		String response = readLine();
		if (!response.startsWith(CONNECTED_SUCCESS)) {
		    throw new IOException("Unexpected response:" + response);
		}
		sendLine("USER " + connectionSetup.getUsername());
		response = readLine();
		// System.out.println(response);
		if (!response.startsWith(USERNAME_SUCCESS)) {

		    throw new IOException("Unexpected Response" + response);
		}
		sendLine("PASS " + connectionSetup.getPassword());
		response = readLine();
		// System.out.println(response);

		if (!response.startsWith(PASSWORD_SUCCESS)) {
		    System.out.print("Fail");
		    throw new IOException("unsuccessful login" + response);
		}
	    
	} catch (IOException e) {
	    Logger.getLogger("FTPlogger").warning(e.toString());
	    }



    }

    /**
     * setPassiveMode() Due to the problems arising from NAT the best solution
     * for the connection of the FTP with a better success rate of uploading
     * files this method attempts to set Passive Mode on the FTP server.
     * 
     */

    public void setPasiveMode()  {

	// System.out.println("Attempting to set pasive mode: ");
	try {
	    sendLine("PASV");
	    String response = readLine();
	    if (!response.startsWith(PASSIVE_MODE_SUCCESS)) {
		    throw new IOException("Could not set passive mode: " + response);
	    }
	    connectionSetup.extractPassiveModeData(response);
	} catch (IOException e) {
	    Logger.getLogger("FTPlogger").warning(e.toString());
	}
	
	
	

    }

    /**
     * setBinaryMode() method FTP has two modes for transfer ASCII which is
     * limited in functionality and Binary mode, this method attempts to
     * activate the Binary mode for transfer.
     * 
     */
    public void setBinaryMode() {
	// System.out.println("Attempting to set binary mode: ");
	try {
	    sendLine("TYPE I");
	    String response = readLine();
		if (!response.startsWith(BINARY_MODE_SUCCESS)) {
		    throw new IOException("Could not set Binary mode: " + response);
		}
	    
	} catch (IOException e) {
	    Logger.getLogger("FTPlogger").warning(e.toString());
	}
	
    }

    /**
     * disconnect() Method This method sends the FTP server the quit command and
     * then nulls the Socket that has been used.
     * 
     */
    public void disconnect() {
	try {
	    sendLine("QUIT");
	} finally {
	    try {
		socket.close();
	    } catch (IOException e) {
		Logger.getLogger("FTPlogger").warning(e.toString());
		Logger.getLogger("FTPlogger").info("Problem Closing socket");
	    }
	}
    }

    /**
     * 
     * sendFile() method Initializes all the neccessary streams and parameters
     * for sending of the file including the loading of the File in question.
     * The method contains a check to see if the correct codes are sent from the
     * FTP server. It also activates the timers and invokes the methods to
     * printOut the statistics of the file transfer.
     * 
     * @param filename
     *            TransferFile The TransferFile Object and location which will
     *            be loaded and sent
     */

    public void sendFile(TransferFile theFile) {
	this.transferFile = theFile;
	String response = "";
	try {
	    fileInputStream = new FileInputStream("." + transferFile.getFileNameWithPath());
	    bis = new BufferedInputStream(fileInputStream);
	    sendLine("STOR " + transferFile.getFileName());
	    uploadDataSocket = new Socket(connectionSetup.getServerIp(), connectionSetup.getPasiveUploadport());
	    response = readLine();
	    // Verifies the FTP response
	    if (!response.startsWith(DATA_CHANNEL_OPEN)) {
		throw new IOException("Problem sending File " + response);
	    }

	    // Timmer for the Statistics
	    double timerIn = System.currentTimeMillis();
	    BufferedOutputStream output = new BufferedOutputStream(uploadDataSocket.getOutputStream());
	    // buffer size
	    byte[] buffer = new byte[4096];
	    int bytesRead = 0;

	    while ((bytesRead = bis.read(buffer)) != -1) {
		output.write(buffer, 0, bytesRead);
	    }

	    output.flush();
	    output.close();
	    bis.close();
	    uploadDataSocket.close();

	    // Timer for the statistics
	    double timerOut = System.currentTimeMillis();
	    response = readLine();

	    executeStatistics(transferFile, timerIn, timerOut);

	} catch (FileNotFoundException e) {
	    Logger.getLogger("FTPlogger").warning("File not Found");
	    Logger.getLogger("FTPlogger").warning(e.toString());
	    return;

	} catch (IOException e) {
	    Logger.getLogger("FTPlogger").warning(e.toString());
	}

    }

    /**
     * executeStatistics() method calculates the transferTime, transferRate and
     * prints out the statistics to the console.
     * 
     * @param theFile
     *            (TransferFile) the file that is being uploaded.
     * @param timerIn
     *            (double) Start timer.
     * @param timerOut
     *            (double)Stop timer.
     */
    private void executeStatistics(TransferFile transferFile, double timerIn, double timerOut) {
	// Setting of statistics for the file in question
	statistics.setFileTransferTime(statTools.calculateMilliSecondsFileTransfer(timerOut, timerIn));
	statistics.setFileTransferRate(
		statTools.calculateAverageSpeed(transferFile.getFileSize(), statistics.getFileTransferTime()));
	// Console printout for the statistics of this upload
	StatisticsTools.ConsolePrintout(transferFile.getFileName(), statistics);
    }

}
