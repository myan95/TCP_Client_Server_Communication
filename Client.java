/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

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
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.internal.objects.Global;

/**
 *
 * @author Sherin
 */
public class Client implements Runnable {

    String name;
    int port;
    String serverIP;
    String fileName;
    DataOutputStream download;
    BufferedReader upload;
    Socket ClientSocket;
    DataInputStream DIS;
    public static int flag = 0;

    Client(String name, int port, String serverIP) {
        this.name = name;
        this.port = port;
        this.serverIP = serverIP;
        System.out.println("WELCOME " + name);
    }

    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);
        String name;
        System.out.println("Please enter client name ipadddress portname separated by space  :");
        String portInfo = scan.nextLine();
        String[] info = portInfo.split(" ");
        String clientName = info[0];
        String serverIP = info[1];
        int port = Integer.parseInt(info[2]);
        new Thread(new Client(clientName, port, serverIP)).start();

    }

    @Override
    public void run() {
        try {
            check();
        } catch (IOException ex) {
            System.out.println("HTTP/1.0 404 NOT FOUND /r/n");

        } catch (InterruptedException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void check() throws IOException, InterruptedException {

        Scanner scanProcess = new Scanner(System.in);

        ClientSocket = new Socket(serverIP, port);
        System.out.println(name + " connected");
        DataInputStream DIS = new DataInputStream(ClientSocket.getInputStream());
        download = new DataOutputStream(ClientSocket.getOutputStream());
        upload = new BufferedReader(new InputStreamReader(ClientSocket.getInputStream()));
        download.writeBytes(name + '\n'); //send client name
        String welcomeServer = upload.readLine(); //receive server name
        System.out.println("WELCOME " + welcomeServer + ",YOUR REQUEST:");

        while (true) {
            String request = scanProcess.nextLine();
            download.writeBytes(request + "\n");

            String[] parseRequest = request.split(" ");

            if (parseRequest[0].equals("get")) {
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
                    DIS = new DataInputStream(ClientSocket.getInputStream());
                    if (fstream == null) {
                        System.out.println("HTTP/1.0 404 NOT FOUND /r/n");
                    }
                    if (fstream != null) {

                        do {
                            FileName = DIS.readUTF();
                            System.out.println(" " + FileName);
                            System.out.println("One File Received");
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
                }

            } else if (parseRequest[0].equals("post")) {
                fileName = parseRequest[1];
                String[] p = fileName.split("\\.");
                System.out.println();
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
                            while ((Counter = FileInput.read(readData)) != -1) {
                                download.write(readData, 0, Counter);
                            }
                            System.out.println("HTTP/1.0 200 ok /r/n");
                            FileInput.close();
                        }
                    } catch (IOException ex) {
                        System.out.println("HTTP/1.0 404 NOT FOUND /r/n");
                    }

                } else {
                    String FileName = parseRequest[1];
                    FileInputStream fstream = new FileInputStream(FileName);
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
                            System.out.println("HTTP/1.0 200 ok /r/n");

                        }
                    }

                }

            } else {
                System.out.println("ERROR IN REQUEST FORMAT");
            }
            if (flag == 0) {
                System.out.println("close socket");
                download.close();
                DIS.close();
                ClientSocket.close();
            }
            System.out.println("ANY EXTRA REQUEST:");
        }

    }
}
