import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerThread extends  Thread {
    private Server S;
    private Socket Ss;

    public ServerThread(Socket Ss,Server S){
        this.S = S;
        this.Ss = Ss;
        System.out.println( "Connection established with: " + this.S );
    }

    public  void run(){
        String [] mssg;
        try {
            DataInputStream DIS = new DataInputStream(Ss.getInputStream());
            while (true){
                String msg = DIS.readUTF();
                mssg = msg.split(":");
                if(mssg[0].equals("changePlayer")){
                    S.ChangePlayer(mssg[1]);
                }else if(mssg[0].equals("RepalyGameConf")){
                    S.ReplayGame(msg);
                }else if(mssg[0].equals("RepalyGameYes")){
                    S.ReplayGame(msg);
                }else if(mssg[0].equals("ResetGameConf")){
                    S.ReplayGame(msg);
                }else if(mssg[0].equals("ResetGameYes")){
                    S.ReplayGame(msg);
                }else{

                    S.SendToAll(msg);
                }
            }
        } catch (IOException e) {

            e.printStackTrace();
        } finally {
            S.DeleteClient(Ss);
        }

    }
}