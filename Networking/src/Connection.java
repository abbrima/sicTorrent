import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Connection {
    ConnectionMessages connect;

    public void sendHandshake(){

    }
    private class receiveHandshake implements Runnable
    {
        ServerSocket server;
        int port;
        boolean port_works= false;
        private void setPort(int port)
        {
            this.port = port;
        }
        public void setport(){
            while (!port_works) {
                try {
                    server = new ServerSocket(6881);
                    System.out.print(server.getLocalPort());
                    port_works = true;

                } catch (Exception e) {
                    setPort(port + 1);
                }
            }
        }
        public void run(){
            while(true)
            {
                try{
                    Socket client = server.accept();
                    BufferedReader n = new BufferedReader( new InputStreamReader( client.getInputStream()));
                    String s = null;
                    while ((s=n.readLine())!=null)
                    {
                        System.out.println(s);
                    }

                }catch (Exception e) {
                }
            }
        }
    }

}
