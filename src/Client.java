import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javafx.scene.control.Button;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

// FOR GETTING OBJECT THAT YOU CLICKED, INBOXTABLE.GETSELECTEDMODEL().GETSELECTITEM();
// FOR DELETE https://www.youtube.com/watch?v=SnAcSCcz0Sw

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

        Text scenetitle = new Text("Welcome");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Button btn = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);
        final Text actiontarget = new Text();
        message = new Label();
        btn.setOnAction(e -> validateUsername(userTextField.getText(), message));

        grid.add(actiontarget, 1, 6);
        stage.show();
    }

    private void validateUsername(String username, Label message) {
        if (username.isEmpty()) {
            message.setText("Please enter your username");
        } else {
            // send username across to the server
            outputToServer.println(username);
            String serverRequest = inputFromServer.nextLine();

            if (serverRequest.equals("true")) {
                LoadClient();
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
        //HBox hbox = addHBoxMain();
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


        Button buttonDelete = new Button("Delete");
        buttonDelete.setOnAction(e -> { Email selectedItem = inboxTable.getSelectionModel().getSelectedItem();
            int index = inboxTable.getSelectionModel().selectedIndexProperty().get();
            inboxTable.getItems().remove(selectedItem);
            //outputToServer.println(selectedItem);
            outputToServer.println("delete_mail");
            outputToServer.println(index);
            //deleteMail();
        });
        buttonDelete.setPrefSize(100, 20);


        Button buttonRefresh = new Button("Refresh");
        buttonRefresh.setOnAction(e -> refreshMail(inboxTable));
        buttonDelete.setPrefSize(100, 20);

        hbox.getChildren().addAll(buttonCompose, buttonDelete,buttonRefresh, buttonReply);

        border.setTop(hbox);


        // sender column and data binded to the mail class property

        inboxTable.setItems(oMail);

        inboxTable.setOnMouseClicked(e -> SelectedEmail(inboxTable,buttonReply));


        // border.setLeft(addVBoxMain());
        border.setLeft(addFlowPaneMain());
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

        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(100);   // Gap between nodes

        TextArea textArea = new TextArea(); //making a TexrArea object
        double height = 400; //making a variable called height with a value 400
        double width = 500;  //making a variable called height with a value 300
        HBox.setHgrow(textArea, Priority.ALWAYS);
        textArea.setWrapText(true);
        textArea.setPrefHeight(height);  //sets height of the TextArea to 400 pixels
        textArea.setPrefWidth(width);    //sets width of the TextArea to 300 pixels
        textArea.setText(emailToReply.getMessage());

        // Use a border pane as the root for scene
        BorderPane border = new BorderPane();

        border.setTop(hbox);
        TextField mailTo = new TextField();
        mailTo.setText(emailToReply.getFrom());
        Button buttonSend = new Button("Send");
        buttonSend.setPrefSize(100, 20);
        buttonSend.setOnAction(event -> sendEmail(mailTo.getText(), textArea.getText())) ;



        hbox.getChildren().addAll(mailTo, textArea, buttonSend );

        scene = new Scene(border, 700, 400);
        stage = new Stage();
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


    private void sendMail() {


        Stage stage;
        Scene scene;

        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(100);   // Gap between nodes

        TextArea textArea = new TextArea(); //making a TexrArea object
        double height = 400; //making a variable called height with a value 400
        double width = 500;  //making a variable called height with a value 300
        HBox.setHgrow(textArea, Priority.ALWAYS);
        textArea.setWrapText(true);
        textArea.setPrefHeight(height);  //sets height of the TextArea to 400 pixels
        textArea.setPrefWidth(width);    //sets width of the TextArea to 300 pixels

        // Use a border pane as the root for scene
        BorderPane border = new BorderPane();

        border.setTop(hbox);
        TextField mailTo = new TextField();
        Button buttonSend = new Button("Send");
        buttonSend.setPrefSize(100, 20);
        buttonSend.setOnAction(event -> sendEmail(mailTo.getText(), textArea.getText())) ;



        hbox.getChildren().addAll(mailTo, textArea, buttonSend );

        scene = new Scene(border, 700, 400);
        stage = new Stage();
        stage.setScene(scene);
        stage.show();

    }


    public void deleteMail(){
        outputToServer.println("delete_mail");

    }

    public void getInbox() {

        System.out.println("BEFORE SENDING INBOX REQUEST");
        outputToServer.println("get_inbox");
        System.out.println("AFTER SENDING INBOX REQUEST");
    }

    public void sendEmail(String to, String message)
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

    }


    public void quitApp() {
        outputToServer.println("close");
    }





    /*
     * Creates a VBox with a list of links for the left region
     */
//    private VBox addVBoxMain() {
//
//        VBox vbox = new VBox();
//        vbox.setPadding(new Insets(10)); // Set all sides to 10
//        vbox.setSpacing(8);              // Gap between nodes
//
//        Text title = new Text("Data");
//        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
//        vbox.getChildren().add(title);
//
//
//        return vbox;
//    }

     //Creates a horizontal flow pane with eight icons in four rows

    private FlowPane addFlowPaneMain() {

        FlowPane flow = new FlowPane();
        flow.setPadding(new Insets(5, 0, 5, 0));
        flow.setVgap(4);
        flow.setHgap(4);
        flow.setPrefWrapLength(170); // preferred width allows for two columns
        flow.setStyle("-fx-background-color: DAE6F3;");

        return flow;
    }

}

