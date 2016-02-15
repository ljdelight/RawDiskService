package com.ljdelight.rawdisk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

import com.ljdelight.rawdisk.generated.RawDisk;

public class RawDiskServer {
    private static final Logger logger = LogManager.getLogger(RawDiskServer.class);

    private static RawDiskHandler handler;
    private static RawDisk.Processor<RawDiskHandler> processor;

    public static void main(String[] args) {
        if (args.length != 1) {
            logger.error("Missing argument to {}", RawDiskServer.class.getName());
            System.err.println("RawDiskServer <port>");
            System.exit(1);
        }
        try {
            logger.trace("In RawDiskServer main()");
            handler = new RawDiskHandler();
            processor = new RawDisk.Processor<RawDiskHandler>(handler);
            int port = Integer.parseInt(args[0]);

            Runnable simple = new Runnable() {
                @Override
                public void run() {
                    simple(processor, port);
                }
            };

            new Thread(simple).start();

        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public static void simple(RawDisk.Processor<RawDiskHandler> processor, int port) {
        try {
            TServerTransport serverTransport = new TServerSocket(port);
            TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));
            logger.info("Starting server on port {}", port);

            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

