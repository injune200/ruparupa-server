# 프로젝트 설치 및 실행 가이드

## 1\. Java 21 설치

[Azul Downloads](https://www.azul.com/downloads/#zulu) 페이지에 접속하여 아래 옵션을 선택해 설치합니다.

  * **Java Version:** `Java 21 (LTS)`
  * **Operating System:** `Windows`
  * **Architecture:** `x86 64-bit`
  * **Java Package:** `JDK`

<!-- end list -->

1.  `.msi` 파일을 다운로드한 후, 모든 설정을 기본값으로 하여 설치를 완료합니다.
2.  **설치 확인:** 터미널(cmd)에서 아래 명령어를 입력했을 때 버전 정보가 출력되면 성공입니다.
    ```bash
    java -version
    ```

-----

## 2\. 프로젝트 클론 및 설정

VSCode에서 프로젝트를 저장할 폴더로 이동한 후 아래 명령어를 입력합니다.

```bash
git clone https://github.com/injune200/ruparupa-server.git
```

### 환경 설정 (application.yml)

1.  `src/main/resources` 경로로 이동합니다. (폴더가 없어서 resources폴더는 직접 생성해야 합니다.)
2.  전달받은 `application.yml` 파일을 해당 폴더 안에 넣습니다.
3.  **주의사항:** `application.yml` 파일은 보안 정보가 포함되어 있으므로, Push 하기 전에 반드시 `.gitignore`에 추가하여 GitHub에 업로드되지 않도록 주의하세요.

-----

## 3\. VSCode 확장 프로그램 설치

원활한 개발을 위해 다음 두 가지 확장 팩을 반드시 설치해야 합니다.

  * **Extension Pack for Java**
  * **Spring Boot Extension Pack**

> 설치 중 왼쪽 하단에 "Extension Pack for Java 확장을 설치하시겠습니까?"라는 알림이 뜨면 \*\*[설치]\*\*를 클릭하세요.

-----

## 4\. 실행 방법

1.  `DemoApplication.java` 파일을 엽니다.
2.  화면 오른쪽 상단의 **[재생(Run)]** 버튼을 클릭합니다.
3.  터미널 콘솔에 `process running for [초]` 메시지가 뜨면 정상적으로 실행된 것입니다.
4.  **최종 확인:** 크롬 브라우저에서 아래 주소로 접속했을 때 로그인 페이지가 나타나면 성공입니다.
      * [http://localhost:8080/login/oauth2/code/kakao](https://www.google.com/search?q=http://localhost:8080/login/oauth2/code/kakao)

-----

# 데이터베이스 데이터 확인 가이드

## 1. DBeaver 설치
1. [DBeaver 공식 다운로드 페이지](https://dbeaver.io/download/)에 접속하여 설치 파일을 다운로드합니다.
2. 설치 프로세스에서 **[Next]**를 계속 눌러 설치를 완료합니다.
3. 실행 후 나타나는 **데이터 개선 제공(통계 송신)** 및 **샘플 데이터베이스 생성** 팝업은 **[거부/아니오]**를 선택합니다.

---

## 2. 데이터베이스 연결 설정
1. DBeaver 왼쪽 상단의 **파란색 플러그 아이콘(새 데이터베이스 연결)**을 클릭합니다.
2. 데이터베이스 목록에서 **MySQL**을 선택합니다.
3. **Connection Settings**에 `application.yml` 파일의 정보를 참조하여 다음 내용을 입력합니다.

| 항목 | 입력 내용 (application.yml 참조) |
| :--- | :--- |
| **Server Host** | 3번째 줄 `url` 부분의 `rupa...com`까지의 주소를 입력 |
| **Port** | `3306` |
| **Username** | 4번째줄 `username` 입력 |
| **Password** | 5번째줄 `password` 입력 |

4. **[완료(Finish)]**를 누릅니다.
5. 연결 과정에서 **드라이버 다운로드** 창이 뜨면 **[Download]** 버튼을 눌러 설치를 진행합니다.

---

## 3. 데이터 확인 방법
연결이 완료되면 왼쪽의 **데이터베이스 네비게이터**에서 아래 경로로 이동하여 데이터를 확인할 수 있습니다.

1.  **경로:** `rupa` 연결 선택 ➔ `Databases` ➔ `ruparuap-db-name` ➔ `Tables` ➔ `users`
2.  `users` 테이블을 더블 클릭합니다.
3.  우측 화면 상단 탭에서 **[Properties]** 왼쪽에 있는 **[Data]** 탭을 클릭합니다.
4.  현재 테이블에 저장된 전체 데이터를 확인합니다.
