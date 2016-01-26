
package com.ljdelight.rawdisk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.ljdelight.rawdisk.generated.RawDisk;

public class RawDiskClient {
    private static final Logger logger = LogManager.getLogger(RawDiskClient.class);

    public static void main(String[] args) {
        if (args.length != 4) {
            logger.error("{} given invalid arguments", RawDiskClient.class.getName());
            System.err.println("RawDiskClient <host> <port> <device> <lba>");
            System.exit(1);
        }
        logger.trace("In RawDiskClient main()");

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String device = args[2];
        long lba = Long.parseLong(args[3]);

        logger.debug("Input arguments are host={} port={} device={} lba={}", host, port, device, lba);
        try (TTransport transport = new TSocket(host, port)) {
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            RawDisk.Client client = new RawDisk.Client(protocol);

            String block = client.readLBAPretty(device, lba);
            logger.trace("Received readLBAPretty reply");

            System.out.println("Native read results");
            System.out.println(block);

            // The try-with-resource closes this connection
            // transport.close();
        } catch (TTransportException x) {
            logger.error("Caught TTransportException", x);
            System.exit(1);
        } catch (TException x) {
            logger.error("Caught TException", x);
            x.printStackTrace();
        }
    }
}
