package docker;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Connector implements Runnable {
    private Socket socket;
    private Scanner in;
    private PrintWriter out;

    public Connector(Socket socket){
        this.socket = socket;
    }
    public void run(){
        try{
            System.out.println(socket.getInetAddress().getHostAddress());
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("Hello user.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
