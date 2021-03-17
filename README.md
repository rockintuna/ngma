# ngma
커플용 웹기반 스케쥴러

- 기술 스택  

maven  
Java  
Spring Boot  
JPA  
Spring Security   
Thymeleaf  
JavaScript  
Jquery  
Ajax  
BootStrap  
Mysql  
H2 DB(for test)  

- 프로젝트 설치하는 방법

**MYSQL DB 타임존 설정 및 DB 생성, 유저 생성, 권한 부여**
```
set global time_zone='Asia/Seoul';
create database ngma;
create user ngma@localhost identified by 'ngma';
grant all privileges on ngma.* to ngma@localhost;
```

만약 아래와 같은 에러 메시지로 타임존 설정이 안될 경우
ERROR 1298 (HY000): Unknown or incorrect time zone: 'Asia/Seoul'
https://dev.mysql.com/downloads/timezones.html 에서 sql 파일을 받아 실행한 뒤 다시 시도한다.
```
use mysql;
source C:\Users\Jeong In Lee\Downloads\timezone_2021a_leaps_sql/timezone_leaps.sql;
```

**git clone으로 git 저장소 불러오기**
```
$ git clone https://github.com/cyr9210/Notice-Project.git
Cloning into 'ngma'...
remote: Enumerating objects: 138, done.
remote: Counting objects: 100% (138/138), done.
remote: Compressing objects: 100% (91/91), done.
Recremote: Total 138 (delta 38), reused 130 (delta 33), pack-reused 0
Receiving objects: 100% (138/138), 1.51 MiB | 8.29 MiB/s, done.
Resolving deltas: 100% (38/38), done.
```

**IDE 프로젝트 open**

![image](https://user-images.githubusercontent.com/52302236/111488763-e1dd4000-877c-11eb-8215-8dc78db83471.png)

**프로젝트 설정에서 JDK 버전 11로 변경**

![image](https://user-images.githubusercontent.com/52302236/111489094-32549d80-877d-11eb-945c-a06ee168eeca.png)

**애플리케이션 실행**

![image](https://user-images.githubusercontent.com/52302236/111489375-6760f000-877d-11eb-8a69-3b29f6246ca1.png)

**localhost:8080 접속**

![image](https://user-images.githubusercontent.com/52302236/111489605-a000c980-877d-11eb-91e9-1f02abe06d4f.png)

- 프로젝트 기능 설명

**회원가입 후 로그인하면 일정/짝꿍/계정변경 탭이 활성화된다.**

![image](https://user-images.githubusercontent.com/52302236/111489727-bb6bd480-877d-11eb-8985-31f38b366f33.png)

**일정기능에서는 나의 또는 내 짝꿍의 일정을 확인하거나 추가 또는 변경 및 제거할 수 있다.**

![image](https://user-images.githubusercontent.com/52302236/111489966-f837cb80-877d-11eb-8265-e933f90b74cd.png)

![image](https://user-images.githubusercontent.com/52302236/111490229-2f0de180-877e-11eb-8057-aba12982a28f.png)

**짝꿍 기능에서는 다른 사람에게 짝꿍을 신청하거나 나의 짝꿍을 확인 또는 다른 사람으로 부터의 신청 내역을 확인할 수 있다.**

![image](https://user-images.githubusercontent.com/52302236/111490386-54025480-877e-11eb-9da6-a525c6592a20.png)

![image](https://user-images.githubusercontent.com/52302236/111490430-5d8bbc80-877e-11eb-8e9a-846cd09bb0f7.png)

![image](https://user-images.githubusercontent.com/52302236/111490479-67152480-877e-11eb-8f36-2c103878339c.png)

![image](https://user-images.githubusercontent.com/52302236/111490565-7a27f480-877e-11eb-84fe-40c20431d930.png)

- 업데이트
