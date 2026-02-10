

DROP TABLE IF EXISTS member_vehicle;	-- 
DROP TABLE IF EXISTS reservation;		-- 
DROP TABLE IF EXISTS payment;			-- 

DROP TABLE IF EXISTS med_staff;			-- 
DROP TABLE IF EXISTS med_dept;			-- 

DROP TABLE IF EXISTS notice;			-- 
DROP TABLE IF EXISTS faq;				-- 
DROP TABLE IF EXISTS voc;				-- 

DROP TABLE IF EXISTS admin_staff;		-- 
DROP TABLE IF EXISTS admin_dept;		-- 

DROP TABLE IF EXISTS parking_spot;		-- 
DROP TABLE IF EXISTS parking_log;		-- 

DROP TABLE IF EXISTS mem;				-- 

-- 회원 정보
CREATE TABLE mem (
    mem_id SERIAL PRIMARY KEY,						-- PK
    id VARCHAR(20) UNIQUE NOT NULL,					-- 아이디 (6~16자, 영어 소문자+숫자만 사용가능)
    password VARCHAR(100) NOT NULL,					-- 비밀번호 (6~16자, 영어 대/소문자 1개이상, 특수문자 1개이상)
    name VARCHAR(30) NOT NULL,						-- 이름
    birthday INTEGER NOT NULL,						-- 생년월일 (숫자로 8자리)
    gender INTEGER NOT NULL,						-- 성별 (남:1, 여:2)
    address VARCHAR(100) NOT NULL,					-- 주소
	address_detail VARCHAR(100) NOT NULL,			-- 상세 주소
    phone_number VARCHAR(20) NOT NULL,				-- 전화번호
    email VARCHAR(50) NOT NULL,						-- 이메일
    del INTEGER NOT NULL DEFAULT 0,					-- 탈퇴 여부
	create_time TIMESTAMP NOT NULL DEFAULT now()	-- 회원가입 일시
);
-- 회원 차량 정보
CREATE TABLE member_vehicle (
    vehicle_id SERIAL PRIMARY KEY,					-- PK
    mem_id INTEGER NOT NULL
		REFERENCES mem(mem_id) ON DELETE CASCADE,	-- FK (mem.mem_id)
    vehicle_num VARCHAR(20) NOT NULL,				-- 차량 번호 (회원이 등록)
    vehicle_type VARCHAR(20) NOT NULL,				-- 차종
    fuel_type VARCHAR(20) NOT NULL,					-- 연료
	create_time TIMESTAMP NOT NULL DEFAULT now()	-- 차량 추가 일시
);
-- 주차장 기록
CREATE TABLE parking_log (
    parking_log_id SERIAL PRIMARY KEY,				-- PK
    vehicle_num VARCHAR(20) NOT NULL,				-- 차량 번호 (OCR 결과)
    entry_time TIMESTAMP NOT NULL DEFAULT now(),	-- 입차 시간
    exit_time TIMESTAMP,							-- 출차 시간
    is_member BOOLEAN NOT NULL DEFAULT FALSE,		-- 회원 여부
    payment_status BOOLEAN NOT NULL DEFAULT FALSE	-- 결제 여부
);
-- 요금 결제 기록
CREATE TABLE payment (
    pay_id SERIAL PRIMARY KEY,						-- PK
    parking_log_id INTEGER NOT NULL
		REFERENCES parking_log(parking_log_id),		-- FK (parking_log.parking_log_id)
    mem_id INTEGER
		REFERENCES mem(mem_id) ON DELETE SET NULL,	-- FK (mem.mem_id)
    amount INTEGER CHECK (amount >= 0),				-- 결제 금액
    pay_method VARCHAR(50),							-- 결제 수단
    pay_date TIMESTAMP NOT NULL DEFAULT now()		-- 결제 일시
);
-- 주차장 구역 상태
CREATE TABLE parking_spot (
    spot_id SERIAL PRIMARY KEY,					-- PK
    parking_log_id INTEGER DEFAULT NULL
		REFERENCES parking_log(parking_log_id),	-- FK(parking_log.parking_log_id)
    floor INTEGER NOT NULL,						-- 층(1, 2, ...)
    zone VARCHAR(1) NOT NULL,					-- 구역(A, B, ...)
	spot_number INTEGER NOT NULL,				-- 번호(1, 2, ...)
	distance_from_entrance INTEGER NOT NULL,	-- 입구로부터의 거리
	is_parked BOOLEAN NOT NULL DEFAULT FALSE	-- 해당 위치 주차 여부
);
-- 의료 부서
CREATE TABLE med_dept (
    med_dept_id SERIAL PRIMARY KEY,	-- PK
	dept_name VARCHAR(50) NOT NULL,	-- 의료 부서 이름
	dept_location VARCHAR(100),		-- 부서 위치
	dept_phone_number VARCHAR(20)	-- 부서 전화번호
);
-- 의료진
CREATE TABLE med_staff (
	med_staff_id SERIAL PRIMARY KEY,				-- PK
	mem_id INTEGER NOT NULL
		REFERENCES mem(mem_id) ON DELETE CASCADE,	-- FK(mem.mem_id)
	role VARCHAR(20) NOT NULL,						-- 직업
	license_number VARCHAR(50) NOT NULL,			-- 의료진 등록 번호
	status VARCHAR(20) NOT NULL DEFAULT '재직',		-- 재직 상태
	med_dept_id INTEGER
		REFERENCES med_dept(med_dept_id),			-- FK(med_dept.med_dept_id)
	spot_id INTEGER
		REFERENCES parking_spot(spot_id),			-- FK(parking_spot.spot_id)
	create_time TIMESTAMP NOT NULL DEFAULT now()	-- 회원가입 일시
);
-- 진료 예약
CREATE TABLE reservation (
	reservation_id SERIAL PRIMARY KEY,		-- PK
	mem_id INTEGER NOT NULL
		REFERENCES mem(mem_id),				-- FK(mem.mem_id)
	med_dept_id INTEGER NOT NULL
		REFERENCES med_dept(med_dept_id),	-- FK(med_dept.med_dept_id)
	doctor_id INTEGER NOT NULL
		REFERENCES med_staff(med_staff_id),	-- FK(med_staff.med_staff_id)
	reservation_date INTEGER NOT NULL,						-- 예약 날짜
	reservation_time VARCHAR(30) NOT NULL,					-- 예약 시간
	reservation_type VARCHAR(20) NOT NULL,					-- 예약 종류
	visit_type VARCHAR(20) NOT NULL DEFAULT '초진',			-- 초진/재진
	reservation_status VARCHAR(20) NOT NULL DEFAULT '예약',	-- 예약 상태
	reservation_memo VARCHAR(1000),							-- 예약 관련 메모
	reservation_made_time TIMESTAMP NOT NULL DEFAULT now()	-- 예약 당시 시간
);
-- 행정 부서
CREATE TABLE admin_dept (
	admin_dept_id SERIAL PRIMARY KEY,	-- PK
	dept_name VARCHAR(50) NOT NULL,		-- 행정 부서 이름
	dept_location VARCHAR(100),			-- 부서 위치
	dept_phone_number VARCHAR(20)		-- 부서 전화번호
);
-- 행정직
CREATE TABLE admin_staff (
	admin_staff_id SERIAL PRIMARY KEY,				-- PK
	mem_id INTEGER NOT NULL
		REFERENCES mem(mem_id) ON DELETE CASCADE,	-- FK(mem.mem_id)
	rank VARCHAR(20),								-- 직급
	emp_number VARCHAR(50) NOT NULL,				-- 사원번호
	status VARCHAR(20) NOT NULL DEFAULT '재직',		-- 재직 상태
	admin_dept_id INTEGER
		REFERENCES admin_dept(admin_dept_id),		-- FK(admin_dept.admin_dept_id)
	spot_id INTEGER
		REFERENCES parking_spot(spot_id),			-- FK(parking_spot.spot_id)
	create_time TIMESTAMP NOT NULL DEFAULT now()	-- 회원가입 일시
);
-- 고객의소리
CREATE TABLE voc (
	voc_id SERIAL PRIMARY KEY,						-- PK
	mem_id INTEGER NOT NULL
		REFERENCES mem(mem_id),						-- FK(mem.mem_id)
	title VARCHAR(200) NOT NULL,					-- 제목
	content VARCHAR(4000) NOT NULL,					-- 내용
	thumbnail_img VARCHAR(500),						-- DB에 저장될 이미지 경로 (/images/abc.jpg)
	write_date TIMESTAMP NOT NULL DEFAULT now(),	-- 작성일시
	
	admin_staff_id INTEGER
		REFERENCES admin_staff(admin_staff_id),		-- FK(admin_staff.admin_staff_id)
	answer_title VARCHAR(200),						-- 답변 제목
	answer_content VARCHAR(4000),					-- 답변 내용
	answer_write_date TIMESTAMP,					-- 답변 작성일시
	
	answer_status BOOLEAN NOT NULL DEFAULT FALSE,	-- 답변 여부
	del INTEGER NOT NULL DEFAULT 0,					-- 글 삭제 여부
	delete_date TIMESTAMP							-- 삭제 버튼 눌린 시간
);
-- 공지사항
CREATE TABLE notice (
	notice_id SERIAL PRIMARY KEY,					-- PK
	admin_staff_id INTEGER NOT NULL
		REFERENCES admin_staff(admin_staff_id),		-- FK(admin_staff.admin_staff_id)
	top_fix BOOLEAN NOT NULL DEFAULT FALSE,			-- 상단고정 여부
	title VARCHAR(200) NOT NULL,					-- 제목
	content VARCHAR(4000) NOT NULL,					-- 내용
	thumbnail_img VARCHAR(500),						-- DB에 저장될 이미지 경로 (/images/abc.jpg)
	write_date TIMESTAMP NOT NULL DEFAULT now(),	-- 작성일시
	read_count INTEGER NOT NULL DEFAULT 0,			-- 조회수
	del INTEGER NOT NULL DEFAULT 0					-- 공지사항 삭제 여부
);
-- FAQ
CREATE TABLE faq (
	faq_id SERIAL PRIMARY KEY,						-- PK
	admin_staff_id INTEGER NOT NULL
		REFERENCES admin_staff(admin_staff_id),		-- FK(admin_staff.admin_staff_id)
	category VARCHAR(50) NOT NULL,					-- 카테고리
	title VARCHAR(200) NOT NULL,					-- 제목
	content VARCHAR(4000) NOT NULL,					-- 내용
	write_date TIMESTAMP NOT NULL DEFAULT now(),	-- 작성일시
	del INTEGER NOT NULL DEFAULT 0					-- 삭제 여부
);
-- 건강이야기
CREATE TABLE health_story (
	health_story_id SERIAL PRIMARY KEY,				-- PK
	admin_staff_id INTEGER NOT NULL
		REFERENCES admin_staff(admin_staff_id),		-- FK(admin_staff.admin_staff_id)
	title VARCHAR(200) NOT NULL,					-- 제목
	content VARCHAR(4000) NOT NULL,					-- 내용
	thumbnail_img VARCHAR(500),						-- DB에 저장될 이미지 경로 (/images/abc.jpg)
	read_count INTEGER NOT NULL DEFAULT 0,			-- 조회수
	write_date TIMESTAMP NOT NULL DEFAULT now(),	-- 작성일시
	del INTEGER NOT NULL DEFAULT 0					-- 삭제 여부
);

DELETE FROM reservation;
DELETE FROM voc;
DELETE FROM notice;
DELETE FROM faq;
DELETE FROM member_vehicle;
DELETE FROM payment;

DELETE FROM med_staff;
DELETE FROM admin_staff;
DELETE FROM parking_spot;

DELETE FROM parking_log;
DELETE FROM med_dept;
DELETE FROM admin_dept;
DELETE FROM mem;



SELECT * FROM mem;				-- 회원
SELECT * FROM med_staff;		-- 의료진
SELECT * FROM admin_staff;		-- 행정 직원

SELECT * FROM med_dept;			-- 의료 부서 (미리 INSERT)
SELECT * FROM admin_dept;		-- 행정 부서 (미리 INSERT)

SELECT * FROM member_vehicle;	-- 회원 차량

SELECT * FROM parking_log;		-- 주차 기록
SELECT * FROM parking_spot;		-- 주차 위치 (미리 INSERT)
SELECT * FROM payment;			-- 결제

SELECT * FROM reservation;		-- 예약

SELECT * FROM voc;				-- 고객의소리
SELECT * FROM notice;			-- 공지사항
SELECT * FROM faq;				-- FAQ



INSERT INTO med_dept (dept_name, dept_location, dept_phone_number) VALUES 
	('내과', '본관 102호', '02-1111-1112'),
	('정형외과', '본관 103호', '02-1111-1113'),
	('신경외과', '본관 201호', '02-1111-1114'),
	('소아청소년과', '별관 101호', '02-1111-1115'),
	('이비인후과', '별관 102호', '02-1111-1116'),
	('피부과', '별관 201호', '02-1111-1117'),
	('안과', '본관 202호', '02-1111-1118'),
	('치과', '별관 202호', '02-1111-1119'),
	('정신건강의학과', '별관 301호', '02-1111-1120'),
	('응급의학과', '본관 100호', '02-1111-1119');

INSERT INTO admin_dept (dept_name, dept_location, dept_phone_number) VALUES 
	('총무과', '본관 B101호', '02-2222-2223'),
	('인사팀', '본관 B102호', '02-2222-2224'),
	('원무과', '본관 100호(로비)', '02-2222-2225'),
	('재무회계팀', '본관 B103호', '02-2222-2226'),
	('시설관리팀', '별관 B101호', '02-2222-2227'),
	('의무기록팀', '별관 105호', '02-2222-2228'),
	('홍보팀', '본관 501호', '02-2222-2229'),
	('보안팀', '본관 100호(보안실)', '02-2222-2230');
