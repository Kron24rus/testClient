package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by kron on 12.10.15.
 */
public class ClientApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Client");
        Parent root = FXMLLoader.load(getClass().getResource("/client.fxml"));
        Scene myScene = new Scene(root, 300, 275);
        primaryStage.setScene(myScene);
        primaryStage.show();
    }
}
