package gorjan.exam.connection;

/** 
 * ConnectionSetup class configures the connection details which are going to be used
 * for the file transfer.
 * 
 * @author Liquid Sun
 *
 */

public class ConnectionSetup {
    private String username;
    private String password;
    private String serverIp;
    private int port;
    private int pasiveUploadport;
    static final int DEF_PORT = 21;
    static final String DEF_SERVER_IP = "127.0.0.1";
    static final String DEF_USERNAME = "user";
    static final String DEF_PASSWORD = "pass";

    
    /** 
     *  ConnectionSetup constructor reads through the argument flags and
     *  if the verification notices that any of the parameters is missing 
     *  assigns default values to the connection parameters.
     * 
     * @param args (String[])are the parameters provided with running of the program
     */
    public ConnectionSetup(String[] args) {
	this.port = DEF_PORT;

	/*
	 * We will first iterate through the arguments provided.
	 */
	for (int i = 0; i < args.length; i++) {
	    /*
	     * If the parameters username or password have not been both
	     * included the app will default to the default predefined values
	     * with a console message that the default Username and Password
	     * will be used.
	     */
	    if (args[i].toLowerCase().equals("-u")) {
		if (args[i + 2].toLowerCase().equals("-p")) {
		    this.username = args[i + 1];
		    this.password = args[i + 3];
		}
	    } 
            if (this.username==null || this.password==null ) {
        	       	
		    System.out.println(
			    "You have not entered the username or password \nUsing default Username and Password");
		    setDefaultUserPassword();
		}
	    /*
	     * This flag configures the port and the IP of the FTP server.
	     */
	    if (args[i].toLowerCase().equals("-server")) {
		this.serverIp = args[i + 1];
		break;
	    }
	}

    }

    /** 
     * Constructor for users with no Username and Password
     * 
     * @param serverIp (String) is the provided String representation of the ip address.
     */
    public ConnectionSetup(String serverIp) {
	this.serverIp = serverIp;
	this.port = DEF_PORT;
	setDefaultUserPassword();
    }

    /**
     * Constructor for users with no server specified
     * @param username (String) representation of the Username
     * @param password (String) representation of the Password
     */
    public ConnectionSetup(String username, String password) {
	this.username = username;
	this.password = password;
	setDefaultServer();
    }

    public ConnectionSetup(ConnectionSetup initial) {
	this.username=initial.username;
	this.password=initial.password;
	this.serverIp=initial.serverIp;
	this.port=initial.port;
	this.pasiveUploadport=0;
    }

    /**
     *  Method that extracts the data recieved by FTP passive mode and calculates
     *  the port to be used for transfer ie. Entering Passive Mode
     *  (127,0,0,1,246,135) to 246*256+135
     * @param response (String) response from the ftp Server.
     */
    public synchronized void extractPassiveModeData(String response) {
	String passiveModeData = null;
	/*
	 * The neccessary data is enclosed in brackets but the response has other characters
	 * with the next steps we extract the neccessary parameters.
	 */
	passiveModeData = response.substring(response.indexOf("("), response.indexOf(")"));
	String[] arrayOfPasiveModeData = passiveModeData.split(",");
	int sendPort = (Integer.parseInt(arrayOfPasiveModeData[4]) * 256
		+ (Integer.parseInt(arrayOfPasiveModeData[5])));
	this.pasiveUploadport = sendPort;
    }

    /**
     * Method to set default Username and Password.
     */
    private void setDefaultUserPassword() {
	this.username = DEF_USERNAME;
	this.password = DEF_PASSWORD;

    }

    /**
     *  Method to set default Server.
     */
    void setDefaultServer() {
	this.serverIp = DEF_SERVER_IP;
    }

    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    public String getServerIp() {
	return serverIp;
    }

    public void setServerIp(String serverIp) {
	this.serverIp = serverIp;
    }

    public int getPort() {
	return port;
    }

    public void setPort(int port) {
	this.port = port;
    }

    public int getPasiveUploadport() {
	return this.pasiveUploadport;
    }

    /**
     * Sets default Upload Port
     * @param pasiveUploadport int of the upload port generated by the ftp and converted by the extractPassiveModeData() method.
     */
    public void setPasiveUploadport(int pasiveUploadport) {
	this.pasiveUploadport = pasiveUploadport;
    }

}
