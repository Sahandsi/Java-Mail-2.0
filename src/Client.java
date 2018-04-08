import javafx.application.Application;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;


public class Client extends Application     // For GUI
{

    private PrintWriter outputToServer;     // Send message to server
    private Scanner inputFromServer;        // Gets response back from the server
    private Socket socket;
    private static InetAddress host = null;
    final int PORT = 6666;

    public static void main(String[] args) {
        try {
            host = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            System.out.println("Host ID Not Found");
        }

        do {
            launch(args);                   // Run the app with server running
        }
        while (true);

    }

    public void start(Stage stage) throws Exception {                               // GUI

        socket = new Socket(host, PORT);
        inputFromServer = new Scanner(socket.getInputStream());                     // Scan input streams from Server side
        outputToServer = new PrintWriter(socket.getOutputStream(), true); // For sending commands to Server


        Label message;
        stage.setTitle("Welcome");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(grid, 500, 400);
        stage.setScene(scene);


        Label userName = new Label("User Name:");              // Username Label
        grid.add(userName, 0, 1);


        Label regName = new Label("whats your name?");         // Label for the Register
        grid.add(regName, 0, 2);

        TextField userTextField = new TextField();                  // Textfield for Username
        grid.add(userTextField, 1, 1);
        grid.setAlignment(Pos.BOTTOM_CENTER);


        TextField regTextField = new TextField();                   // Textfield for registration
        grid.add(regTextField, 1, 2);


        ImageView image = new ImageView("File:image/1.png");    // Welcome page Image
        grid.add(image, 1, 0);

        Button btn = new Button("Sign in");                    // Sign in Button

        Button regbtn = new Button("Register");                // Register Button

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_CENTER);
        hbBtn.getChildren().add(btn);

        hbBtn.getChildren().add(regbtn);                            // Adding the buttons to grid

        grid.add(hbBtn, 2, 1);
        grid.add(regbtn, 2, 2);


        final Text actiontarget = new Text();
        message = new Label();

        grid.add(message,1,3);

        btn.setOnAction(e -> validateUsername(userTextField.getText(), message,stage));
        regbtn.setOnAction(e -> registerUser(regTextField.getText(), message));

        grid.add(actiontarget, 1, 6);
        stage.show();
    }


    private static Connection getConnection()                      // All the settings for the SQL connection
    {
        try
        {
            String driver = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql://localhost:8889/Java";
            String username = "root";
            String password = "root";
            Class.forName(driver);
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connected");
            return conn;

        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        return null;
    }

    private void registerUser(String username, Label message)      // For registering the user to the SQL
    {
        String t = username;

        try
        {
            Connection con = getConnection();
            PreparedStatement posted = con.prepareStatement("INSERT INTO Users (Name) VALUES ('"+t+"')");
            posted.executeUpdate();
            message.setText("Register Complete!");

        }
        catch (Exception e)
        {
            System.out.println(e);
        }

        finally {
            System.out.println("Insert complete");
        }

    }

    private void validateUsername(String username, Label message,Stage stage)
    {
        if (username.isEmpty())
        {
            message.setText("Enter your username!");
        }
        else {
            outputToServer.println(username);                      // Send username across to the server
            String serverRequest = inputFromServer.nextLine();     // Get the response back

            if (serverRequest.equals("true"))
            {
                LoadClient();
                stage.close();
            }
            else
                {
                message.setText("Wrong username!");
                }
        }
    }

    private void LoadClient()                                         // The main page for the inbox

    {
        outputToServer.println("get_inbox");                          // Tell server get inbox items
        ArrayList<Email> inbox = null;
        ObjectInputStream is = null;

        try
        {
            is = new ObjectInputStream(socket.getInputStream());
        }
        catch  (IOException e)
        {
            e.printStackTrace();
        }

        try
        {
            inbox = (ArrayList<Email>) is.readObject();               // Store mails in array

        }
        catch (IOException i)
        {
            i.printStackTrace();
        }
        catch (ClassNotFoundException c)
        {
            c.printStackTrace();
        }

        ObservableList<Email> oMail = FXCollections.observableArrayList(inbox);


        Stage stage;
        Scene scene;

        // Use a border pane as the root for scene
        BorderPane border = new BorderPane();
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(30);   // Gap between nodes
        hbox.setStyle("-fx-background-color: #336699;");


        TableColumn<Email, String> sentBy = new TableColumn<>("Sent By");
        sentBy.setPrefWidth(250);
        sentBy.setCellValueFactory(new PropertyValueFactory<>("from"));


        TableColumn<Email, String> message = new TableColumn<>("Message");
        message.setPrefWidth(250);
        message.setCellValueFactory(new PropertyValueFactory<>("message"));


        TableView<Email> inboxTable = new TableView<Email>();

        inboxTable.getColumns().addAll(sentBy, message);          // Add columns of the sender and message


        Button buttonCompose = new Button("Compose");
        buttonCompose.setPrefSize(100, 20);
        buttonCompose.setOnAction(e -> sendMail());

        Button buttonReply = new Button("Reply");
        buttonReply.setPrefSize(100, 20);


        Button buttonDelete = new Button("Delete");
        buttonDelete.setOnAction(e -> { Email selectedItem = inboxTable.getSelectionModel().getSelectedItem();
            int index = inboxTable.getSelectionModel().selectedIndexProperty().get();
            inboxTable.getItems().remove(selectedItem);
            outputToServer.println("delete_mail");
            outputToServer.println(index);
        });

        buttonDelete.setPrefSize(100, 20);


        Button buttonExit = new Button("Close App");
        buttonExit.setOnAction(e ->  quitApp());
        buttonExit.setPrefSize(100, 20);


        Button buttonRefresh = new Button("Refresh");
        buttonRefresh.setOnAction(e -> refreshMail(inboxTable));
        buttonRefresh.setPrefSize(100, 20);



        hbox.getChildren().addAll(buttonCompose, buttonDelete,buttonRefresh , buttonReply,buttonExit);

        border.setTop(hbox);
        inboxTable.setItems(oMail);             // Joining the column an data

        inboxTable.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {

            if( e.isPrimaryButtonDown() && e.isSecondaryButtonDown())
            {
                System.out.println( "Both down");
            }

            else if( e.isPrimaryButtonDown())
            {
                System.out.println( "Primary down");
                inboxTable.setOnMouseClicked(q -> SelectedEmail(inboxTable,buttonReply));
            }

            else if( e.isSecondaryButtonDown())
            {
                       inboxTable.setOnMouseClicked(q -> SeeEmail(inboxTable));

                System.out.println( "Secondary down");
            }

        });

        border.setCenter(inboxTable);
        scene = new Scene(border, 800, 500);
        stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }

    private void SelectedEmail(TableView<Email> inboxTable, Button buttonReply)
    {
        Email email = inboxTable.getSelectionModel().getSelectedItem();
        buttonReply.setOnAction(e -> reply(email));
    }

    private void SeeEmail(TableView<Email> inboxTable)
    {
        Email email = inboxTable.getSelectionModel().getSelectedItem();
        Cmail(email);
    }

    private void reply(Email emailToReply)
    {
        Stage stage;
        Scene scene;
        stage = new Stage();

        BorderPane border = new BorderPane();
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);   // Gap between nodes
        hbox.setStyle("-fx-background-color: #336699;");

        TextField mailTo = new TextField();
        Button buttonSend = new Button("Send");
        buttonSend.setPrefSize(100, 20);



        Button buttonAttach = new Button("Attach");
        buttonAttach.setOnAction(e -> attachMail());
        buttonAttach.setPrefSize(100, 20);




        hbox.getChildren().addAll(mailTo,buttonSend,buttonAttach);
        border.setTop(hbox);
        TextArea textArea = new TextArea(); //making a TexrArea object


        textArea.setText("\n\n------------------------------\n"+"------------------------------\n\n"+emailToReply.getMessage());
                mailTo.setText(emailToReply.getFrom());

        buttonSend.setOnAction(event -> sendEmail(mailTo.getText(), textArea.getText(),buttonAttach.) ;     // Sender column and data added to the mail class

        mailTo.setPromptText("Recipient Mail Address");
        textArea.setPromptText("Enter text");
        border.setCenter(textArea);
        scene = new Scene(border, 800, 500);

        stage.setScene(scene);
        stage.show();

    }


    private void refreshMail(TableView<Email> inboxTable)
    {
        outputToServer.println("get_inbox");
        inboxTable.getItems().clear();

        ArrayList<Email> inbox = null;
        ObjectInputStream is = null;

        try
        {
            is = new ObjectInputStream(socket.getInputStream());
        }
        catch  (IOException e)
        {
            e.printStackTrace();
        }

        try
        {
            inbox = (ArrayList<Email>) is.readObject();

        }
        catch (IOException i)
        {
            i.printStackTrace();
        }
        catch (ClassNotFoundException c)
        {
            c.printStackTrace();
        }

        ObservableList<Email> oMail = FXCollections.observableArrayList(inbox);
        inboxTable.setItems(oMail);

    }

//    public void attachMail(){
//
//
//        // File chooser code goes here usually
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Open Resource File");
//        File selectedFile = fileChooser.showOpenDialog(null);
//        if (selectedFile !=null)
//        {
//            selectedFile.getAbsolutePath();
//        }
//        System.out.println(selectedFile);
//
//        System.out.println("choosing");
//
//    }


    public void Cmail(Email Cmail)
    {

        Stage stage;
        Scene scene;
        stage = new Stage();

        BorderPane border = new BorderPane();
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);   // Gap between nodes
        hbox.setStyle("-fx-background-color: #336699;");

        Button buttonClose = new Button("Close");
        buttonClose.setPrefSize(100, 20);


        hbox.getChildren().addAll(buttonClose);
        border.setTop(hbox);

        TextArea textArea = new TextArea(); //making a TexrArea object
        textArea.setText(Cmail.getMessage());

        border.setCenter(textArea);
        scene = new Scene(border, 500, 500);

        stage.setScene(scene);
        stage.show();
        buttonClose.setOnAction(e -> stageclose(stage));

    }

    private void stageclose (Stage stage)
    {
        stage.close();
    }

    private void sendMail()
    {

        Stage stage;
        Scene scene;
        stage = new Stage();

        BorderPane border = new BorderPane();
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);   // Gap between nodes
        hbox.setStyle("-fx-background-color: #336699;");

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Open Resource File");
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile !=null)
        {
            selectedFile.getAbsolutePath();
        }
        System.out.println(selectedFile);

        System.out.println("choosing");

        Button buttonAttach = new Button("Attach");
        buttonAttach.setOnAction(e -> selectFile);
        buttonAttach.setPrefSize(100, 20);





        TextField mailTo = new TextField();
        Button buttonSend = new Button("Send");

        buttonSend.setPrefSize(100, 20);
        hbox.getChildren().addAll(mailTo,buttonSend,buttonAttach);

        border.setTop(hbox);
        TextArea textArea = new TextArea();

        buttonSend.setOnAction(event -> sendEmail(mailTo.getText(), textArea.getText(),stage,selectedFile.getAbsolutePath());

        mailTo.setPromptText("Recipient Mail Address");
        textArea.setPromptText("Enter text");
        border.setCenter(textArea);
        scene = new Scene(border, 800, 500);

        stage.setScene(scene);
        stage.show();


    }

    public void sendEmail(String to, String message,Stage stage,String attachMail)
    {

        String finish =".";

        if (to.isEmpty() || message.isEmpty()) // Check if the parameters are empty
        {
            System.out.println("One of these fields are empty");
        }
        else
        {
            outputToServer.println("send_email");
            outputToServer.println(to);
            outputToServer.println(message+finish);
            outputToServer.println(attachMail);
            System.out.println(message);
            System.out.println(attachMail);


        }

        stage.close();                        // Close the page after this check how to do this online

    }

    public void quitApp()
    {
        outputToServer.println("close");
        System.exit(0);
    }

}

