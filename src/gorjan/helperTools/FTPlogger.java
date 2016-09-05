package gorjan.helperTools;


import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FTPlogger class configures the channels to be used in the logger and output settings.
 * @author Liquid Sun
 *
 */
public class FTPlogger {

    private static final Logger LOGGER = Logger.getLogger("FTPlogger");
    
    public static void configureLogger(){
        LOGGER.setUseParentHandlers(false);
	LOGGER.setLevel(Level.ALL);

	ConsoleHandler handler = new ConsoleHandler();
	handler.setLevel(Level.ALL);
	LOGGER.addHandler(handler);
    }


}
