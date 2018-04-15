import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Server implements Serializable
{

    private static ArrayList<String> users = new ArrayList<String>();       // List of users array(list)
    private static ArrayList<Email> lstEmails = new ArrayList<Email>();     // List of emails array(list)
    private static ArrayList<String> messages = new ArrayList<String>();     // List of emails array(list)


    private static Connection getConnection()
    {
        try{
            Connection link = null;
            Statement statement = null;
            ResultSet results = null;
            String driver = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql://localhost:8889/Java";
            String username = "root";
            String password = "root";

            Class.forName(driver);

            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connected");
            statement = conn.createStatement();
            String sql = "SELECT Name from Users";
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next())
            {


                String names = rs.getString("Name");
                users.add(names);

            }


        }

        catch(Exception e)
        {
            System.out.println(e);
        }
        return null;
    }



    public static void main(String[] args) throws IOException

    {

        getConnection();
        Socket client;                    // Client socket

        ServerSocket serverSocket = null; // Server socket
        final int PORT = 6666;

        ClientHandler clientHandler;


        try                               // Set up the server socket
        {
            serverSocket = new ServerSocket(PORT);
        }
        catch (IOException ioEx)
        {
            System.out.println("Unable to setup port.!");
            System.exit(1);
        }

        System.out.println("\n Server available.!");

        do
            {
            client = serverSocket.accept();             // Accept the client to the server

            String validUser = validateUser(client);    // Function that to validate the user
            clientHandler = new ClientHandler(validUser, client);
            clientHandler.start();                      // Calls the run function

            }
        while (true);

    }

    public static ArrayList<Email> RetrieveEmails()
    {

        return lstEmails;

    }



    private static String validateUser(Socket client)
    {
       Scanner inputFromClient = null;
       // BufferedReader inputFromClient = null;
        PrintWriter outputToClient = null;
        boolean validUser = false;

        try
        {

          inputFromClient = new Scanner(client.getInputStream());                               // Server to get the input from the client
           // inputFromClient = new BufferedReader(client);                               // Server to get the input from the client

            outputToClient = new PrintWriter(client.getOutputStream(), true);            // Server to send data to the client


        }
        catch(IOException io)
        {
            System.out.println("Problem initialising variables");
        }


        String userToValidate = inputFromClient.nextLine();

        while  (validUser == false)
        {
            for(String username : users)
            {

                if (username.equals(userToValidate)) // Match the user array to the name inputted
                {
                    validUser = true;                // User is valid send to client
                    break;
                }
                else
                {
                    validUser = false;
                }
            }

            if(validUser == false)
            {

                outputToClient.println("false");    // For right username
                userToValidate = inputFromClient.nextLine();
            }
            else
            {
                outputToClient.println("true");
            }

        }

        return userToValidate;                      // Return right username

    }

}


class ClientHandler extends Thread implements Serializable
{
    private Socket client;
    private Scanner input;
    private PrintWriter output;

    private String username;

    public ClientHandler(String username, Socket client)
    {
        this.username = username;
        this.client = client;

        try
        {
            input = new Scanner(client.getInputStream());
            output = new PrintWriter(client.getOutputStream(), true);
        }

        catch(IOException io)
        {
            System.out.println("Client Handler Error");
        }

    }

    public void run()
    {


String line =null;
        String request = input.nextLine();

        while(!request.equals("close")) {
            if (request.equals("get_inbox")) {
                System.out.println("INSIDE INBOX REQUEST");

                ArrayList<Email> lstInbox = new ArrayList<Email>();
                for (Email inbox : Server.RetrieveEmails()) {
                    if (inbox.getTo().equals(username)) {
                        lstInbox.add(inbox);
                    }
                }

                ObjectOutputStream os = null;
                try {

                    os = new ObjectOutputStream(client.getOutputStream());

                    os.writeObject(lstInbox);

                    System.out.println(lstInbox);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else if (request.equals("register_user")) {

                String t = input.nextLine();

                try {
                    System.out.println("REGISTER REQUEST");
                    Connection con = null;
                    PreparedStatement posted = con.prepareStatement("INSERT INTO Users (Name) VALUES ('" + t + "')");
                    posted.executeUpdate();
                } catch (Exception e) {
                    System.out.println(e);
                } finally {
                    System.out.println("Insert complete");
                }
            } else if (request.equals("send_email")) {
                System.out.println("INSIDE SEND EMAIL REQUEST");



                String to = input.nextLine();
                String message = input.nextLine();
                String attachmentName =input.nextLine();
                System.out.println(attachmentName);




                byte[] byteArray = null;

                // get the attachment
                ObjectInputStream inStream = null;
                try
                {
                    inStream = new ObjectInputStream(client.getInputStream());
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                try
                {
                    byteArray = (byte[])inStream.readObject();
                }
                catch (ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }



                Email email = new Email(username, to, message, byteArray,attachmentName);

                Server.RetrieveEmails().add(email);

                for (Email inbox : Server.RetrieveEmails())
                {
                    System.out.println("From:"  + inbox.getFrom());
                    System.out.println("To:"  + inbox.getTo());
                    System.out.println("Message:"  + inbox.getMessage());
                    System.out.println("Attachment Name:"  + inbox.getAttachment());
                    System.out.println("Attachment:"  + inbox.getAttachmentFiles());


                }


            }

            else if (request.equals("delete_mail"))
            {
                String i = input.nextLine();
                int indexToRemove = Integer.parseInt(i);
                System.out.println(indexToRemove);

                Server.RetrieveEmails().remove(indexToRemove);
            }

            request = input.nextLine();                 // Wait for the new request
        }

        try
        {
            System.out.println("Ending connection");
            client.close();
            System.exit(0);
        }
        catch(IOException io)
        {
            System.out.println("Could't close connection");
        }

    }

}
