
namespace java com.ljdelight.rawdisk.generated
#
# Need a way to scan local disk devices. For each device,
# send a ReadCapacity and if it doesn't fail store the
# blocksize, total lba, and determine total space in bytes (or KiB).
#
# On linux we can enumerate all /dev/sd* devices and send the command.
# Windows, likely the same.
#
#
# Client determine drives on server:
#   - Client getDiskDevices -> server -> enumerate drives
#        -> ReadCapacity on each
#        <-- list of disk devices
# Client read raw block of a drive on server:
#   - Client getRawBlock(dev, lba) -> server -> scsi readcap on device
#        <-- return raw data or struct with size+data
#        Maybe need multiblock reads
#
#

struct DiskDevice {
    1: required string path;
    2: required i64 block_size_bytes;
    3: required i64 total_lba_blocks;
    4: required i64 totalSpace;
}

service RawDisk {
    list<DiskDevice> getDiskDevices(),
    string readLBAPretty(1:string path, 2:i64 offset_lba),
}



