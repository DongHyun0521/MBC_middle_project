DROP TABLE IF EXISTS member_vehicle;	-- 1번
DROP TABLE IF EXISTS payment;			-- 2번
DROP TABLE IF EXISTS mem;				-- 3번
DROP TABLE IF EXISTS parking_spot;		-- 4번
DROP TABLE IF EXISTS parking_log;		-- 5번

-- mem 테이블 (회원 정보)
CREATE TABLE mem (
    mem_id SERIAL PRIMARY KEY,				-- PK
    id VARCHAR(20) UNIQUE NOT NULL,			-- 아이디 (6~16자, 영어 소문자+숫자만 사용가능)
    password VARCHAR(100) NOT NULL,			-- 비밀번호 (6~16자, 영어 대/소문자 1개이상, 특수문자 1개이상)
    name VARCHAR(30) NOT NULL,				-- 이름
    birthday INTEGER NOT NULL,				-- 생년월일 (숫자로 8자리)
    gender INTEGER NOT NULL,				-- 성별 (남:1, 여:2)
    address VARCHAR(100) NOT NULL,			-- 주소
	address_detail VARCHAR(100) NOT NULL,	-- 상세 주소
    phone_number VARCHAR(20) NOT NULL,		-- 전화번호
    email VARCHAR(50) NOT NULL,				-- 이메일
    del INTEGER DEFAULT 0 NOT NULL			-- 탈퇴 여부
);
-- member_vehicle 테이블 (회원 차량 정보)
CREATE TABLE member_vehicle (
    vehicle_id SERIAL PRIMARY KEY,					-- PK
    mem_id INTEGER NOT NULL
		REFERENCES mem(mem_id) ON DELETE CASCADE,	-- FK (mem.mem_id)
    vehicle_num VARCHAR(20) NOT NULL,				-- 차량 번호 (회원이 등록)
    vehicle_type VARCHAR(20),						-- 차종
    fuel_type VARCHAR(20)							-- 연료
);
-- parking_log 테이블 (주차 이용 기록)
CREATE TABLE parking_log (
    parking_log_id SERIAL PRIMARY KEY,		-- PK
    vehicle_num VARCHAR(20) NOT NULL,		-- 차량 번호 (OCR 결과)
    entry_time TIMESTAMP DEFAULT now(),		-- 입차 시간
    exit_time TIMESTAMP,					-- 출차 시간
    is_member BOOLEAN DEFAULT FALSE,		-- 회원 여부
    payment_status BOOLEAN DEFAULT FALSE	-- 결제 여부
);
-- payment 테이블 (결제 정보)
CREATE TABLE payment (
    pay_id SERIAL PRIMARY KEY,						-- PK
    parking_log_id INTEGER NOT NULL
		REFERENCES parking_log(parking_log_id),		-- FK (parking_log.parking_log_id)
    mem_id INTEGER
		REFERENCES mem(mem_id) ON DELETE SET NULL,	-- FK (mem.mem_id)
    amount INTEGER CHECK (amount >= 0),				-- 결제 금액
    pay_method VARCHAR(20),							-- 결제 수단
    pay_date TIMESTAMP DEFAULT now()				-- 결제 일시
);
-- parking_spot 테이블 (주차 구역 상태)
CREATE TABLE parking_spot (
    spot_id SERIAL PRIMARY KEY,					-- PK
    parking_log_id INTEGER DEFAULT NULL
		REFERENCES parking_log(parking_log_id),	-- FK(parking_log.parking_log_id)
    floor INTEGER NOT NULL,						-- 층수
    row_num INTEGER NOT NULL,					-- 행 번호
    column_num INTEGER NOT NULL,				-- 열 번호
    is_parked BOOLEAN NOT NULL DEFAULT FALSE	-- 주차 여부
);

SELECT * FROM mem;
SELECT * FROM member_vehicle;
SELECT * FROM parking_log;
SELECT * FROM parking_spot;
SELECT * FROM payment;

DELETE FROM payment;		-- 1번
DELETE FROM parking_log;	-- 2번

DELETE FROM member_vehicle;	-- 1번
DELETE FROM mem;			-- 2번

INSERT INTO mem (id, password, name, birthday, gender, address, address_detail, phone_number, email)
	VALUES ('abc123', 'Password!', '가나다', '20010521', 1, '서울 종로구 종로 69', '서울YMCA 3층 MBC아카데미', '010-1234-5678', 'test1@shospital.com');
INSERT INTO mem (id, password, name, birthday, gender, address, address_detail, phone_number, email)
	VALUES ('def456', 'A!1234', '하파타', '19981225', 2, '경기 성남시 중원구 상대원동 사기막골로9번길 40', '에스트래픽 성남캠퍼스', '017-098-7654', 'test2@shospital.com');

INSERT INTO member_vehicle (mem_id, vehicle_num, vehicle_type, fuel_type)
	VALUES (1, '10거1425', 'SUV', '휘발유');
INSERT INTO member_vehicle (mem_id, vehicle_num, vehicle_type, fuel_type)
	VALUES (2, '10다4776', '세단', '전기');
INSERT INTO member_vehicle (mem_id, vehicle_num, vehicle_type, fuel_type)
	VALUES (2, '11나8192', 'SUV', '경유');
