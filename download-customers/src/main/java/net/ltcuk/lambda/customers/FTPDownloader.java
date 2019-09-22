package net.ltcuk.lambda.customers;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
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
            return ftpClient.login(username, password);
        } catch (IOException ie) {
            log.debug(ie.toString());
            return false;
        }
    }

    public boolean closeConnection() {
        if (ftpClient != null) {
            try {
                ftpClient.disconnect();
                return true;
            } catch (IOException ie) {
                ie.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean download(String remotePath, String localPath) {
        if (ftpClient.isConnected()) {
            ftpClient.enterLocalPassiveMode();
            try {
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                File downloadFile = new File(localPath);
                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
                boolean success = ftpClient.retrieveFile(remotePath, outputStream);
                outputStream.close();
                return success;
            } catch (IOException ie) {
                ie.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }
}
