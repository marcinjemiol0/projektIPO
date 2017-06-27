package views;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Theme;

public class Visitors implements EventHandler{
    
    private Stage stage;
    private Scene scene;
    private LogIn login;
    private VBox layout;
    private Text text;
    private String cssPath;
    private Button btnGoLogin;
    private TableView<Theme> tabela;
    
    public void setVisitorsScene(Stage primaryStage,ObservableList<Theme> Themes)
    {
        stage=new Stage();
        stage=primaryStage;
        
        layout = new VBox(40);
        tabela=new TableView();
        
        TableColumn<Theme,Integer> colId = new TableColumn("Id:");
        colId.setCellValueFactory(new PropertyValueFactory("Id"));
        colId.setMinWidth(75);
        
        TableColumn<Theme,String> colName = new TableColumn("Name:");
        colName.setCellValueFactory(new PropertyValueFactory("Name"));
        colName.setMinWidth(400);
        
        TableColumn<Theme,Integer> colRez = new TableColumn("Zarezerwowany:");
        colRez.setCellValueFactory(new PropertyValueFactory("Rezerwacja"));
        colRez.setMinWidth(275);
        
        TableColumn<Theme, Integer> colUzyt = new TableColumn("Id_uzytkownika:");
        colUzyt.setCellValueFactory(new PropertyValueFactory("Id_uzytkownika"));
        colUzyt.setMinWidth(275);
        
        tabela.getColumns().addAll(colId,colName,colRez,colUzyt);
        tabela.setItems(Themes);
        tabela.setEditable(false);
        
        text=new Text("Lista tematów projektów");
        text.setId("tableText");
        
        btnGoLogin=new Button("Strona logowania");
        btnGoLogin.addEventHandler(ActionEvent.ACTION, this);
        
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(25,25,25,25));
        
        layout.getChildren().addAll(text,tabela,btnGoLogin);
               
        
        scene = new Scene(layout, 800, 600);
        cssPath = this.getClass().getResource("loginstyle.css").toExternalForm();
        scene.getStylesheets().add(cssPath);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void handle(Event event) {
       
        login=new LogIn();
        login.setLoginScene(stage);
        
    }

    
    
}
