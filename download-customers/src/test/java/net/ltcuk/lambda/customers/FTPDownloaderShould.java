package net.ltcuk.lambda.customers;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

import java.io.File;

public class FTPDownloaderShould {

    private static FakeFtpServer fakeFtpServer;
    private static String username = "username";
    private static String password = "password";
    private static int port = 2222;

    private static String fileContents = "Site,CLI,From Date,To Date,Quantity,Unit Cost,Total Cost,Description,User,Department,End User Unit Cost,End User Total Cost,End User Description,End User Account,End User Billing,VAT Status,End User VAT Status\n" +
            "OVR90120,\"02089929829\",01/11/2018,30/11/2018,1,1.2200,1.2200,\"I2e Calling Line Id Presentation\",,,1.9900,1.9900,\"I2e Calling Line Id Presentation\",OEU00244,OEU00244,\"VAT 20%\",\"VAT 20%\"\n" +
            "OVR90120,\"02089976734\",01/11/2018,30/11/2018,2,2.1000,4.2000,\"I2e Level 4 Service Care\",,,2.4000,4.8000,\"I2e Level 4 Service Care\",OEU00876,OEU00876,\"VAT 20%\",\"VAT 20%\"\n";

    @BeforeAll
    public static void setup() {
        fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.addUserAccount(new UserAccount(username, password, "/Monthly"));

        FileSystem fileSystem = new UnixFakeFileSystem();
        fileSystem.add(new DirectoryEntry("/Monthly/2019"));
        fileSystem.add(new FileEntry("/Monthly/2019/file.csv", fileContents));
        fakeFtpServer.setFileSystem(fileSystem);
        fakeFtpServer.setServerControlPort(port);

        fakeFtpServer.start();
    }

    @AfterAll
    public static void teardown() {
        fakeFtpServer.stop();
    }

    @Test
    void logIntoAnFTPServer() {
        FTPDownloader downloader = new FTPDownloader(username, password, "localhost", fakeFtpServer.getServerControlPort());
        boolean successful = downloader.openConnection();
        downloader.closeConnection();
        assert(successful);
    }

    @Test
    void downloadFile() {
        FTPDownloader downloader = new FTPDownloader(username, password, "localhost", fakeFtpServer.getServerControlPort());
        downloader.openConnection();
        boolean successful = downloader.download("/Monthly/2019/file.csv", "file.csv");
        downloader.closeConnection();
        new File("file.csv").delete();
        assert(successful);
    }

}
