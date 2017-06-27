package views;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
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


public class Users implements EventHandler {

    private Stage stage;
    private Scene scene;
    private GridPane gridLayout;
    private LogIn login;
    private VBox layout;
    private Text text;
    private Text text2;
    private String cssPath;
    private Button btnGoLogin, btnRezerwuj;
    private TableView<Theme> tabela;
    private ObservableList<Theme> Themes;
    private Integer indeks;

    public void setUsersScene(Stage primaryStage, ObservableList<Theme> Themes, Integer indeks) {

        stage = new Stage();
        this.stage = primaryStage;
        this.Themes = Themes;
        this.indeks = indeks;

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
        tabela.setEditable(false);

        text = new Text("Lista tematów projektów");
        text.setId("tableText");

        text2 = new Text("Zalogowany jako: " + indeks);
        text2.setId("tableText2");

        gridLayout = new GridPane();
        gridLayout.setAlignment(Pos.CENTER);
        gridLayout.setHgap(10);
        gridLayout.setVgap(10);
        
        btnGoLogin = new Button("Strona logowania");
        btnGoLogin.addEventHandler(ActionEvent.ACTION, this);
     
        btnRezerwuj = new Button("Rezerwuj wybrany temat");
        btnRezerwuj.addEventHandler(ActionEvent.ACTION, this);

        gridLayout.add(btnRezerwuj, 0, 0,3,1);
        gridLayout.add(btnGoLogin,3,0,3,1);

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(25, 25, 25, 25));

        layout.getChildren().addAll(text, tabela, gridLayout, text2);

        scene = new Scene(layout, 800, 600);
        cssPath = this.getClass().getResource("loginstyle.css").toExternalForm();
        scene.getStylesheets().add(cssPath);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void handle(Event event) {

        Object source = event.getSource();
        if (source == btnGoLogin) {
            login = new LogIn();
            login.setLoginScene(stage);
        }
        else if (source == btnRezerwuj && tabela.getSelectionModel().isEmpty())
        {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("Nie wybrałeś tematu, który chcesz zarezerwować!");

                alert.showAndWait();  
                
        }
        else if (source == btnRezerwuj && !tabela.getSelectionModel().isEmpty()) 
        {

            Theme selectedTheme;
            selectedTheme = tabela.getSelectionModel().getSelectedItem();
            Integer id_tematu = selectedTheme.getId();

            if ("TAK".equals(Themes.get(id_tematu).getRezerwacja())) 
            {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("Temat jest już zarezerwowany!!");
                alert.setContentText("Wybierz inny temat!");

                alert.showAndWait();
            } 
            else if (rezerwujTemat(id_tematu) != 0) 
            {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("Temat zarezerwowany!");

                alert.showAndWait();

                login = new LogIn();
                setUsersScene(stage, login.setModel(), indeks);

            }

        }
    }
// metoda rezerwuj temat pozwalajaca zarezerwowac temat uzytkownikowi
    public Integer rezerwujTemat(Integer id_tematu) {

        Integer flaga = 0;
        try {
            URL url = new URL("http://localhost:8080/ServerSide/ServerSide?param=3&id_tematu=" + id_tematu + "&id_uzytkownika=" + indeks);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(url.openStream(),"UTF-8"));

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

}
