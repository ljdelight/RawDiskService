package com.ljdelight.rawdisk;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

import com.ljdelight.rawdisk.generated.RawDisk;

public class RawDiskServer {

    public static RawDiskHandler handler;
    public static RawDisk.Processor<RawDiskHandler> processor;

    public static void main(String[] args) {
        try {
            handler = new RawDiskHandler();
            processor = new RawDisk.Processor<RawDiskHandler>(handler);

            Runnable simple = new Runnable() {
                @Override
                public void run() {
                    simple(processor);
                }
            };

            new Thread(simple).start();

        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public static void simple(RawDisk.Processor<RawDiskHandler> processor) {
        try {
            TServerTransport serverTransport = new TServerSocket(9093);
            TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));
            System.out.println("Starting the simple server...");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
