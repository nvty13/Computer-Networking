rm -rf ./lib/*
cp -rf ~aiguo/javalib/CNC01F/classes/* ./lib/.
javac -classpath ~aiguo/javalib/jre1.8.0_211-mac/Contents/Home/lib/:./lib -d ./lib *.java
