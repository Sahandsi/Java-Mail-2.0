import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
        stage.setTitle("JavaFX Welcome");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(grid, 300, 275);
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

        Stage stage;
        Scene scene;

        // Use a border pane as the root for scene
        BorderPane border = new BorderPane();
        HBox hbox = addHBoxMain();

        border.setTop(hbox);
        border.setLeft(addVBoxMain());
        border.setLeft(addFlowPaneMain());

        scene = new Scene(border, 500, 500);
        stage = new Stage();
        stage.setScene(scene);
        stage.show();

    }

    private void sendMail() {

        Stage stage;
        Scene scene;

        // Use a border pane as the root for scene
        BorderPane border = new BorderPane();
        HBox hbox = addHBoxCompose();

        border.setTop(hbox);
        HBox hboxText = addHBoxText();
        border.setCenter(hboxText);

        // border.setLeft(addVBoxMain());
       // border.setLeft(addFlowPaneMain());

        scene = new Scene(border, 400, 400);
        stage = new Stage();
        stage.setScene(scene);
        stage.show();

    }

    public void getInbox() {
        System.out.println("BEFORE SENDING INBOX REQUEST");
        outputToServer.println("get_inbox");
        System.out.println("AFTER SENDING INBOX REQUEST");
    }

    public void getEmail() {
        outputToServer.println("send_email");
    }

    public void quitApp() {
        outputToServer.println("close");
    }



    /*
     * Creates an HBox with two buttons for the top region
     */

    private HBox addHBoxMain() {

        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);   // Gap between nodes
        hbox.setStyle("-fx-background-color: #336699;");

        Button buttonCompose = new Button("Compose");
        buttonCompose.setPrefSize(100, 20);
        buttonCompose.setOnAction(e -> sendMail());

        Button buttonDelete = new Button("Delete");
        buttonDelete.setPrefSize(100, 20);

        hbox.getChildren().addAll(buttonCompose, buttonDelete);

        return hbox;
    }


    private HBox addHBoxCompose() {

        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);   // Gap between nodes
        hbox.setStyle("-fx-background-color: #336699;");

        Button buttonCompose = new Button("Send");
        buttonCompose.setPrefSize(100, 20);

//        Button buttonDelete = new Button("Delete");
//        buttonDelete.setPrefSize(100, 20);

        TextField userTextField = new TextField();


        hbox.getChildren().addAll(userTextField,buttonCompose);

        return hbox;
    }

    private HBox addHBoxText() {

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

        hbox.getChildren().addAll(textArea);

        return hbox;
    }
    /*
     * Creates a VBox with a list of links for the left region
     */
    private VBox addVBoxMain() {

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10)); // Set all sides to 10
        vbox.setSpacing(8);              // Gap between nodes

        Text title = new Text("Data");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        vbox.getChildren().add(title);


        return vbox;
    }

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

