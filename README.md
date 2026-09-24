# EATZ - API 웹 서버

![EATZ](./images/eatz-main.png)

사용자가 현재 보유한 재료와 도구를 기반으로 즉시 요리할 수 있는 레시피를 찾아주는 소셜 레시피 플랫폼 EATZ의 백엔드 RESTful API 서버입니다. 
EATZ iOS 클라이언트 [EATZ-Client-AppleSilicon](https://github.com/imWhS/EATZ-Client-AppleSilicon)과 통신하는 서버이기도 합니다.

| 카테고리 | 스택 및 기술 |
| --- | --- |
| 언어 | Java |
| 웹 프레임워크 | Spring Boot |
| 데이터베이스 | MySQL (AWS RDS), Redis |
| ORM | Spring Data JPA, QueryDSL |
| 인증 | Spring Security, JWT |
| 스토리지 및 인프라 | AWS EC2, AWS S3, AWS RDS |
| 환경 | Linux |

## 주요 특징

- Spring Security와 JWT 기반의 Stateless 인증 구조를 사용합니다.
- 인증 필터를 통해 JWT 검증 및 인증 처리를 수행하며, 리프레시 토큰 재발급 시 기존 토큰을 폐기하고 새로운 Refresh Token을 발급합니다.
- Spring Data JPA와 QueryDSL을 사용하여 리포지토리(데이터 접근) 계층을 구성합니다.
- 주요 도메인 레코드에 논리적 삭제를 적용합니다.
- Redis를 활용하여 조회 수, 인증 코드 유효성 검사 등의 일시적인 데이터를 관리합니다.
