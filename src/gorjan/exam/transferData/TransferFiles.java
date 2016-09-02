package gorjan.exam.transferData;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.regex.Matcher;

/**
 * TransferFiles class is Singletone object containing the list of files to be
 * used for the batch upload.
 * 
 * @author Liquid Sun
 */

public class TransferFiles {

    private static TransferFiles instance = null;
    private LinkedList<String> fileList;
    private int SizeOfList;

    // Default Singleton Constructor
    private TransferFiles() {
    }
    
    // Singletone initialisation and instance retriever
    public static TransferFiles getInstance() {
	if (instance == null) {
	    instance = new TransferFiles();
	}
	return instance;
    }
    
    /**
     * setTransferFiles() method parses the arguments received by the main
     * method looking for the "-files" flag then proceeds to parse
     * the files to a LinkedList <String> which will be used by the
     * connection. The PrepareFiles checks the length of the list. If
     * the list exceeds five files the program writes a console error
     * message to the terminal and quits the execution of the program.
     *  
     * @param args(String[]) are the program start parameters
     *     
     */
    public void setTransferFiles(String[] args) {
	fileList = new LinkedList<String>();
	for (int i = 0; i < args.length; i++) {
	    if (args[i].toLowerCase().equals("-files")) {
		this.fileList = new LinkedList<String>(Arrays
			.asList(args[i + 1].replaceAll("/", Matcher.quoteReplacement(File.separator)).split(";")));
	    }
	}

	if (fileList.size() > 5) {
	    System.out.println(
		    "Current limit is 5 files per one application run \nThe application will now close, please select 5 files max");
	    System.exit(0);
	}
	this.SizeOfList=this.fileList.size();
    }
 
    /**
     * ArrayOfFiles() method builds a string representation of all files that are
     * going to be used in the transfer.
     */
    public  String arrayOfFiles() {
	String files = "";
	for (String file : this.fileList) {
	    files = files + file + " ";
	}
	System.out.println("Files in total: " + fileList.size());
	return files;
    }

    /**
     * returnPopedFileName() method pops a single Filename with path from the LinkedList 
     * @return String
     */
    public String returnPopedFileName(){
        return fileList.pop();
    }
    
    //Getter
    public int getSize (){
        return this.SizeOfList;
    }
}
