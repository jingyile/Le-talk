import javafx.event.ActionEvent;
import login.Login;
import javafx.stage.Stage;
import register.Register;

public class Welcome {
    Register register = new Register();

    public static void main(String[] args) throws Exception {
        Welcome welcome = new Welcome();
        //welcome.changeWindow();
    }

    /*public void changeWindow() {
        try {
            register.showWindow();
            //sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
