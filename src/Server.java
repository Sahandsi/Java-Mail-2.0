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


    private static void getUsers()
    {
        try{



            Statement statement = null;

            //Class.forName(driver);

            Connection conn = getConnection();
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
        //return null;
    }

    public static Connection getConnection()
    {
        String url = "jdbc:mysql://localhost:8889/Java";
        String username = "root";
        String password = "root";
        Connection conn = null;
        try {
             conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return conn;

    }




    public static void main(String[] args) throws IOException

    {

        getUsers();
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


//    private void registerUser(String username)      // For registering the user to the SQL
//    {
//        String t = username;
//
//        try
//        {
//            Connection con = getConnection();
//            PreparedStatement posted = con.prepareStatement("INSERT INTO Users (Name) VALUES ('"+t+"')");
//            posted.executeUpdate();
//            // outputToServer.println(t);
//            //message.setText("Register Complete!");
//
//        }
//        catch (Exception e)
//        {
//            System.out.println(e);
//        }
//
//        finally
//        {
//            System.out.println("Insert complete");
//        }
//
//    }



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


        String request = input.nextLine();

        while(!request.equals("close")) {
            if (request.equals("get_inbox")) {
                System.out.println("INSIDE INBOX REQUEST");

                // remove all email and read them from database again
                Server.RetrieveEmails().clear();



                ArrayList<Email> lstInbox = new ArrayList<Email>();
                Connection conn = Server.getConnection();
                try
                {
                    Statement statement = conn.createStatement();
                    String select = "SELECT * FROM EMAIL";
                    ResultSet resultSet = statement.executeQuery(select);

                    while (resultSet.next())
                    {
                        int userID = resultSet.getInt("EmailID");
                        String usernameTo = resultSet.getString("usernameTo");
                        String usernameFrom = resultSet.getString("usernameFrom");
                        String emailSubject = resultSet.getString("emailSubject");
                        String message = resultSet.getString("message");
                        byte[] attachmentFile = resultSet.getBytes("attachmentFile");
                        String attachment = resultSet.getString("attachment");
                        System.out.println("user id: " + userID) ;


                        Server.RetrieveEmails().add(new Email(userID, usernameFrom, usernameTo,emailSubject ,message, attachmentFile, attachment));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }


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
                String emailSubject =input.nextLine();
                String message = input.nextLine();
                String attachmentName =input.nextLine();
                System.out.println(attachmentName);


                if(attachmentName.equals("Attachment: Empty"))
                {
                    System.out.println("INSIDE IF ATTACHMENT NAME ==");
                    output.println("Attachment: Empty");


                    //Email email = new Email(username, to, message, null, null);
                    //Server.RetrieveEmails().add(email);

                    Connection conn = Server.getConnection();
                    // MAKE THIS PREPARED
                    try {
                        Statement statement = conn.createStatement();
                        String insertStatement = "INSERT INTO EMAIL (usernameTo, usernameFrom, emailSubject,message, attachmentFile, attachment)" +
                                "VALUES (" + "'" + to + "'" + "," + "'" +username + "'" + "," + "'"+ emailSubject +"'" + "," + "'" + message + "'" + "," + " null, null )";
                        System.out.println(insertStatement);
                        statement.executeUpdate(insertStatement);

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }


                }
                else
                {
                    System.out.println("INSIDE ELSE OF ATTACHMENT NAME ==");
                    output.println("attachment");
                    byte[] byteAttachment = null;

                    // get the attachment
                    ObjectInputStream inStream = null;

                    try {
                        inStream = new ObjectInputStream(client.getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        try {
                            byteAttachment = (byte[])inStream.readObject();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    Connection conn = Server.getConnection();
                    try {
                        Statement statement = conn.createStatement();
                        String insertStatement = "INSERT INTO EMAIL (usernameTo, usernameFrom, emailSubject,message, attachmentFile, attachment)" +
                                "VALUES (" + "'" + to + "'" + "," + "'" +username +"'" + "," + "'"+emailSubject + "'" + "," + "'" + message + "'" + "," + "'" + byteAttachment + "'"
                                + ", " + "'" + attachmentName + "'" + ")";
                        System.out.println(insertStatement);
                        statement.executeUpdate(insertStatement);

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }


                    System.out.println("hahay");
                }


                for (Email inbox : Server.RetrieveEmails())
                {
                    System.out.println("From:"  + inbox.getFrom());
                    System.out.println("To:"  + inbox.getTo());
                    System.out.println("Subject:"  + inbox.getemailSubject());
                    System.out.println("Message:"  + inbox.getMessage());
                    System.out.println("Attachment Name:"  + inbox.getAttachment());
                    System.out.println("Attachment:"  + inbox.getAttachmentFiles());

                }

            }

            else if (request.equals("delete_mail"))
            {
                //String i = input.nextLine();
                int indexToRemove = input.nextInt();
                System.out.println(indexToRemove);

                Connection conn = Server.getConnection();
                Statement statement = null;
                try {
                    statement = conn.createStatement();

                    String insertStatement = " DELETE FROM EMAIL WHERE EmailID = " + indexToRemove;
                    System.out.println(insertStatement);
                    statement.executeUpdate(insertStatement);

                } catch (SQLException e) {
                    e.printStackTrace();
                }


                //Server.RetrieveEmails().remo(indexToRemove);
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
