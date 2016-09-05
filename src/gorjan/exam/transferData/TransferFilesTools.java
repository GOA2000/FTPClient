package gorjan.exam.transferData;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

/**
 * TransferFiles class is Singletone object containing the list of files to be
 * used for the batch upload.
 * 
 * @author Liquid Sun
 */

public class TransferFilesTools {

   
    /**
     * setTransferFiles() method parses the arguments received by the main
     * method looking for the "-files" flag then proceeds to parse
     * the files to a LinkedList (String) which will be used by the
     * connection. The PrepareFiles checks the length of the list. If
     * the list exceeds five files the program writes a console error
     * message to the terminal and quits the execution of the program.
     *  
     * @param args(String[]) are the program start parameters
     *     
     */
    public static List<String> ListOfFiles(String[] args) {
	List<String> fileList = null;

	for (int i = 0; i < args.length; i++) {
            //If -files flag is found the next field in array is exstracted and converted to List.
	    if (args[i].toLowerCase().equals("-files") && (i == args.length - 2)) {
		fileList = (Arrays
			.asList(args[i + 1].replaceAll("/", Matcher.quoteReplacement(File.separator)).split(";")));

	    }
            //Check if files are found after the -files flag.
	    if (i == args.length - 1) {
		if (args[i].toLowerCase().equals("-files")) {
		    System.out.println("The files list is missing! Please try again.");
		    System.exit(0);
		}
                //Check if the -files flag is present in expected position.
		if (!args[i - 1].toLowerCase().equals("-files")) {
		    System.out.println("The file flag \"-files\" is missing! Please try again.");
		    System.exit(0);
		}
	    }
	}
        //TransferFile size check. If the list is larger than 5 files the program outputs message and closes.
	if (fileList.size() > 5) {
	    System.out.println(
		    "Current limit is 5 files per one application run \nThe application will now close, please select 5 files max");
	    System.exit(0);
	}
	return fileList;

    }

    /**
     * ArrayOfFiles() method builds a string representation of all files that are
     * going to be used in the transfer.
     */
    public  String arrayOfFiles(List<String> fileList) {
	String files = "";
	for (String file : fileList) {
	    files = files + file + " ";
	}
	System.out.println("Files in total: " + fileList.size());
	return files;
    }


}
