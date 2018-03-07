import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.*;
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.*;
import javafx.scene.control.Button;


public class Client extends Application // for GUI
{

    private PrintWriter outputToServer; // send message to server
    private Scanner inputFromServer;    // gets response back from the server
    private String username;
    private Socket socket;
    private static InetAddress host = null;
    final int PORT = 6666;

    public static void main(String[] args) {
        try {
            host = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            System.out.println("Host ID Not Found");
        }


        // run the app with server running
        do {
            launch(args);
        } while (true);


    }

    // GUI

    public void start(Stage stage) throws Exception {

        socket = new Socket(host, PORT);
        // scanner set up so that it can scan for any input stream (responses) that come from the server
        inputFromServer = new Scanner(socket.getInputStream());
        outputToServer = new PrintWriter(socket.getOutputStream(), true);




        Label message;
        stage.setTitle("Welcome");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(grid, 500, 400);
        stage.setScene(scene);


        Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);

        ///this is for the register
        Label regName = new Label("whats your name?");
        grid.add(regName, 0, 2);
        ///this is for the register

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);
        grid.setAlignment(Pos.BOTTOM_CENTER);



        ImageView image = new ImageView("File:image/1.png");
    //grid.getChildren().add(new ImageView(image));
        grid.add(image, 1, 0);



//this is the register
        TextField regTextField = new TextField();
        grid.add(regTextField, 1, 2);
//this is the register
        Button btn = new Button("Sign in");
        //this is the register
        Button regbtn = new Button("Register");
//this is the register
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_CENTER);
        hbBtn.getChildren().add(btn);
        //this is for reguster
        hbBtn.getChildren().add(regbtn);
//this is the reguster
        grid.add(hbBtn, 2, 1);
        grid.add(regbtn, 2, 2);


        final Text actiontarget = new Text();
        message = new Label();
        btn.setOnAction(e -> validateUsername(userTextField.getText(), message,stage));
      //  this is the register
        regbtn.setOnAction(e -> registerUser(regTextField.getText(), message));
        //this is the register
        grid.add(actiontarget, 1, 6);
        stage.show();
    }



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


    private void registerUser(String username, Label message) {
        String t = username;

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


    private void validateUsername(String username, Label message,Stage stage) {
        if (username.isEmpty()) {
            message.setText("Please enter your username");
        } else {
            // send username across to the server
            outputToServer.println(username);
            String serverRequest = inputFromServer.nextLine();

            if (serverRequest.equals("true")) {
                LoadClient();
                stage.close();
            } else {
                message.setText("wrong username please try again");
            }
        }
    }


    private void LoadClient() {

        outputToServer.println("get_inbox");
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


        Stage stage;
        Scene scene;

        // Use a border pane as the root for scene
        BorderPane border = new BorderPane();
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);   // Gap between nodes
        hbox.setStyle("-fx-background-color: #336699;");


        TableColumn<Email, String> sentBy = new TableColumn<>("Sent By");
        sentBy.setPrefWidth(250);
        sentBy.setCellValueFactory(new PropertyValueFactory<>("from"));


        TableColumn<Email, String> message = new TableColumn<>("Message");
        message.setPrefWidth(250);
        message.setCellValueFactory(new PropertyValueFactory<>("message"));


        TableView<Email> inboxTable = new TableView<Email>();
        // add columns
        inboxTable.getColumns().addAll(sentBy, message);


        Button buttonCompose = new Button("Compose");
        buttonCompose.setPrefSize(100, 20);
        buttonCompose.setOnAction(e -> sendMail());

        Button buttonReply = new Button("Reply");
        buttonReply.setPrefSize(100, 20);

        Button buttonCmail = new Button("See Mail");
        buttonCmail.setPrefSize(100, 20);



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
        buttonDelete.setPrefSize(100, 20);


        Button buttonRefresh = new Button("Refresh");
        buttonRefresh.setOnAction(e -> refreshMail(inboxTable));
        buttonDelete.setPrefSize(100, 20);

        hbox.getChildren().addAll(buttonCmail,buttonCompose, buttonDelete,buttonRefresh, buttonReply,buttonExit);

        border.setTop(hbox);


        // sender column and data binded to the mail class property

        inboxTable.setItems(oMail);

        inboxTable.setOnMouseClicked(e -> SelectedEmail(inboxTable,buttonReply));

        
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



        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");



        hbox.getChildren().addAll(mailTo,buttonSend);
        border.setTop(hbox);
        TextArea textArea = new TextArea(); //making a TexrArea object



        textArea.setText("\n\n------------------------------\n"+"------------------------------\n\n"+emailToReply.getMessage());
                mailTo.setText(emailToReply.getFrom());

        // sender column and data binded to the mail class property
        buttonSend.setOnAction(event -> sendEmail(mailTo.getText(), textArea.getText(),stage)) ;
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
        scene = new Scene(border, 800, 500);

        stage.setScene(scene);
        stage.show();


    }


    private void sendMail() {


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



        hbox.getChildren().addAll(mailTo,buttonSend);

        border.setTop(hbox);

        TextArea textArea = new TextArea(); //making a TexrArea object
        // sender column and data binded to the mail class property
        buttonSend.setOnAction(event -> sendEmail(mailTo.getText(), textArea.getText(),stage)) ;
        mailTo.setPromptText("Recipient Mail Address");
        textArea.setPromptText("Enter text");
        border.setCenter(textArea);
        scene = new Scene(border, 800, 500);

        stage.setScene(scene);
        stage.show();


    }



    public void sendEmail(String to, String message,Stage stage)
    {
        // check if the parameters are empty
        if (to.isEmpty() || message.isEmpty())
        {
            System.out.println("One of these fields are empty");
        }
        else
        {

            outputToServer.println("send_email");
            outputToServer.println(to);
            outputToServer.println(message);
            // close the page after this check how to do this online
        }

        stage.close();

    }



    public void quitApp() {
        outputToServer.println("close");
        System.exit(0);
    }



}

