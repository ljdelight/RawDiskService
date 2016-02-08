
BUILD := build

.PHONY: final
final: jar readfromdev

.PHONY: jar
jar:
	gradle fatJar

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

gen-deb: jar readfromdev LICENSE rawdisk.deb.init
	mkdir -p build/deb/opt/ljdelight/rawdisk/
	mkdir -p build/deb/etc/init.d/
	install --mode=0444 build/libs/*.jar build/deb/opt/ljdelight/rawdisk/
	install --mode=0444 LICENSE build/deb/opt/ljdelight/rawdisk/
	install --mode=0555 src/main/d/readfromdev/readfromdev build/deb/opt/ljdelight/rawdisk/
	install --mode=0755 rawdisk.deb.init build/deb/etc/init.d/rawdisk
	fpm -s dir -t deb -C build/deb \
		--version 0.1.0 --name ljdelight-rawdisk --architecture amd64 \
		--deb-no-default-config-files \
		--deb-user root --deb-group root
	touch $@

.PHONY: clean
clean:
	-dub --root=src/main/d/readfromdev/ clean
	-rm -rf ${BUILD}
	-rm -f gen-*
	-rm -f *.deb

