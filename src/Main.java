import java.io.IOException;
import java.net.ServerSocket;

public class Main {

    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub

        Server s = new Server(8754);
        s.start();


    }

}
