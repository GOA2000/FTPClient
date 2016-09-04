package gorjan.exam.transferData;

import java.io.File;

/**
 * TransferFile class that holds the data for the specific file to be
 * transfered.
 * 
 * @author Liquid Sun
 *
 */
public class TransferFile {
    private File transferFile;
    private long fileSize;
    private String fileNameWithPath;
    private String fileName;

    /**
     * Constructor for assigning the default values.
     * @param fileNameWithPath(String) Name of the file with path
     */
    public TransferFile(String fileNameWithPath) {
	this.transferFile = new File("." + fileNameWithPath);
	this.fileSize = transferFile.length();
	this.fileName = transferFile.getName();
	this.fileNameWithPath = fileNameWithPath;
    }
    
    //Getter
    public String getFileName() {
	return this.fileName;
    }
    
    //Setter
    public void setFileName(String fileName) {
	this.fileName = fileName;
    }
    
    //Getter
    public String getFileNameWithPath() {
	return fileNameWithPath;
    }
    
    //Setter
    public void setFileNameWithPath(String fileNameWithPath) {
	this.fileNameWithPath = fileNameWithPath;
    }
    
    //Getter
    public File getTransferFile() {
	return transferFile;
    }
    
    //Setter
    public void setTransferFile(File transferFile) {
	this.transferFile = transferFile;
    }
    
    //Getter
    public long getFileSize() {
	return fileSize;
    }
    
    //Setter
    public void setFileSize(long fileSize) {
	this.fileSize = fileSize;
    }

}
