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

import gorjan.exam.connection.ConnectionSetup;
import gorjan.exam.statistics.Statistics;
import gorjan.exam.statistics.StatisticsTools;
import gorjan.exam.transferData.TransferFile;

/**
 * 
 * @author Liquid Sun
 *
 */
public class FtpCommunication extends FtpCode {
    private ConnectionSetup connectionSetup;
    private BufferedReader reader;
    private Socket socket;
    private BufferedWriter writer;
    private BufferedInputStream bis;
    private FileInputStream fileInputStream;
    private Socket uploadDataSocket;
    private Statistics statistics;
    private StatisticsTools statTools;

    /**
     * ftpInitialize() sets up the necessary parameters which will be used during
     * the use of methods in this class.
     * 
     * @param connectionSetup
     *            ConnectionSetup is the forwarded setup parameters to be used
     *            during the execution
     */
    public void ftpInitialize(ConnectionSetup connectionSetup) {

	this.connectionSetup = connectionSetup;
	this.statistics = new Statistics();
	this.statTools = new StatisticsTools();
    }

    /**
     * ReadLine() connects to the pre-configured BufferedReader and identifies if
     * the the last read line contains one of the FTP response codes.
     * 
     * @return Last line of response code FTP message
     * @throws IOException
     * @throws InterruptedException
     */
    public String readLine() throws IOException, InterruptedException {
	String strLine = null;
	String line = null;

	while (((line = reader.readLine()) != null)) {
	    for (int i = 99; i < 10069; i++) {
		if (line.contains(i + " ")) {
		    strLine = line;
		    return strLine;
		}
	    }
	}

	return strLine;
    }

    /**
     * Sendline() sends via the buffered writer.
     * 
     * @param line String is the String we send to the FTP server.
     * @throws IOException
     */
    public void sendLine(String line) throws IOException {
	if (socket == null) {
	    throw new IOException("No FTPConnection");
	}
	try {
	    writer.write(line + "\r\n");
	    writer.flush();
	} catch (IOException e) {
	    socket.close();
	    socket = null;
	    throw e;
	}
    }

    /**
     * connect() method Connects to the server in question and expects the FTP
     * code for the succesfull connection. The method then proceedes to connect
     * with the provided data (Username and Password)or the default preconfigured data if the data
     * provided is incomplete.
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    public void connect() throws IOException, InterruptedException {
        //Configuration of socket, reader and writer.
	socket = new Socket(connectionSetup.getServerIp(), connectionSetup.getPort());
	reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

	String response = readLine();
	//System.out.println(response);

	if (!response.startsWith(connectedSuccess)) {
	    throw new IOException("Unexpected response:" + response);
	}
	sendLine("USER " + connectionSetup.getUsername());
	response = readLine();
	//System.out.println(response);
	if (!response.startsWith(usernameSuccess)) {

	    throw new IOException("Unexpected Response" + response);
	}
	sendLine("PASS " + connectionSetup.getPassword());
	response = readLine();
	//System.out.println(response);

	if (!response.startsWith(passwordSuccess)) {
	    System.out.print("Fail");
	    throw new IOException("unsuccessful login" + response);
	}
	//System.out.println(response);
    }

    /**
     * setPassiveMode() Due to the problems arising from NAT the best solution for the connection
     * of the FTP with a better success rate of uploading files this method
     * attempts to set Passive Mode on the FTP server.
     * 
     * @throws IOException
     * @throws InterruptedException
     */

    public void setPasiveMode() throws IOException, InterruptedException {

	//System.out.println("Attempting to set pasive mode: ");
	sendLine("PASV");
	String response = readLine();
	if (!response.startsWith(passiveModeSuccess)) {
	    throw new IOException("Could not set passive mode: " + response);
	}
	connectionSetup.extractPassiveModeData(response);

	//System.out.println("***" + response + "***");

    }

    /**
     * setBinaryMode() method FTP has two modes for transfer ASCII which is
     * limited in functionality and Binary mode, this method attempts to
     * activate the Binary mode for transfer.
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    public void setBinaryMode() throws IOException, InterruptedException {
	//System.out.println("Attempting to set binary mode: ");
	sendLine("TYPE I");
	String response = readLine();
	if (!response.startsWith(binaryModeSuccess)) {
	    throw new IOException("Could not set Binary mode: " + response);
	}
    }

    /**
     * disconnect() Method This method sends the FTP server the quit command and
     * then nulls the Socket that has been used.
     * 
     * @throws IOException
     */
    public void disconnect() throws IOException {
	try {
	    sendLine("QUIT");
	} finally {
	    socket = null;
	}
    }

    /**
     * 
     *    sendFile() method Initializes all the neccessary
     *    streams and parameters for sending of the file including the
     *    loading of the File in question. The method contains a check
     *    to see if the correct codes are sent from the FTP server. It
     *    also activates the timers and invokes the methods to printOut
     *    the statistics of the file transfer.
     * @param filename TransferFile  The TransferFile Object and location which will be loaded and sent
     * @throws IOException
     * @throws InterruptedException
     */

    public void sendFile(TransferFile theFile) throws InterruptedException, IOException {
        String response = "";
        try {
           fileInputStream = new FileInputStream("." + theFile.getFileNameWithPath());
	} catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
	    return;
	   } 
	bis = new BufferedInputStream(fileInputStream);

	sendLine("STOR " + theFile.getFileName());
	uploadDataSocket = new Socket(connectionSetup.getServerIp(), connectionSetup.getPasiveUploadport());
	response = readLine();
	
        //Verifies the FTP response
	if (!response.startsWith(dataChannelOpen)) {
	    throw new IOException("Problem sending File " + response);
	}
	BufferedOutputStream output = new BufferedOutputStream(uploadDataSocket.getOutputStream());

	byte[] buffer = new byte[4096];
	int bytesRead = 0;
	//Timmer for the Statistics
	double timerIn = System.currentTimeMillis();
	while ((bytesRead = bis.read(buffer)) != -1) {
	    output.write(buffer, 0, bytesRead);
	}
	output.flush();
	//Timer for the statistics
	double timerOut = System.currentTimeMillis();

	output.close();
	bis.close();
	uploadDataSocket.close();
	response = readLine();
	
	//Setting of statistics for the file in question
	statistics.setFileTransferTime(statTools.calculateMilliSecondsFileTransfer(timerOut, timerIn));
	statistics.setFileTransferRate(
		statTools.calculateAverageSpeed(theFile.getFileSize(), statistics.getFileTransferTime()));
        // Console printout for the statistics of this upload
	StatisticsTools.ConsolePrintout(theFile.getFileName(), statistics);
      }

}
