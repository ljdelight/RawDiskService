
BUILD := build

final: .gen-java readfromdev
	gradle fatJar

.PHONY: readfromdev
readfromdev:
	dub --root=src/main/d/readfromdev/ build

.gen-java: src/main/thrift/rawdisk.thrift
	thrift --gen java:hashcode,generated_annotations=undated -out src/main/java/ $<
	touch $@

run-client: final
	sudo PATH=src/main/d/readfromdev/:$$PATH \
		java -cp build/libs/RawDiskService-all.jar com.ljdelight.rawdisk.RawDiskServer 9003 \
		& echo $$! > server.pid
	sleep 2
	java -cp build/libs/RawDiskService-all.jar com.ljdelight.rawdisk.RawDiskClient \
		localhost 9093 /dev/sdb 1
	sudo kill -9 `cat server.pid`
	rm -f server.pid

.PHONY: clean
clean:
	dub --root=src/main/d/readfromdev/ clean
	-rm -rf src/main/java/com/ljdelight/rawdisk/generated/
	-rm -rf ${BUILD}
	-rm -f .gen-java
