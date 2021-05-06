gradle clean
rm -rf out build
gradle build -x test -Dfile.encoding=UTF-8
