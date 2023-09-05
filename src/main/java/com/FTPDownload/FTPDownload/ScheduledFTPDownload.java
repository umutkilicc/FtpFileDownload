package com.FTPDownload.FTPDownload;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ScheduledFTPDownload {

    public static void main(String[] args) {
        Timer timer = new Timer();
        long delay = 0; // Başlangıçta hemen çalıştır
        long period = 30000; // Her 1 saatte bir çalıştır (3600000 milisaniye)

        timer.scheduleAtFixedRate(new FTPDownloadTask(), delay, period);
    }

    static class FTPDownloadTask extends TimerTask {
        @Override
        public void run() {
            // FTP sunucusuna bağlanmak için gerekli bilgiler
            String server = "test.rebex.net";
            int port = 21; // FTP standart portu
            String username = "demo";
            String password = "password";
            String remoteDirectory = "/pub/example/";

            FTPClient client = new FTPClient();
            client.configure(new FTPClientConfig(FTPClientConfig.SYST_UNIX)); //WIN32NT Hatasını gidermek için

            String localDirectory = "/Users/umutkilic/Downloads/FTPDownload/Indirilenler/"; //Local'de nereye kaydedilecekse

            try {
                client.connect(server, port);
                boolean login = client.login(username, password);

                if (login) {
                    DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("dd/MM/yyy HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    System.out.println("Login başarılı. "+ dateTime.format(now));

                    // Uzak dizindeki dosyaları listele
                    FTPFile[] files = client.listFiles(remoteDirectory);

                    // Her dosyayı indir
                    for (FTPFile file : files) {
                        String remoteFileName = file.getName();
                        String localFileName = localDirectory +remoteFileName;

                        try (OutputStream os = new FileOutputStream(localFileName)) {
                            boolean status = client.retrieveFile(remoteDirectory + remoteFileName, os);
                            if (status) {
                                System.out.println(remoteFileName + " dosyası indirildi.");
                            } else {
                                System.out.println(remoteFileName + " dosyası indirilemedi.");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    // Bağlantıyı kapat
                    client.logout();
                } else {
                    System.out.println("Login başarısız.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    client.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        }
}
