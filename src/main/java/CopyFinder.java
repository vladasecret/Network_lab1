import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CopyFinder {
    private final InetAddress groupIP;
    private final UUID ID = UUID.randomUUID();
    private final int port = 150;
    private final Map<Pair<UUID, InetAddress>, Long> copies;
    private final long sndInterval = 5000;
    private final long rcvInterval = 25000;
    private final byte[] rcvBuf = new byte[512];

    public CopyFinder(InetAddress grpIP) {
        groupIP = grpIP;
        copies = new HashMap();
    }

    public void run() throws IOException {
        try (MulticastSocket socket = new MulticastSocket(port)) {
            socket.joinGroup(new InetSocketAddress(groupIP, port), getInterface());
            byte[] sndMessage = ID.toString().concat(" alive").getBytes();
            DatagramPacket sndPacket = new DatagramPacket(sndMessage, sndMessage.length, groupIP, port);
            socket.send(sndPacket);
            long lastSendTime = System.currentTimeMillis();
            DatagramPacket rcvPacket = new DatagramPacket(rcvBuf, rcvBuf.length);
            for (int i = 0; ; ++i) {
                while (System.currentTimeMillis() - lastSendTime < sndInterval) {
                    socket.setSoTimeout((int) (sndInterval + lastSendTime - System.currentTimeMillis()));
                    try {
                        socket.receive(rcvPacket);
                    }
                    catch (SocketTimeoutException exception){
                        break;
                    }
                    updateData(rcvPacket);
                }
                socket.send(sndPacket);
                lastSendTime = System.currentTimeMillis();
            }
        }


    }

    private void updateData(DatagramPacket packet) {
        String[] parsedData = parseData(packet.getData());
        if (!checkData(parsedData))
            return;
        Pair<UUID, InetAddress> key = new Pair(UUID.fromString(parsedData[0]), packet.getAddress());

        if (copies.containsKey(key)) copies.replace(key, System.currentTimeMillis());
        else {
            System.out.println("+ " + key.getSecond() + ((key.getFirst().equals(ID)) ? " (me)" : ""));
            copies.put(key, System.currentTimeMillis());
        }

        copies.forEach((x, y) -> {
            if (System.currentTimeMillis() - y > rcvInterval) {
                copies.remove(x);
                System.out.println("- " + key.getSecond() + ((key.getFirst().equals(ID)) ? " (me)" : ""));
            }

        });
    }

    private String[] parseData(byte[] data) {
        String strData = new String(data);
        return strData.split("\\s");
    }

    private boolean checkData(String[] data) {
        if (data.length == 2) {
            if (data[0].length() == 36)
                if (data[1].startsWith("alive"))
                    return true;
        }
        return false;
    }


    private NetworkInterface getInterface() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface elem = interfaces.nextElement();

            if (elem.supportsMulticast() && elem.isUp() && !elem.isLoopback()) {
                return elem;
            }
        }
        return null;
    }


}
