
import javafx.fxml.FXML;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Server implements Serializable // used to send object from     client to server
{
    // list of users of type string
    private static ArrayList<String> users = new ArrayList<String>();
    private static ArrayList<Email> mails = new ArrayList<Email>();

    public static void main(String[] args) throws IOException
    {
        // set up 3 users
        users.add("U1");
        users.add("U2");
        users.add("U3");

        Socket client; // client
        ServerSocket serverSocket = null; // server
        final int PORT = 1234;

        ClientHandler clientHandler;

        // set up the server socket
        try
        {
            serverSocket = new ServerSocket(1234);
        }
        catch (IOException ioEx)
        {
            System.out.println("Can't set up port");
            System.exit(1);
        }

        System.out.println("\n Server running");

        do {
            client = serverSocket.accept(); // accept the client to the   server
            System.out.println("Client accepted");
            // create a function that will validate the user
            String validUser = validateUser(client);
            clientHandler = new ClientHandler(validUser, client);
            clientHandler.start(); // calls the run function

        } while (true);


    }

    private static String validateUser(Socket client)
    {
        Scanner inputFromClient = null;
        PrintWriter outputToClient = null;
        boolean validUser = false;

        try
        {
            // allows the server to retrieve the input from the client
            inputFromClient = new Scanner(client.getInputStream());
            // allow server to send things to the client
            outputToClient = new PrintWriter(client.getOutputStream(),  true);

        }
        catch(IOException io)
        {
            System.out.println("Problem initialising variables");
        }

        // get the input from the client
        System.out.printf("BEFORE THE VALUE");
        String userToValidate = inputFromClient.nextLine();


        while  (validUser == false)
        {
            for(String username : users)
            {
                // check the user to validate matches the user from the  client
                if (username.equals(userToValidate))
                {
                    // tell the client that user is valid
                    validUser = true;
                    break;
                }
            }

            if(validUser == false)
            {
                // user is invalid so wait for a new user to pass from the client to the server
                outputToClient.println("false");
                userToValidate = inputFromClient.nextLine();
            }
            else
            {
                outputToClient.println("true");
            }

        }
        // return the correct username
        return userToValidate;

    }


    // get the mail from the server so it can be accessed in the clienthandler
    private static ArrayList<Email> getMail()
    {
        return mails;
    }

}
// each client will have their unique username
class ClientHandler extends Thread implements Serializable {
    private Socket client;
    // retrieve requests from the client
    private Scanner input;
    // send requests to the client
    private PrintWriter output;
    private String username;

    public ClientHandler(String username, Socket client) {
        this.username = username;
        this.client = client;
        try {
            input = new Scanner(client.getInputStream());
            output = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException io) {
            System.out.println("Client Handler not set up properly");
        }

    }

    public void run() {
        // recieve request from the server
        String request = input.nextLine();

        System.out.println(request);
        // check the request
        while (!request.equals("close")) {
            // do whatever the user wants to do
            if (request.equals("get_inbox")) {
                System.out.println("INSIDE INBOX REQUEST");
            } else if (request.equals("send_email")) {
                System.out.println("INSIDE SEND EMAIL REQUEST");
            }

            request = input.nextLine(); // get new request from server
        }

        // end the client connection
        try {
            System.out.println("Ending connection");
            client.close();
        } catch (IOException io) {
            System.out.println("Coulnd't close connection");
        }


    }


}