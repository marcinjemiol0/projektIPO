package views;

import java.net.*;
import java.io.*;
import java.nio.charset.Charset;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import model.Theme;


public class LogIn implements EventHandler {

    private Stage stage;
    private Scene scene;
    private GridPane grid;
    private Text sceneTitle;
    private Label userIndeksLabel, surnameLabel;
    private TextField userIndeksField, surnameField;
    private Button btnLogIn;
    private Button btnLookingThemes;
    private HBox hBoxPane;
    private String cssPath;
    private Visitors visitor;
    private Users user;
    private Admin admin;
    private Integer isAdmin;

    public ObservableList<Theme> Themes = FXCollections.observableArrayList();

    public void setLoginScene(Stage primaryStage) {

        stage = primaryStage;
        stage.setTitle("System rezerwacji projektów");
        stage.setMaximized(false);
        stage.resizableProperty().set(false);
        stage.setHeight(600);
        stage.setWidth(800);

        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        sceneTitle = new Text("Witamy");
        sceneTitle.setId("Witamy");
        grid.add(sceneTitle, 0, 0, 2, 1);

        userIndeksLabel = new Label("Indeks użytkownika:");
        grid.add(userIndeksLabel, 0, 1);

        userIndeksField = new TextField();
        grid.add(userIndeksField, 1, 1);

        surnameLabel = new Label("Nazwisko:");
        grid.add(surnameLabel, 0, 2);

        surnameField = new TextField();
        grid.add(surnameField, 1, 2);

        btnLogIn = new Button("Zaloguj");
        btnLookingThemes = new Button("Przeglądaj tematy");
        hBoxPane = new HBox(10);
        hBoxPane.setAlignment(Pos.BOTTOM_RIGHT);
        hBoxPane.getChildren().addAll(btnLogIn, btnLookingThemes);
        grid.add(hBoxPane, 1, 4);

        //obsługa zdarzeń kliknięcia w przycisk loguj i przeglądaj tematy
        btnLookingThemes.addEventHandler(ActionEvent.ACTION, this);
        btnLogIn.addEventHandler(ActionEvent.ACTION, this);

        scene = new Scene(grid, 800, 600);
        cssPath = this.getClass().getResource("loginstyle.css").toExternalForm();
        scene.getStylesheets().add(cssPath);
        stage.setScene(scene);
        stage.show();

    }

    @Override
    public void handle(Event event) {

        Object source = event.getSource();

        if (source == btnLookingThemes) {

            setModel();
            visitor = new Visitors();
            visitor.setVisitorsScene(stage, Themes);

        } else if (source == btnLogIn) {

            int indeks = checkPerson();
            if (indeks != 0) {

                if (isAdmin == 0) {
                    user = new Users();
                    user.setUsersScene(stage, setModel(), indeks);
                } else {
                    admin = new Admin();
                    admin.setAdminScene(stage, setModel());

                }

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("Błędne dane logowania!!");
                alert.setContentText("Spróbuj jeszcze raz lub przejdź do przeglądania listy tematów!");

                alert.showAndWait();
            }
        }
    }

    public ObservableList<Theme> setModel() {
        try {
            URL url = new URL("http://localhost:8080/ServerSide/ServerSide?param=1");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(url.openStream(), "UTF-8"));
            String line;
            Themes.clear();
            while ((line = in.readLine()) != null) {
                String id = line.substring(line.indexOf("<Id>") + 4, line.indexOf("</Id>"));
                String name = line.substring(line.indexOf("<Name>") + 6, line.indexOf("</Name>"));
                String flaga = line.substring(line.indexOf("<Rezerwacja>") + 12, line.indexOf("</Rezerwacja>"));
                String uzytkownik = line.substring(line.indexOf("<Uzytkownik>") + 12, line.indexOf("</Uzytkownik>"));

                Themes.add(new Theme(Integer.parseInt(id), name, flaga, Integer.parseInt(uzytkownik)));
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Themes;
    }

    public int checkPerson() {
        int id = 0;
        String indeks;
        String surname;
        indeks = userIndeksField.getText();
        surname = surnameField.getText();
        isAdmin = 0;
        try {

            String str = "param=2&name=" +indeks+"&surname="+surname;

            Charset charset = Charset.forName("UTF8");
            URL url = new URL("http://localhost:8080/ServerSide/ServerSide");
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("accept-charset", "UTF-8");
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded ; charset=utf-8");
            conn.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");

            writer.write(str);
            writer.flush();
            writer.close();

            BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), charset));
            
            String line;
            while ((line = in.readLine()) != null) {
                id = Integer.parseInt(line.substring(line.indexOf("<Indeks>") + 8, line.indexOf("</Indeks>")));
                isAdmin = Integer.parseInt(line.substring(line.indexOf("<Admin>") + 7, line.indexOf("</Admin>")));
            }
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return id;
    }
}
