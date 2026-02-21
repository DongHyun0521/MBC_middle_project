
DROP TABLE IF EXISTS member_vehicle;	-- 
DROP TABLE IF EXISTS reservation;		-- 
DROP TABLE IF EXISTS payment;			-- 

DROP TABLE IF EXISTS med_staff;			-- 
DROP TABLE IF EXISTS med_dept;			-- 

DROP TABLE IF EXISTS notice;			-- 
DROP TABLE IF EXISTS faq;				-- 
DROP TABLE IF EXISTS voc;				-- 
DROP TABLE IF EXISTS health_story;		-- 

DROP TABLE IF EXISTS admin_staff;		-- 
DROP TABLE IF EXISTS admin_dept;		-- 

DROP TABLE IF EXISTS parking_spot;		-- 
DROP TABLE IF EXISTS parking_log;		-- 

DROP TABLE IF EXISTS mem;				-- 

--==================================================

-- 회원 정보
CREATE TABLE mem (
    mem_id SERIAL PRIMARY KEY,						-- PK
    id VARCHAR(20) UNIQUE NOT NULL,					-- 아이디 (6~16자, 영어 소문자+숫자만 사용가능)
    password VARCHAR(100) NOT NULL,					-- 비밀번호 (6~16자, 영어 대/소문자 1개이상, 특수문자 1개이상)
    name VARCHAR(30) NOT NULL,						-- 이름
    birthday INTEGER NOT NULL,						-- 생년월일 (숫자 8자리)
    gender INTEGER NOT NULL,						-- 성별 (남:1, 여:2)
    address VARCHAR(100) NOT NULL,					-- 주소
	address_detail VARCHAR(100),					-- 상세 주소
    phone_number VARCHAR(20) NOT NULL,				-- 전화번호
    email VARCHAR(50) NOT NULL,						-- 이메일
    del INTEGER NOT NULL DEFAULT 0,					-- 탈퇴 여부
	create_time TIMESTAMP NOT NULL DEFAULT now()	-- 가입 일시
);
-- 회원 차량 정보
CREATE TABLE member_vehicle (
    vehicle_id SERIAL PRIMARY KEY,					-- PK
    mem_id INTEGER NOT NULL
		REFERENCES mem(mem_id) ON DELETE CASCADE,	-- FK (mem.mem_id)
    vehicle_num VARCHAR(20) NOT NULL,				-- 차량 번호 (회원이 등록)
    vehicle_type VARCHAR(20) NOT NULL,				-- 차종
    fuel_type VARCHAR(20) NOT NULL,					-- 연료
	create_time TIMESTAMP NOT NULL DEFAULT now()	-- 등록 일시
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
		REFERENCES parking_log(parking_log_id),	-- FK (parking_log.parking_log_id)
    floor INTEGER NOT NULL,						-- 층 (1, 2, ...)
    zone VARCHAR(1) NOT NULL,					-- 구역 (A, B, ...)
	spot_number INTEGER NOT NULL,				-- 번호 (1, 2, ...)
	distance_from_entrance INTEGER NOT NULL,	-- 입구로부터의 거리
	is_parked BOOLEAN NOT NULL DEFAULT FALSE	-- 주차 여부
);
-- 의료 부서
CREATE TABLE med_dept (
    med_dept_id SERIAL PRIMARY KEY,		-- PK
	med_dept_name VARCHAR(50) NOT NULL,	-- 의료 부서명
	dept_location VARCHAR(100),			-- 부서 위치
	dept_phone_number VARCHAR(20)		-- 부서 전화번호
);
-- 의료진
CREATE TABLE med_staff (
	med_staff_id SERIAL PRIMARY KEY,				-- PK
	mem_id INTEGER NOT NULL
		REFERENCES mem(mem_id) ON DELETE CASCADE,	-- FK (mem.mem_id)
	role VARCHAR(20) NOT NULL,						-- 직업
	license_number VARCHAR(50) NOT NULL,			-- 의료진 등록 번호 (의사: 13또는 14로 시작하는 6자리 숫자 / 간호사: 50또는 55로 시작하는 6자리 숫자)
	status VARCHAR(20) NOT NULL DEFAULT '재직',		-- 재직 상태
	med_dept_id INTEGER
		REFERENCES med_dept(med_dept_id),			-- FK (med_dept.med_dept_id)
	spot_id INTEGER
		REFERENCES parking_spot(spot_id),			-- FK (parking_spot.spot_id)
	create_time TIMESTAMP NOT NULL DEFAULT now()	-- 회원가입 일시
);
-- 진료 예약
CREATE TABLE reservation (
	reservation_id SERIAL PRIMARY KEY,		-- PK
	mem_id INTEGER NOT NULL
		REFERENCES mem(mem_id),				-- FK (mem.mem_id)
	med_dept_id INTEGER NOT NULL
		REFERENCES med_dept(med_dept_id),	-- FK (med_dept.med_dept_id)
	doctor_id INTEGER NOT NULL
		REFERENCES med_staff(med_staff_id),	-- FK (med_staff.med_staff_id)
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
	admin_dept_id SERIAL PRIMARY KEY,			-- PK
	admin_dept_name VARCHAR(50) NOT NULL,		-- 행정 부서 이름
	dept_location VARCHAR(100),					-- 부서 위치
	dept_phone_number VARCHAR(20)				-- 부서 전화번호
);
-- 행정직
CREATE TABLE admin_staff (
	admin_staff_id SERIAL PRIMARY KEY,				-- PK
	mem_id INTEGER NOT NULL
		REFERENCES mem(mem_id) ON DELETE CASCADE,	-- FK (mem.mem_id)
	rank VARCHAR(20),								-- 직급
	emp_number VARCHAR(50) NOT NULL,				-- 사원번호 (알파벳 한자리 + 숫자 8자리)
	status VARCHAR(20) NOT NULL DEFAULT '재직',		-- 재직 상태
	admin_dept_id INTEGER
		REFERENCES admin_dept(admin_dept_id),		-- FK (admin_dept.admin_dept_id)
	spot_id INTEGER
		REFERENCES parking_spot(spot_id),			-- FK (parking_spot.spot_id)
	create_time TIMESTAMP NOT NULL DEFAULT now()	-- 가입 일시
);
-- 고객의소리
CREATE TABLE voc (
	voc_id SERIAL PRIMARY KEY,						-- PK
	mem_id INTEGER NOT NULL
		REFERENCES mem(mem_id),						-- FK (mem.mem_id)
	title VARCHAR(200) NOT NULL,					-- 제목
	content VARCHAR(4000) NOT NULL,					-- 내용
	thumbnail_img VARCHAR(500),						-- DB에 저장될 이미지 경로 (/images/abc.jpg)
	write_date TIMESTAMP NOT NULL DEFAULT now(),	-- 작성일시
	
	admin_staff_id INTEGER
		REFERENCES admin_staff(admin_staff_id),		-- FK (admin_staff.admin_staff_id)
	answer_content VARCHAR(4000),					-- 답변 내용
	answer_write_date TIMESTAMP,					-- 답변 작성일시
	answer_status BOOLEAN NOT NULL DEFAULT FALSE,	-- 답변 여부
	
	del INTEGER NOT NULL DEFAULT 0,	-- 글 삭제 여부
	delete_date TIMESTAMP			-- 삭제 버튼 눌린 시간
);
-- 공지사항
CREATE TABLE notice (
	notice_id SERIAL PRIMARY KEY,					-- PK
	admin_staff_id INTEGER NOT NULL
		REFERENCES admin_staff(admin_staff_id),		-- FK (admin_staff.admin_staff_id)
	top_fix BOOLEAN NOT NULL DEFAULT FALSE,			-- 상단 고정 여부
	title VARCHAR(200) NOT NULL,					-- 제목
	content VARCHAR(4000) NOT NULL,					-- 내용
	thumbnail_img VARCHAR(500),						-- DB에 저장될 이미지 경로 (/images/abc.jpg)
	write_date TIMESTAMP NOT NULL DEFAULT now(),	-- 작성일시
	read_count INTEGER NOT NULL DEFAULT 0,			-- 조회수
	del INTEGER NOT NULL DEFAULT 0					-- 삭제 여부
);
-- FAQ
CREATE TABLE faq (
	faq_id SERIAL PRIMARY KEY,						-- PK
	admin_staff_id INTEGER NOT NULL
		REFERENCES admin_staff(admin_staff_id),		-- FK (admin_staff.admin_staff_id)
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

--==================================================

DELETE FROM reservation;
DELETE FROM voc;
DELETE FROM notice;
DELETE FROM faq;
DELETE FROM health_story;
DELETE FROM member_vehicle;
DELETE FROM payment;

DELETE FROM med_staff;
DELETE FROM admin_staff;
DELETE FROM parking_spot;

DELETE FROM parking_log;
DELETE FROM med_dept;
DELETE FROM admin_dept;
DELETE FROM mem;

--==================================================

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
SELECT * FROM health_story;		-- 건강이야기

--==================================================

-- INSERT 의료 부서
INSERT INTO med_dept (med_dept_name, dept_location, dept_phone_number) VALUES 
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

--==================================================

-- INSERT 행정 부서
INSERT INTO admin_dept (admin_dept_name, dept_location, dept_phone_number) VALUES 
	('총무과', '본관 B101호', '02-2222-2223'),
	('인사팀', '본관 B102호', '02-2222-2224'),
	('원무과', '본관 100호(로비)', '02-2222-2225'),
	('재무회계팀', '본관 B103호', '02-2222-2226'),
	('시설관리팀', '별관 B101호', '02-2222-2227'),
	('의무기록팀', '별관 105호', '02-2222-2228'),
	('홍보팀', '본관 501호', '02-2222-2229'),
	('보안팀', '본관 100호(보안실)', '02-2222-2230');

--==================================================

-- 회원 10명
WITH gen_mem AS (
    SELECT
        i AS mem_idx,
        'user' || LPAD(i::TEXT, 4, '0') AS gen_id,
        'User123!@' AS gen_pw,
        '일반회원_' || i AS gen_name,
        19900000 + i AS gen_birth,
        (i % 2) + 1 AS gen_gender,
        '서울시 일반구 회원동' AS gen_addr,
        i || '길' AS gen_addr_detail,
        '010-1111-' || LPAD(i::TEXT, 4, '0') AS gen_phone,
        'user' || LPAD(i::TEXT, 4, '0') || '@hospital.com' AS gen_email
    FROM generate_series(1, 10) AS s(i)
)
INSERT INTO mem (id, password, name, birthday, gender, address, address_detail, phone_number, email)
SELECT gen_id, gen_pw, gen_name, gen_birth, gen_gender, gen_addr, gen_addr_detail, gen_phone, gen_email
FROM gen_mem;

--==================================================

-- 의사 부서별 10명 (10 * 10 = 100명)
WITH gen_doc AS (
    SELECT
        dept.id AS dept_id,
        doc.idx AS doc_idx,
        'doc' || LPAD(dept.id::TEXT, 2, '0') || LPAD(doc.idx::TEXT, 2, '0') AS gen_id,
        'Doctor123!@' AS gen_pw,
        '의사_' || dept.id || '_' || doc.idx AS gen_name,
        19800000 + (dept.id * 100) + doc.idx AS gen_birth,
        (doc.idx % 2) + 1 AS gen_gender,
        '서울시 메디컬구 의사동' AS gen_addr,
        dept.id || '길 ' || doc.idx || '호' AS gen_addr_detail,
        '010-2222-' || LPAD(((dept.id - 1) * 10 + doc.idx)::TEXT, 4, '0') AS gen_phone,
        'doc' || LPAD(dept.id::TEXT, 2, '0') || LPAD(doc.idx::TEXT, 2, '0') || '@hospital.com' AS gen_email,
        '13' || LPAD(dept.id::TEXT, 2, '0') || LPAD(doc.idx::TEXT, 2, '0') AS gen_license
    FROM generate_series(1, 10) AS dept(id)
    CROSS JOIN generate_series(1, 10) AS doc(idx)
),
inserted_mem AS (
    INSERT INTO mem (id, password, name, birthday, gender, address, address_detail, phone_number, email)
    SELECT gen_id, gen_pw, gen_name, gen_birth, gen_gender, gen_addr, gen_addr_detail, gen_phone, gen_email
    FROM gen_doc
    RETURNING mem_id, id
)
INSERT INTO med_staff (mem_id, role, license_number, status, med_dept_id)
SELECT m.mem_id, '의사', g.gen_license, '재직', g.dept_id
FROM inserted_mem m
JOIN gen_doc g ON m.id = g.gen_id;

--==================================================

-- 간호사 부서별 10명 (10 * 10 = 100명)
WITH gen_nurse AS (
    SELECT
        dept.id AS dept_id,
        nur.idx AS nur_idx,
        'nur' || LPAD(dept.id::TEXT, 2, '0') || LPAD(nur.idx::TEXT, 2, '0') AS gen_id,
        'Nurse123!@' AS gen_pw,
        '간호사_' || dept.id || '_' || nur.idx AS gen_name,
        19850000 + (dept.id * 100) + nur.idx AS gen_birth,
        2 AS gen_gender, -- 간호사는 예시로 성별 2(여) 고정 배정
        '서울시 메디컬구 간호동' AS gen_addr,
        dept.id || '길 ' || nur.idx || '호' AS gen_addr_detail,
        '010-3333-' || LPAD(((dept.id - 1) * 10 + nur.idx)::TEXT, 4, '0') AS gen_phone,
        'nur' || LPAD(dept.id::TEXT, 2, '0') || LPAD(nur.idx::TEXT, 2, '0') || '@hospital.com' AS gen_email,
        '50' || LPAD(dept.id::TEXT, 2, '0') || LPAD(nur.idx::TEXT, 2, '0') AS gen_license
    FROM generate_series(1, 10) AS dept(id)
    CROSS JOIN generate_series(1, 10) AS nur(idx)
),
inserted_mem AS (
    INSERT INTO mem (id, password, name, birthday, gender, address, address_detail, phone_number, email)
    SELECT gen_id, gen_pw, gen_name, gen_birth, gen_gender, gen_addr, gen_addr_detail, gen_phone, gen_email
    FROM gen_nurse
    RETURNING mem_id, id
)
INSERT INTO med_staff (mem_id, role, license_number, status, med_dept_id)
SELECT m.mem_id, '간호사', g.gen_license, '재직', g.dept_id
FROM inserted_mem m
JOIN gen_nurse g ON m.id = g.gen_id;

--==================================================

-- 행정 부서별 10명 (10 * 8 = 80명)
WITH gen_admin AS (
    SELECT
        dept.id AS dept_id,
        adm.idx AS adm_idx,
        'adm' || LPAD(dept.id::TEXT, 2, '0') || LPAD(adm.idx::TEXT, 2, '0') AS gen_id,
        'Admin123!@' AS gen_pw,
        '행정직_' || dept.id || '_' || adm.idx AS gen_name,
        19920000 + (dept.id * 100) + adm.idx AS gen_birth,
        (adm.idx % 2) + 1 AS gen_gender,
        '서울시 메디컬구 행정동' AS gen_addr,
        dept.id || '길 ' || adm.idx || '호' AS gen_addr_detail,
        '010-4444-' || LPAD(((dept.id - 1) * 10 + adm.idx)::TEXT, 4, '0') AS gen_phone,
        'adm' || LPAD(dept.id::TEXT, 2, '0') || LPAD(adm.idx::TEXT, 2, '0') || '@hospital.com' AS gen_email,
        'A0000' || LPAD(dept.id::TEXT, 2, '0') || LPAD(adm.idx::TEXT, 2, '0') AS gen_emp_num,
        '사원' AS gen_rank
    FROM generate_series(1, 8) AS dept(id) -- 행정부서 8개 기준
    CROSS JOIN generate_series(1, 10) AS adm(idx)
),
inserted_mem AS (
    INSERT INTO mem (id, password, name, birthday, gender, address, address_detail, phone_number, email)
    SELECT gen_id, gen_pw, gen_name, gen_birth, gen_gender, gen_addr, gen_addr_detail, gen_phone, gen_email
    FROM gen_admin
    RETURNING mem_id, id
)
INSERT INTO admin_staff (mem_id, rank, emp_number, status, admin_dept_id)
SELECT m.mem_id, g.gen_rank, g.gen_emp_num, '재직', g.dept_id
FROM inserted_mem m
JOIN gen_admin g ON m.id = g.gen_id;

--==================================================

-- INSERT 주차 자리
INSERT INTO parking_spot (floor, zone, spot_number, distance_from_entrance, is_parked)
VALUES
  (1, 'A', 1, 242, false),
  (1, 'A', 2, 283, false),
  (1, 'A', 3, 324, false),
  (1, 'A', 4, 365, false),
  (1, 'A', 5, 526, false),
  (1, 'A', 6, 567, false),
  (1, 'A', 7, 608, false),
  (1, 'A', 8, 649, false),
  (1, 'B', 1, 295, false),
  (1, 'B', 2, 336, false),
  (1, 'B', 3, 377, false),
  (1, 'B', 4, 418, false),
  (1, 'B', 5, 473, false),
  (1, 'B', 6, 514, false),
  (1, 'B', 7, 555, false),
  (1, 'B', 8, 596, false),
  (1, 'B', 9, 465, false),
  (1, 'B', 10, 506, false),
  (1, 'B', 11, 547, false),
  (1, 'B', 12, 588, false),
  (1, 'B', 13, 643, false),
  (1, 'B', 14, 684, false),
  (1, 'B', 15, 725, false),
  (1, 'B', 16, 766, false),
  (1, 'C', 1, 465, false),
  (1, 'C', 2, 506, false),
  (1, 'C', 3, 547, false),
  (1, 'C', 4, 588, false),
  (1, 'C', 5, 643, false),
  (1, 'C', 6, 684, false),
  (1, 'C', 7, 725, false),
  (1, 'C', 8, 766, false),
  (1, 'C', 9, 635, false),
  (1, 'C', 10, 676, false),
  (1, 'C', 11, 717, false),
  (1, 'C', 12, 758, false),
  (1, 'C', 13, 813, false),
  (1, 'C', 14, 854, false),
  (1, 'C', 15, 895, false),
  (1, 'C', 16, 936, false),
  (1, 'D', 1, 582, false),
  (1, 'D', 2, 623, false),
  (1, 'D', 3, 664, false),
  (1, 'D', 4, 705, false),
  (1, 'D', 5, 866, false),
  (1, 'D', 6, 907, false),
  (1, 'D', 7, 948, false),
  (1, 'D', 8, 989, false),
  (1, 'E', 1, 763, false),
  (1, 'E', 2, 804, false),
  (1, 'E', 3, 845, false),
  (1, 'E', 4, 900, false),
  (1, 'E', 5, 941, false),
  (1, 'E', 6, 982, false),
  (2, 'A', 1, 242, false),
  (2, 'A', 2, 283, false),
  (2, 'A', 3, 324, false),
  (2, 'A', 4, 365, false),
  (2, 'A', 5, 526, false),
  (2, 'A', 6, 567, false),
  (2, 'A', 7, 608, false),
  (2, 'A', 8, 649, false),
  (2, 'B', 1, 295, false),
  (2, 'B', 2, 336, false),
  (2, 'B', 3, 377, false),
  (2, 'B', 4, 418, false),
  (2, 'B', 5, 473, false),
  (2, 'B', 6, 514, false),
  (2, 'B', 7, 555, false),
  (2, 'B', 8, 596, false),
  (2, 'B', 9, 465, false),
  (2, 'B', 10, 506, false),
  (2, 'B', 11, 547, false),
  (2, 'B', 12, 588, false),
  (2, 'B', 13, 643, false),
  (2, 'B', 14, 684, false),
  (2, 'B', 15, 725, false),
  (2, 'B', 16, 766, false),
  (2, 'C', 1, 465, false),
  (2, 'C', 2, 506, false),
  (2, 'C', 3, 547, false),
  (2, 'C', 4, 588, false),
  (2, 'C', 5, 643, false),
  (2, 'C', 6, 684, false),
  (2, 'C', 7, 725, false),
  (2, 'C', 8, 766, false),
  (2, 'C', 9, 635, false),
  (2, 'C', 10, 676, false),
  (2, 'C', 11, 717, false),
  (2, 'C', 12, 758, false),
  (2, 'C', 13, 813, false),
  (2, 'C', 14, 854, false),
  (2, 'C', 15, 895, false),
  (2, 'C', 16, 936, false),
  (2, 'D', 1, 582, false),
  (2, 'D', 2, 623, false),
  (2, 'D', 3, 664, false),
  (2, 'D', 4, 705, false),
  (2, 'D', 5, 866, false),
  (2, 'D', 6, 907, false),
  (2, 'D', 7, 948, false),
  (2, 'D', 8, 989, false),
  (2, 'E', 1, 763, false),
  (2, 'E', 2, 804, false),
  (2, 'E', 3, 845, false),
  (2, 'E', 4, 900, false),
  (2, 'E', 5, 941, false),
  (2, 'E', 6, 982, false),
  (3, 'A', 1, 242, false),
  (3, 'A', 2, 283, false),
  (3, 'A', 3, 324, false),
  (3, 'A', 4, 365, false),
  (3, 'A', 5, 526, false),
  (3, 'A', 6, 567, false),
  (3, 'A', 7, 608, false),
  (3, 'A', 8, 649, false),
  (3, 'B', 1, 295, false),
  (3, 'B', 2, 336, false),
  (3, 'B', 3, 377, false),
  (3, 'B', 4, 418, false),
  (3, 'B', 5, 473, false),
  (3, 'B', 6, 514, false),
  (3, 'B', 7, 555, false),
  (3, 'B', 8, 596, false),
  (3, 'B', 9, 465, false),
  (3, 'B', 10, 506, false),
  (3, 'B', 11, 547, false),
  (3, 'B', 12, 588, false),
  (3, 'B', 13, 643, false),
  (3, 'B', 14, 684, false),
  (3, 'B', 15, 725, false),
  (3, 'B', 16, 766, false),
  (3, 'C', 1, 465, false),
  (3, 'C', 2, 506, false),
  (3, 'C', 3, 547, false),
  (3, 'C', 4, 588, false),
  (3, 'C', 5, 643, false),
  (3, 'C', 6, 684, false),
  (3, 'C', 7, 725, false),
  (3, 'C', 8, 766, false),
  (3, 'C', 9, 635, false),
  (3, 'C', 10, 676, false),
  (3, 'C', 11, 717, false),
  (3, 'C', 12, 758, false),
  (3, 'C', 13, 813, false),
  (3, 'C', 14, 854, false),
  (3, 'C', 15, 895, false),
  (3, 'C', 16, 936, false),
  (3, 'D', 1, 582, false),
  (3, 'D', 2, 623, false),
  (3, 'D', 3, 664, false),
  (3, 'D', 4, 705, false),
  (3, 'D', 5, 866, false),
  (3, 'D', 6, 907, false),
  (3, 'D', 7, 948, false),
  (3, 'D', 8, 989, false),
  (3, 'E', 1, 763, false),
  (3, 'E', 2, 804, false),
  (3, 'E', 3, 845, false),
  (3, 'E', 4, 900, false),
  (3, 'E', 5, 941, false),
  (3, 'E', 6, 982, false);
  