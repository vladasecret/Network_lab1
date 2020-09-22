import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        String strGroupIP = null;
        Scanner scanner = new Scanner(System.in);
        if (args.length == 0){
            System.out.println("Please, enter the group multicast IP:\n");
            strGroupIP = scanner.next();
        }
        else strGroupIP = args[0];

        InetAddress groupIP = null;
        for (int i = 0; i < 3; ++i) {
            try {
                groupIP = InetAddress.getByName(strGroupIP);
                if (groupIP.isMulticastAddress())
                    break;
                System.out.println("Entered IP is not multicast. Try again:");
            } catch (UnknownHostException e) {
                System.out.println(e.getLocalizedMessage() + "\nTry again:");
            }
            strGroupIP = scanner.next();
        }
        new CopyFinder(groupIP).run();


    }
}
