package views;


import javafx.application.Application;
import javafx.stage.Stage;



public class Views extends Application {

    
    private LogIn login;

    @Override
    public void start(Stage primaryStage) {
      
        login = new LogIn();
        login.setLoginScene(primaryStage);  
        
    }

    public static void main(String[] args) {
        launch(args);
    }

}
