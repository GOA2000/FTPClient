# FTPClient
Basic command line FTPClient with parallel 5 file upload 
capability.It also has a converter tool if you want to 
do statistics reporting in minutes instead of seconds.

The FTPClient logs in on a FTPServer 
Sets the mode to Passive for upload.
Sets the transfer mode to Binary 
Sends the file to the FTP server
Prints out the statistics.

The default server settings can be found in the
package "gorjan.exam.connection" class: ConnectionSetup

The Client does not use existing FTPClient packages.
The program has been writen in java 1.7.


HOW TO:
1. Clone the repository
2. Open in preffered IDE software
3. Export as Runnable JAR File.
4. Open terminal/prompt
5. Navigate to the JAR file location.
6. Usage:
Flags:
-u is a flag for Username
-p is a flag for the Password
-server is a flag for the IP address of the FTP server.
-files is a flag for the file list (different files must be separated by ";")

If in the folder where the JAR is located you have
a folder named "var" that contains 5 files ie(1.mpr,2.mp3,3.mp3,4.mp3,5.mp3)
you will use the syntax:

java -jar FTPClient.jar -u user1 -p password -server 127.0.0.1 -files /var/1.mp3;/var/2.mp3;/var/3.mp3;/var/4.mp3;/var/5.mp3

If values are not provided the FTPClient it(FTPClient) will substitute the incomplete values for its pre-defined default values

ie:

java -jar FTPClient.jar -files /var/1.mp3;/var/2.mp3;/var/3.mp3;/var/4.mp3;/var/5.mp3

The program has been tested with the FilleZilla Server 0.958 beta.
