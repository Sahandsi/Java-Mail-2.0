import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.*;
//import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.IOException;
import java.sql.*;


public class Client extends Application // for GUI

{
private PrintWriter outputToServer ; // send message to server
private Scanner inputFromServer;    // gets response back from the server
private Socket socket;
    private static InetAddress host = null;
//    final int PORT = 1234;
    Connection link = null;
    Statement statement = null;
    ResultSet results = null;



    public static void main(String[] args)
    {

        try {
            host = InetAddress.getLocalHost();
            }

            catch (UnknownHostException ex)
            {
            System.out.println("Host ID Not Found");
            }

        do
            {
            launch(args);
            }

            while (true);

    }


    @FXML
    public void start(Stage stage) throws Exception
    {
        // set up variables
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene scene;
        scene = new Scene(root, 640, 450);
        //add the scene to the stage
        stage = new Stage();
        stage.setScene(scene);
        stage.show();

    }


    @FXML
    private void composeButtonHandler(ActionEvent event) throws Exception

    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Compose.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        //set what you want on your stage
        stage.setTitle("Report Page");
        stage.setScene(new Scene(root1));
        stage.setResizable(false);
        stage.show();
    }


    @FXML private Label message;

    @FXML
    private TextField Input;

    @FXML
    private void sendMail()
    {




    }

    private void getEmail()
    {


    }

//
//
//    private void connection(){
//        try {
//            Class.forName("com.mysql.jdbc.Driver");
//            link = DriverManager.getConnection("jdbc:mysql://localhost:8889/Java", "root", "root");
//        }
//        catch(Exception ex)
//        {
//
//        }
//    }

    private static Connection getConnection()
    {
        try{
            String driver = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql://localhost:8889/Java";
            String username = "root";
            String password = "root";

            Class.forName(driver);

            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connected");
            return conn;

        } catch(Exception e){
            System.out.println(e);
        }
        return null;
    }

    @FXML
    private TextField registerInput;

    @FXML
    private void registerUser() throws Exception{
        String t = registerInput.getText();

        try{
            Connection con = getConnection();
            PreparedStatement posted = con.prepareStatement("INSERT INTO Users (Name) VALUES ('"+t+"')");

            posted.executeUpdate();
        }catch (Exception e){
            System.out.println(e);
        }

        finally {
            System.out.println("Insert complete");
        }

    }


    @FXML
    private void validateUsername()
    {
        String t = Input.getText();

    try {
        Connection con = getConnection();
        //connection();
        statement = con.createStatement();
        String sql = "SELECT Name from Users WHERE Name LIKE \'"+ t + "\'";

        System.out.println(sql);
        results = statement.executeQuery(sql);
        int count =0;
        while(results.next())
        {
            count = count+1;
        }

        System.out.println(count);
        if (count ==1){
        System.out.println("user found");
        Scene scene;
        Stage stage;
        closeButtonAction();

        Parent root = FXMLLoader.load(getClass().getResource("mainPage.fxml"));
        scene = new Scene(root, 800, 500);
        stage = new Stage();
        stage.setScene(scene);
        stage.show();

    }

        else if (count>1)
        {
            System.out.println("two users!!!!");
        }
            else{
                System.out.println("no user!");
            }

            }
            catch(Exception ex)
        {

        }


    }

    @FXML private javafx.scene.control.Button login;

    //For closing the old Scene once user logs in.
    @FXML
    private void closeButtonAction(){
        // get a name of the button to the stage
        Stage stage = (Stage) login.getScene().getWindow();
        // close the old stage
        stage.close();
    }
//
//        public void getEmail() throws Exception {
//        //outputToServer.println("get_email"); // send request
//        ObjectInputStream recieveObjFromServer = null; // recieve obj from serber
//
//        try
//        {
//            recieveObjFromServer = new ObjectInputStream(socket.getInputStream());
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//
//        ArrayList<Email> inbox;
//
//
//        inbox = (ArrayList<Email>) recieveObjFromServer.readObject();
//
//        // now display this in a table view or
//
//    }


    public void getInbox() {
        System.out.println("BEFORE SENDING INBOX REQUEST");
//        outputToServer.println("get_inbox");
        System.out.println("AFTER SENDING INBOX REQUEST");
    }

    public void quitApp() {
       // outputToServer.println("close");
        System.exit(0);
    }
}







