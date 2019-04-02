package login;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.text.html.ImageView;
import java.awt.*;

public class Login extends Application {
    Stage stage=new Stage();
        public void start(Stage primaryStage) throws Exception {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            primaryStage.setTitle("Hello World");
            primaryStage.setScene(new Scene(root, 500, 300));
            primaryStage.show();
        }
    public void close(Stage primaryStage) throws Exception {
            primaryStage.hide();
        primaryStage.close();
    }
    public static void main(String[] args) {
        launch(args);
    }
    public void  showWindow() throws Exception {
        start(stage);
    }
    public void closeWindow() throws Exception{
            close(stage);
    }
}
