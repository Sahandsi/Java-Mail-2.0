import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.*;
import java.io.IOException;
import java.io.*;
import java.net.*;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Client extends Application // for GUI
{

    private PrintWriter outputToServer; // send message to server
    private Scanner inputFromServer;    // gets response back from the server
    //private String username;
    private Socket socket;
    private static InetAddress host = null;
    final int PORT = 1234;

    public static void main(String[] args) {
        try {
            host = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            System.out.println("Host ID Not Found");
        }

        do {
            launch(args);
        } while (true);

    }

    public void loader() throws Exception
    {
        socket = new Socket(host, PORT);
        // scanner set up so that it can scan for any input stream (responses) that come from the server
        inputFromServer = new Scanner(socket.getInputStream());
        outputToServer = new PrintWriter(socket.getOutputStream(), true);

    }

//    public void start(Stage stage) throws Exception {
//        // set up variables
//
////        socket = new Socket(host, PORT);
////        // scanner set up so that it can scan for any input stream (responses) that come from the server
////        inputFromServer = new Scanner(socket.getInputStream());
////        outputToServer = new PrintWriter(socket.getOutputStream(), true);
//
//
//        Parent root = FXMLLoader.load(getClass().getResource("welcome.fxml"));
//        Scene scene;
//        scene = new Scene(root, 500, 500);
//        //add the scene to the stage
//        stage = new Stage();
//        stage.setScene(scene);
//        stage.show();
//
//    }

    @FXML
    public void start(Stage stage) throws Exception
    {

        // set up variables
        Parent root = FXMLLoader.load(getClass().getResource("welcome.fxml"));
        Scene scene;
        scene = new Scene(root, 640, 450);
        //add the scene to the stage
        stage = new Stage();
        stage.setScene(scene);
        stage.show();

    }



    @FXML
    private TextField Input;

    @FXML
    private void loginButton (ActionEvent event) throws Exception
    {

        String t = Input.getText();
        //validateUsername(t);
        System.out.println(t);
        validateUsername(t);
    }


    @FXML
    private void validateUsername(String t) throws Exception {
//        if (username.isEmpty()) {
//           // message.setText("Please enter your username");
//        } else {
//           //  send username across to the server
//        System.out.println(username);
//String u;
//u= username;

            loader();
            outputToServer.println(t);
            outputToServer.flush();
            String serverRequest = inputFromServer.nextLine();

            if (serverRequest.equals("true")) {
                LoadClient();
            }
        //}
    }

    private void LoadClient() throws Exception
    {
//        Scene scene;
//        VBox vbox;
//        Stage stage;
//
//        Button inbox = new Button("Inbox");
//        Button email = new Button("Email");
//        Button quit = new Button("Quit");
//
////        inbox.setOnAction(e -> getInbox());
////        //email.setOnAction(e -> getEmail());
////        quit.setOnAction(e -> quitApp());
////
////        // add buttons to layout
//        vbox = new VBox();
////        vbox.getChildren().add(inbox);
////        vbox.getChildren().add(email);
////        vbox.getChildren().add(quit);
//
//        scene = new Scene(vbox, 500, 500);
//        stage = new Stage();
//        stage.setScene(scene);
//        stage.show();
        Scene scene;
        Stage stage;
       // closeButtonAction();

        Parent root = FXMLLoader.load(getClass().getResource("mainPage.fxml"));
        scene = new Scene(root, 800, 500);
        stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }

//    public void getInbox() {
//        System.out.println("BEFORE SENDING INBOX REQUEST");
//        outputToServer.println("get_inbox");
//        System.out.println("AFTER SENDING INBOX REQUEST");
//    }

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

//    public void getEmail() throws Exception {
//        //outputToServer.println("get_email"); // send request
//        ObjectInputStream recieveObjFromServer = null; // recieve obj from serber
//
//
//        try
//        {
//             recieveObjFromServer = new ObjectInputStream(socket.getInputStream());
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

    @FXML
    private TextArea content;

//    @FXML
//    public void sendMail() throws Exception {
//        loader();
//        String t = content.getText();
//        System.out.println("send_email");
//        outputToServer.write(t);
//        outputToServer.flush();
//
//        //System.out.println(t);
//
//        //loader();
//        //outputToServer.println(t);
//        // String serverRequest = inputFromServer.nextLine();
//
//        // send a request to a server indicating the user wants to send a valid message
//
//
//        }
    //}

    public void getInbox() {
        try {
            loader();
            System.out.println("BEFORE SENDING INBOX REQUEST");
            outputToServer.println("get_inbox");
            System.out.println("AFTER SENDING INBOX REQUEST");
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public void getEmail() {
        outputToServer.println("send_email");
    }

    public void quitApp() {
        outputToServer.println("close");
    }

}
