package views;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Theme;


public class Admin implements EventHandler {

    private Stage stage;
    private Scene scene;
    private GridPane gridLayout1, gridLayout2;
    private Label label;
    private TextField nameField;
    private LogIn login;
    private VBox layout;
    private Text text;
    private Text text2;
    private String cssPath;
    private Button btnGoLogin, btnDodaj, btnRemove, btnUpdate, btnGetToUpdate, btnZwolnij;
    private TableView<Theme> tabela;
    private ObservableList<Theme> Themes;
    private Integer rememberId;

    public void setAdminScene(Stage primaryStage, ObservableList<Theme> Themes) {

        stage = new Stage();
        this.stage = primaryStage;
        this.Themes = Themes;

        text = new Text("Lista tematów projektów");
        text.setId("tableText");

        layout = new VBox(10);
        tabela = new TableView();

        TableColumn<Theme, Integer> colId = new TableColumn("Id:");
        colId.setCellValueFactory(new PropertyValueFactory("Id"));
        colId.setMinWidth(75);

        TableColumn<Theme, String> colName = new TableColumn("Name:");
        colName.setCellValueFactory(new PropertyValueFactory("Name"));
        colName.setMinWidth(400);

        TableColumn<Theme, Integer> colRez = new TableColumn("Rezerwacja:");
        colRez.setCellValueFactory(new PropertyValueFactory("Rezerwacja"));
        colRez.setMinWidth(275);

        TableColumn<Theme, Integer> colUzyt = new TableColumn("Id_uzytkownika:");
        colUzyt.setCellValueFactory(new PropertyValueFactory("Id_uzytkownika"));
        colUzyt.setMinWidth(275);

        tabela.getColumns().addAll(colId, colName, colRez, colUzyt);
        tabela.setItems(Themes);

        btnRemove = new Button("Usuń wybrane");
        btnRemove.addEventHandler(ActionEvent.ACTION, this);

        btnGetToUpdate = new Button("Pobierz temat do modyfikacji");
        btnGetToUpdate.addEventHandler(ActionEvent.ACTION, this);

        btnZwolnij = new Button("Zwolnij temat");
        btnZwolnij.addEventHandler(ActionEvent.ACTION, this);

        gridLayout1 = new GridPane();
        gridLayout1.setAlignment(Pos.CENTER);
        gridLayout1.setHgap(10);
        gridLayout1.setVgap(10);

        gridLayout1.add(btnGetToUpdate, 0, 0, 3, 1);
        gridLayout1.add(btnRemove, 3, 0, 3, 1);
        gridLayout1.add(btnZwolnij, 6, 0, 3, 1);

        btnDodaj = new Button("Dodaj");
        btnDodaj.addEventHandler(ActionEvent.ACTION, this);

        btnUpdate = new Button("Modyfikuj");
        btnUpdate.setDisable(true);
        btnUpdate.addEventHandler(ActionEvent.ACTION, this);

        gridLayout2 = new GridPane();
        gridLayout2.setAlignment(Pos.CENTER);
        gridLayout2.setHgap(10);
        gridLayout2.setVgap(10);

        label = new Label("Temat:");
        nameField = new TextField("");
        nameField.setPrefWidth(400);

        gridLayout2.add(label, 0, 0);
        gridLayout2.add(nameField, 1, 0, 3, 1);
        gridLayout2.add(btnDodaj, 4, 0);
        gridLayout2.add(btnUpdate, 5, 0);

        text2 = new Text("Zalogowany jako:  admin");
        text2.setId("tableText2");

        btnGoLogin = new Button("Strona logowania");
        btnGoLogin.addEventHandler(ActionEvent.ACTION, this);

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(25, 25, 25, 25));

        layout.getChildren().addAll(text, tabela, gridLayout1, gridLayout2, btnGoLogin, text2);

        scene = new Scene(layout, 800, 600);
        cssPath = this.getClass().getResource("loginstyle.css").toExternalForm();
        scene.getStylesheets().add(cssPath);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void handle(Event event) {

        Object source = event.getSource();
        
        if (source == btnGoLogin) 
        {
            login = new LogIn();
            login.setLoginScene(stage);
        } 
        else if (source == btnZwolnij && !tabela.getSelectionModel().isEmpty() && tabela.getSelectionModel().getSelectedItem().getRezerwacja().equals("TAK")) 
        {
            Theme selectedTheme;
            Integer id;

            selectedTheme = tabela.getSelectionModel().getSelectedItem();
            id = selectedTheme.getId();

            try {
                if (releaseTheme(id) != 0) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText("Temat został zwolniony!");

                    alert.showAndWait();

                    login = new LogIn();
                    setAdminScene(stage, login.setModel());
                }
            } catch (IOException ex) {
                Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
            }

        } 
        else if (source == btnGetToUpdate && !tabela.getSelectionModel().isEmpty()) 
        {
            Theme selectedTheme;
            String name;

            selectedTheme = tabela.getSelectionModel().getSelectedItem();
            name = selectedTheme.getName();
            rememberId = selectedTheme.getId();
            nameField.setText(name);
            btnDodaj.setDisable(true);
            btnUpdate.setDisable(false);
        } 
        else if (source == btnUpdate) 
        {
            try {
                if (updateTheme() != 0) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText("Temat został poprawiony!");

                    alert.showAndWait();

                    login = new LogIn();
                    setAdminScene(stage, login.setModel());
                }

            } catch (IOException ex) {
                Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
            }

        } 
        else if (source == btnRemove && !tabela.getSelectionModel().isEmpty()) 
        {
            Theme selectedTheme;
            Integer id_tematu;

            selectedTheme = tabela.getSelectionModel().getSelectedItem();
            id_tematu = selectedTheme.getId();

            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Potwierdzenie usunięcia");
            alert.setHeaderText("Czy chcesz usunąć temat: ?");
            alert.setContentText(selectedTheme.getName());

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {

                if (deleteSelectedTheme(id_tematu) != 0) {
                    alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText("Temat został usunięty!");

                    alert.showAndWait();

                    login = new LogIn();
                    setAdminScene(stage, login.setModel());
                }
            }

        } else if (source == btnDodaj) 
        {

            if (nameField.getText().isEmpty()) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("Nie wprowadziłeś treści tematu!!");
                alert.setContentText("Wprowadź jeszcze raz!");

                alert.showAndWait();
            } else {
                try {
                    if (dodajTemat() != 0) {

                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Information Dialog");
                        alert.setHeaderText(null);
                        alert.setContentText("Temat dodany poprawnie!");

                        alert.showAndWait();

                        login = new LogIn();
                        setAdminScene(stage, login.setModel());
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
// metoda dodajTemat odpowiada za dodawanie tematow przez administratora
    public Integer dodajTemat() throws MalformedURLException, IOException {

        Integer flaga = 0;
        String temat = nameField.getText();
        String str = "param=4&theme=" + temat;

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
            flaga = Integer.parseInt(line);
        }
        in.close();
        return flaga;
    }

    public Integer deleteSelectedTheme(Integer id) {

        Integer flaga = 0;
        try {
            String adres = "http://localhost:8080/ServerSide/ServerSide?param=5&id_tematu=" + id;
            URL url = new URL(adres);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(url.openStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                flaga = Integer.parseInt(line);
            }
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return flaga;

    }
// metoda modyfikujaca temat
    public Integer updateTheme() throws MalformedURLException, IOException {

        Integer flaga = 0;
        String temat = nameField.getText();
        String str = "param=6&theme=" + temat + "&id_tematu=" + rememberId;

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
            flaga = Integer.parseInt(line);

        }

        in.close();

        return flaga;
    }

    public Integer releaseTheme(Integer id) throws MalformedURLException, IOException {

        Integer flaga = 0;
        String str = "param=7&&id_tematu=" + id;

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
            flaga = Integer.parseInt(line);

        }

        in.close();

        return flaga;
    }

}
