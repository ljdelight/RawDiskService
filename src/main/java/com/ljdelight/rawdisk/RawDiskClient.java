
package com.ljdelight.rawdisk;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.ljdelight.rawdisk.generated.RawDisk;

public class RawDiskClient {
    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("RawDiskClient <host> <port> <device> <lba>");
            System.exit(1);
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String device = args[2];
        long lba = Long.parseLong(args[3]);
        try (TTransport transport = new TSocket(host, port)) {
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            RawDisk.Client client = new RawDisk.Client(protocol);

            String block = client.readLBAPretty(device, lba);
            System.out.println("Native read results");
            System.out.println(block);

            // The try-with-resource closes this connection
            // transport.close();
        } catch (TTransportException x) {
            System.err.println("Transport error: " + x.getMessage());
            System.exit(1);
        } catch (TException x) {
            x.printStackTrace();
        }
    }
}
