import java.io.IOException;

public class main2 {

    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub


        Thread th = new Thread(new Interface());
        th.start();

    }
}
