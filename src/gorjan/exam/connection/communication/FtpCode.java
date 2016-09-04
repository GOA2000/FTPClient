package gorjan.exam.connection.communication;

/**
 * FtpCode class contains the default FTP responses.
 * @author Liquid Sun
 *
 */
public class FtpCode {
    public final static String CONNECTED_SUCCESS="220 ";
    public final static  String USERNAME_SUCCESS="331 ";
    public final static String PASSWORD_SUCCESS="230 ";
    public final static  String PASSIVE_MODE_SUCCESS="227 ";
    public final static  String BINARY_MODE_SUCCESS="200 ";
    public final static  String TRANSFER_SUCCESS="226 ";
    public final static  String DATA_CHANNEL_OPEN="150 ";
	public FtpCode() {
    }

}
