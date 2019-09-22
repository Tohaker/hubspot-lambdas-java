package net.ltcuk.lambda.customers;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;

@Slf4j
@Getter
public class FTPDownloader {

    private String username, password;
    private String server;
    private int port;
    private FTPClient ftpClient;

    public FTPDownloader(String username, String password, String server, int port) {
        this.username = username;
        this.password = password;
        this.server = server;
        this.port = port;
    }

    public boolean openConnection() {
        ftpClient = new FTPClient();
        ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

        try {
            log.info(String.format("Opening connection to FTP Server: %s", this.server));
            ftpClient.connect(server, port);
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                log.info(String.format("Could not connect to FTP Server. Reply Code %d", reply));
                return false;
            }
            log.info(String.format("Connected to FTP Server. Reply Code %d", reply));

            boolean loginSuccess = ftpClient.login(username, password);
            log.debug(String.format("Login Response from FTP Server: %s", ftpClient.getReplyString()));
            return loginSuccess;
        } catch (IOException ie) {
            log.debug(ie.toString());
            return false;
        }
    }

    public boolean closeConnection() {
        if (ftpClient != null) {
            log.info("Closing FTP Connection...");
            try {
                ftpClient.disconnect();
                log.info("Connection to FTP Server has closed.");
                return true;
            } catch (IOException ie) {
                ie.printStackTrace();
                log.debug(String.format("Connection to FTP Server could not be closed: %s", ie.toString()));
                return false;
            }
        } else {
            log.debug("FTPClient Object was null.");
            return false;
        }
    }

    public boolean download(String remotePath, String remoteFileName, String localPath, String localFileName) {
        if (fileExists(remotePath, remoteFileName)) {
            String fullRemotePath = remotePath + remoteFileName;
            String fullLocalPath = localPath + localFileName;
            ftpClient.enterLocalPassiveMode();
            try {
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                File downloadFile = new File(fullLocalPath);
                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
                boolean success = ftpClient.retrieveFile(fullRemotePath, outputStream);
                outputStream.close();
                return success;
            } catch (IOException ie) {
                log.debug(ie.toString());
                return false;
            }
        } else {
            log.info(String.format("%S could not be downloaded.", remoteFileName));
            return false;
        }
    }

    private boolean fileExists(String remotePath, String fileName) {
        if ((ftpClient != null) && (ftpClient.isConnected())) {
            try {
                log.info(String.format("Checking if file %s exists...", fileName));
                FTPFile[] fileList = ftpClient.listFiles(remotePath);
                for (FTPFile file : fileList) {
                    if (file.getName().equals(fileName)) {
                        log.info(String.format("Found file %s", file));
                        return true;
                    }
                }
                return false;
            } catch (IOException ie) {
                log.debug(ie.toString());
                return false;
            }
        } else {
            log.info("FTPClient not connected.");
            return false;
        }
    }
}
