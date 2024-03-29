import javafx.application.Application;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.util.Duration;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;



public class Client extends Application     // For GUI
{

    File file;
    Media sound;
    MediaPlayer player;


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


        Label regName = new Label("Whats your name?");         // Label for the Register
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
           // Connection conn = null;
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

//        Connection connection = null;
//        try
//        {
//            connection = DriverManager.getConnection(
//                "jdbc:mysql://homepages.shu.ac.uk:3306/b5038121_db2",
//                                                    "b5038121","Sami1369");
//
//        }
//        catch(SQLException sqlEx)
//        {
//            System.out.println(
//                        "* Cannot connect to database! *");
//            System.exit(1);
//        }
//        return connection;
    }

    private void registerUser(String username, Label message)      // For registering the user to the SQL
    {
        String t = username;

        try
        {
            Connection con = getConnection();
            PreparedStatement posted = con.prepareStatement("INSERT INTO Users (Name) VALUES ('"+t+"')");
            posted.executeUpdate();
           // outputToServer.println(t);
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

        TableColumn<Email, String> ID = new TableColumn<>("USERID");
        ID.setPrefWidth(250);
        ID.setCellValueFactory(new PropertyValueFactory<>("userID"));
        ID.setVisible(false);

        TableColumn<Email, String> sentBy = new TableColumn<>("Sent By");
        sentBy.setPrefWidth(125);
        sentBy.setCellValueFactory(new PropertyValueFactory<>("from"));

        TableColumn<Email, String> mailSubject = new TableColumn<>("Email Subject");
        mailSubject.setPrefWidth(250);
        mailSubject.setCellValueFactory(new PropertyValueFactory<>("emailSubject"));


        TableColumn<Email, String> message = new TableColumn<>("Message");
        message.setPrefWidth(250);
        message.setCellValueFactory(new PropertyValueFactory<>("message"));

        TableColumn<Email, String> attachment = new TableColumn<>("Attachment");
        attachment.setPrefWidth(250);
        attachment.setCellValueFactory(new PropertyValueFactory<>("attachment"));



        TableView<Email> inboxTable = new TableView<>();

        inboxTable.getColumns().addAll(sentBy, mailSubject,message,attachment);          // Add columns of the sender and message


        Button buttonCompose = new Button("Compose");
        buttonCompose.setPrefSize(100, 20);
        buttonCompose.setOnAction(e -> sendMail());



        Button buttonReply = new Button("Reply");
        buttonReply.setPrefSize(100, 20);


        Button buttonDelete = new Button("Delete");
        buttonDelete.setOnAction(e -> { Email selectedItem = inboxTable.getSelectionModel().getSelectedItem();
            int index = selectedItem.getUserID();
            System.out.println(index);
            outputToServer.println("delete_mail");
            outputToServer.println(index);
            inboxTable.getItems().remove(selectedItem);
        });

        buttonDelete.setPrefSize(100, 20);


        Button buttonExit = new Button("Close App");
        buttonExit.setOnAction(e ->  quitApp());
        buttonExit.setPrefSize(100, 20);


        Button buttonRefresh = new Button("Refresh");
        buttonRefresh.setOnAction(e -> refreshMail(inboxTable));
        buttonRefresh.setPrefSize(100, 20);

        hbox.getChildren().addAll(buttonCompose, buttonDelete,buttonRefresh, buttonReply,buttonExit);

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
        seeEmail(email,inboxTable);
    }


    private void downloadMail(Email mail, Stage stage)
    {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(mail.getExtension() + "File",  "*." + mail.getExtension()));
        System.out.println(mail.getExtension());
        File file = fileChooser.showSaveDialog(stage);



        if(file != null) // prevenet null pointer exception
        {
            FileOutputStream outFile;
            try
            {
                outFile = new FileOutputStream(file.getName());
                try
                {
                    outFile.write(mail.getAttachmentFiles());


                }
                catch (IOException e1)
                {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                try
                {
                    outFile.close();
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            catch (FileNotFoundException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private void reply(Email emailToReply)
    {


        FileChooser fileChooser;
        Label attachmentName;
        File file = null;

        Stage stage;
        Scene scene;
        stage = new Stage();

        BorderPane border = new BorderPane();
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);   // Gap between nodes
        hbox.setStyle("-fx-background-color: #336699;");


        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

        attachmentName = new Label("Attachment: Empty");



        TextField mailTo = new TextField();
        TextField mailSubject = new TextField();

        Button buttonSend = new Button("Send");

        buttonSend.setPrefSize(100, 20);




        hbox.getChildren().addAll(mailTo,mailSubject,buttonSend,attachmentName);

        border.setTop(hbox);
        TextArea textArea = new TextArea();


        textArea.setText("\n\n------------------------------\n"+"------------------------------\n\n"+emailToReply.getMessage());
        mailTo.setText(emailToReply.getFrom());


        buttonSend.setOnAction(event -> sendEmail(mailTo.getText(), mailSubject.getText(),textArea.getText(),stage,attachmentName)) ;     // Sender column and data added to the mail class



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

    public void seeEmail(Email seeEmail,TableView<Email> inboxTable)
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

        Button buttonDowload = new Button("Download");
        buttonDowload.setPrefSize(100, 20);
        buttonDowload.setOnAction(e -> downloadMail(seeEmail, stage));

        Button buttonViewAttachment = new Button("View Attachment");
        buttonViewAttachment.setPrefSize(100, 20);


        hbox.getChildren().addAll(buttonClose,buttonViewAttachment, buttonDowload);
        border.setTop(hbox);

        TextArea textArea = new TextArea(); //making a TexrArea object
        textArea.setText(seeEmail.getMessage());

        border.setCenter(textArea);
        scene = new Scene(border, 500, 500);

        stage.setScene(scene);
        stage.show();
        buttonClose.setOnAction(e -> stageclose(stage));
        buttonViewAttachment.setOnAction(e -> btnViewAttachment_click(inboxTable));

    }

    private void stageclose (Stage stage)
    {
        stage.close();
    }

    private void sendMail()
    {

        FileChooser fileChooser;
        Label attachmentName;
        File file = null;

        Stage stage;
        Scene scene;
        stage = new Stage();

        BorderPane border = new BorderPane();
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);   // Gap between nodes
        hbox.setStyle("-fx-background-color: #336699;");


        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

        attachmentName = new Label("Attachment: Empty");

        System.out.println(System.getProperty("user.dir"));

        TextField mailTo = new TextField();
        TextField mailSubject = new TextField();

        Button buttonSend = new Button("Send");

        buttonSend.setPrefSize(100, 20);

        Button buttonAttach = new Button("Attach");

        buttonAttach.setPrefSize(100, 20);


        hbox.getChildren().addAll(mailTo,mailSubject,buttonSend,buttonAttach,attachmentName);

        border.setTop(hbox);
        TextArea textArea = new TextArea();


        // event handler
        buttonAttach.setOnAction(event ->buttonAttach(fileChooser, stage, attachmentName));
        buttonSend.setOnAction(event -> sendEmail(mailTo.getText(), mailSubject.getText(),textArea.getText(),stage, attachmentName));
        mailTo.setPromptText("Recipient Mail Address");
        textArea.setPromptText("Enter text");
        border.setCenter(textArea);
        scene = new Scene(border, 800, 500);

        stage.setScene(scene);
        stage.show();


    }

    public void sendEmail(String to, String mailSubject,String message,Stage stage,Label attachmentname)
    {

        stage.close();

        FileInputStream fileIn;
        int intFileLen;
        byte[] byteArray = null;


        String filename = attachmentname.getText();







        if (to.isEmpty() || message.isEmpty()) // Check if the parameters are empty
        {
            System.out.println("One of these fields are empty");
        }
        else
        {
            outputToServer.println("send_email");
            outputToServer.println(to);
            outputToServer.println(mailSubject);
            outputToServer.println(message);
            outputToServer.println(filename);
            String serverRequest = inputFromServer.nextLine();
            if (serverRequest.equals("attachment"))
            {
                try
                {
                    fileIn = new FileInputStream(filename); // set up input stream
                    intFileLen = (int)(new File(filename)).length(); // cast the file length to an int
                    byteArray = new byte[intFileLen];

                    try
                    {
                        fileIn.read(byteArray);
                        fileIn.close();
                        ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
                        objectOutput.writeObject(byteArray);
                        objectOutput.flush(); // flush the stream
                        System.out.println(byteArray);



                        // create another with number -1 at the end
                        String newAttachmentName = filename.substring(0, filename.indexOf('.')); // before the extension .
                        newAttachmentName = newAttachmentName  + "-1" + "." + filename.substring(filename.indexOf('.') + 1); // -1.extensionNaem
                        System.out.println(newAttachmentName);

                        FileOutputStream outFile = new FileOutputStream(newAttachmentName);
                        outFile.write(byteArray);
                        outFile.close();
                        outputToServer.println(newAttachmentName);

                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
            }

            return;


        }

    }

    public void quitApp()
    {
        outputToServer.println("close");
        System.exit(0);
    }


    private void buttonAttach(FileChooser fileChooser, Stage stage, Label attachmentName)
    {

        File file = fileChooser.showOpenDialog(stage);
        // check if file has been selected
        if (file != null)
        {
            attachmentName.setText(file.getName());
        }
    }

    public void btnViewAttachment_click(TableView<Email> inboxTable)
    {

        String attachmentname = inboxTable.getSelectionModel().getSelectedItem().getAttachment();

//        System.out.println("hi my name is"+attachmentname);

        String extension = "";

        extension = attachmentname.substring(attachmentname.lastIndexOf('.') + 1);

        if (extension.equals("mp3"))
        {
            playMedia(attachmentname);
        }

        if (extension.equals("wav"))
        {
            playMedia(attachmentname);
        }
         if (extension.equals("gif"))
        {
            displayImage(attachmentname);
        }
        else if (extension.equals("flv"))
        {
            playMedia(attachmentname);
        }
        else if (extension.equals("txt"))
        {
            showText(attachmentname);
        }
    }


    public void displayImage(String attachmentname)
    {
        String filename = attachmentname;
        Stage primaryStage = new Stage();
        Image image;
        ImageView imageView;
        BorderPane pane;
        Scene scene;


        image = new Image(new File(filename).toURI().toString());


        imageView = new ImageView(image);
        imageView.setFitWidth(500);
        imageView.setFitHeight(350);
        imageView.setPreserveRatio(true);
        pane = new BorderPane();
        pane.setTop(imageView);

        scene = new Scene(pane);


        primaryStage.setScene(scene);
        primaryStage.setTitle("Image Demo");
        primaryStage.show();
}





    public void playMedia(String attachmentname)
    {


        MediaView viewer;
        Button startBtn;
        Button pauseBtn;
        Button stopBtn;
        Button darkBtn;
        HBox hbox;


        Stage primaryStage = new Stage();


        VBox vbox1,vbox2,vbox3,vbox4;

        vbox1 = new VBox();
        vbox2 = new VBox();
        vbox3 = new VBox();
        vbox4 = new VBox();


        viewer = new MediaView(player);
        viewer.setPreserveRatio(true);


        startBtn = new Button("Start");
        startBtn.setOnAction(event -> resumeSound());
        pauseBtn = new Button("Pause");
        pauseBtn.setOnAction(event->pauseSound());
        stopBtn = new Button("Stop");
        stopBtn.setOnAction(event->stopSound());

        darkBtn = new Button("Enable Dark Mode");
        darkBtn.setOnAction(event -> darkMode(vbox2,vbox3,vbox4));
        hbox = new HBox();



        vbox1.getChildren().addAll(vbox2,vbox3,vbox4);
        vbox4.getChildren().addAll(hbox);

        hbox.getChildren().addAll(pauseBtn,startBtn, stopBtn,darkBtn);
        hbox.setAlignment(Pos.BOTTOM_CENTER);
        BorderPane.setAlignment(hbox, Pos.BOTTOM_CENTER);

        hbox.setPadding(new Insets(10, 50, 10, 50));
        hbox.setSpacing(10);



                startBtn.setStyle(
                "-fx-background-radius: 5em; " +
                        "-fx-min-width: 60px; " +
                        "-fx-min-height: 60px; " +
                        "-fx-max-width: 60px; " +
                        "-fx-max-height: 60px;"
        );
                pauseBtn.setStyle(
                "-fx-background-radius: 5em; " +
                        "-fx-min-width: 60px; " +
                        "-fx-min-height: 60px; " +
                        "-fx-max-width: 60px; " +
                        "-fx-max-height: 60px;"
        );
                stopBtn.setStyle(
                "-fx-background-radius: 5em; " +
                        "-fx-min-width: 60px; " +
                        "-fx-min-height: 60px; " +
                        "-fx-max-width: 60px; " +
                        "-fx-max-height: 60px;"
        );
        darkBtn.setStyle(
                "-fx-background-radius: 5em; " +
                        "-fx-min-width: 60px; " +
                        "-fx-min-height: 60px; " +
                        "-fx-max-width: 60px; " +
                        "-fx-max-height: 60px;"
        );




        file = new File(attachmentname);
        sound = new Media(file.toURI().toString());


        System.out.println("hi my name is media"+sound);

        double MIN_CHANGE = 0.5 ;

                player = new MediaPlayer(sound);
       MediaView mediaView = new MediaView(player);


        Slider slider = new Slider();
        player.totalDurationProperty().addListener((obs, oldDuration, newDuration) -> slider.setMax(newDuration.toSeconds()));

        vbox2.getChildren().add(mediaView);
        vbox2.setAlignment(Pos.CENTER);
        vbox2.setPadding(new Insets(10, 0, 0, 0));
        vbox3.setAlignment(Pos.CENTER);
        vbox3.getChildren().add(slider);
        vbox4.setStyle("-fx-background-color: #336699;");


        BorderPane root = new BorderPane(vbox3, vbox2, null, vbox4, null);



        slider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (! isChanging) {
                player.seek(Duration.seconds(slider.getValue()));
            }
        });

        slider.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (! slider.isValueChanging()) {
                double currentTime = player.getCurrentTime().toSeconds();
                if (Math.abs(currentTime - newValue.doubleValue()) > MIN_CHANGE) {
                    player.seek(Duration.seconds(newValue.doubleValue()));
                }
            }
        });

        player.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (! slider.isValueChanging()) {
                slider.setValue(newTime.toSeconds());
            }
        });

        Scene scene = new Scene(root, 740, 380);
        primaryStage.setScene(scene);
        primaryStage.show();

        player.play();

        primaryStage.setOnCloseRequest((ae) ->player.pause());


    }


    public void darkMode(VBox vbox2,VBox vbox3,VBox vbox4)
    {

        vbox2.setStyle("-fx-background-color: #000000;");
        vbox3.setStyle("-fx-background-color: #000000;");
        vbox4.setStyle("-fx-background-color: #000000;");

    }

    public void resumeSound()
    {
        player.play();
    }

    public void pauseSound()
    {
        player.pause();
    }

    public void stopSound()
    {
        player.stop();
    }

    public void showText(String attachmentname) {


        Stage stage;
        Scene scene;
        stage = new Stage();


        BorderPane border = new BorderPane();
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);   // Gap between nodes
        hbox.setStyle("-fx-background-color: #336699;");



        TextArea textArea = new TextArea(); //making a TexrArea object



        try {



            Scanner input = new Scanner(attachmentname);
            File file = new File(input.nextLine());
            input = new Scanner(file);


            while (input.hasNextLine())
            {
                String line = input.nextLine();
               // System.out.println(line);
                textArea.appendText("\n" + input.nextLine());



//////////////////////saving the textfile
//                PrintWriter out = new PrintWriter("Text-Attachment.txt");
//                out.println(line);
//                out.close();
///////////////////////////////////////////////////




        }
            input.close();

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }




        border.setTop(hbox);
        textArea.setEditable(false);
        border.setCenter(textArea);
        scene = new Scene(border, 500, 500);

        stage.setScene(scene);
        stage.show();


    }

}

