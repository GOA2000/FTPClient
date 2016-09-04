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
 * methods to be used with the FTPServer and methods setting the communication modes.
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
    private String response;

    /**
     * FtpCommunication() constructor sets up the necessary parameters which
     * will be used during the use of methods in this class.
     * 
     * @param connectionSetup
     *            ConnectionSetup is the forwarded setup parameters to be used
     *            during the execution
     */
    public FtpCommunication(ConnectionSetup connectionSetup) {
	this.connectionSetup = new ConnectionSetup(connectionSetup);
	this.statistics = new Statistics();
	this.statTools = new StatisticsTools();
	this.socket = new Socket();
    }

    /**
     * ReadLine() connects to the pre-configured BufferedReader and identifies
     * if the the last read line contains one of the FTP response codes.
     * 
     * @return Last line of response code FTP message
     */
    public String readLine()  {
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
     * Sendline() sends a string via the buffered writer.
     * 
     * @param line
     *            (String) is the String we send to the FTP server.
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
     * code for the successful connection. The method then proceeds to connect
     * with the provided data (Username and Password) or the default
     * preconfigured data if the data provided is incomplete.
     * 
     */
    public void connect() {
	
        try {
	    // Configuration of socket, reader and writer.
	    socket = new Socket(connectionSetup.getServerIp(), connectionSetup.getPort());
	    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	    response = readLine();
	    
	    if (!response.startsWith(CONNECTED_SUCCESS)) {
		problemClose("Connection Error: "+response);
	    }
	    sendLine("USER " + connectionSetup.getUsername());
	    response = readLine();
	    
	    if (!response.startsWith(USERNAME_SUCCESS)) {
		problemClose("Username Error: "+response);
	    }
	    sendLine("PASS " + connectionSetup.getPassword());
	    response = readLine();

	    if (!response.startsWith(PASSWORD_SUCCESS)) {
		problemClose("Password Error: "+response);
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

    public void setPasiveMode() {

	sendLine("PASV");
	response = readLine();
	if (!response.startsWith(PASSIVE_MODE_SUCCESS)) {
	problemClose("Passive mode error: "+response);
	}
	connectionSetup.extractPassiveModeData(response);

    }

    /**
     * setBinaryMode() method FTP has two modes for transfer ASCII which is
     * limited in functionality and Binary mode, this method attempts to
     * activate the Binary mode for transfer.
     * 
     */
    public void setBinaryMode() {
	
	sendLine("TYPE I");
	response = readLine();
	if (!response.startsWith(BINARY_MODE_SUCCESS)) {
	problemClose("Binary mode error: "+response);
	}

    }

    /**
     * disconnect() Method This method sends the FTP server the quit command and
     * then closes  the Socket that has been used.
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
     * sendFile() method Initializes all the necessary streams and parameters
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
	try {
	    fileInputStream = new FileInputStream("." + transferFile.getFileNameWithPath());
	    bis = new BufferedInputStream(fileInputStream);
	    sendLine("STOR " + transferFile.getFileName());
	    // Openning of the upload data socket with the calculated passive
	    // port
	    uploadDataSocket = new Socket(connectionSetup.getServerIp(), connectionSetup.getPasiveUploadport());
	    response = readLine();
	    // Verifies the FTP response
	    if (!response.startsWith(DATA_CHANNEL_OPEN)) {
		problemClose("Problem sending File: "+response);
	    }

	    BufferedOutputStream output = new BufferedOutputStream(uploadDataSocket.getOutputStream());
	    // buffer size[4096]
	    byte[] buffer = new byte[4096];
	    int bytesRead = 0;
	    // Timmer for the Statistics
	    double timerIn = System.currentTimeMillis();

	    while ((bytesRead = bis.read(buffer)) != -1) {
		output.write(buffer, 0, bytesRead);
	    }
	    //Closing of streams and upload socket.
	    output.flush();
	    output.close();
	    bis.close();
	    uploadDataSocket.close();

	    // Timer for the statistics
	    double timerOut = System.currentTimeMillis();
	    response = readLine();
            //Statistics console write out.
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
    /**
     * problemClose method closes the system and prints out the last server response.
     * @param response(String)
     */
    private void problemClose(String response){
	Logger.getLogger("FTPlogger").warning("Server response: "+response);
	System.exit(0);
    }

}
