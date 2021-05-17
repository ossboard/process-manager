# process-manager

## 설명
> 프로세스를 자동으로 관리하는 프로그램
> supervisor 기능을 자바 버전으로 개발 진행중

## 스팩
- java 1.8.x 이상
- gradle 6.3.x 이상
- spring-boot 2.2.x
- use `logback` logger

### 빌드
```
$ ./gradlew build -x test
$ ./gradlew bootRun -x test --args='-Dspring.profiles.active=developer'
``` 

### 실행
``` 
$ java -jar process-manager-2021.1.jar
실행후 바로 종료 
config.ini <-- 생성된 파일 편집

$ sh startup.sh start
$ sh startup.sh [start,stop,restart,status,log

```

