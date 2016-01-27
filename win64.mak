
DUB=dub
GRADLE=gradle
RELEASE=release

RAWDISK_JAR=build\libs\RawDiskService-all.jar
READFROMDEV_DIR=src\main\d\readfromdev
READFROMDEV_BIN=$(READFROMDEV_DIR)\readfromdev.exe

final: $(RAWDISK_JAR) $(READFROMDEV_BIN) run-server.bat run-client.bat

$(RAWDISK_JAR): .phony
	$(GRADLE) fatJar

$(READFROMDEV_BIN): .phony
	$(DUB) --root="$(READFROMDEV_DIR)" build --build=$(RELEASE)

run-server.bat:
	echo rem THIS BAT STARTS THE SERVER AND MUST BE EXECUTED AS ROOT > $@
	echo PATH=$(READFROMDEV_DIR);^%PATH^% >> $@
	echo start java -cp $(RAWDISK_JAR) com.ljdelight.rawdisk.RawDiskServer 9093 >> $@

run-client.bat:
	echo rem RawDiskClient ^<host^> ^<port^> ^<device^> ^<lba^> > $@
	echo rem java -cp $(RAWDISK_JAR) com.ljdelight.rawdisk.RawDiskClient %1 %2 %3 %4 >> $@
	echo java -cp $(RAWDISK_JAR) com.ljdelight.rawdisk.RawDiskClient localhost 9093 \\.\physicaldrive0 1 >> $@

.phony:

clean:
	$(GRADLE) clean
	$(DUB) --root=$(READFROMDEV_DIR) clean
	del $(READFROMDEV_BIN)
	del run-client.bat run-server.bat
