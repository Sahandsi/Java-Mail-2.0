
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
        import java.io.Serializable;
        import java.net.ServerSocket;
        import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
        import java.util.Scanner;

public class Server implements Serializable // used to send object from client to server
{
    // list of users of type string
    private static ArrayList<String> users = new ArrayList<String>();
    private static ArrayList<Email> lstEmails = new ArrayList<Email>();
   // private static ArrayList<Email> mails = new ArrayList<Email>();



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
            //Connection conn = getConnection();
            //connection();
            statement = conn.createStatement();
            String sql = "SELECT Name from Users";


            System.out.println(sql);
            ResultSet rs = statement.executeQuery(sql);
            // return conn;

            while (rs.next()) {


                String names = rs.getString("Name");
                System.out.println("names : " + names);
                users.add(names);

            }


        } catch(Exception e){
            System.out.println(e);
        }
        return null;
    }



    public static void main(String[] args) throws IOException
    {
        // set up 3 users
//        users.add("U1");
//        users.add("U2");
//        users.add("U3");

        getConnection();

        Socket client; // client

        ServerSocket serverSocket = null; // server
        final int PORT = 6666;

        ClientHandler clientHandler;

        // set up the server socket
        try
        {
            serverSocket = new ServerSocket(PORT);
        }
        catch (IOException ioEx)
        {
            System.out.println("Unable to setup port.!");
            System.exit(1);
        }

        System.out.println("\n Server available.!");

        do {
            client = serverSocket.accept(); // accept the client to the server
            // create a function that will validate the user
            String validUser = validateUser(client);
            clientHandler = new ClientHandler(validUser, client);
            clientHandler.start(); // calls the run function

        } while (true);



    }

    public static ArrayList<String> RetrieveUsers()
    {
        return users;
    }

    public static ArrayList<Email> RetrieveEmails()
    {
        return lstEmails;
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
            outputToClient = new PrintWriter(client.getOutputStream(), true);

        }
        catch(IOException io)
        {
            System.out.println("Problem initialising variables");
        }


        // get the input from the client
        String userToValidate = inputFromClient.nextLine();

        while  (validUser == false)
        {
            for(String username : users)
            {
                // check the user to validate matches the user from the client
                if (username.equals(userToValidate))
                {
                    // tell the client that user is valid
                    validUser = true;
                    break;
                }
                else
                {
                    validUser = false;
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
  //  private static ArrayList<Email> getMail()
//    {
//        return mails;
//    }


}

// each client will have their unique username
class ClientHandler extends Thread implements Serializable
{
    private Socket client;
    // retrieve requests from the client
    private Scanner input;
    // send requests to the client
    private PrintWriter output;

    private String username;

    public ClientHandler(String username, Socket client)
    {
        this.username = username;
        this.client = client;
        System.out.println("BEFORE TRY");
        try
        {
            input = new Scanner(client.getInputStream());
            output = new PrintWriter(client.getOutputStream(), true);
        }

        catch(IOException io)
        {
            System.out.println("Client Handler not set up properly");
        }

    }

    public void run()
    {
        // recieve request from the server
        String request = input.nextLine();

        System.out.println(request);
        // check the request
        while(!request.equals("close"))
        {
            // do whatever the user wants to do
            if (request.equals("get_inbox"))
            {
                System.out.println("INSIDE INBOX REQUEST");

                ArrayList<Email> lstInbox = new ArrayList<Email>();
                for (Email inbox : Server.RetrieveEmails())
                {
                    if (inbox.getTo().equals(username))
                    {
                        lstInbox.add(inbox);
                    }
                }
                //lstInbox.remove(index here)
                ObjectOutputStream os = null;
                try
                {
                    os =
                            new ObjectOutputStream(client.getOutputStream());
                    os.writeObject(lstInbox);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

            }
            else if (request.equals("send_email"))
            {
                System.out.println("INSIDE SEND EMAIL REQUEST");
                String to = input.nextLine();
                String message = input.nextLine();
                Email email = new Email(username, to, message);

                // add the email
                Server.RetrieveEmails().add(email);
                for (Email inbox : Server.RetrieveEmails())
                {
                    System.out.println("From:"  + inbox.getFrom());
                    System.out.println("To:"  + inbox.getTo());
                    System.out.println("Message:"  + inbox.getMessage());
                }
            }

            request = input.nextLine(); // get new request from server
        }

        // end the client connection
        try
        {
            System.out.println("Ending connection");
            client.close();
        }
        catch(IOException io)
        {
            System.out.println("Coulnd't close connection");
        }

    }


}
