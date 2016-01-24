
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
        try (TTransport transport = new TSocket("localhost", 9093)) {
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            RawDisk.Client client = new RawDisk.Client(protocol);

            System.out.println("Native read results");
            String block = client.readLBAPretty("/dev/sda", 2);
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
