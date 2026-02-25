
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

-- ==================================================

-- 회원 정보
CREATE TABLE mem (
    mem_id SERIAL PRIMARY KEY,						-- PK
    id VARCHAR(20) UNIQUE NOT NULL,					-- 아이디 (6~16자, 영어 소문자+숫자만 사용가능)
    password VARCHAR(100) NOT NULL,					-- 비밀번호 (6~16자, 영어 대/소문자 1개이상, 특수문자 1개이상)
    name VARCHAR(30) NOT NULL,						-- 이름
    birthday INTEGER NOT NULL,						-- 생년월일 (숫자 8자리)
    gender INTEGER NOT NULL,						-- 성별 (남:1, 여:2)
    address VARCHAR(100) NOT NULL,					-- 주소
	address_detail VARCHAR(100),			-- 상세 주소
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
	license_number VARCHAR(50) NOT NULL,			-- 의료진 등록 번호
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
	emp_number VARCHAR(50) NOT NULL,				-- 사원번호
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
	upload_img VARCHAR(500),						-- DB에 저장될 이미지 경로 (/images/abc.jpg)
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

-- ==================================================

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

-- ==================================================

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

-- ==================================================

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

INSERT INTO admin_dept (admin_dept_name, dept_location, dept_phone_number) VALUES 
	('총무과', '본관 B101호', '02-2222-2223'),
	('인사팀', '본관 B102호', '02-2222-2224'),
	('원무과', '본관 100호(로비)', '02-2222-2225'),
	('재무회계팀', '본관 B103호', '02-2222-2226'),
	('시설관리팀', '별관 B101호', '02-2222-2227'),
	('의무기록팀', '별관 105호', '02-2222-2228'),
	('홍보팀', '본관 501호', '02-2222-2229'),
	('보안팀', '본관 100호(보안실)', '02-2222-2230');
	
-- ==================================================

-- 회원 1
INSERT INTO mem (id, password, name, birthday, gender, address, address_detail, phone_number, email)
VALUES ('mem123', 'Pass123!', '김회원', 19901123, 1, '서울시 강남구 XX오피스텔', '108호', '010-1453-7897', 'user01@naver.com');

-- 회원 2
INSERT INTO mem (id, password, name, birthday, gender, address, address_detail, phone_number, email)
VALUES ('mem456', 'Pass123!', '이회원', 19850728, 2, '서울시 중구 XX빌라', '304호', '010-2734-8905', 'user02@kakao.com');

-- 회원 3
INSERT INTO mem (id, password, name, birthday, gender, address, address_detail, phone_number, email)
VALUES ('mem789', 'Pass123!', '박회원', 20010521, 1, '서울시 서초구 OO오피스텔', '1104호', '010-0324-5843', 'user03@gmail.com');

-- ==================================================

-- 행정:원무 1
WITH inserted_admin AS (
    INSERT INTO mem (id, password, name, birthday, gender, address, address_detail, phone_number, email)
    VALUES ('admin111', 'Pass123!', '최원무', 19700829, 2, '서울시 성북구 KK빌라', '404호', '010-3475-1048', 'admin31@shospital.com')
    RETURNING mem_id
)
INSERT INTO admin_staff (mem_id, rank, emp_number, status, admin_dept_id)
SELECT mem_id, '대리', 'A25850492', '재직', 3
FROM inserted_admin;

-- 행정:원무 2
WITH inserted_admin AS (
    INSERT INTO mem (id, password, name, birthday, gender, address, address_detail, phone_number, email)
    VALUES ('admin222', 'Pass123!', '정원무', 19910923, 1, '서울시 강북구 PP아파트', '1104호', '010-1254-6941', 'admin32@shospital.com')
    RETURNING mem_id
)
INSERT INTO admin_staff (mem_id, rank, emp_number, status, admin_dept_id)
SELECT mem_id, '팀장', 'L10583085', '재직', 3
FROM inserted_admin;

-- 행정:원무 3
WITH inserted_admin AS (
    INSERT INTO mem (id, password, name, birthday, gender, address, address_detail, phone_number, email)
    VALUES ('admin333', 'Pass123!', '강원무', 19870421, 1, '서울시 노원구 MM오피스텔', '2104호', '010-1474-5082', 'admin33@shospital.com')
    RETURNING mem_id
)
INSERT INTO admin_staff (mem_id, rank, emp_number, status, admin_dept_id)
SELECT mem_id, '사원', 'Q19450235', '재직', 3
FROM inserted_admin;

-- ==================================================

-- 행정:홍보 1
WITH inserted_admin AS (
    INSERT INTO mem (id, password, name, birthday, gender, address, address_detail, phone_number, email)
    VALUES ('admin777', 'Pass123!', '조홍보', 19921204, 1, '서울시 광진구 WW아파트', '205호', '010-3643-4587', 'admin71@shospital.com')
    RETURNING mem_id
)
INSERT INTO admin_staff (mem_id, rank, emp_number, status, admin_dept_id)
SELECT mem_id, '주임', 'G02934543', '재직', 7
FROM inserted_admin;

-- 행정:홍보 2
WITH inserted_admin AS (
    INSERT INTO mem (id, password, name, birthday, gender, address, address_detail, phone_number, email)
    VALUES ('admin888', 'Pass123!', '윤홍보', 19940929, 2, '서울시 중랑구 TT아파트', '1503호', '010-1465-4954', 'admin72@shospital.com')
    RETURNING mem_id
)
INSERT INTO admin_staff (mem_id, rank, emp_number, status, admin_dept_id)
SELECT mem_id, '대리', 'D10394857', '재직', 7
FROM inserted_admin;

-- 행정:홍보 3
WITH inserted_admin AS (
    INSERT INTO mem (id, password, name, birthday, gender, address, address_detail, phone_number, email)
    VALUES ('admin999', 'Pass123!', '장홍보', 19890726, 2, '서울시 동대문구 XX아파트', '908호', '010-0645-1490', 'admin73@shospital.com')
    RETURNING mem_id
)
INSERT INTO admin_staff (mem_id, rank, emp_number, status, admin_dept_id)
SELECT mem_id, '팀장', 'M10495029', '재직', 7
FROM inserted_admin;

-- ==================================================

WITH inserted_doc AS (
    INSERT INTO mem (id, password, name, birthday, gender, address, address_detail, phone_number, email)
    VALUES ('doc123', 'Pass123!', '임의사', 19800505, 1, '서울시 서초구 강남대로', '202호', '010-9999-1111', 'doc9999@hospital.com')
    RETURNING mem_id
)
INSERT INTO med_staff (mem_id, role, license_number, status, med_dept_id)
SELECT mem_id, '의사', '130101', '재직', 1
FROM inserted_doc;

-- 의사 (10개 부서별 10명씩)
WITH dept_data AS (
    SELECT * FROM (VALUES
        (1, '내과'), (2, '정형외과'), (3, '신경외과'), (4, '소아청소년과'), (5, '이비인후과'),
        (6, '피부과'), (7, '안과'), (8, '치과'), (9, '정신건강의학과'), (10, '응급의학과')
    ) AS t(dept_id, dept_name)
),
surnames AS (
    SELECT * FROM (VALUES
        (1, '김'), (2, '이'), (3, '박'), (4, '최'), (5, '정'),
        (6, '강'), (7, '조'), (8, '윤'), (9, '장'), (10, '임')
    ) AS t(idx, surname)
),
gen_doc AS (
    SELECT
        d.dept_id,
        s.idx AS doc_idx,
        'doctor' || d.dept_id || LPAD(s.idx::TEXT, 2, '0') AS gen_id, -- 예: doctor101, doctor1001
        'aaaa!A' AS gen_pw,                                           -- 비밀번호 통일
        s.surname || d.dept_name AS gen_name,                         -- 예: 김내과, 이내과
        19800000 + (d.dept_id * 100) + s.idx AS gen_birth,            -- 생년월일 자동 생성
        (s.idx % 2) + 1 AS gen_gender,                                -- 성별 교차 배정
        '서울시 메디컬구 의사동' AS gen_addr,
        d.dept_id || '길 ' || s.idx || '호' AS gen_addr_detail,
        '010-2222-' || LPAD(((d.dept_id - 1) * 10 + s.idx)::TEXT, 4, '0') AS gen_phone,
        'doctor' || d.dept_id || LPAD(s.idx::TEXT, 2, '0') || '@shospital.com' AS gen_email, -- 이메일 지정
        '13' || LPAD(d.dept_id::TEXT, 2, '0') || LPAD(s.idx::TEXT, 2, '0') AS gen_license   -- 면허번호 지정
    FROM dept_data d
    CROSS JOIN surnames s
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

-- ==================================================

-- 주차 자리
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

-- ==================================================

SELECT * FROM parking_log;
-- 비회원 차량
WITH new_log AS (
    INSERT INTO parking_log (vehicle_num, entry_time, is_member, payment_status)
    VALUES ('18하6294', now() - INTERVAL '3 hours', false, false)
    RETURNING parking_log_id
)
UPDATE parking_spot
SET parking_log_id = (SELECT parking_log_id FROM new_log),
    is_parked = true
WHERE spot_id = 30;
-- 회원 차량 (진료X)
WITH new_log AS (
    INSERT INTO parking_log (vehicle_num, entry_time, is_member, payment_status)
    VALUES ('62서0424', now() - INTERVAL '3 hours', false, false)
    RETURNING parking_log_id
)
UPDATE parking_spot
SET parking_log_id = (SELECT parking_log_id FROM new_log),
    is_parked = true
WHERE spot_id = 31;
-- 회원 차량 (진료O)
WITH new_log AS (
    INSERT INTO parking_log (vehicle_num, entry_time, is_member, payment_status)
    VALUES ('89루0528', now() - INTERVAL '3 hours', false, false)
    RETURNING parking_log_id
)
UPDATE parking_spot
SET parking_log_id = (SELECT parking_log_id FROM new_log),
    is_parked = true
WHERE spot_id = 32;

-- ==================================================

-- FAQ 병원이용 카테고리 4개마다 10개씩
INSERT INTO faq (admin_staff_id, category, title, content, write_date) VALUES

(1, '병원이용', '진료 예약은 어떻게 하나요?', '진료 예약은 홈페이지, 모바일 앱, 그리고 전화(대표번호 02-1111-1111)를 통해 가능합니다. 홈페이지와 앱에서는 24시간 언제든 원하시는 날짜와 의료진을 선택해 예약하실 수 있습니다. 당일 진료 예약의 경우, 진료과 대기 사정에 따라 조기 마감될 수 있으니 가급적 사전 예약을 권장해 드립니다.', now() - INTERVAL '40 days'),
(2, '홈페이지 이용', '홈페이지 회원가입 시 어떤 장점이 있나요?', '회원으로 가입하시면 간편한 온라인 진료 예약/취소가 가능하며, 본인의 과거 진료 내역, 처방 내역, 검사 결과 등을 편리하게 조회하실 수 있습니다. 관심 질환을 설정해두시면 맞춤형 건강 콘텐츠, 무료 건강강좌 등 유용한 병원 소식도 받아보실 수 있습니다.', now() - INTERVAL '39 days'),
(3, '증명서발급', '진단서 발급은 어떻게 받나요?', '진단서는 의료법에 따라 반드시 담당 의사와 대면 진료 시에만 발급이 가능합니다. 외래 진료 예약 후 내원하시어 담당 의사에게 진단서 발급을 요청해 주시기 바랍니다. 발급받으신 진단서는 원무과 제증명 창구에서 수납 및 직인을 받은 후 수령하실 수 있습니다.', now() - INTERVAL '38 days'),
(1, '건강검진', '건강검진 예약은 어떻게 하나요?', '건강검진 예약은 홈페이지 ''건강검진센터'' 메뉴에서 온라인으로 예약하시거나, 전용 상담 전화(02-1111-2222)를 통해 예약하실 수 있습니다. 국가건강검진의 경우 연말(10~12월)에는 예약이 집중되어 대기가 길어질 수 있으니 상반기 예약을 권장합니다.', now() - INTERVAL '37 days'),

(1, '병원이용', '주차 요금 및 주차장 이용 안내', '외래 진료 환자의 경우 진료 당일 4시간 무료 주차가 가능하며, 입/퇴원 환자는 당일에 한해 24시간 무료 주차가 제공됩니다. 일반 방문객의 경우 최초 30분은 무료이며, 이후 10분당 500원의 요금이 부과됩니다. 주차 등록은 원무과 무인 수납기 또는 출구 정산소에서 진료 영수증 바코드를 인식하여 간편하게 정산하실 수 있습니다.', now() - INTERVAL '36 days'),
(3, '증명서발급', '대리인이 증명서를 발급받을 수 있나요?', '환자 본인이 방문하기 어려운 경우 대리인 발급이 가능하지만, 의료법 제21조에 따라 환자의 신분증 사본, 대리인의 신분증, 환자 자필 서명이 포함된 동의서 및 위임장, 가족관계증명서(가족인 경우)를 반드시 지참하셔야 합니다. 상세 양식은 홈페이지에서 다운로드 가능합니다.', now() - INTERVAL '35 days'),
(2, '홈페이지 이용', '비밀번호를 변경하고 싶습니다.', '비밀번호 변경은 홈페이지 로그인 후 [마이페이지] - [개인정보 수정] - [비밀번호 변경] 메뉴에서 언제든지 가능합니다. 안전한 정보 보호를 위해 영문 대/소문자, 숫자, 특수문자 중 2가지 이상을 조합하여 6~16자리로 설정하셔야 하며, 주기적인 변경을 권장합니다.', now() - INTERVAL '34 days'),
(2, '건강검진', '수면내시경 후 바로 운전해도 되나요?', '아니요, 절대 불가합니다. 수면(진정) 내시경 후에는 충분히 휴식을 취하더라도 하루 정도 약기운이 남아있어 판단력, 반사 신경이 저하됩니다. 사고 예방을 위해 검사 당일 자가 운전이나 기계 조작은 절대 금하며, 대중교통 이용 또는 보호자와 동행하셔야 합니다.', now() - INTERVAL '33 days'),

(2, '증명서발급', '진료비 영수증과 세부내역서는 어떻게 발급받나요?', '진료 당일에는 원무과 수납 창구나 무인 수납기에서 즉시 발급 가능합니다. 과거 진료 기록에 대한 영수증이 필요하신 경우, 본관 1층 제증명 창구를 방문하시거나, 본원 홈페이지 ''나의 진료내역'' 메뉴에서 본인 인증 후 무료로 출력하실 수 있습니다.', now() - INTERVAL '32 days'),
(1, '병원이용', '처음 방문했는데 접수는 어떻게 해야 하나요?', '본원에 처음 방문하신 경우, 본관 1층 로비에 위치한 ''처음 오신 분'' 창구(원무과)로 오셔서 진료신청서를 작성하신 후 신분증과 함께 제출해 주시기 바랍니다. 다른 병원에서 진료의뢰서를 받아오신 경우 접수 시 함께 제출해 주시면 원활한 진료 안내가 가능합니다.', now() - INTERVAL '31 days'),
(1, '건강검진', '건강검진 전날 주의사항이 궁금합니다.', '정확한 검사를 위해 건강검진 전날 밤 9시부터는 금식하셔야 합니다. 식사, 물, 껌, 커피, 담배 등 일절 금합니다. 전날 저녁은 가볍게 드시고 과음, 과로는 피해주십시오. 대장내시경 예약자는 3일 전부터 식단을 조절하고 장정결제를 복용하셔야 합니다.', now() - INTERVAL '30 days'),
(1, '홈페이지 이용', '로그인 아이디나 비밀번호를 잊어버렸어요.', '메인 로그인 화면 하단의 ''아이디/비밀번호 찾기'' 버튼을 클릭해 주세요. 가입 시 등록하신 휴대폰 번호 또는 이메일을 통한 본인 인증 과정을 거치면 아이디를 확인하거나 임시 비밀번호를 발급받을 수 있습니다. 임시 비밀번호 로그인 후에는 반드시 새 비밀번호로 변경해 주세요.', now() - INTERVAL '29 days'),

(1, '증명서발급', '영문 진단서 발급도 가능한가요?', '네, 영문 진단서 발급이 가능합니다. 진료 시 담당 의사에게 미리 요청해 주시고, 환자분의 영문 이름(여권명과 동일한 스펠링)을 정확히 알려주셔야 합니다. 영문 서류의 특성상 국문 진단서보다 발급에 다소 시간이 소요될 수 있습니다.', now() - INTERVAL '28 days'),
(1, '건강검진', '국가건강검진 대상자인지 어떻게 확인하나요?', '올해 국가건강검진 대상자 여부는 국민건강보험공단 홈페이지나 본원 건강검진센터 안내 데스크에서 조회 가능합니다. 홀수 연도 출생자는 홀수 해, 짝수 연도 출생자는 짝수 해에 대상이 되며, 연령과 성별에 따라 5대 암 검진 대상 항목이 추가됩니다.', now() - INTERVAL '27 days'),
(1, '병원이용', '병문안(면회) 시간은 어떻게 되나요?', '환자의 안정과 감염 예방을 위해 병문안 시간은 평일 오후 6시~8시, 주말 및 공휴일은 오전 10시~12시, 오후 6시~8시로 제한 운영하고 있습니다. 감염성 질환자나 임산부, 노약자, 어린이의 경우 병문안을 엄격히 제한하고 있으니 양해 부탁드립니다. 면회 시에는 반드시 마스크를 착용하시고 병동 입구에서 손 소독을 진행해 주시기 바랍니다.', now() - INTERVAL '26 days'),
(1, '홈페이지 이용', '회원 탈퇴는 어떻게 하나요?', '로그인 후 우측 상단의 [마이페이지] - [개인정보 수정] 하단에 있는 ''회원 탈퇴'' 버튼을 통해 진행하실 수 있습니다. 탈퇴 시 웹 회원정보는 영구 파기되지만, 병원에서의 실제 진료 기록(의무기록)은 의료법상 의무 보관 규정에 따라 별도로 안전하게 보관됩니다.', now() - INTERVAL '25 days'),

(3, '건강검진', '검진 시 복용하던 약은 어떻게 해야 하나요?', '혈압약/심장약은 검진 당일 새벽 6시경 최소한의 물로 복용하세요. 단, 당뇨약(인슐린 포함)은 당일 아침 절대 복용 금지입니다. 내시경 검사가 있다면, 아스피린이나 항응고제 복용자는 출혈 위험이 있으니 검사 1주일 전 주치의와 상의해 약물 중단 여부를 결정해야 합니다.', now() - INTERVAL '24 days'),
(2, '증명서발급', '입퇴원 확인서 발급 절차를 알려주세요.', '퇴원 수속 시 원무과 퇴원 창구에서 발급을 요청하시면 즉시 발급해 드립니다. 퇴원 이후에 추가로 필요하신 경우에는 본관 1층 제증명 창구로 신분증을 지참하고 내원하시거나, 홈페이지를 통한 인터넷 제증명 발급 서비스를 이용해 주시기 바랍니다.', now() - INTERVAL '23 days'),
(2, '병원이용', '입원할 때 준비해야 할 준비물은 무엇인가요?', '입원 시 기본적으로 세면도구(수건, 칫솔, 치약, 비누 등), 슬리퍼, 물통, 개인용 컵(텀블러), 속옷, 화장지 등을 준비해 주시기 바랍니다. 환자복과 침구류는 병원에서 기본적으로 제공됩니다. 또한, 복용 중이신 약이 있다면 약 처방전이나 약 봉투를 반드시 지참하여 입원 수속 시 병동 간호사에게 전달해 주시기 바랍니다.', now() - INTERVAL '22 days'),
(3, '홈페이지 이용', '모바일(스마트폰)에서도 홈페이지 이용이 가능한가요?', '네, 본원 홈페이지는 스마트폰, 태블릿 등 모바일 기기 화면에 맞춰 자동 조절되는 모바일 최적화 웹사이트입니다. 스마트폰 브라우저로 접속하시거나, 모바일 진료카드, 실시간 알림 기능 등을 원하신다면 앱스토어에서 병원 공식 모바일 앱을 다운로드해 이용해 보세요.', now() - INTERVAL '21 days'),

(3, '병원이용', '응급실은 24시간 운영하나요?', '네, 본원 응급의료센터는 365일 24시간 전문의와 전문 응급 간호 인력이 상주하여 응급 상황에 대처하고 있습니다. 본관 1층 응급의료센터 입구를 통해 야간 및 공휴일에도 언제든 진료를 받으실 수 있습니다. 응급실 내원 시 원무 창구에서 먼저 접수를 진행해 주시면 중증도 분류에 따라 진료가 시작됩니다.', now() - INTERVAL '20 days'),
(2, '건강검진', '검진 결과는 언제 나오며, 어떻게 확인하나요?', '결과는 검사일로부터 약 2주일 이내에 우편 또는 이메일로 발송해 드립니다. 홈페이지 회원가입을 하신 경우 홈페이지나 모바일 앱의 ''검진결과 조회'' 메뉴에서 PDF 파일로 상시 확인하실 수 있습니다. 이상 소견 발견 시 지체 없이 해당 진료과 전문의에게 연계해 드립니다.', now() - INTERVAL '19 days'),
(2, '홈페이지 이용', '회원가입 시 본인 인증 단계에서 자꾸 실패합니다.', '본인 인증 실패 시, 입력하신 정보(이름, 생년월일, 통신사, 휴대폰 번호)가 개통된 통신사의 실제 등록 정보와 정확히 일치하는지 확인해 주십시오. 최근 개명하셨거나 통신사를 변경하신 경우 반영에 시간이 걸릴 수 있습니다. 계속 실패 시 통신사 고객센터로 문의 바랍니다.', now() - INTERVAL '18 days'),
(1, '증명서발급', '온라인으로 제증명 발급이 가능한 서류는 무엇인가요?', '병원에 방문하지 않으셔도 홈페이지를 통해 진료비 영수증, 진료비 세부내역서, 연말정산용 의료비 납입 증명서, 외래 진료 확인서 등을 온라인으로 발급받을 수 있습니다. 단, 진단서나 소견서 등 의사의 서명이 들어가는 서류는 반드시 내원 발급만 가능합니다.', now() - INTERVAL '17 days'),

(1, '병원이용', '외국인 진료도 가능한가요?', '네, 가능합니다. 본원에서는 외국인 환자분들의 원활한 진료를 돕기 위해 외국인 전용 국제진료센터를 운영하고 있습니다. 영어, 중국어, 일본어 통역 코디네이터가 상주하여 진료 예약부터 접수, 진료 동행, 수납까지 1:1 맞춤 안내 서비스를 제공하고 있습니다.', now() - INTERVAL '16 days'),
(1, '건강검진', '여성의 경우 생리 중일 때 검진이 가능한가요?', '생리 중에는 소변 및 부인과 검사에 혈액이 섞여 정확한 결과를 얻기 어렵습니다. 생리가 끝난 후 5~7일 이후로 날짜를 변경하시는 것을 권장합니다. 부득이하게 내원하신 경우, 해당 검사를 제외한 나머지 항목만 진행하고 미실시 항목은 추후 재방문하여 검사받을 수 있습니다.', now() - INTERVAL '15 days'),
(1, '증명서발급', '팩스나 이메일로 서류를 받을 수 있나요?', '죄송합니다. 환자의 민감한 개인정보 보호 및 의료법 위반 소지가 있어, 의무기록 사본과 제증명 서류는 팩스나 개인 이메일 발송이 절대 불가합니다. 본인 또는 대리인이 직접 내원하시거나, 보안이 적용된 홈페이지를 통한 프린터 출력 발급만 가능합니다.', now() - INTERVAL '14 days'),
(1, '홈페이지 이용', '홈페이지에서 개인정보(연락처, 주소) 수정은 어떻게 하나요?', '로그인 후 우측 상단의 [마이페이지] - [개인정보 수정] 메뉴를 클릭하시고 비밀번호를 한 번 더 입력하시면 정보를 수정할 수 있습니다. 진료 예약 안내 문자나 중요 검사 결과지 우편 발송 누락을 방지하기 위해 연락처와 주소는 항상 최신 정보로 업데이트해 주시기 바랍니다.', now() - INTERVAL '13 days'),

(3, '건강검진', '검진 당일 뇌 MRI나 심장 초음파 등 추가 검사가 가능한가요?', '원칙적으로 당일 추가 검사는 영상의학과 및 각 검사실의 예약 스케줄에 여유가 있을 때만 가능합니다. 원하시는 검사를 받지 못하시고 돌아가시는 일이 없도록, 검진 예약 시 원하시는 추가 검사 항목을 미리 상담하고 예약해 주시는 것이 가장 좋습니다.', now() - INTERVAL '12 days'),
(3, '병원이용', '휠체어 대여는 어떻게 하나요?', '거동이 불편하신 환자분들을 위해 본관 1층 안내 데스크에서 휠체어를 무료로 대여해 드리고 있습니다. 신분증을 맡기시면 당일 원내 진료 시간 동안 자유롭게 이용하실 수 있으며, 사용 후에는 반드시 대여하신 곳으로 반납해 주시기 바랍니다.', now() - INTERVAL '11 days'),
(3, '홈페이지 이용', '온라인으로 예약한 진료 내역은 어디서 확인하나요?', '홈페이지에서 로그인하신 후 화면 우측 상단의 [마이페이지]를 클릭하고, [진료 예약/조회] 메뉴로 들어가시면 예약 일자, 진료과, 담당 의료진 등 상세한 예약 내역을 확인하실 수 있습니다. 해당 메뉴에서 직접 예약 날짜 변경 및 취소도 가능합니다.', now() - INTERVAL '10 days'),
(1, '증명서발급', '영상자료(MRI, CT, X-ray 등) CD 복사는 어떻게 하나요?', '타 병원 진료 등의 목적으로 영상 자료 복사가 필요하신 경우, 진료 시 담당 의사에게 먼저 복사 오더(처방)를 받으셔야 합니다. 이후 무인 CD 복사기 또는 영상의학과 접수 창구에서 소정의 비용(보통 1장당 1~2만 원 선)을 결제하신 후 복사본을 수령하실 수 있습니다.', now() - INTERVAL '9 days'),

(1, '병원이용', '원내에서 물건을 잃어버렸는데 어디서 찾을 수 있나요?', '병원 내에서 분실하신 물건은 본관 1층 고객지원센터(보안실)에서 통합하여 보관 및 관리하고 있습니다. 분실물을 습득하시거나 찾으실 때는 신분증을 지참하시고 고객지원센터로 방문해 주시거나 전화(02-1111-1112)로 문의해 주시기 바랍니다.', now() - INTERVAL '8 days'),
(1, '건강검진', '회사(기업) 단체 검진 예약은 어떻게 진행되나요?', '본원은 다양한 기업 및 기관과 협약을 맺고 맞춤형 직장인 단체 검진을 진행하고 있습니다. 기업검진 전담 부서(02-1111-3333)로 문의해 주시면 기업의 예산과 요구사항에 맞춘 검진 프로그램 설계 및 임직원 일괄 예약 등록 절차를 친절히 안내해 드립니다.', now() - INTERVAL '7 days'),
(2, '증명서발급', '미성년자 자녀의 진단서 발급 시 필요한 서류는요?', '만 19세 미만 미성년 환자의 법정대리인(부모 등)이 방문하실 경우, 신청자(부모)의 신분증 원본과 환자와의 관계를 증명할 수 있는 서류(주민등록번호가 모두 표기된 가족관계증명서 또는 주민등록등본)를 반드시 지참하셔야 발급이 가능합니다.', now() - INTERVAL '6 days'),
(3, '홈페이지 이용', '홈페이지 화면이 깨지거나 결제 창이 안 열립니다.', '원활한 홈페이지 이용을 위해 구글 크롬(Chrome) 또는 마이크로소프트 엣지(Edge) 브라우저 사용을 권장합니다. 인터넷 익스플로러(IE)는 지원이 종료되어 정상 작동하지 않을 수 있습니다. 접속 오류 시 브라우저 설정에서 ''인터넷 사용 기록(캐시)''을 삭제한 뒤 재접속해 보시기 바랍니다.', now() - INTERVAL '5 days'),

(2, '건강검진', '임산부도 건강검진을 받을 수 있나요?', '임신 중이거나 임신 가능성이 있는 경우, 태아에게 영향을 줄 수 있는 방사선 검사(X-ray, CT, 유방촬영 등)와 수면내시경, 자궁경부암 검사 등은 절대 불가합니다. 예약 및 검진 전 문진 시 반드시 임신 사실 또는 가능성을 의료진에게 고지해 주셔야 합니다.', now() - INTERVAL '4 days'),
(2, '병원이용', '처방전을 받았는데 원내에 약국이 있나요?', '의약분업 및 관련 의료법에 따라 병원 내부에는 외래 환자를 위한 일반 약국이 없습니다. 수납 시 발급받으신 처방전을 지참하시고 병원 정문 밖에 위치한 인근 외부 약국을 이용해 주시기 바랍니다.', now() - INTERVAL '3 days'),
(1, '증명서발급', '과거에 발급받은 진단서를 재발급할 때 비용이 드나요?', '네, 최초 발급 시에는 진단서 종류에 따라 1~2만 원의 발급 수수료가 발생하며, 동일한 내용의 진단서를 재발급(사본) 받으시는 경우에는 통상적으로 1장당 1,000원의 제증명 수수료가 부과됩니다. 재발급은 원무과 창구에서 즉시 처리 가능합니다.', now() - INTERVAL '2 days'),
(1, '홈페이지 이용', '최근에 개명했는데, 홈페이지 회원 정보는 어떻게 바꾸나요?', '홈페이지는 나이스평가정보(NICE)의 실명 인증 시스템을 기반으로 운영됩니다. 따라서 나이스평가정보(www.namecheck.co.kr)에 먼저 개명된 이름의 실명 등록을 신청하여 처리 완료 문자를 받으신 후, 본원 홈페이지 고객센터(1:1 문의)로 연락 주시면 변경 처리해 드립니다.', now());

-- ==================================================

-- 건강이야기 20개
INSERT INTO health_story (admin_staff_id, title, content, thumbnail_img, read_count, write_date) VALUES
(1, '[건강정보] 봄철 불청객, 알레르기 비염 예방과 생활 수칙', '따뜻한 봄바람과 함께 찾아오는 꽃가루와 황사는 알레르기 비염 환자들에게 큰 고통입니다. 외출 시에는 반드시 보건용 마스크를 착용하고, 귀가 후에는 손 씻기와 생리식염수를 이용한 코 세척을 생활화하는 것이 좋습니다. 실내 습도는 40~50%로 유지해 주세요.', '/images/health_story_01.jpg', 850, now() - INTERVAL '20 days'),
(1, '[건강정보] 하루 물 8잔, 내 몸을 살리는 올바른 수분 섭취법', '우리 몸의 70%는 수분으로 이루어져 있습니다. 갈증을 느끼기 전에 미리 조금씩 자주 마시는 것이 중요하며, 기상 직후 마시는 미지근한 물 한 잔은 신진대사를 촉진하고 혈액순환을 돕습니다. 커피나 녹차 등 카페인 음료는 이뇨작용을 하므로 맹물을 드시는 것을 권장합니다.', NULL, 420, now() - INTERVAL '19 days'),
(1, '[건강칼럼] 허리 통증을 줄여주는 올바른 수면 자세', '아침에 일어났을 때 허리가 뻐근하다면 수면 자세를 점검해 보아야 합니다. 천장을 보고 바르게 누울 때는 무릎 아래에 쿠션을 받치면 척추의 부담이 줄어듭니다. 옆으로 누워 자는 습관이 있다면 양 무릎 사이에 베개를 끼워 골반이 틀어지는 것을 방지해 주세요.', '/images/health_story_02.jpg', 1230, now() - INTERVAL '18 days'),
(3, '[건강가이드] 소리 없는 암살자 나트륨, 맛있게 줄이는 비법', '과도한 나트륨 섭취는 고혈압과 위암의 주요 원인입니다. 국물 요리를 드실 때는 건더기 위주로 섭취하고, 조리 시 소금 대신 후추, 마늘, 양파 등 천연 향신료를 활용해 풍미를 높이세요. 칼륨이 풍부한 토마토, 바나나, 시금치를 함께 드시면 나트륨 배출에 도움이 됩니다.', NULL, 560, now() - INTERVAL '17 days'),
(2, '[안과소식] 스마트폰이 부르는 안구건조증과 디지털 노안', '스마트폰 화면에 집중하다 보면 눈을 깜빡이는 횟수가 평소의 3분의 1로 줄어들어 안구건조증이 유발됩니다. "20-20-20 법칙"을 기억하세요. 20분 전자기기를 사용한 후에는 20피트(약 6m) 이상 떨어진 곳을 20초 동안 바라보며 눈의 피로를 풀어주어야 합니다.', '/images/health_story_03.jpg', 910, now() - INTERVAL '16 days'),
(2, '[운동가이드] 직장인을 위한 거북목 예방 5분 스트레칭', '하루 종일 모니터를 보는 직장인들은 거북목 증후군에 취약합니다. 의자에 바르게 앉아 양손을 깍지 끼고 뒤통수에 댄 후, 가슴을 활짝 펴고 고개를 뒤로 젖히는 동작을 10초간 유지하세요. 한 시간마다 가벼운 기지개를 켜는 것만으로도 목과 어깨의 긴장이 크게 완화됩니다.', NULL, 340, now() - INTERVAL '15 days'),
(3, '[영양정보] 제2의 뇌, 장 건강을 지키는 프로바이오틱스', '면역 세포의 70%가 존재하는 장은 우리 몸의 방어막 역할을 합니다. 장내 유익균을 늘리기 위해서는 프로바이오틱스(유산균)뿐만 아니라, 유익균의 먹이가 되는 프리바이오틱스(식이섬유, 올리고당 등)를 함께 섭취하는 것이 좋습니다. 발효식품과 통곡물을 밥상에 올려보세요.', '/images/health_story_04.jpg', 1500, now() - INTERVAL '14 days'),
(1, '[감염예방] 올바른 손 씻기 6단계, 감염병 절반을 막습니다', '감염병 예방의 가장 기본이자 확실한 백신은 바로 "손 씻기"입니다. 비누를 사용하여 흐르는 물에 30초 이상 손을 씻어야 하며, 손바닥뿐만 아니라 손등, 손가락 사이, 두 손 모아, 엄지손가락, 손톱 밑까지 꼼꼼하게 문질러 씻는 6단계 수칙을 생활화해 주십시오.', NULL, 280, now() - INTERVAL '13 days'),
(3, '[건강정보] 현대인에게 꼭 필요한 햇빛 비타민, 비타민D', '실내 생활이 많은 현대인들의 대다수는 비타민D 결핍 상태입니다. 비타민D는 칼슘 흡수를 도와 뼈 건강을 지키고 우울감 감소에도 효과적입니다. 하루 15~20분 정도는 자외선 차단제를 바르지 않은 상태로 햇빛을 쬐거나, 연어, 달걀노른자, 표고버섯 등을 섭취하시기 바랍니다.', '/images/health_story_05.jpg', 880, now() - INTERVAL '12 days'),
(2, '[운동가이드] 하루 30분 걷기의 놀라운 기적', '거창한 운동 기구 없이 튼튼한 두 다리만 있다면 걷기 운동을 시작해 보세요. 하루 30분 이상의 규칙적인 걷기는 심폐 기능을 향상시키고 혈당 강하 및 체지방 연소에 탁월한 효과가 있습니다. 약간 숨이 찰 정도의 속도로 팔을 크게 흔들며 걷는 파워워킹을 추천합니다.', NULL, 670, now() - INTERVAL '11 days'),
(1, '[정신건강] 만병의 근원 스트레스, 슬기롭게 관리하는 법', '지속적인 스트레스는 자율신경계의 불균형을 초래하여 두통, 소화불량, 불면증을 유발합니다. 완벽주의를 조금 내려놓고, 나만의 취미 생활이나 명상, 복식호흡을 통해 마음의 안정을 찾는 것이 중요합니다. 힘들 때는 혼자 참지 말고 주변이나 전문가의 도움을 적극적으로 받으세요.', '/images/health_story_06.jpg', 1120, now() - INTERVAL '10 days'),
(2, '[영양정보] 달콤한 독, 당류 섭취를 줄이는 생활 습관', '무심코 마시는 과일 주스나 탄산음료에는 각설탕 7~10개 분량의 당이 들어 있습니다. 과도한 당 섭취는 비만과 당뇨병의 지름길입니다. 음료를 고를 때는 영양성분표의 "당류" 함량을 꼭 확인하시고, 간식이 당길 때는 빵이나 과자 대신 견과류나 신선한 채소를 드셔보세요.', NULL, 450, now() - INTERVAL '9 days'),
(1, '[건강칼럼] 아침 식사, 선택이 아닌 필수인 이유', '바쁘다는 이유로 아침을 거르는 분들이 많습니다. 하지만 아침 식사는 수면 중 멈춰있던 뇌에 에너지를 공급하고 폭식을 예방하는 중요한 역할을 합니다. 소화가 잘되는 오트밀, 달걀, 사과, 그릭 요거트 등으로 구성된 가벼운 아침 식사로 활기찬 하루를 시작해 보세요.', '/images/health_story_07.jpg', 760, now() - INTERVAL '8 days'),
(2, '[관절건강] 기온 뚝! 찬 바람에 시린 관절 건강 지키기', '날씨가 추워지면 관절 주변의 근육과 인대가 경직되어 통증이 심해집니다. 외출 시에는 무릎 담요나 핫팩으로 관절 부위를 따뜻하게 보온해 주고, 무리한 등산이나 계단 오르내리기보다는 평지 걷기나 실내 자전거 타기 등 관절에 무리가 덜 가는 운동을 꾸준히 해 주어야 합니다.', NULL, 510, now() - INTERVAL '7 days'),
(3, '[심혈관질환] 겨울철 돌연사의 주범, 심뇌혈관 질환 주의보', '겨울철 갑작스러운 추위는 혈관을 수축시켜 혈압을 급격히 상승시킵니다. 특히 고혈압, 고지혈증, 당뇨 환자나 노약자는 이른 아침 야외 운동을 피하고, 외출 시 모자, 목도리, 장갑을 착용하여 체온 손실을 막아야 합니다. 가슴 통증이나 편측 마비 증상이 나타나면 즉시 119에 연락하세요.', '/images/health_story_08.jpg', 1450, now() - INTERVAL '6 days'),
(1, '[피부건강] 마스크 속 붉어진 피부, 진정시키는 스킨케어 팁', '장시간 마스크 착용과 난방으로 인해 피부가 건조해지고 트러블이 발생하기 쉽습니다. 귀가 직후에는 약산성 클렌저로 피부 자극을 최소화하여 세안하고, 보습제와 수분 크림을 충분히 발라 피부 장벽을 튼튼하게 유지해 주는 것이 겨울철 건강한 피부를 지키는 비결입니다.', NULL, 380, now() - INTERVAL '5 days'),
(2, '[치아건강] 치주질환(잇몸병)이 전신 건강을 위협한다?', '구강 내 세균은 잇몸을 통해 혈관으로 침투하여 심혈관계 질환, 치매, 류마티스 관절염 등 전신 질환의 위험을 높일 수 있습니다. 하루 3번 꼼꼼한 양치질은 물론, 치간 칫솔과 치실 사용을 습관화하고 정기적인 스케일링(연 1회 건강보험 적용)을 통해 치석을 제거해야 합니다.', '/images/health_story_09.jpg', 920, now() - INTERVAL '4 days'),
(3, '[영양정보] 노년기 근감소증 예방을 위한 단백질 식단', '나이가 들면 자연스럽게 근육량이 감소합니다. 근육의 재료가 되는 단백질 섭취가 부족하면 낙상과 골절의 위험이 크게 증가합니다. 매 끼니 고기, 생선, 달걀, 콩, 두부 등의 단백질 반찬을 주먹 크기만큼 챙겨 드시고, 가벼운 근력 운동을 병행하여 근육을 유지해 주십시오.', NULL, 610, now() - INTERVAL '3 days'),
(3, '[뇌건강] 치매를 예방하는 두뇌 트레이닝과 식습관', '치매는 아직 완치제가 없어 예방이 최선입니다. 새로운 언어를 배우거나 책을 읽는 등 지속적인 두뇌 활동이 중요하며, 등푸른생선에 풍부한 오메가-3와 호두, 아몬드 등의 견과류 섭취는 뇌세포 보호에 도움이 됩니다. 이웃이나 친구들과의 활발한 사회적 교류도 뇌 건강에 큰 활력소가 됩니다.', '/images/health_story_10.jpg', 1780, now() - INTERVAL '2 days'),
(1, '[건강검진] 내 몸이 보내는 신호, 정기검진으로 잡으세요', '대부분의 중증 질환은 초기 증상이 없어 병이 상당히 진행된 후에야 발견되는 경우가 많습니다. 특별한 이상이 없더라도 1~2년에 한 번씩 자신의 연령과 가족력에 맞는 맞춤형 종합 건강검진을 받아 질병을 조기 발견하고 예방하는 것이 건강 100세 시대를 준비하는 가장 현명한 방법입니다.', NULL, 850, now() - INTERVAL '1 day');

-- ==================================================

-- 공지사항 20개
INSERT INTO notice (admin_staff_id, top_fix, title, content, thumbnail_img, read_count, write_date) VALUES
(1, FALSE, '[안내] 2026년 설 연휴 진료 및 응급실 운영 안내', '안녕하세요. 다가오는 설 연휴 기간 동안의 본원 진료 및 응급의료센터 운영 일정을 안내해 드립니다. 연휴 기간 중 외래 진료는 휴진이오나, 응급의료센터는 24시간 정상 운영됩니다. 병원 이용에 차질 없으시길 바랍니다.', '/images/notice_01.jpg', 1250, now() - INTERVAL '15 days'),
(1, TRUE, '[공지] 원내 와이파이(Wi-Fi) 서비스 접속 방식 변경 안내', '환자 및 보호자 여러분의 편의를 위해 원내 무료 공용 와이파이 장비를 전면 교체하였습니다. 새로운 와이파이 네트워크 이름(SSID)은 "Hospital_Free_WiFi"이며, 별도의 비밀번호 없이 약관 동의 후 바로 사용하실 수 있습니다.', NULL, 342, now() - INTERVAL '14 days'),
(2, FALSE, '[캠페인] 2026년 상반기 독감(인플루엔자) 예방접종 실시', '독감 유행 주의보 발령에 따라 본원에서 예방접종을 실시합니다. 만 65세 이상 어르신 및 임산부, 소아 등 무료 접종 대상자는 신분증 및 필요 서류를 지참하시어 내원해 주시기 바랍니다. 안전한 겨울나기를 위해 예방접종에 적극 참여해 주세요.', '/images/notice_02.jpg', 890, now() - INTERVAL '13 days'),
(3, FALSE, '[도입] 진료비 하이패스(등록 카드 자동 결제) 서비스 도입', '진료 후 수납 창구를 들를 필요 없이, 사전에 등록해 둔 신용카드로 진료비가 자동 결제되는 "하이패스 서비스"가 도입되었습니다. 등록을 원하시는 분은 본관 1층 원무과 수납 창구에 방문하여 신청서를 작성해 주시기 바랍니다.', NULL, 512, now() - INTERVAL '12 days'),
(3, FALSE, '[소식] 최첨단 3.0T MRI 영상 진단 장비 신규 가동', '우리 병원 영상의학과에 더욱 정밀하고 빠른 검사가 가능한 최첨단 3.0T MRI 장비가 새로 도입되어 본격적인 가동을 시작했습니다. 기존 장비 대비 촬영 시간이 단축되고 소음이 적어 환자분들의 불편을 크게 줄일 수 있게 되었습니다.', '/images/notice_03.jpg', 420, now() - INTERVAL '11 days'),
(2, TRUE, '[점검] 홈페이지 서버 정기 점검에 따른 서비스 일시 중단 안내', '안정적인 서비스 제공을 위한 홈페이지 서버 정기 점검이 진행됩니다. 점검 시간 동안에는 진료 예약 및 제증명 발급 등 홈페이지 내 모든 서비스 이용이 중단되오니, 고객 여러분의 너른 양해를 부탁드립니다. (점검일시: 이번 주 토요일 00:00~04:00)', NULL, 150, now() - INTERVAL '10 days'),
(1, TRUE, '[공사] 별관 주차장 바닥 우레탄 보수 공사 안내', '안전하고 쾌적한 주차 환경 조성을 위해 별관 주차장 바닥 우레탄 보수 공사를 실시합니다. 공사 기간 중 별관 주차장 진입이 일부 통제되오니, 가급적 본관 지하 주차장 및 외부 임시 주차장을 이용해 주시기 바랍니다. 불편을 드려 죄송합니다.', '/images/notice_04.jpg', 670, now() - INTERVAL '9 days'),
(1, FALSE, '[안내] 제증명 서류 발급 무인 키오스크 추가 설치', '환자분들의 제증명 서류 발급 대기 시간을 단축하기 위해 본관 1층 로비와 별관 2층에 무인 발급 키오스크 2대를 추가로 설치하였습니다. 진료비 영수증, 세부내역서, 진료확인서 등은 키오스크를 통해 빠르고 간편하게 발급받으실 수 있습니다.', NULL, 280, now() - INTERVAL '8 days'),
(3, FALSE, '[이벤트] 건강검진센터 리모델링 확장 오픈 기념 특별 이벤트', '본원 건강검진센터가 고객 편의를 중심으로 한 대대적인 리모델링을 마치고 확장 오픈하였습니다. 이를 기념하여 이번 달 종합검진 예약자에 한해 초음파 검사 1종 무료 추가 이벤트를 진행하오니 많은 관심 바랍니다.', '/images/notice_05.jpg', 1500, now() - INTERVAL '7 days'),
(2, FALSE, '[수칙] 입원 환자 병문안 제한 시간 및 원내 방역 수칙 재안내', '감염 예방과 환자의 절대 안정을 위해 병문안 시간을 평일 오후 6시~8시, 주말 오전 10시~12시 및 오후 6시~8시로 제한하고 있습니다. 방문객 여러분께서는 반드시 마스크를 착용하시고 병동 입구에서 손 소독을 철저히 해주시기 바랍니다.', NULL, 430, now() - INTERVAL '6 days'),
(1, FALSE, '[소식] 소아청소년과 대기실 어린이 놀이방 새단장', '어린이 환자들이 병원 진료 대기 중 느끼는 불안감을 줄일 수 있도록 소아청소년과 대기실 놀이방을 친환경 소재로 새단장하였습니다. 아이들이 좋아하는 다양한 도서와 안전한 장난감이 비치되어 있으니 많은 이용 바랍니다.', '/images/notice_06.jpg', 390, now() - INTERVAL '5 days'),
(2, FALSE, '[캠페인] 진료 예약 부도(No-Show) 방지 캠페인', '당일 진료 예약 취소 없이 내원하지 않으시는 경우, 진료가 꼭 필요한 다른 환자분들의 소중한 치료 기회를 빼앗게 됩니다. 사정이 생겨 방문이 어려우실 경우 최소 하루 전까지 홈페이지나 콜센터를 통해 예약을 취소해 주시길 당부드립니다.', NULL, 210, now() - INTERVAL '4 days'),
(1, FALSE, '[강좌] 지역 주민과 함께하는 당뇨병 예방 및 관리 건강 교실 개최', '내분비내과 전문의와 임상영양사가 함께하는 "건강한 삶을 위한 당뇨병 관리" 무료 건강 강좌를 개최합니다. 질환에 대한 올바른 이해와 실생활 식이요법에 대해 자세히 알아보는 시간에 여러분을 초대합니다. (장소: 본관 대강당)', '/images/notice_07.jpg', 560, now() - INTERVAL '4 days'),
(2, FALSE, '[안내] 물가 상승에 따른 구내식당 외부인 식단가 인상 안내', '최근 지속적인 식자재 물가 상승으로 인해 부득이하게 환자 보호자 및 일반 방문객 대상의 구내식당 식권을 다음 달 1일 자로 500원 인상하게 되었습니다. 입원 환자분들의 병원식 단가는 변동 없으며, 더 나은 품질의 식사로 보답하겠습니다.', NULL, 780, now() - INTERVAL '3 days'),
(1, FALSE, '[소식] 의료기관 인증평가 3주기 연속 우수 병원 획득', '본원이 보건복지부 산하 의료기관평가인증원으로부터 실시된 인증평가에서 환자 안전과 의료의 질 향상 부문 우수성을 인정받아 3주기 연속 "우수 인증 병원" 마크를 획득하였습니다. 앞으로도 믿고 찾을 수 있는 병원이 되겠습니다.', '/images/notice_08.jpg', 820, now() - INTERVAL '3 days'),
(1, FALSE, '[규정] 원내 분실물(유실물) 보관 및 처리 규정 안내', '병원 내에서 습득된 분실물은 본관 1층 보안실에서 통합 관리하고 있습니다. 분실물 접수 후 30일이 경과하여도 소유자가 나타나지 않는 물품(귀중품 제외)은 관련 규정에 따라 자체 폐기되오니 물품 관리에 각별히 유의해 주시기 바랍니다.', NULL, 120, now() - INTERVAL '2 days'),
(3, FALSE, '[출시] 더욱 빠르고 편리해진 병원 공식 모바일 앱(App) 2.0 리뉴얼', '환자 맞춤형 서비스를 강화한 공식 모바일 앱 2.0 버전이 새롭게 출시되었습니다. 모바일 진료카드, 실시간 대기 순번 확인, 진료비 모바일 결제 등 다양한 스마트 기능을 지금 바로 구글 플레이스토어와 앱스토어에서 만나보세요.', '/images/notice_09.jpg', 1100, now() - INTERVAL '2 days'),
(3, FALSE, '[안내] 거동 불편 환자를 위한 휠체어 및 유모차 무료 대여 서비스', '노약자 및 거동이 불편하신 환자분들을 위해 본관 1층 안내 데스크에서 휠체어와 유모차를 무료로 대여해 드리고 있습니다. 신분증을 맡기시면 당일 진료 시간 내에 자유롭게 이용하실 수 있으니 필요한 분들의 많은 이용 바랍니다.', NULL, 290, now() - INTERVAL '1 day'),
(3, FALSE, '[안내] 2026년 귀속 연말정산용 의료비 납입 증명서 발급 안내', '연말정산 기간을 맞아 의료비 납입 증명서 발급 방법을 안내해 드립니다. 국세청 홈택스 연말정산 간소화 서비스에 본원 진료 내역이 자동 전송되오니 홈택스에서 먼저 확인해 주시기 바라며, 누락된 건에 대해서만 홈페이지를 통해 출력해 주시기 바랍니다.', '/images/notice_10.jpg', 2300, now() - INTERVAL '1 day'),
(1, FALSE, '[변경] 외래 진료 구역 재배치에 따른 진료실 이전 안내', '진료 효율성과 환자 동선 개선을 위해 외래 진료 구역이 일부 재배치되었습니다. 기존 본관 2층에 위치했던 피부과와 안과가 이번 주부터 별관 2층 새 진료실로 이전하여 진료를 시작하오니 방문 시 착오 없으시기 바랍니다.', NULL, 640, now());

-- ==================================================

-- 고객의소리 미답변 10개
INSERT INTO voc (mem_id, title, content, upload_img, write_date) VALUES
(1, '내과 외래 간호사 선생님 정말 친절하십니다.', '어제 내과 진료를 보러 가셨던 저희 어머니께서 간호사 선생님의 친절한 안내 덕분에 병원 이용이 정말 편했다고 칭찬해 달라고 하셨습니다. 바쁘신 와중에도 웃으며 설명해 주셔서 진심으로 감사드립니다.', '/images/voc_01.jpg', now() - INTERVAL '20 days'),
(1, '본관 지하 주차장 결제 시스템이 너무 느려요.', '진료 끝나고 출차할 때마다 무인 정산기 터치 반응이 너무 느려서 뒤에 차가 밀리는 경우가 많습니다. 기계를 업그레이드하거나 바코드 인식률을 높여주시면 좋겠습니다.', NULL, now() - INTERVAL '19 days'),
(1, '진료 예약 시간보다 1시간이나 더 대기했습니다.', '오전 10시 예약이었는데, 11시가 넘어서야 진료실에 들어갈 수 있었습니다. 응급 환자가 있어서 지연되었다고는 하지만, 미리 양해를 구하거나 안내 방송이라도 해주셨어야 하는 것 아닌가요?', '/images/voc_02.jpg', now() - INTERVAL '18 days'),
(1, '진단서 재발급 비용 및 절차가 궁금합니다.', '한 달 전에 발급받은 진단서를 분실해서 다시 발급받고 싶습니다. 굳이 의사 선생님 진료를 다시 봐야 하는지, 아니면 원무과에서 바로 출력만 가능한지, 비용은 얼마인지 알려주세요.', NULL, now() - INTERVAL '17 days'),
(1, '정형외과 교수님 덕분에 다시 걷게 되었습니다.', '교통사고로 크게 다쳐서 걷기 힘들었는데, 정형외과 교수님의 수술과 재활 치료 덕분에 이제 혼자서도 산책을 할 수 있게 되었습니다. 생명의 은인이십니다. 정말 고맙습니다.', '/images/voc_03.jpg', now() - INTERVAL '16 days'),
(2, '보험사 제출용 영문 소견서 발급 관련', '해외 보험사에 진료 기록을 제출해야 해서 영문 소견서가 필요합니다. 여권 사본을 가져가야 하는지, 영문 번역 비용은 병원에 내는 건지 절차가 헷갈려서 문의 남깁니다.', NULL, now() - INTERVAL '9 days'),
(2, '청소 여사님들 덕분에 병동이 항상 쾌적합니다.', '매일 아침 일찍부터 병실 구석구석 쓸고 닦아주시는 미화 여사님들 덕분에 입원 기간 내내 쾌적하게 지내고 있습니다. 항상 밝은 미소로 인사해 주셔서 기분도 좋아집니다.', '/images/voc_07.jpg', now() - INTERVAL '8 days'),
(2, '외래 대기실 의자가 너무 딱딱해서 허리가 아픕니다.', '대기 시간이 긴 편인데, 플라스틱 의자가 너무 딱딱해서 허리와 엉덩이가 배깁니다. 환자들 연령대가 높은 편이니 폭신한 쿠션 방석이라도 깔아주시면 큰 도움이 될 것 같습니다.', NULL, now() - INTERVAL '7 days'),
(3, '무인 수납기 오류 발생 시 안내 직원이 없어요.', '수납 바코드가 안 찍혀서 무인 수납기 앞에서 계속 헤맸는데, 도와주시는 직원이 안 계셔서 한참을 서 있었습니다. 기계에 익숙하지 않은 어르신들을 위해 전담 안내 직원이 있으면 좋겠습니다.', '/images/voc_08.jpg', now() - INTERVAL '6 days'),
(3, '토요일 오후나 주말에도 MRI 촬영이 가능한가요?', '평일에는 도저히 직장을 뺄 수가 없어서 주말에 검사를 받아야 합니다. 주말에도 영상의학과 MRI 검사 예약 및 촬영이 정상적으로 운영되는지 알고 싶습니다.', NULL, now() - INTERVAL '5 days'),
