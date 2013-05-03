#cat linux_files.lst |while read i; do echo l/$i; rm l/$i.dbg; done 
find l/ -name *.dbg -delete
find l/ -name *.pi -delete
find l/ -name *.err -delete
