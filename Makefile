
BUILD := build

.PHONY: final
final: readfromdev

.PHONY: readfromdev
readfromdev:
	dub --root=src/main/d/readfromdev/ build

# Use this target when the thrift file is modified.
#
.PHONY: gen-thrift
gen-thrift: src/main/thrift/rawdisk.thrift
	thrift --gen java:hashcode,generated_annotations=undated -out src/main/java/ $<

run-client: final
	sudo PATH=src/main/d/readfromdev/:$$PATH \
		java -cp build/libs/RawDiskService-all.jar com.ljdelight.rawdisk.RawDiskServer 9093 \
		& echo $$! > server.pid
	sleep 2
	java -cp build/libs/RawDiskService-all.jar com.ljdelight.rawdisk.RawDiskClient \
		localhost 9093 /dev/sdb 1
	sudo kill -9 `cat server.pid`
	rm -f server.pid

dockerimage: gen-deb
	docker build -t ljdelight/rawdisk:latest .
	docker run -d -p 9093:9093 --device=/dev/nvme0n1 ljdelight/rawdisk:latest
	sleep 3
	java -cp build/distributions/opt/ljdelight/rawdisk/lib/\* com.ljdelight.rawdisk.RawDiskClient localhost 9093 /dev/nvme0n1 1

.PHONY: gen-deb
gen-deb: readfromdev
	./gradlew debpackage

.PHONY: clean
clean:
	-dub --root=src/main/d/readfromdev/ clean
	-./gradlew clean
	-rm -f gen-*
