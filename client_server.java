import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.Random;

public class client_server{
    private JTextField text;
    private JButton button;
    private JButton exit;
    private JTextArea area;

    InetAddress group;
    MulticastSocket broadcast;
    int UDPPORT=1234;

    private  ServerSocket server=null;
    private Socket client=null;
    private Socket cli=null;


    private DataOutputStream Oserver =null;
    private DataInputStream inServer=null ;

    private DataOutputStream Oclient = null;
    //    DataInputStream inClient = null;
    private DataInputStream inClient = null;
//    Charset charset = Charset.forName("ISO-8859-1");

    public client_server(){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Peer Chat");
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridLayout(2,1));
        //create the main broadcasting connection
        try {
            group = InetAddress.getByName("239.0.0.0");
            broadcast = new MulticastSocket(UDPPORT);
            broadcast.setTimeToLive(0);
            broadcast.joinGroup(group);
        } catch (IOException e) {
            e.printStackTrace();
        }

        text = new JTextField("write you words....");
        text.setColumns(50);
        text.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                text.setText("");
            }
        });

        button = new JButton( "send");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String message = text.getText();

                        //broadcasting
                        if (server==null && client==null){   // it shows that we dont create a client or server so we are chatting public
                            broadcasting(frame,message);

                        //send message as client or server
                        }else {
                            if (server!=null){  // server is alive and send message to client
                                send_to_client(message);

                            }else {    //send data to servre
                                send_to_server(message);
                            }
                        }
                    }
                }).start();
            }
        });

        area = new JTextArea();
        area.setEditable(false);
        area.setColumns(50);
        area.setRows(10);
        area.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(true){
                            //it is show the message of broadcasting
                            if (server==null && client==null){
                                show_broadcasting(frame);
                                //showing data as client or server
                            }else if(server != null && cli!=null && client==null) {  // we are in server code and client is accepted...
                                server_read();

                            }else if(client!=null && server ==null && cli==null){       //we are client and show the recieved message from server
                                    System.out.println("in reading data  of server :");
                                    client_read();
                            }
                        }
                    }
                }).start();
            }
            @Override
            public void focusLost(FocusEvent e) {}
        });



        exit = new JButton("exit");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource()==exit){
                    if (client!=null){
                        try {
                            client.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }else if (server!=null){
                        try {
                            cli.close();
                            server.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });



//        JScrollPane scrolltxt = new JScrollPane(area);
        JScrollPane scrolltxt = new JScrollPane(area, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);


        JPanel main = new JPanel( new FlowLayout());

        frame.add(scrolltxt,"center");
//        main.add(area);
        main.add(text);
        main.add(button);
        main.add(exit);

        frame.add(main,"south");
        frame.setSize(500,300);
        frame.setVisible(true);
        frame.pack();
    }

    private void send_to_server(String message) {
        if (Oclient==null){
            try {
                Oclient = new DataOutputStream(client.getOutputStream());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        //send data to output of client buffer
        try {
            Oclient.writeUTF(message+"\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void send_to_client(String message) {
        if (Oserver==null){
            try {
                Oserver = new DataOutputStream(cli.getOutputStream());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        try {
            Charset charset = Charset.forName("ISO-8859-1");
//                                    Oserver.writeUTF(message+"\n");
//                                    Oserver.write(message.getBytes(charset));
//            Oserver.write(message.getBytes());
//            Oserver.flush();
            Oserver.writeUTF(message);
            System.out.println("server wrote"+message);
//                                    inServer.writeObject(message+"\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void broadcasting(JFrame frame,String message) {
        if (message.contains("lets chat")){
            //send "lets chat" message in another port.......
            String[] info = message.split(" ");
            String ip = info[0];
            Random random = new Random();
            int temproryPort = random.nextInt(6000)+1234;
            int tcpPort = Integer.parseInt(info[info.length-1]);

            try {
                //you should create server and send if just to specific peer
                DatagramSocket socket = new DatagramSocket(temproryPort);
                InetAddress address = InetAddress.getByName(ip);
                byte buf[] = message.getBytes();
                DatagramPacket dp = new DatagramPacket(buf, buf.length, address, UDPPORT);
                socket.connect(address, UDPPORT);
                //befor sending packet initializing the server
                server = new ServerSocket(tcpPort);

                socket.send(dp);
                System.out.println("...packet sent successfully....");
                socket.close();
                //create tcp server .......................................................................
                JOptionPane.showMessageDialog(frame,"you are server ");
                try {
                    System.out.println("Server started");
                    System.out.println("Waiting for a client ...");
                    cli = server.accept();
                    System.out.println("Client accepted");
                    Oserver = new DataOutputStream(cli.getOutputStream());
                    inServer = new DataInputStream(cli.getInputStream());

                    System.out.println(cli.getRemoteSocketAddress());
                    System.out.println(cli.getLocalSocketAddress());

                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                //if the message does not contain special word...
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        //message is a normal messsage
        }else {
            try {
                //send data to another
                message = group + " ---> " + message;
                byte[] buffer = message.getBytes();
                DatagramPacket datagram = new DatagramPacket(buffer,buffer.length,group,UDPPORT);
                broadcast.send(datagram);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }


    private void client_read() {
        if (inClient==null){
            try {
                inClient = new DataInputStream(client.getInputStream());
//                                            inClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        try {
//                                        if (inClient.available()>0){
            String message = inClient.readUTF();
//                                            String message = String.valueOf(inClient.readByte());
//                                            String message = inClient.readLine();
//                                        String message = inClient.readLine();
            System.out.println("Message: " + message);
            area.append(message+"\n");
//                                        }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void server_read() {
        if (inServer==null){
            try {
                inServer = new DataInputStream(cli.getInputStream());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        try {
            String message = inServer.readUTF();
            System.out.println("Message Received: " + message);
            area.append(message+"\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void show_broadcasting(JFrame frame) {
        byte[] buffer = new byte[1024];
        DatagramPacket datagram = new DatagramPacket(buffer,buffer.length,group,UDPPORT);
        String message;
        try {
            broadcast.receive(datagram);
            message = new String(buffer,0,datagram.getLength(),"UTF-8");
            //it is client
            if (message.contains("lets chat") && server ==null){
                // connect to the tcp server with special port and ip
                String[] info = message.split(" ");
                System.out.println(info[0]);
                System.out.println(Integer.valueOf(info[info.length-1]));
                InetAddress serverAddress = datagram.getAddress();
                int serverport = Integer.parseInt(info[info.length-1]);
                System.out.println("ip and port of tcp server are : "+serverAddress +serverport);
                try {
                    //you should create client and connect to that client
                    System.out.println("the server ip and port and address "+serverAddress.getHostAddress()+ serverport);
//                                            System.out.println("the client ip and port are : " );
                    client = new Socket(serverAddress.getHostAddress(),serverport);
                    inClient  = new DataInputStream(client.getInputStream());
//                                            inClient = new BufferedReader(new InputStreamReader(client.getInputStream(),charset));
//                                            inClient = new BufferedReader(new InputStreamReader(client.getInputStream()));

                    Oclient= new DataOutputStream(client.getOutputStream());
                    JOptionPane.showMessageDialog(frame,"you are client");
                    area.append("you are client ");
                    System.out.println("client is completed");
                    System.out.println(client.getRemoteSocketAddress());
                    System.out.println(client.getLocalSocketAddress());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                //simple message in broadcasting....
            }else {
                message = message+"\n";
                area.append(message);
            }
        } catch(IOException e){
            System.out.println("Socket closed!");
        }
    }


    public static void main(String[] args) {
        new client_server();
    }
}
