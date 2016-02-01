RawDiskService [![Build Status](https://travis-ci.org/ljdelight/RawDiskService.svg?branch=master)](https://travis-ci.org/ljdelight/RawDiskService)
======
RawDisk service provides block-level read access to remote clients. This means remote clients have full read access to _any_ drive in the host system that supports the Read10 [SCSI CDB](https://en.wikipedia.org/wiki/SCSI_CDB).

The goal of this project is to enable client-side tools that work with a number of different operating systems running the service.


Example Clients
======

### Inspect GPT Header
Say there's a need to look at a system's GPT header to verify the CRC32 and compare contents with the secondary GPT header. The GPT header is at LBA 1 and reading that block is simply
```java
TTransport transport = new TSocket(host, port);
// a few more lines of setup...
String response = client.readLBAPretty("/dev/sda", 1);
```

The response is a hexdump for LBA 1, the GPT header:
```yaml
000000: 45 46 49 20 50 41 52 54  00 00 01 00 5c 00 00 00  |EFI PART....\...|
000010: d2 cc 1d 05 00 00 00 00  01 00 00 00 00 00 00 00  |................|
000020: af 44 f2 1b 00 00 00 00  22 00 00 00 00 00 00 00  |.D......".......|
000030: 8e 44 f2 1b 00 00 00 00  2e 05 9a e4 8c e6 43 42  |.D............CB|
000040: a7 a5 58 64 26 20 27 af  02 00 00 00 00 00 00 00  |..Xd& '.........|
000050: 80 00 00 00 80 00 00 00  60 4b 08 e5 00 00 00 00  |........`K......|
```

The secondary GPT header is the 8 bytes at offset 0x20 in the above hexdump. In little endian that's `0x1bf244af` (468862127 decimal), and obtain the secondary GTP header with `client.readLBAPretty("/dev/sda", 0x1bf244af);`

With both headers in hand, we can take over the world.


---

Implementation Details
=====
RawDiskService supports both Windows and Linux, and requires root to run the server. Typically only \\\\.\\physicaldriveN (on windows) and /dev/sdX (on linux) will work with the service.

Two libraries made the implementation very simple (initial prototype working within 2 hours):
* [Apache Thrift](https://thrift.apache.org/). It's an interface design language (IDL) that solves the problem of client-server communication. From their site: _The Apache Thrift software framework, for scalable cross-language services development, combines a software stack with a code generation engine to build services that work efficiently and seamlessly._ Just write a thrift file and implement the server-side handlers, and Thrift does the rest.
* [sgio.d](https://github.com/ljdelight/sgio.d). I wrote this D libarary to simplify creating scsi device drivers and prototypes. It's used in [readfromdev](https://github.com/ljdelight/RawDiskService/tree/master/src/main/d/readfromdev), which does all of the native ioctl calls.




Author
=====
Written by [Lucas Burson](http://ljdelight.com) ljdelight@gmail.com
