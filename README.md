# 미니프로젝트2: 오늘의 이웃, 오이마켓
> LG U+ 유레카 교육과정에서 미니 프로젝트로 진행한 당근마켓 클론 코딩 프로젝트입니다. <br />
> 개발기간: 2024.09.10 ~ 2024.09.24 (약 2주)
<br />

![image](https://github.com/user-attachments/assets/b8dffa59-3570-4e8c-a4fa-56d6dfad9129)

## _Intro._
- 자신의 중고 물품을 판매하고, 거래할 수 있는 커뮤니티 공간입니다.
- 유저는 구매하고자 하는 물품 게시글에 댓글을 작성할 수 있습니다.
- 찾고자 하는 물품이 있다면, 검색창에 키워드를 입력하여 관련된 게시글을 조회할 수 있습니다.

<br />

## _Documents._
- [회의록](https://jet-glue-2e7.notion.site/3fbe0b007902411d8d3f69b29ccbd026?pvs=4)
- [RestAPI 기반 API 명세서 작성](https://jet-glue-2e7.notion.site/API-65e1b23508844534a3940ae5261a2744?pvs=4)
- [프로젝트 컨벤션](https://jet-glue-2e7.notion.site/RULES-be85d4478f2747b0be93ddabf286d58e?pvs=4)

<br />

## _ER Diagram._
![erd-2](https://github.com/user-attachments/assets/71bbbfad-97ce-43b7-b601-642334e78ad9)

<br />

## _Stacks._
> Backend
- Java 17, SpringBoot 3.3
- Spring Security, JWT
- Spring Data JPA
- MySQL
- JUnit 5, Mockito

> Frontend
- HTML5/CSS
- Vanilla JS

<br />

## _Member._

<div align="center">
  
| **김영철** | **김정동** | **이승희** | **임민아** | **정시은** |
| :------: |  :------: | :------: | :------: | :------: |
| [<img src="https://github.com/user-attachments/assets/4661e9b5-5450-4f5c-8051-282bd7e7abd2" height=150 width=150> <br/> @good4y](https://github.com/good4y) | [<img src="https://avatars.githubusercontent.com/u/115388726?v=4" height=150 width=150> <br/> @hellokorea](https://github.com/hellokorea) | [<img src="https://avatars.githubusercontent.com/u/87460638?v=4" height=150 width=150> <br/> @leeseunghee00](https://github.com/leeseunghee00) | [<img src="https://github.com/user-attachments/assets/58c8732a-6c72-49b7-ae7f-61bf2c32696b" height=150 width=150> <br/> @01MINAH](https://github.com/01MINAH) | [<img src="https://avatars.githubusercontent.com/u/80161733?v=4" height=150 width=150> <br/> @Sieun53](https://github.com/Sieun53) |

</div>

#### 김영철(팀장)
- Comment CRUD
- Comment Test Code 작성
- Comment UI
- Board 상세 조회 UI

#### 김정동(QA)
- Auth (login/logout)
- Auth Test Code 작성
- Auth UI

#### 이승희
- Board 조회 및 검색 구현
- Board Test Code 작성
- Main 및 Board 조회 UI

#### 임민아
- Member 조회/수정/삭제 구현
- Member Test Code 작성
- Member UI
  
#### 정시은
- Board 생성/수정/삭제 구현
- Board Test Code 작성
- Board 생성/수정/삭제 UI

<br />

## _Achievements._

도메인별 모든 메소드의 단위 테스트를 진행했고 약 150개의 테스트를 작성하였습니다.
그 결과, JpaAuditingConfig 를 제외한 핵심 비즈니스 로직과 기능의 **테스트 커버리지를 100%** 충족했습니다.

![image](https://github.com/user-attachments/assets/2f502ef7-aa8f-406c-b8ea-5ffcad9a4471)
![image](https://github.com/user-attachments/assets/cd32f76c-ee6b-4ec7-89d7-5f9a7bf233b9)

