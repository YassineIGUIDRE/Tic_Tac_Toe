import java.io.IOException;

public class main1 {

    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub


        Thread th = new Thread(new Interface());
        th.start();

    }

}
