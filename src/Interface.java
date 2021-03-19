import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class Interface extends JFrame implements ActionListener,Runnable{
    private JButton btnChange,btnRep,btnRes,btnExit;
    private JLabel lblN1,lblN2,lblN3,lblN4,lblN5,lblN6;
    private JRadioButton radP1,radP2;
    private JPanel Panel1,Panel11,Panel12,Panel2,Panel21,Panel22,Panel3,Panel31,Panel32,Panel4;
    private int dim = 6;
    private JButton [][] btn = new JButton[dim+5][dim+5];
    private Socket s;
    private int start = 2;
    private int end = 2;
    private String winner = "";
    private long clicked = 0;
    private String choix = "X";
    private Integer p1Score = 0;
    private Integer p2Score = 0;

    public Interface() {
        btnChange = new JButton("Change Player");
        btnRep = new JButton("Replay");
        btnRes = new JButton("Reset");
        btnExit = new JButton("EXIT");

        btnExit.addActionListener(this);
        btnRes.addActionListener(this);
        btnRep.addActionListener(this);
        btnChange.addActionListener(this);

        lblN1 = new JLabel("Player 1");
        lblN2 = new JLabel("Player 2");
        lblN3 = new JLabel("0");
        lblN4 = new JLabel("0");
        lblN5 = new JLabel("Player 1");
        lblN6 = new JLabel("Player 2");

        radP1 = new JRadioButton("",true);
        radP2 = new JRadioButton("");
        ButtonGroup btnGroup;
        btnGroup = new ButtonGroup();
        btnGroup.add(radP1);
        btnGroup.add(radP2);

        Panel1 = new JPanel();
        Panel1.add(lblN5);
        Panel1.add(radP1);
        Panel1.add(radP2);
        Panel1.add(lblN6);

        Panel2 = new JPanel();
        Panel2.setBorder(BorderFactory.createTitledBorder("'Score'"));
        Panel2.setPreferredSize(new Dimension(200,350));
        Panel2.setLayout(new GridBagLayout());

        GroupLayout groupLayout = new GroupLayout(Panel2);
        Panel2.setLayout(groupLayout);

        groupLayout.setAutoCreateGaps(true);
        groupLayout.setAutoCreateContainerGaps(true);
        groupLayout.preferredLayoutSize(Panel2);

        groupLayout.setHorizontalGroup(
                groupLayout.createSequentialGroup()
                    .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(lblN1)
                            .addComponent(lblN3)
                    )
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,GroupLayout.DEFAULT_SIZE, 25)
                    .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(lblN2)
                            .addComponent(lblN4)
                    )
        );

        groupLayout.setVerticalGroup(
                groupLayout.createSequentialGroup()
                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(lblN1)
                    .addComponent(lblN2)
                )
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,GroupLayout.DEFAULT_SIZE, 25)
                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(lblN3)
                    .addComponent(lblN4)
                )
        );

        Panel3 = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Panel3.add(btnChange,gbc);Panel3.add(btnRep,gbc);Panel3.add(btnRes,gbc);Panel3.add(btnExit,gbc);
        Panel3.setBorder(BorderFactory.createTitledBorder("'Menu'"));
        Panel3.setPreferredSize(new Dimension(200,350));

        Panel4 = new JPanel(new GridLayout(dim,dim));
        Panel4.setBounds(200,100,200,350);

        for (int i =0 ; i<dim+5 ;i++) {
            for(int j =0 ; j<dim+5 ;j++) {
                btn[i][j] = new JButton ();
            }
        }
        for (int i =start ; i<dim+end ;i++) {
            for(int j =start ; j<dim+end ;j++) {

                btn[i][j].setText(" ");
                btn[i][j].setBackground(Color.WHITE);
                btn[i][j].addActionListener(this);
                Panel4.add(btn[i][j]);
            }
        }
        this.add(Panel1,BorderLayout.NORTH);
        this.add(Panel2,BorderLayout.WEST);
        this.add(Panel3,BorderLayout.EAST);
        this.add(Panel4,BorderLayout.CENTER);

        setSize(800, 350);
        this.setVisible(true);

        try {
            s = new Socket("localhost",8754);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // choose  who will start first
        for(int i = start; i<dim+end ; i++) {
            for(int j =start ; j<dim+end ;j++) {
                if(e.getSource() == btn[i][j]) {

                    clicked = clicked + 1;
                    btn[i][j].setForeground(Color.DARK_GRAY);
                    btn[i][j].setText(choix);
                    String mssg="";
                    mssg = choix+":"+i+":"+j+":"+s.getLocalPort()+"";

                    for (int k = start; k <dim+end ; k++) {
                        for (int l = start; l <dim+end ; l++) {
                            btn[k][l].setEnabled(false);
                        }
                    }


                    try {
                        DataOutputStream Out = new DataOutputStream(s.getOutputStream());
                        Out.writeUTF(mssg);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    btn[i][j].setEnabled(false);
                    if(radP1.isSelected()) {
                        radP2.setSelected(true);
                        radP1.setSelected(false);
                    } else if(radP2.isSelected()){
                        radP1.setSelected(true);
                        radP2.setSelected(false);
                    }

                    IsWinner(i,j,0);
                }
            }
        }

        if(e.getSource() == btnChange ) {
            SendTo("changePlayer");
            ResetGame();
        }
        /// replay button
        if( e.getSource() == btnRep )  {
            SendTo("RepalyGameConf");
        }
        ///  reset button
        if(e.getSource() == btnRes ) {
            SendTo("ResetGameConf");
        }
        if(e.getSource() == btnExit ) {
            System.exit(0);
        }
    }
    private void SendTo(String mssg) {
        String msgTo="";
        msgTo = mssg+":"+s.getLocalPort();
        try {
            DataOutputStream Out = new DataOutputStream(s.getOutputStream());
            Out.writeUTF(msgTo);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
    private void ResetGame() {
        winner = "";
        for (int i =start ; i<dim+end ;i++) {
            for(int j =start ; j<dim+end ;j++) {
                btn[i][j].setBackground(Color.WHITE);
                btn[i][j].setEnabled(true);
                btn[i][j].setForeground(Color.DARK_GRAY);
                btn[i][j].setText(" ");
                lblN3.setText("0");
                lblN4.setText("0");
                radP1.setSelected(true);
                clicked =0;
            }
        }
    }

    private void ReplayGame() {
        winner = "";
        for (int i =start ; i<dim+end ;i++) {
            for(int j =start ; j<dim+end ;j++) {
                btn[i][j].setBackground(Color.WHITE);
                btn[i][j].setForeground(Color.DARK_GRAY);
                btn[i][j].setEnabled(true);
                btn[i][j].setText(" ");
                radP1.setSelected(true);
                clicked =0;
            }
        }
    }

    public void IsWinner(int x, int y , int counter ) {
        if(btn[x][y].getText()!=" "&& counter <2) {

                if (btn[x][y].getText().equals(btn[x - 1][y - 1].getText()) && btn[x][y].getText().equals(btn[x + 1][y + 1].getText())) {
                    setWinner(btn[x][y], btn[x + 1][y + 1], btn[x - 1][y - 1]);
                }else if (btn[x][y].getText().equals(btn[x + 1][y].getText()) && btn[x][y].getText().equals(btn[x - 1][y].getText())) {
                    setWinner(btn[x][y], btn[x - 1][y ], btn[x + 1][y ]);

                }else if (btn[x][y].getText().equals(btn[x][y + 1].getText()) && btn[x][y].getText().equals(btn[x][y - 1].getText())) {
                    setWinner(btn[x][y], btn[x][y - 1], btn[x][y + 1]);

                }else if (btn[x][y].getText().equals(btn[x - 1][y + 1].getText()) && btn[x][y].getText().equals(btn[x + 1][y - 1].getText())) {
                    setWinner(btn[x][y], btn[x - 1][y + 1], btn[x + 1][y - 1]);

                }else{
                    if (btn[x][y].getText().equals(btn[x - 1][y - 1].getText())) {

                        IsWinner(x - 1, y - 1, counter + 1);
                    }
                    if (btn[x][y].getText().equals(btn[x + 1][y + 1].getText())) {

                        IsWinner(x + 1, y + 1, counter + 1);
                    }
                    if (btn[x][y].getText().equals(btn[x + 1][y - 1].getText())) {
                        IsWinner(x + 1, y - 1, counter + 1);
                    }

                    if (btn[x][y].getText().equals(btn[x - 1][y + 1].getText())) {
                        IsWinner(x - 1, y + 1, counter + 1);
                    }

                    if (btn[x][y].getText().equals(btn[x][y - 1].getText())) {
                        IsWinner(x, y - 1, counter + 1);
                    }
                    if (btn[x][y].getText().equals(btn[x][y + 1].getText())) {
                        IsWinner(x, y + 1, counter + 1);
                    }
                    if (btn[x][y].getText().equals(btn[x + 1][y].getText())) {
                        IsWinner(x + 1, y, counter + 1);
                    }

                    if (btn[x][y].getText().equals(btn[x - 1][y].getText())) {
                        IsWinner(x - 1, y , counter + 1);
                    }

                }

        }

        ///check for draw
        if(clicked==Math.pow(dim,2) && winner=="" ) {
            JOptionPane.showMessageDialog(null," YOU BOTH ARE  LOOSERS !!!");
        }

    }
/*
    private void TestyDim(int x, int y, int counter) {
        if (btn[x][y].getText().equals(btn[x - 1][y].getText())) {
            IsWinner(x-1, y , counter + 1);
        }
        if (btn[x][y].getText().equals(btn[x + 1][y].getText())) {
            IsWinner(x + 1, y , counter + 1);
        }
    }

    private void TestxDim(int x, int y, int counter) {
        if (btn[x][y].getText().equals(btn[x][y - 1].getText())) {
            IsWinner(x, y - 1, counter + 1);
        }
        if (btn[x][y].getText().equals(btn[x][y + 1].getText())) {
            IsWinner(x, y + 1, counter + 1);
        }
    }

    private void TestY(int x, int y, int counter) {
        if (btn[x][y].getText().equals(btn[x - 1][y + 1].getText())) {
            IsWinner(x - 1, y + 1, counter + 1);
        }
        TestyDim(x, y, counter);
        if (btn[x][y].getText().equals(btn[x][y + 1].getText())) {
            IsWinner(x , y + 1, counter + 1);
        }
    }

    private void TestX(int x, int y, int counter) {
        if (btn[x][y].getText().equals(btn[x + 1][y - 1].getText())) {
            IsWinner(x + 1, y - 1, counter + 1);
        }
        TestxDim(x, y, counter);
        if (btn[x][y].getText().equals(btn[x + 1][y].getText())) {
            IsWinner(x + 1, y, counter + 1);
        }
    }
*/
    private  void MessageGameConf(String mssg , String mssgTo ){
        int result = JOptionPane.showConfirmDialog(null,
                mssg,null, JOptionPane.YES_NO_OPTION);
        if(result == JOptionPane.YES_OPTION) {
            if(mssgTo.equals("RepalyGameYes:")){

                ReplayGame();
            }else if(mssgTo.equals("ResetGameYes:")){

                ResetGame();
            }
            String msgReponse="";
            msgReponse = mssgTo+s.getLocalPort();
            try {
                DataOutputStream Out = new DataOutputStream(s.getOutputStream());
                Out.writeUTF(msgReponse);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }


    private void setWinner(JButton Btn1 ,JButton Btn2 ,JButton Btn3) {
        winner = Btn1.getText();

        Btn1.setText("-"+winner+"-");
        Btn2.setText("-"+winner+"-");
        Btn3.setText("-"+winner+"-");
        Btn1.setBackground(Color.GREEN);
        Btn2.setBackground(Color.GREEN);
        Btn3.setBackground(Color.GREEN);
        Btn1.setForeground(Color.WHITE);
        Btn2.setForeground(Color.WHITE);
        Btn3.setForeground(Color.WHITE);
        ///check winner
        if(winner!="") {
            JOptionPane.showMessageDialog(null,"The winner is    " + winner + "     !!!");
            for (int i =start ; i<dim+end ;i++) {
                for(int j =start ; j<dim+end ;j++) {
                    btn[i][j].setEnabled(false);
                }
            }
            //show result
            if(winner.equals("X")) {
                p1Score=p1Score+1;
            }
            if(winner.equals("O")){
                p2Score=p2Score+1;
            }
            winner="";
            lblN3.setText(" "+p1Score+" ");
            lblN4.setText(" "+p2Score+" ");
        }
    }

    public void run() {
        String mssg = "";
        String[] nw ;
        while(true) {
            try {
                DataInputStream In = new DataInputStream(s.getInputStream());
                if(!In.equals(null)){
                    mssg =In.readUTF();
                    nw = mssg.split(":");
                    if(nw[0].equals("changePlayer")){
                       ResetGame();
                    }else if(nw[0].equals("RepalyGameConf")){
                        MessageGameConf("The other player wants to replay , replay the  game ? ","RepalyGameYes:");
                    }else if(nw[0].equals("ResetGameConf")){
                        MessageGameConf("The other player wants to reset  , reset the game ? ","ResetGameYes:");
                    }else  if(nw[0].equals("RepalyGameYes")){
                        ReplayGame();
                    }else if(nw[0].equals("ResetGameYes")){
                        ResetGame();
                    }else  {
                        if(!Integer.toString(s.getLocalPort()).equals(nw[3])){

                            btn[Integer.parseInt(nw[1])][Integer.parseInt(nw[2])].setText(nw[0]);
                            btn[Integer.parseInt(nw[1])][Integer.parseInt(nw[2])].setForeground(Color.DARK_GRAY);
                            System.out.println(Integer.parseInt(nw[1])+" "+Integer.parseInt(nw[2]));
                            IsWinner(Integer.parseInt(nw[1]),Integer.parseInt(nw[2]),0);
                            System.out.println(s.getLocalPort()+""+nw[3]);
                            for (int i = start; i <dim+end ; i++) {
                                for (int j = start; j <dim+end ; j++) {
                                    if(btn[i][j].getText().equals(" ")){
                                        btn[i][j].setEnabled(true);
                                    }
                                }
                            }

                        }
                        if(nw[0].trim().equals("X")){
                            choix = "O";
                        }else{
                            choix = "X";
                        }
                        if(radP1.isSelected()) {
                            radP2.setSelected(true);
                            radP1.setSelected(false);
                        } else if(radP2.isSelected()){
                            radP1.setSelected(true);
                            radP2.setSelected(false);
                        }
                    }

                }
            } catch (Exception e) {
                System.out.println( e);
            }
        }
    }

}

