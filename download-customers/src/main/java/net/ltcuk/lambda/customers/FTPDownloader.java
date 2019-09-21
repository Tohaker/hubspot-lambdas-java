package net.ltcuk.lambda.customers;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;

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
            ftpClient.connect(server, port);
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                return false;
            }

            return ftpClient.login(username, password);
        } catch (IOException ie) {
            ie.printStackTrace();
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
