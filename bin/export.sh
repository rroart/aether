rsync -a ~/usr/data/h2/backup/ ~/usr/data/h2/backup.1

cp -p ~/usr/data/h2/stuff.* ~/usr/data/h2/backup

java -cp /home/roart/.m2/repository/com/h2database/h2/1.3.173/h2-1.3.173.jar org.h2.tools.Script -url jdbc:h2:~/usr/data/h2/stuff -user "" -script ~/usr/data/h2/backup/t.sql

cd ~/usr/data/h2/
java -cp /home/roart/.m2/repository/com/h2database/h2/1.3.173/h2-1.3.173.jar org.h2.tools.Recover
mv stuff.h2.sql backup
