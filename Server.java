/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Sherin
 */
public class Server implements Runnable {

    String name;
    BufferedReader upload;
    String fileName;
    ServerSocket WelcomeSocket;
    Socket csocket;

    public static int flag = 0;
    // Socket sock;

    Server(Socket csocket, String name) {
        this.csocket = csocket;
        this.name = name;

    }

    public static void main(String[] args) throws IOException, Exception {
        Scanner s = new Scanner(System.in);
        System.out.println("Please Enter  Server name separeted by space thenPort NO: ");
        String input = s.nextLine();
        String[] splited = input.split(" ");
        int port = Integer.parseInt(splited[1]);
        String serverName = splited[0];
        // Server socket
        ServerSocket WelcomeSocket = new ServerSocket(port);
        System.out.println("socket done");
        while (true) {
            // Welcoming socket
            Socket sock = WelcomeSocket.accept();
            new Thread(new Server(sock, serverName)).start();
        }
    }

    @Override
    public void run() {

        try {
            check();
        } catch (IOException ex) {
            System.out.println("HTTP/1.0 404 NOT FOUND /r/n");
            flag = 1;
        } catch (InterruptedException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void check() throws IOException, InterruptedException {
        DataInputStream DIS = new DataInputStream(csocket.getInputStream());
        upload = new BufferedReader(new InputStreamReader(csocket.getInputStream()));
        DataOutputStream download = new DataOutputStream(csocket.getOutputStream());
        String clientWelcome = upload.readLine();
        System.out.println("WELCOME client:: " + clientWelcome);
        download.writeBytes(name + "\n");

        while (true) {
            String request = upload.readLine();
            String[] parseRequest = request.split(" ");
            if (request != null) {
                System.out.println(request);
            }
            if (parseRequest[0].equals("get")) {
                fileName = parseRequest[1];
                String[] p = fileName.split("\\.");
                if (p[1].equals("png") || p[1].equals("jpg")) {
                    try {

                        File file = new File(fileName);
                        FileInputStream FileInput = new FileInputStream(file);
                        byte[] readData = new byte[1024];
                        int Counter;
                        if (file == null) {
                            System.out.println("HTTP/1.0 404 NOT FOUND /r/n");
                        }
                        if (file != null) {
                            {
                                while ((Counter = FileInput.read(readData)) != -1) {
                                    download.write(readData, 0, Counter);
                                }

                                System.out.println("HTTP/1.0 200 OK /r/n");
                                FileInput.close();
                            }
                        }
                    } catch (IOException ex) {
                        System.out.println("HTTP/1.0 404 NOT FOUND /r/n");
                    }
                } else {
                    String FileName = parseRequest[1];
                    FileInputStream fstream = new FileInputStream(FileName);
                    // Get the object of DataInputStream 
                    DataInputStream in = new DataInputStream(fstream);
                    BufferedReader bcr = new BufferedReader(new InputStreamReader(in));
                    String s1;
                    if (fstream == null) {
                        System.out.println("HTTP/1.0 404 NOT FOUND /r/n");
                    }
                    if (fstream != null) {
                        while ((s1 = bcr.readLine()) != null) {
                            System.out.println("" + s1);

                            download.writeUTF(s1);
                            download.flush();
                            Thread.currentThread().sleep(500);
                            System.out.println("File sent");
                        }

                    }
                }

            }
            if (parseRequest[0].equals("post")) {
                fileName = parseRequest[1];
                String[] p = fileName.split("\\.");
                if (p[1].equals("png") || p[1].equals("jpg")) {
                    File file = new File(fileName);
                    FileOutputStream fout = new FileOutputStream(file);
                    byte[] readData = new byte[1024];
                    int counter;
                    if (file == null) {
                        System.out.println("HTTP/1.0 404 NOT FOUND /r/n");
                    }
                    if (file != null) {
                        while ((counter = DIS.read(readData)) != -1) {
                            fout.write(readData, 0, counter);
                        }
                        fout.flush();
                        fout.close();
                    }
                } else {

                    String FileName = parseRequest[1];
                    FileWriter fstream = new FileWriter(FileName);
                    PrintWriter out = new PrintWriter(fstream);
                    if (fstream == null) {
                        System.out.println("HTTP/1.0 404 NOT FOUND /r/n");
                    }
                    if (fstream != null) {
                        do {
                            FileName = DIS.readUTF();
                            System.out.println(" " + FileName);
                            System.out.println("HTTP/1.0 200 OK /r/n");
                            out.println(FileName);
                            if (out == null) {
                                System.out.println("breaked");
                                break;
                            }
                            out.flush();

                            if (FileName == null) {
                                System.out.println("HTTP/1.0 404 NOT FOUND /r/n");
                                break;
                            }

                        } while (true);

                    }
                    System.out.println("HTTP/1.0 200 OK /r/n");
                }

            }
        }

    }

}
