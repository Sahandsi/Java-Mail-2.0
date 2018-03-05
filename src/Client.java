import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.*;
import java.io.IOException;
import java.io.*;
import java.net.*;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;


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


        // indefinate
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


        // Scene scene;
        VBox vbox;
        Stage stage;
        Scene scene;
//
//        Button inbox = new Button("Inbox");
//        Button email = new Button("Email");
//        Button quit = new Button("Quit");
//
//        inbox.setOnAction(e -> getInbox());
//        email.setOnAction(e -> getEmail());
//        quit.setOnAction(e -> quitApp());
//
//        // add buttons to layout
//        vbox = new VBox();
//        vbox.getChildren().add(inbox);
//        vbox.getChildren().add(email);
//        vbox.getChildren().add(quit);


// Use a border pane as the root for scene
        BorderPane border = new BorderPane();
        HBox hbox = addHBox();
        border.setTop(hbox);
        border.setLeft(addVBox());

// Add a stack to the HBox in the top region
        //addStackPane(hbox);

// To see only the grid in the center, uncomment the following statement
// comment out the setCenter() call farther down
//        border.setCenter(addGridPane());

// Choose either a TilePane or FlowPane for right region and comment out the
// one you aren't using
        border.setLeft(addFlowPane());
//        border.setRight(addTilePane());

// To see only the grid in the center, comment out the following statement
// If both setCenter() calls are executed, the anchor pane from the second
// call replaces the grid from the first call
       // border.setCenter(addAnchorPane(addGridPane()));

        // Scene scene = new Scene(border);
        // stage.setScene(scene);
        // stage.setTitle("Layout Sample");

        scene = new Scene(border, 500, 500);
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

    private HBox addHBox() {

        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);   // Gap between nodes
        hbox.setStyle("-fx-background-color: #336699;");

        Button buttonCompose = new Button("Compose");
        buttonCompose.setPrefSize(100, 20);

        Button buttonDelete = new Button("Delete");
        buttonDelete.setPrefSize(100, 20);

        hbox.getChildren().addAll(buttonCompose, buttonDelete);

        return hbox;
    }

    /*
     * Creates a VBox with a list of links for the left region
     */
    private VBox addVBox() {

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10)); // Set all sides to 10
        vbox.setSpacing(8);              // Gap between nodes

        Text title = new Text("Data");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        vbox.getChildren().add(title);


        return vbox;
    }

    /*
     * Uses a stack pane to create a help icon and adds it to the right side of an HBox
     *
     * @param hb HBox to add the stack to
     */
//    private void addStackPane(HBox hb) {
//
//        StackPane stack = new StackPane();
//        Rectangle helpIcon = new Rectangle(30.0, 25.0);
//        helpIcon.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
//                new Stop[]{
//                        new Stop(0, Color.web("#4977A3")),
//                        new Stop(0.5, Color.web("#B0C6DA")),
//                        new Stop(1, Color.web("#9CB6CF")),}));
//        helpIcon.setStroke(Color.web("#D0E6FA"));
//        helpIcon.setArcHeight(3.5);
//        helpIcon.setArcWidth(3.5);
//
//        Text helpText = new Text("?");
//        helpText.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
//        helpText.setFill(Color.WHITE);
//        helpText.setStroke(Color.web("#7080A0"));
//
//        stack.getChildren().addAll(helpIcon, helpText);
//        stack.setAlignment(Pos.CENTER_RIGHT);
//        // Add offset to right for question mark to compensate for RIGHT
//        // alignment of all nodes
//        StackPane.setMargin(helpText, new Insets(0, 10, 0, 0));
//
//        hb.getChildren().add(stack);
//        HBox.setHgrow(stack, Priority.ALWAYS);
//
//    }


     //Creates a horizontal flow pane with eight icons in four rows

    private FlowPane addFlowPane() {

        FlowPane flow = new FlowPane();
        flow.setPadding(new Insets(5, 0, 5, 0));
        flow.setVgap(4);
        flow.setHgap(4);
        flow.setPrefWrapLength(170); // preferred width allows for two columns
        flow.setStyle("-fx-background-color: DAE6F3;");

        return flow;
    }

    /*
     * Creates an anchor pane using the provided grid and an HBox with buttons
     *
     * @param grid Grid to anchor to the top of the anchor pane
     */

}

