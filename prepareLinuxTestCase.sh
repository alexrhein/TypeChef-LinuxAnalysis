#!/bin/sh -v
version=linux-3.4
CURPATH=`pwd`

if [ ! -d $version ]; then
	tarball=$version.tar.bz2
	if [ ! -f $tarball ]; then
	  wget http://www.kernel.org/pub/linux/kernel/v3.0/$tarball
	fi
	tar xjf $tarball

	ln -s $version l
	
	cd $version

	patchesPath=../linux-2.6.33.3-patches
	while read i; do
	  patch -p1 -i $patchesPath/$i
	done < $patchesPath/series
	
	#so we can cd to $version again in next line
	cd ../
fi

cd $version
make allnoconfig ARCH=x86
make prepare ARCH=x86
# Creates include/generated/compile.h needed for init/version.o; the command
# will give an error which we want to ignore.
make SUBDIRS=init ARCH=x86 &> /dev/null

# generates some more header files (e.g. arch/x86/include/generated/asm/unistd_64_x32.h)
make allnoconfig ARCH=x86_64
make prepare ARCH=x86_64

cd $CURPATH
if [ ! -d systems/redhat ]; then
	mkdir systems
	mkdir systems/redhat
	cd systems/redhat
	wget http://www.informatik.uni-marburg.de/%7Ekaestner/tmp/includes-redhat.tar.bz2
	tar xjf includes-redhat.tar.bz2
	rm includes-redhat.tar.bz2
fi

cd $CURPATH
if [ ! -f run.sh ]; then
	java -Xmx1024M -Xss32M -XX:MaxPermSize=256M -jar ../TypeChef/sbt-launch.jar mkrun
fi
./run.sh de.fosd.typechef.linux.ProcessFileList linux_3.4_pcs.txt --workingDir l/ --openFeatureList openFeaturesList.txt
