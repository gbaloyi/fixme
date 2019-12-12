import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author SHUBHAM
 */
public class Client1{

    public static void main(String args[]) throws Exception
    {
        Socket sk=new Socket("127.0.0.1",5000);
//        sk.run();

        BufferedReader sin=new BufferedReader(new InputStreamReader(sk.getInputStream()));
        PrintStream sout=new PrintStream(sk.getOutputStream());
        BufferedReader stdin=new BufferedReader(new InputStreamReader(System.in));
        String s;
        while (  true )
        {
            System.out.print("Client : ");
            s=stdin.readLine();
            sout.println(s);
            if ( s.equalsIgnoreCase("BYE") )
            {
                System.out.println("Connection ended by client");
                break;
            }
            s=sin.readLine();
            System.out.print("Server : "+s+"\n");

        }
        sk.close();
        sin.close();
        sout.close();
        stdin.close();
    }

//    public Client(){
//        Socket s = new Socket();
//    }

    public void run() {

//        Server server = null;
        Socket client = null;
        BufferedReader reader = null;
        PrintStream printing = null;
        Scanner scan = new Scanner(System.in);
        int id;
        String s;
        int x=1;
        try{
            while(true){
                s=reader.readLine();

                System. out.print("Client :"+s+"\n");
//                    System.out.print("Server : ");
                //s=stdin.readLine();
//                    s=scan.nextLine();
                if (s.equalsIgnoreCase("bye"))
                {
//                        printing.println("BYE");
                    x=0;
                    System.out.println("Connection ended by server");
                    break;
                }
                printing.println(s);
            }


//            reader.close();
//            client.close();
//            printing.close();
            if(x==0) {
                System.out.println( "Server cleaning up." );
                System.exit(0);
            }
        }
        catch(IOException ex){
            System.out.println("Error : "+ex);
        }


    }

}

