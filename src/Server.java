import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class Server extends Thread  {
    private ArrayList <Socket> ClientsOutStreams= new ArrayList<>();
    private ArrayList <Socket> arrS= new ArrayList<>();
    private HashMap <Socket , Socket> Players = new HashMap<>();
    private ServerSocket Ss ;
    private Socket s,newPlayer ;

    public  Server(int port)  throws IOException {
        Ss = new ServerSocket(port);
    }

    @Override
    public void run(){
        listen();
    }

    private void listen(){
        try {
            while (true) {
                s = Ss.accept();
                arrS.add(s);

                ServerThread ST = new ServerThread(s, this);
                ST.start();
                if(arrS.size() >= 2){
                    MatchingP();
                }
                ClientsOutStreams.add(s);
                System.out.println( ClientsOutStreams);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void ChangePlayer(String sc)  {
            try {
                Players.forEach((socket, socket2) -> {
                    if(Integer.toString(socket.getPort()).equals(sc)){
                        arrS.add(socket);
                        arrS.add(socket2);
                    }else if(Integer.toString(socket2.getPort()).equals(sc)){
                        arrS.add(socket);
                        arrS.add(socket2);
                    }
                });
                //todo
                Players.remove(arrS.get(1));
                MatchingP();
            }catch (Exception e){
                System.out.println(e);
            }
    }

    private void MatchingP() {
        Players.put(arrS.get(0),arrS.get(1));
        String[] mssg ;
        mssg = "ResetGameYes".split(":");
        MessageReplay(mssg,arrS.get(0));
        MessageReplay(mssg,arrS.get(1));
        arrS.remove(0);
        arrS.remove(0);
    }

    public void ReplayGame(String sr) {
        try {
            String[] mssg ;
            mssg = sr.split(":");
            Players.forEach((socket, socket2) -> {
                if(Integer.toString(socket.getPort()).equals(mssg[1])){
                    MessageReplay(mssg, socket2);

                }else if(Integer.toString(socket2.getPort()).equals(mssg[1])){
                    MessageReplay(mssg, socket);
                }
            });
            /*Players.remove(arrS.get(1),arrS.get(2));
            Players.put(arrS.get(0),arrS.get(1));
            arrS.remove(0);
            arrS.remove(0);*/
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private void MessageReplay(String[] mssg, Socket socket) {
        if(mssg[0].equals("RepalyGameYes")){

            String  msgReplay = "RepalyGameYes";
            try {
                DataOutputStream Dos = new DataOutputStream(socket.getOutputStream());
                Dos.writeUTF(msgReplay);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else if(mssg[0].equals("RepalyGameConf")){

            String  msgReplay = "RepalyGameConf";
        try {
            DataOutputStream Dos = new DataOutputStream(socket.getOutputStream());
            Dos.writeUTF(msgReplay);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        if(mssg[0].equals("ResetGameYes")){
               System.out.println("hnaaaaaaaaaa");
            String  msgReplay = "ResetGameYes";
            try {
                DataOutputStream Dos = new DataOutputStream(socket.getOutputStream());
                Dos.writeUTF(msgReplay);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else if(mssg[0].equals("ResetGameConf")){

            String  msgReplay = "ResetGameConf";
            try {
                DataOutputStream Dos = new DataOutputStream(socket.getOutputStream());
                Dos.writeUTF(msgReplay);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void SendToAll(String mssg ) {
        synchronized (Players) {

                     String[] nw = mssg.split(":");
                     Players.forEach((socket, socket2) -> {
                         if(Integer.toString(socket.getPort()).equals(nw[3])){
                             System.out.println("hnaa1");
                             try {
                                 DataOutputStream Dos = new DataOutputStream(socket2.getOutputStream());
                                 Dos.writeUTF(mssg);
                             } catch (IOException e) {
                                 e.printStackTrace();
                             }
                         }else if (Integer.toString(socket2.getPort()).equals(nw[3])){
                             System.out.println("hnaa2");
                             try {
                                 DataOutputStream Dos = new DataOutputStream(socket.getOutputStream());
                                 Dos.writeUTF(mssg);
                             } catch (IOException e) {
                                 e.printStackTrace();
                             }
                         }
                     });

        }
    }

    public void DeleteClient(Socket s){
        ClientsOutStreams.remove(ClientsOutStreams.indexOf(s));
        arrS.remove(s);
        //// TODO: 18/03/2021
        final Socket[] socketRem = {null};

        Players.forEach((socket, socket2) -> {
            System.out.println("hnaaErr00");
            if(socket.equals(s)){
                System.out.println("hnaaErr1");
                arrS.remove(Players.get(socket));
                arrS.add(Players.get(socket));
                socketRem[0] = socket;
            }else if (socket2.equals(s)){
                System.out.println("hnaaErr2");
                arrS.remove(Players.get(socket));
                arrS.add(Players.get(socket));
                socketRem[0] = socket;
                System.out.println(arrS.size());

            }

        });
        Players.remove(socketRem[0]);

    }

}