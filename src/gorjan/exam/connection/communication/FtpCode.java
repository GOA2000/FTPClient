package gorjan.exam.connection.communication;

/**
 * FtpCode class contains the default FTP responses.
 * @author Liquid Sun
 *
 */
public class FtpCode {
    public final static String connectedSuccess="220 ";
    public final static  String usernameSuccess="331 ";
    public final static String passwordSuccess="230 ";
    public final static  String passiveModeSuccess="227 ";
    public final static  String binaryModeSuccess="200 ";
    public final static  String transferSuccess="226 ";
    public final static  String dataChannelOpen="150 ";
	public FtpCode() {
    }

}
