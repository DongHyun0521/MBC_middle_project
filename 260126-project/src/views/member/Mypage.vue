<template>
  <div class="full-dashboard">
    <aside class="sidebar">
      <div class="profile-area">
        <div class="avatar">{{ userRoleIcon }}</div>
        <p class="user-id">{{ userInfo.name }} 님</p>
        <p class="user-tag">{{ userRoleDisplay }}</p>
        <p class="join-date">
          {{ userType === 'MEMBER' ? '가입일' : '입사일' }}: {{ formatDate(userInfo.createTime || userInfo.create_time) }}
        </p>
      </div>
      <nav class="side-nav">
        <ul>
          <li :class="{ active: currentView === 'dash' }" @click="changeView('dash')">대시보드 홈</li>
          <li :class="{ active: currentView === 'res' }" @click="changeView('res')">내 진료 예약 내역</li>

          <template v-if="isDoctor">
            <li :class="{ active: currentView === 'doc_res' }" @click="changeView('doc_res')">진료 업무 일정</li>
            <li :class="{ active: currentView === 'doc_history' }" @click="changeView('doc_history')">담당 환자 조회</li>
          </template>

          <template v-if="isNurse">
            <li :class="{ active: currentView === 'nur_work' }" @click="changeView('nur_work')">진료 업무 일정</li>
            <li :class="{ active: currentView === 'nur_schedule' }" @click="changeView('nur_schedule')">근무 일정 (Shift)
            </li>
            <li :class="{ active: currentView === 'nur_ward' }" @click="changeView('nur_ward')">병동 현황</li>
          </template>

          <template v-if="userType === 'ADMIN'">
            <li :class="{ active: currentView === 'admin_voc' }" @click="changeView('admin_voc')">고객의 소리 (VOC)</li>
            <li :class="{ active: currentView === 'admin_todo' }" @click="changeView('admin_todo')">업무 관리 (To-Do)</li>
          </template>

          <li :class="{ active: currentView === 'vehi' }" @click="changeView('vehi')">차량 관리</li>
          <li :class="{ active: currentView === 'edit' }" @click="changeView('edit')">개인 정보 수정</li>
        </ul>
      </nav>
    </aside>

    <main class="main-content">
      <header class="dashboard-header">
        <div class="welcome-text">
          <h2>{{ getTimeGreeting() }}, <span class="blue-txt">{{ userInfo.name }}</span>님</h2>
          <p class="current-time">{{ currentTime }}</p>
        </div>
      </header>

      <div v-if="currentView === 'dash'" class="dash-home-grid">
        <section class="dash-card profile-card">
          <div class="card-head">
            <h3>👤 내 정보 요약</h3>
          </div>
          <div class="info-list">
            <div class="info-item">
              <span class="label">소속/주소</span>
              <p class="val">{{ userType === 'MEMBER' ? userInfo.address : (userInfo.adminDeptName || userInfo.deptName || '소속없음') }}</p>
            </div>
            <div class="info-item">
              <span class="label">연락처</span>
              <p class="val">{{ userInfo.phoneNumber }}</p>
            </div>
            <div class="info-item">
              <span class="label">생년월일</span>
              <p class="val">{{formatDate( userInfo.birthday)}}</p>
            </div>
            <div class="info-item"><span class="label">이메일</span>
              <p class="val">{{ userInfo.email }}</p>
            </div>
            <div v-if="userType !== 'MEMBER'" class="info-item">
              <span class="label">직책</span>
              <p class="val">{{ userInfo.role || userInfo.rank || '직원' }}</p>
            </div>
          </div>
        </section>

        <template v-if="isDoctor || isNurse">
          <section class="dash-card">
            <div class="card-head">
              <h3>오늘 진료 현황</h3>
            </div>
            <div class="stat-grid">
              <div class="stat-box blue">
                <span>{{doctorSchedules.filter(s => s.reservation_status === '예약').length}}</span>명<br>예약 대기
              </div>
              <div class="stat-box">
                <span>{{doctorSchedules.filter(s => s.reservation_status === '완료').length}}</span>명<br>진료 완료
              </div>
            </div>
          </section>
        </template>

        <template v-if="userType === 'ADMIN'">
          <section class="dash-card">
            <div class="card-head">
              <h3>긴급 업무</h3>
            </div>
            <div class="stat-grid">
              <div class="stat-box red">
                <span>{{vocList.filter(v => !v.answerStatus).length}}</span>건<br>VOC 미답변
              </div>
              <div class="stat-box">
                <span>{{todoList.filter(t => !t.done).length}}</span>건<br>미완료 업무
              </div>
            </div>
          </section>
        </template>

        <section class="dash-card">
          <div class="card-head">
            <h3>나의 병원 예약</h3>
          </div>
          <div v-if="upcomingRes" class="res-highlight">
            <span class="d-day">D-{{ calculateDday(upcomingRes.reservation_date) }}</span>
            <p class="res-time-txt">{{ upcomingRes.reservation_date }} ({{ upcomingRes.reservation_time }})</p>
            <p class="res-doc-txt">{{ upcomingRes.dept_name }} | {{ upcomingRes.doctor_name }} 의사</p>
          </div>
          <div v-else class="empty-res">예정된 예약이 없습니다</div>
        </section>
      </div>

      <div v-if="currentView === 'doc_res' || currentView === 'nur_work'" class="view-section">
        <div class="section-card">
          <div class="card-head">
            <h3>진료 업무 일정</h3>
            <div class="toggle-group">
              <button :class="{ active: docViewMode === 'calendar' }" @click="docViewMode = 'calendar'">달력</button>
              <button :class="{ active: docViewMode === 'list' }" @click="docViewMode = 'list'">리스트</button>
            </div>
          </div>

          <div v-if="docViewMode === 'calendar'" class="calendar-wrap">
            <div class="cal-header">
              <button @click="changeMonth(-1)">&lt;</button>
              <h4>{{ calYear }}년 {{ calMonth }}월</h4>
              <button @click="changeMonth(1)">&gt;</button>
            </div>
            <div class="cal-grid">
              <div v-for="day in ['일', '월', '화', '수', '목', '금', '토']" :key="day" class="cal-day-head">{{ day }}</div>
              <div v-for="(date, idx) in calendarDays" :key="idx"
                :class="['cal-cell', { 'diff-month': !date.isCurrentMonth, 'today': isToday(date.fullDate), 'selected': selectedDate === date.fullDate }]"
                @click="handleCalDateClick(date.fullDate)">
                <span class="day-num">{{ date.day }}</span>
                <div class="cal-events">
                  <div v-for="(evt, i) in getEventsForDate(date.fullDate).slice(0, 3)" :key="i"
                    :class="['cal-dot', getBadgeClass(evt.reservation_status)]">
                    {{ String(evt.reservation_time).substring(11, 16) }} {{ evt.patient_name }}
                  </div>
                  <div v-if="getEventsForDate(date.fullDate).length > 3" class="cal-more">...</div>
                </div>
              </div>
            </div>

            <div v-if="selectedDate" class="selected-date-list">
              <h4>{{ selectedDate }} 예약 리스트</h4>
              <table class="hospital-tbl mt-10">
                <thead>
                  <tr>
                    <th>시간</th>
                    <th>환자명</th>
                    <th>상태</th>
                    <th class="txt-center">관리</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="sc in selectedDateSchedules" :key="sc.reservation_id">
                    <td>{{ String(sc.reservation_time).substring(11, 16) }}</td>
                    <td class="bold-blue">{{ sc.patient_name }}</td>
                    <td><span :class="['status-badge', getBadgeClass(sc.reservation_status)]">{{ sc.reservation_status
                    }}</span></td>
                    <td class="txt-center">
                      <div v-if="isDoctor && sc.reservation_status === '예약'" class="btn-group">
                        <button class="btn-action complete" @click="completeTreatment(sc)">완료</button>
                        <button class="btn-action noshow" @click="handleNoShow(sc)">미방문</button>
                        <button class="btn-action cancel" @click="handleForceCancel(sc)">취소</button>
                      </div>
                      <span v-else>-</span>
                    </td>
                  </tr>
                  <tr v-if="selectedDateSchedules.length === 0">
                    <td colspan="4" class="txt-center py-20">해당 날짜에 예약이 없습니다.</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <div v-else>
            <div class="filter-tabs">
              <button v-for="st in ['전체', '예약', '완료', '취소', '미방문']" :key="st"
                :class="['filter-btn', { active: docFilter === st }]" @click="docFilter = st">
                {{ st }}
              </button>
            </div>
            <table class="hospital-tbl">
              <thead>
                <tr>
                  <th>날짜</th>
                  <th>시간</th>
                  <th>환자명</th>
                  <th>상태</th>
                  <th>메모</th>
                  <th class="txt-center" width="180px">관리</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="sc in filteredDoctorSchedules" :key="sc.reservation_id">
                  <td class="bold-text">{{ formatDate(sc.reservation_date) }}</td>
                  <td>{{ String(sc.reservation_time).substring(11, 16) }}</td>
                  <td class="bold-blue">{{ sc.patient_name }}</td>
                  <td>
                    <span :class="['status-badge', getBadgeClass(sc.reservation_status)]">{{ sc.reservation_status
                    }}</span>
                  </td>
                  <td>{{ sc.reservation_memo || '-' }}</td>

                  <td class="txt-center">
                    <div v-if="isDoctor && sc.reservation_status === '예약'" class="btn-group">
                      <button class="btn-action complete" @click="completeTreatment(sc)">완료</button>
                      <button class="btn-action noshow" @click="handleNoShow(sc)">미방문</button>
                      <button class="btn-action cancel" @click="handleForceCancel(sc)">취소</button>
                    </div>
                    <span v-else>-</span>
                  </td>
                </tr>
              </tbody>
            </table>
            <div v-if="filteredDoctorSchedules.length === 0" class="empty-msg">내역이 없습니다.</div>
          </div>
        </div>
      </div>

      <div v-if="currentView === 'nur_schedule'" class="view-section">
        <div class="section-card">
          <div class="card-head">
            <h3>근무 일정표 (Shift)</h3>
            <p class="sub-desc">날짜를 클릭하여 근무 메모를 남기세요.</p>
          </div>
          <div class="calendar-wrap">
            <div class="cal-header">
              <button @click="changeMonth(-1)">&lt;</button>
              <h4>{{ calYear }}년 {{ calMonth }}월</h4>
              <button @click="changeMonth(1)">&gt;</button>
            </div>
            <div class="cal-grid">
              <div v-for="day in ['일', '월', '화', '수', '목', '금', '토']" :key="day" class="cal-day-head">{{ day }}</div>
              <div v-for="(date, idx) in calendarDays" :key="idx"
                :class="['cal-cell', { 'diff-month': !date.isCurrentMonth }]"
                @click="handleCalDateClick(date.fullDate)">
                <span class="day-num">{{ date.day }}</span>
                <div class="cal-events">
                  <div v-if="getShiftMemo(date.fullDate)" class="cal-dot shift">
                    {{ getShiftMemo(date.fullDate) }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-if="currentView === 'admin_todo'" class="view-section">
        <div class="split-view">
          <div class="section-card flex-1">
            <div class="card-head">
              <h3>업무 달력</h3>
            </div>
            <div class="calendar-wrap mini">
              <div class="cal-header">
                <button @click="changeMonth(-1)">&lt;</button>
                <span>{{ calYear }}.{{ calMonth }}</span>
                <button @click="changeMonth(1)">&gt;</button>
              </div>
              <div class="cal-grid">
                <div v-for="d in calendarDays" :key="d.fullDate"
                  :class="['cal-cell', { 'today': isToday(d.fullDate) }]">
                  <span class="day-num">{{ d.day }}</span>
                </div>
              </div>
            </div>
          </div>
          <div class="section-card flex-1">
            <div class="card-head">
              <h3>To-Do List</h3>
            </div>
            <div class="todo-input-box">
              <input v-model="newTodo" @keyup.enter="addTodo" placeholder="업무를 입력해 주세요" />
              <button @click="addTodo">추가</button>
            </div>
            <ul class="todo-list">
              <li v-for="(todo, i) in todoList" :key="i">
                <input type="checkbox" v-model="todo.done" />
                <span :class="{ done: todo.done }">{{ todo.text }}</span>
                <button @click="removeTodo(i)" class="btn-del-x">×</button>
              </li>
            </ul>
          </div>
        </div>
      </div>

      <div v-if="currentView === 'admin_voc'" class="view-section">
        <div class="section-card">
          <div class="card-head">
            <h3>고객의 소리 관리</h3>
          </div>
          <table class="hospital-tbl">
            <thead>
              <tr>
                <th>상태</th>
                <th>제목</th>
                <th>작성자</th>
                <th>작성일</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="voc in vocList" :key="voc.vocId">
                <td>
                  <span v-if="!voc.answerStatus" class="red-alert">🔴 미답변</span>
                  <span v-else class="badge-gray">답변완료</span>
                </td>
                <td>{{ voc.title }}</td>
                <td>{{ voc.writerName }}</td>
                <td>{{ formatDate(voc.writeDate) }}</td>
              </tr>
            </tbody>
          </table>
          <div v-if="vocList.length === 0" class="empty-msg">접수된 VOC가 없습니다.</div>
        </div>
      </div>

      <div v-if="currentView === 'doc_history'" class="view-section">
        <div class="section-card">
          <div class="card-head">
            <h3>담당 환자 조회</h3>
          </div>
          <div class="empty-msg">준비중입니다.</div>
        </div>
      </div>
      <div v-if="currentView === 'nur_ward'" class="view-section">
        <div class="section-card">
          <div class="card-head">
            <h3>병동 현황</h3>
          </div>
          <div class="empty-msg">병동 시스템 연동 준비중</div>
        </div>
      </div>

      <div v-if="currentView === 'vehi'" class="view-section">
        <div class="section-card">
          <div class="card-head">
            <h3>차량 관리</h3><button @click="router.push('/vehiregi')" class="btn-add-sm">+ 새 차량 등록</button>
          </div>
          <table class="hospital-tbl">
            <thead>
              <tr>
                <th>차량번호</th>
                <th>차종/유종</th>
                <th class="txt-center">관리</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="car in myVehicles" :key="car.vehicleNum">
                <td class="bold-blue">{{ car.vehicleNum }}</td>
                <td>{{ car.vehicleType }} / {{ car.fuelType }}</td>
                <td class="txt-center"><button @click="deleteVehicle(car.vehicleNum)" class="btn-cancel-table">차량
                    삭제</button></td>
              </tr>
            </tbody>
          </table>
          <div v-if="myVehicles.length === 0" class="empty-msg">등록된 차량이 없습니다.</div>
        </div>
      </div>

      <div v-if="currentView === 'res'" class="view-section">
        <div class="section-card">
          <div class="card-head">
            <h3>내 진료 예약 내역 (개인)</h3>
          </div>
          <div class="filter-tabs">
            <button v-for="tab in ['전체', '예약', '완료', '취소']" :key="tab"
              :class="['filter-btn', { active: resFilter === tab }]" @click="resFilter = tab">{{ tab }}</button>
          </div>
          <table class="hospital-tbl">
            <thead>
              <tr>
                <th>진료과</th>
                <th>담당의</th>
                <th>예약일시</th>
                <th>상태</th>
                <th class="txt-center">관리</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="res in filteredMyReservations" :key="res.reservation_id">
                <td class="bold-blue">{{ res.dept_name }}</td>
                <td class="bold-blue">{{ res.doctor_name }}</td>
                <td>{{ formatDate(res.reservation_date) }} {{ res.reservation_time }}</td>
                <td><span :class="['status-badge', res.reservation_status === '예약' ? 'active' : 'done']">{{
                  res.reservation_status }}</span></td>
                <td class="txt-center"><button v-if="res.reservation_status === '예약'" class="btn-cancel-table"
                    @click="cancelRes(res.reservation_id)">예약취소</button><span v-else>-</span></td>
              </tr>
            </tbody>
          </table>
          <div v-if="filteredMyReservations.length === 0" class="empty-msg">
            {{ resFilter === '전체' ? '예약 내역이 없습니다' : '해당하는 내역이 없습니다' }}
          </div>
        </div>
      </div>

      <div v-if="currentView === 'edit'" class="view-section centered">
        <div class="edit-card-wrap">
          <div class="section-card">
            <div class="card-head">
              <h3>개인 정보 수정</h3>
            </div>
            <div class="edit-form">
              <div class="f-row">
                <span>아이디</span>
                <p class="readonly-val">{{ userInfo.id }}</p>
              </div>
              <div class="f-row">
                <span>이름</span>
                <input v-model="userInfo.name" type="text" />
              </div>

              <template v-if="userType !== 'MEMBER'">
                <div class="f-row"><span>소속 부서</span>
                  <p class="readonly-val">{{ userInfo.adminDeptName || userInfo.deptName || '소속없음' }}</p>
                </div>
                <div class="f-row"><span>직급/직책</span>
                  <p class="readonly-val">{{ userInfo.rank || userInfo.role }}</p>
                </div>
                <div class="f-row"><span>입사일</span>
                  <p class="readonly-val">{{ formatDate(userInfo.createTime || userInfo.create_time) }}</p>
                </div>
              </template>

              <template v-if="userType === 'MEMBER'">
                <div class="f-row"><span>생년월일</span>
                  <p class="readonly-val">{{ formatBirthday(userInfo.birthday) }}</p>
                </div>
                <div class="f-row"><span>주소</span>
                  <div class="addr-box-flex"><input v-model="userInfo.address" type="text" readonly
                      @click="openPostcode" placeholder="주소 검색" /><button type="button" @click="openPostcode"
                      class="btn-addr-search">검색</button></div>
                </div>
                <div class="f-row"><span>상세주소</span><input v-model="userInfo.addressDetail" type="text"
                    id="detailAddr" /></div>
              </template>

              <div class="f-row"><span>연락처</span>
                <input v-model="userInfo.phoneNumber" type="text" />
              </div>
              <div class="f-row"><span>이메일</span>
                <input v-model="userInfo.email" type="email" />
              </div>

              <div class="btn-area-center">
                <button @click="saveUserInfo" class="btn-blue-full">정보 업데이트</button>
              </div>
            </div>
          </div>

          <div class="section-card mt-50">
            <div class="card-head">
              <h3>비밀번호 변경</h3>
            </div>

            <div class="edit-form">
              <div class="f-row">
                <span>새 비밀번호</span>
                <div class="pw-group">
                  <div class="pw-field-box">
                    <input v-model="pwData.newPw" :type="showNewPw ? 'text' : 'password'" placeholder="영문 대문자+특수문자 포함 6~16자" @input="validatePassword" />
                    <div class="pw-eye-icon" @click="showNewPw = !showNewPw">
                      <svg v-if="showNewPw" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                      <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/>
                        <line x1="1" y1="1" x2="23" y2="23"/></svg>
                    </div>
                  </div>
                  <p v-if="pwErrorMsg" class="error-msg">{{ pwErrorMsg }}</p>
                </div>
              </div>

              <div class="f-row">
                <span>비밀번호 확인</span>
                <div class="pw-group">
                  <div class="pw-field-box">
                    <input v-model="pwData.newPwConfirm" :type="showNewPwConfirm ? 'text' : 'password'" placeholder="비밀번호를 한 번 더 입력하세요" @input="validatePassword" />
                    <div class="pw-eye-icon" @click="showNewPwConfirm = !showNewPwConfirm">
                      <svg v-if="showNewPwConfirm" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                      <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/>
                        <line x1="1" y1="1" x2="23" y2="23"/></svg>
                    </div>
                  </div>
                  <p v-if="pwConfirmMsg && !pwErrorMsg" :class="['error-msg', { 'success-msg': isPwMatch }]">{{ pwConfirmMsg }}</p>
                </div>
              </div>

              <div class="btn-area-center">
                <button @click="handlePasswordUpdate" class="btn-action-submit gray">비밀번호 변경 완료</button>
              </div>
            </div>
          </div>

          <div class="withdraw-container">
            <span class="withdraw-link" @click="startWithdraw">회원 탈퇴</span>
          </div>
        </div>
      </div>
    </main>

    <div v-if="isAuthModalOpen" class="modal-overlay">
      <div class="modal-card auth-modal">
        <h3>보안을 위해 비밀번호를 입력해 주세요</h3>
        <div class="pw-field-box mb-25">
          <input v-model="authPw" :type="showAuthPw ? 'text' : 'password'" class="auth-pw-input" @keyup.enter="verifyAccess" />
          <div class="pw-eye-icon" @click="showAuthPw = !showAuthPw">
            <svg v-if="showAuthPw" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor"
              stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
              <circle cx="12" cy="12" r="3"></circle>
            </svg>
            <svg v-else width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
              stroke-linecap="round" stroke-linejoin="round">
              <path
                d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24">
              </path>
              <line x1="1" y1="1" x2="23" y2="23"></line>
            </svg>
          </div>
        </div>
        <div class="modal-btns">
          <button @click="verifyAccess" class="btn-modal-confirm">확인</button>
          <button @click="cancelAccess" class="btn-modal-cancel">취소</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// ref: 데이터와 화면을 연결해서, 데이터만 바꾸면 화면도 알아서 변하도록 하는 프레임워크
// vue: 일반 데이터를 Vue가 실시간으로 감시할 수 있는 반응형 주머니에 담는 도구. 주머니 속 내용물을 꺼낼 땐 .value를 꼭 붙여야 함
// onMounted: 화면(DOM)이 그려지자마자 이것 먼저 실행하라고 시키는 시작 버튼이며, 주로 서버에서 데이터를 처음 가져올 때 사용
// computed: 결과값을 캐싱(저장)해둬서 똑같은 계산을 반복 안 함. 연결된 데이터가 바뀔 때만 다시 계산해서 효율적

import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'

// [API Import] 서버 통신 함수들
import { updateInfoReq, withdrawReq, getMyInfoReq } from '@/api/member'
import { getMyResReq, cancelResReq, getAllDoctorsReq, getDocSchedReq } from '@/api/reservation'
import { getVehiReq, delVehiReq } from '@/api/vehicle'
import { getAdminVocListReq } from '@/api/customer'

// 1. 기본 설정 및 상태 변수 (State)
const router = useRouter()
const currentView = ref('dash') // 현재 화면 상태

// 사용자 정보
const userInfo = ref({})
const userType = ref('MEMBER') // MEMBER, MED, ADMIN

// 리스트 데이터
const myReservations = ref([])   // 내 예약 목록
const doctorSchedules = ref([])  // 의료진 스케줄
const myVehicles = ref([])       // 내 차량 목록
const vocList = ref([])          // VOC 목록

// UI 제어용
const currentTime = ref("")       // 시계
const isAuthModalOpen = ref(false)// 비밀번호 모달
const authPw = ref("")            // 비밀번호 입력값
const pwData = ref({ newPw: '' }) // 새 비밀번호 입력값

// [추가] 눈 표시 상태 변수 (SVG 제어용)
const showAuthPw = ref(false)
const showNewPw = ref(false)

// [의료진용] 캘린더 및 필터
const docViewMode = ref('calendar') // 보기 모드 (calendar / list)
const docFilter = ref('예약')       // [초기설정] '예약' 상태인 것만 먼저 보여줌
const calYear = ref(new Date().getFullYear())
const calMonth = ref(new Date().getMonth() + 1)
const selectedDate = ref(null)      // [달력] 클릭한 날짜 저장

// [환자용] 내 예약 필터
const resFilter = ref('전체');

// [로컬 기능] To-Do / 근무메모
const newTodo = ref('')
const todoList = ref([{ text: '부서 회의 준비', done: false }, { text: '비품 재고 파악', done: true }])
const nurseShiftMemos = ref({})

// (추가) 상세 정보 호출 함수
const fetchMyDetailInfo = async () => {
  try {
    const res = await getMyInfoReq()
    if (res.data) {
      // (추가) 기존 세션에 있던 비밀번호(잘 되던 것)를 임시 저장
      const safePassword = userInfo.value.password;

      // 기존 userInfo(세션 정보)에 서버에서 받은 상세 정보를 덮어씌움
      userInfo.value = {
        ...userInfo.value,
        ...res.data
      }

      // (추가) 만약 덮어쓴 데이터에 비밀번호가 없다면, 임시 저장해둔 걸로 복구
      if (!userInfo.value.password) {
        userInfo.value.password = safePassword;
      }

      console.log("상세 정보:", userInfo.value)
    }
  } catch (e) {
    console.error("상세 정보 가져오기 실패", e)
  }
}

// 2. Computed (계산된 속성)
// 권한 확인
const isDoctor = computed(() => userType.value === 'MED' && (userInfo.value.role || '').toUpperCase().includes('의사' || 'DOCTOR'));
const isNurse = computed(() => userType.value === 'MED' && (userInfo.value.role || '').toUpperCase().includes('간호사' || 'NURSE'));

// 아이콘 및 역할 표시
const userRoleIcon = computed(() => {
  if (userType.value === 'ADMIN') return '💼';
  if (isDoctor.value) return '👨‍⚕️';
  if (isNurse.value) return '💉';
  if (userType.value === 'MED') return '🏥';
  return '👤';
});

const userRoleDisplay = computed(() => {
  if (userType.value === 'MEMBER') return `${formatBirthday(userInfo.value.birthday)} | ${userInfo.value.gender == 1 ? '남' : '여'}`;
  return `${userInfo.value.adminDeptName || userInfo.value.deptName || '소속없음'} | ${userInfo.value.role || userInfo.value.rank || '직원'}`;
});

// 가장 가까운 예약 찾기
const upcomingRes = computed(() => myReservations.value.find(r => r.reservation_status === '예약'));

// [의료진] 스케줄 필터링
const filteredDoctorSchedules = computed(() => {
  if (docFilter.value === '전체') return doctorSchedules.value;
  return doctorSchedules.value.filter(s => s.reservation_status === docFilter.value);
});

// [달력] 클릭한 날짜의 예약만 필터링 (하단 리스트용)
const selectedDateSchedules = computed(() => {
  if (!selectedDate.value) return [];
  return doctorSchedules.value.filter(s => formatDate(s.reservation_date) === selectedDate.value);
});

// [환자] 내 예약 필터링
const filteredMyReservations = computed(() => {
  if (resFilter.value === '전체') return myReservations.value;
  return myReservations.value.filter(r => r.reservation_status === resFilter.value);
});

// [달력] 날짜 생성 로직
const calendarDays = computed(() => {
  const year = calYear.value;
  const month = calMonth.value - 1;
  const firstDay = new Date(year, month, 1);
  const lastDay = new Date(year, month + 1, 0);
  const days = [];

  // 첫째 주 빈칸 채우기
  for (let i = 0; i < firstDay.getDay(); i++) days.push({ day: '', isCurrentMonth: false });
  // 날짜 채우기
  for (let d = 1; d <= lastDay.getDate(); d++) {
    const mm = month + 1 < 10 ? '0' + (month + 1) : (month + 1);
    const dd = d < 10 ? '0' + d : d;
    days.push({ day: d, isCurrentMonth: true, fullDate: `${year}-${mm}-${dd}` });
  }
  return days;
});

// 3. UI Helper Functions
// 화면 전환 및 데이터 로드
const changeView = (view) => {
  if (view === 'edit') isAuthModalOpen.value = true;
  else {
    currentView.value = view;
    if (view === 'vehi') fetchVehicles();
    if (view === 'res') fetchReservations();
    if ((isDoctor.value || isNurse.value) && (view === 'doc_res' || view === 'nur_work')) fetchDoctorSchedules();
    if (userType.value === 'ADMIN' && (view === 'admin_voc' || view === 'dash')) fetchVocList();
  }
};

// 달력 월 이동
const changeMonth = (delta) => {
  let newMonth = calMonth.value + delta;
  if (newMonth > 12) { calYear.value++; newMonth = 1; }
  else if (newMonth < 1) { calYear.value--; newMonth = 12; }
  calMonth.value = newMonth;
};

// To-Do 관리
const addTodo = () => { if (newTodo.value.trim()) { todoList.value.push({ text: newTodo.value, done: false }); newTodo.value = ''; } };
const removeTodo = (idx) => { todoList.value.splice(idx, 1); };

// 간호사 근무 메모
const addShiftMemo = (dateStr) => {
  const memo = prompt(`${dateStr} 근무 내용을 입력하세요`, nurseShiftMemos.value[dateStr] || '');
  if (memo !== null) nurseShiftMemos.value[dateStr] = memo;
};
const getShiftMemo = (dateStr) => nurseShiftMemos.value[dateStr];

// [달력] 날짜 클릭 핸들러 (의사 | 간호사)
const handleCalDateClick = (dateStr) => {
  if (isNurse.value) addShiftMemo(dateStr); // 간호사: 메모 입력
  if (isDoctor.value) selectedDate.value = dateStr; // 의사: 해당 날짜 예약 조회
};

// 날짜/시간 포맷 유틸
const isToday = (str) => { const t = new Date(); const d = new Date(str); return t.toDateString() === d.toDateString(); };
const getEventsForDate = (str) => {
  const clean = str.replace(/-/g, '');
  return doctorSchedules.value.filter(s => String(s.reservation_date).replace(/-/g, '') === clean && s.reservation_status !== '취소');
};

const getBadgeClass = (s) => {
  if (s === '예약') return 'active';
  if (s === '완료') return 'done';
  if (s === '미방문') return 'noshow-badge';
  return 'cancel';
};

const formatDate = (date) => {
  if (!date) return '-'
  const s = String(date)

  // 1. YYYYMMDD 형태인 경우 (예: 20260208)
  if (s.length === 8) {
    return `${s.substring(0, 4)}년 ${s.substring(4, 6)}월 ${s.substring(6, 8)}일`
  }

  // 2. 날짜 객체나 ISO 스트링인 경우 (예: 2026-02-08T...)
  try {
    const d = new Date(date)
    const year = d.getFullYear()
    const month = d.getMonth() + 1
    const day = d.getDate()

    // 월/일이 10보다 작을 때 앞에 0을 붙이기 위함
    const mm = month < 10 ? `0${month}` : month
    const dd = day < 10 ? `0${day}` : day

    return `${year}년 ${mm}월 ${dd}일`
  } catch (e) {
    return s
  }
}

const formatBirthday = (birth) => birth ? String(birth).replace(/(\d{4})(\d{2})(\d{2})/, '$1.$2.$3') : '';
const calculateDday = (dateStr) => {
  // 예: dateStr = "20260206" (YYYYMMDD)
  if (!dateStr) return 0;

  // 1. 날짜 변환 (YYYY-MM-DD로 만들기)
  const formattedDate = String(dateStr).replace(/(\d{4})(\d{2})(\d{2})/, '$1-$2-$3');
  // 결과: "2026-02-06"

  // 2. Date 객체 생성
  const targetDate = new Date(formattedDate);
  const today = new Date();

  // 3. 차이 계산 (밀리초 단위)
  const diff = targetDate - today;

  // 4. 밀리초 -> '일(Day)' 단위로 변환
  // Math.ceil : 소수점 올림 처리
  return Math.ceil(diff / (1000 * 3600 * 24));
};

const getTimeGreeting = () => {
  const h = new Date().getHours();
  if (h < 12) return '좋은 아침입니다';
  if (h < 18) return '즐거운 오후입니다';
  return '편안한 저녁입니다';
};

const updateClock = () => {
  currentTime.value = new Date().toLocaleString('ko-KR', { month: 'long', day: 'numeric', weekday: 'short', hour: '2-digit', minute: '2-digit', second: '2-digit' });
};

// 비밀번호 확인 및 주소검색
const verifyAccess = () => {
  if (authPw.value === userInfo.value.password) { isAuthModalOpen.value = false; currentView.value = 'edit'; authPw.value = ''; }
  else { alert('비밀번호가 일치하지 않습니다.'); }
};
const cancelAccess = () => { isAuthModalOpen.value = false; authPw.value = ''; };
const openPostcode = () => { new window.daum.Postcode({ oncomplete: (data) => { userInfo.value.address = data.roadAddress; } }).open(); };


// 4. API Calls (서버 통신 함수들)
// [환자] 내 예약 내역 조회
const fetchReservations = async () => {
  try {
    const res = await getMyResReq();
    myReservations.value = res.data;
  } catch (e) {
    console.error("예약 조회 실패", e);
  }
};

// [환자] 예약 취소 요청
const cancelRes = async (id) => {
  if (!confirm("정말 예약을 취소하시겠습니까?")) return;

  try {
    await cancelResReq(id);
    fetchReservations(); // 목록 새로고침
    alert("취소되었습니다.");
  } catch (e) {
    alert("오류가 발생했습니다.");
  }
};

// [공통] 등록된 차량 목록 조회
const fetchVehicles = async () => {
  try {
    const res = await getVehiReq();
    myVehicles.value = res.data;
  } catch (e) {
    console.error("차량 조회 실패", e);
  }
};

// [공통] 차량 삭제
const deleteVehicle = async (num) => {
  if (!confirm("선택한 차량을 삭제하시겠습니까?")) return;

  try {
    await delVehiReq(num);
    alert("삭제되었습니다.");
    fetchVehicles(); // 목록 새로고침
  } catch (e) {
    alert("오류가 발생했습니다.");
  }
};

// [관리자] VOC(고객의 소리) 전체 목록 조회
const fetchVocList = async () => {
  try {
    const res = await getAdminVocListReq('all');
    vocList.value = res.data || [];
  } catch (e) {
    console.error("VOC 로딩 실패", e);
  }
};

// [개인정보] 회원 정보 수정 내용 저장
const saveUserInfo = async () => {
  try {
    await updateInfoReq(userInfo.value);
    alert("정보가 수정되었습니다.");
  } catch (e) {
    alert("수정에 실패했습니다.");
  }
};

// * [추가] 실시간 유효성 검사 함수 (입력할 때마다 실행됨)
const validatePassword = () => {
  pwData.value.newPw = pwData.value.newPw.replace(/[ㄱ-ㅎㅏ-ㅣ가-힣]/g, '');
  pwData.value.newPwConfirm = pwData.value.newPwConfirm.replace(/[ㄱ-ㅎㅏ-ㅣ가-힣]/g, '');
  
  const { newPw, newPwConfirm } = pwData.value;
  
  // 초기화
  pwErrorMsg.value = '';
  pwConfirmMsg.value = '';
  isPwMatch.value = false;

  if (!newPw) return;

  // 1. 기존 비밀번호 비교
  if (newPw === userInfo.value.password) {
    pwErrorMsg.value = "기존 비밀번호와 동일한 비밀번호는 사용할 수 없습니다.";
    return;
  }

  // 2. 정규식 검사 (대문자, 특수문자, 6~16자, 한글불가)
  const pwRegex = /^(?=.*[A-Z])(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{6,16}$/;
  if (!pwRegex.test(newPw)) {
    pwErrorMsg.value = "영문 대문자, 특수문자 포함 6 ~ 16자 이내";
    return;
  }

  // 3. 비밀번호 확인 일치 여부
  if (newPwConfirm) {
    if (newPw !== newPwConfirm) {
      pwConfirmMsg.value = "비밀번호가 일치하지 않습니다.";
      isPwMatch.value = false;
    } else {
      pwConfirmMsg.value = "비밀번호가 일치합니다.";
      isPwMatch.value = true;
    }
  }
};

// [수정] 비밀번호 변경 (제출 시 최종 체크)
const handlePasswordUpdate = async () => {
  const { newPw, newPwConfirm } = pwData.value;

  // 입력 누락 체크
  if (!newPw || !newPwConfirm) return alert("비밀번호를 모두 입력해 주세요.");
  
  // 실시간 에러가 남아있는지 체크
  if (pwErrorMsg.value) return alert("비밀번호 규칙을 확인해 주세요.");
  if (!isPwMatch.value) return alert("비밀번호가 일치하지 않습니다.");

  try { 
    await updateInfoReq({ ...userInfo.value, password: newPw }); 
    alert("비밀번호가 변경되었습니다.\n보안을 위해 다시 로그인해 주세요."); 
    sessionStorage.clear(); 
    router.push('/login'); 
  } catch (e) { alert("비밀번호 변경에 실패했습니다."); }
};

// [개인정보] 회원 탈퇴
const startWithdraw = async () => {
  if (!confirm("정말로 탈퇴하시겠습니까?\n모든 데이터가 삭제됩니다.")) return;

  try {
    await withdrawReq();
    sessionStorage.clear(); // 세션 비우기
    router.push('/');       // 메인 화면으로 이동
  } catch (e) {
    alert("탈퇴 처리 중 오류가 발생했습니다.");
  }
};

// 의료진 스케줄 조회
const fetchDoctorSchedules = async () => {
  if (!userInfo.value.id) return;
  try {
    const docListRes = await getAllDoctorsReq();
    const allStaff = docListRes.data || [];
    const myInfo = allStaff.find(s => String(s.user_id) === String(userInfo.value.id));

    if (myInfo) {
      if (myInfo.role === '의사' || myInfo.role === 'DOCTOR') {
        const schedRes = await getDocSchedReq(myInfo.staff_id);
        doctorSchedules.value = schedRes.data || [];
      } else if (myInfo.role === '간호사' || myInfo.role === 'NURSE') {
        const myDeptName = myInfo.dept_name;
        const ourDeptDoctors = allStaff.filter(s => s.dept_name === myDeptName && (s.role === '의사' || s.role === 'DOCTOR'));
        let allSchedules = [];
        const promises = ourDeptDoctors.map(doc => getDocSchedReq(doc.staff_id));
        const results = await Promise.all(promises);
        results.forEach(res => { if (res.data) allSchedules = [...allSchedules, ...res.data]; });
        doctorSchedules.value = allSchedules;
      }
    }
  } catch (e) { }
};

// 5. 비즈니스 로직 (시간 체크 및 상태 변경)
// [시간 체크] 예약 시간이 현재보다 미래인지 확인
const isFutureTime = (item) => {
  if (!item) return false;
  try {
    const now = new Date();
    // 숫자만 추출 (예: 2026-02-06 -> 20260206)
    let d = String(item.reservation_date).replace(/[^0-9]/g, '');
    let t = String(item.reservation_time).replace(/[^0-9]/g, '');

    // 시간 문자열이 길면(날짜포함) 뒤에서 6자리만 자름
    if (t.length > 6) t = t.slice(-6);
    t = t.padEnd(6, '0'); // 혹시 짧으면 0으로 채움

    const year = parseInt(d.substring(0, 4));
    const month = parseInt(d.substring(4, 6)) - 1; // 월은 0부터
    const day = parseInt(d.substring(6, 8));
    const hour = parseInt(t.substring(0, 2));
    const min = parseInt(t.substring(2, 4));

    const targetDate = new Date(year, month, day, hour, min);

    // 예약시간이 현재보다 미래면 true
    return targetDate > now;
  } catch (e) {
    return false; // 에러나면 일단 통과 (사용자 경험 방해 X)
  }
};

// [진료 완료]
const completeTreatment = (scheduleItem) => {
  // 1. 시간 체크
  if (isFutureTime(scheduleItem)) {
    alert("아직 예약 시간이 되지 않았습니다.\n시간이 지난 후에 처리해 주세요.");
    return;
  }
  // 2. 실행
  if (confirm(`${scheduleItem.patient_name} 님 진료를 완료 처리하시겠습니까?`)) {
    scheduleItem.reservation_status = '완료';
    alert("진료 완료 처리되었습니다.");
  }
};

// [미방문 처리]
const handleNoShow = (scheduleItem) => {
  // 1. 시간 체크
  if (isFutureTime(scheduleItem)) {
    alert("아직 예약 시간이 지나지 않았습니다.\n시간이 지난 후에 처리해 주세요.");
    return;
  }
  // 2. 실행
  if (confirm(`${scheduleItem.patient_name} 님을 [미방문] 처리하시겠습니까?`)) {
    scheduleItem.reservation_status = '미방문';
    alert("미방문 처리되었습니다.");
  }
};

// [강제 취소]
const handleForceCancel = async (scheduleItem) => {
  if (confirm("예약을 강제로 취소(삭제)하시겠습니까?")) {
    try {
      await cancelResReq(scheduleItem.reservation_id);
      scheduleItem.reservation_status = '취소';
      alert("예약이 취소되었습니다.");
    } catch (e) {
      alert("오류가 발생했습니다.");
    }
  }
};

// 6. 초기화 (Lifecycle)
onMounted(async () => {
  // 로그인 체크
  const loginData = sessionStorage.getItem('loginId');
  if (!loginData) { router.push('/login'); return; }

  try {
    const parsed = JSON.parse(loginData);
    userInfo.value = parsed;

    // (추가) 진짜 상세 정보 서버에서 긁어오기
    await fetchMyDetailInfo();

    // 사용자 타입 설정 (MEMBER, MED, ADMIN)
    if (sessionStorage.getItem('loginType')) userType.value = sessionStorage.getItem('loginType');
    else userType.value = parsed.role ? 'MED' : 'MEMBER';
    if ((parsed.loginType || '').toUpperCase() === 'ADMIN') userType.value = 'ADMIN';
  } catch (e) { }

  // 초기 화면 설정
  if (userType.value === 'ADMIN') currentView.value = 'admin_todo';
  else if (userType.value === 'MED') {
    if (isDoctor.value) { currentView.value = 'doc_res'; docViewMode.value = 'calendar'; }
    else if (isNurse.value) currentView.value = 'nur_schedule';
    else currentView.value = 'dash';
  } else currentView.value = 'dash';

  // 데이터 로드
  fetchReservations();
  fetchVehicles();

  if (userType.value === 'MED') {
    await fetchDoctorSchedules();
  }

  // 시계 작동
  updateClock();
  setInterval(updateClock, 1000);
});
</script>

<style scoped>
/* * [수정] 윈도우/엣지 브라우저 기본 눈 숨기기 (중요!) */
input::-ms-reveal,
input::-ms-clear {
  display: none;
}

/* 비밀번호 그룹핑 (메세지 표시용) */
.pw-group {
  flex: 1;
  display: flex;
  flex-direction: column;
}

/* 실시간 유효성 메세지 스타일 */
.error-msg {
  font-size: 12px;
  color: #dc3545;
  margin-top: 5px;
  margin-left: 2px;
}

.success-msg {
  color: #0171e9; /* 성공 시 파란색 */
}

.pw-field-box {
  position: relative;
  width: 100%;
}

.auth-pw-input,
.pw-field-box input {
  width: 100%;
  padding: 12px;
  padding-right: 45px;
  margin-bottom: 0;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-sizing: border-box;
}

.pw-eye-icon {
  position: absolute;
  top: 50%;
  right: 15px;
  transform: translateY(-50%);
  cursor: pointer;
  color: #999;
  display: flex;
  align-items: center;
  justify-content: center;
}

.mb-25 {
  margin-bottom: 25px;
}

.mt-50 {
  margin-top: 50px;
}

.address-row {
  align-items: flex-start;
}

.addr-group {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.addr-top {
  display: flex;
  gap: 8px;
  width: 100%;
}

.input-addr-main {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.btn-addr-search {
  background: #404347;
  color: #fff;
  border: none;
  padding: 0 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  white-space: nowrap;
}

.input-addr-detail {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-sizing: border-box;
}

.btn-area-center {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.btn-action-submit {
  width: 300px;
  padding: 15px;
  background: #0171e9;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-weight: 600;
  cursor: pointer;
  font-size: 14px;
}

.btn-action-submit.gray {
  background: #6c757d;
}

.btn-blue-full {
  width: 300px;
  padding: 15px;
  background: #0171e9;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-weight: 600;
  cursor: pointer;
  font-size: 14px;
}

.withdraw-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 30px;
  padding-right: 10px;
}

.withdraw-link {
  font-size: 12px;
  color: #aaa;
  cursor: pointer;
  text-decoration: underline;
}

.withdraw-link:hover {
  color: #dc3545;
}

.full-dashboard {
  display: flex;
  min-height: 100vh;
  background-color: #f4f7fa;
  width: 100%;
}

.sidebar {
  width: 260px;
  background-color: #fff;
  color: #043264;
  display: flex;
  flex-direction: column;
  position: sticky;
  top: 0;
  height: 100vh;
  flex-shrink: 0;
}

.profile-area {
  padding: 40px 20px;
  text-align: center;
  border-bottom: 2px solid rgba(168, 168, 168, 0.289);
}

.avatar-circle {
  width: 60px;
  height: 60px;
  background: #f0f7ff;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 auto 15px;
}

.avatar {
  font-size: 40px;
  margin-bottom: 10px;
}

.user-id {
  font-size: 18px;
  font-weight: 700;
}

.user-tag,
.join-date {
  font-size: 12px;
  color: #aaa;
  margin-top: 4px;
}

.side-nav {
  flex: 1;
  padding: 20px 0;
}

.side-nav li {
  padding: 15px 30px;
  cursor: pointer;
  color: #a4a4a4;
  transition: 0.2s;
  font-size: 14px;
}

.side-nav li:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.05);
}

.side-nav li.active {
  background: #043264;
  color: #fff;
  font-weight: 600;
  border-right: 4px solid #5c93ae;
}

.main-content {
  flex: 1;
  padding: 50px;
  overflow-y: auto;
}

.dashboard-header {
  margin-bottom: 40px;
}

.blue-txt {
  color: #043264;
  font-weight: 700;
}

.current-time {
  font-size: 14px;
  color: #888;
  margin-top: 5px;
}

.dash-home-grid {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.dash-card {
  flex: 1;
  min-width: 300px;
  background: #fff;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.03);
  border: 1px solid #eee;
}

.card-head h3 {
  font-size: 18px;
  margin-bottom: 20px;
  color: #333;
  font-weight: 700;
}

.info-item {
  display: flex;
  margin-bottom: 12px;
  font-size: 14px;
  border-bottom: 1px solid #f9f9f9;
  padding-bottom: 8px;
}

.info-item .label {
  width: 80px;
  color: #999;
  font-weight: 600;
  flex-shrink: 0;
}

.info-item .val {
  color: #444;
}

.stat-grid {
  display: flex;
  gap: 10px;
}

.stat-box {
  flex: 1;
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  text-align: center;
  color: #666;
}

.stat-box span {
  display: block;
  font-size: 20px;
  font-weight: 800;
  margin-bottom: 5px;
}

.stat-box.blue span {
  color: #0171e9;
}

.stat-box.red span {
  color: #dc3545;
}

.hospital-tbl {
  width: 100%;
  border-collapse: collapse;
}

.hospital-tbl th,
.hospital-tbl td {
  padding: 12px;
  border-bottom: 1px solid #eee;
  text-align: left;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 200px;
}

.hospital-tbl th {
  background: #f9f9f9;
  color: #666;
  font-weight: 600;
}

.hospital-tbl th:last-child,
.hospital-tbl td:last-child {
  min-width: 160px;
}

.bold-blue {
  color: #0171e9;
  font-weight: 600;
}

.bold-text {
  font-weight: 700;
  color: #333;
}

.btn-add-sm {
  background: #0171e9;
  color: #fff;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 600;
  margin-left: auto;
}

.btn-cancel-table {
  padding: 4px 8px;
  border: 1px solid #eee;
  background: #fff;
  color: #e03131;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.btn-complete {
  padding: 4px 8px;
  border: 1px solid #0171e9;
  background: #fff;
  color: #0171e9;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 600;
}

.btn-complete:hover {
  background: #0171e9;
  color: #fff;
}

.status-badge {
  padding: 4px 8px;
  font-size: 11px;
  font-weight: 600;
}

.status-badge.active {
  background: #e3f2fd;
  color: #0171e9;
}

.status-badge.done {
  background: #f1f3f5;
  color: #868e96;
}

.status-badge.cancel {
  background: #ffebee;
  color: #c92a2a;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.auth-modal {
  background: #fff;
  padding: 40px;
  border-radius: 8px;
  width: 400px;
  text-align: center;
}

.modal-btns button {
  padding: 10px 20px;
  margin: 0 5px;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  font-weight: 600;
}

.btn-modal-confirm {
  background: #0171e9;
  color: #fff;
}

.btn-modal-cancel {
  background: #eee;
}

.view-section.centered {
  display: flex;
  justify-content: center;
}

.edit-card-wrap {
  width: 100%;
  max-width: 600px;
}

.f-row {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  border-bottom: 1px solid #f9f9f9;
  padding-bottom: 10px;
}

.f-row span {
  width: 100px;
  font-weight: 600;
  color: #666;
  font-size: 14px;
}

.f-row input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.empty-msg {
  text-align: center;
  padding: 40px;
  color: #999;
}

.empty-res {
  text-align: center;
  padding: 30px 0;
  color: #aaa;
}

.filter-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.filter-btn {
  background: none;
  border: none;
  font-size: 14px;
  font-weight: 600;
  color: #999;
  cursor: pointer;
  padding: 5px 10px;
}

.filter-btn.active {
  color: #0171e9;
  border-bottom: 2px solid #0171e9;
}

.calendar-wrap {
  margin-top: 20px;
}

.cal-header {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}

.cal-header h4 {
  font-size: 20px;
  font-weight: 700;
  color: #333;
}

.cal-header button {
  background: #fff;
  border: 1px solid #ddd;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  cursor: pointer;
}

.cal-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  border: 1px solid #eee;
}

.cal-day-head {
  text-align: center;
  padding: 10px;
  background: #f9f9f9;
  border-bottom: 1px solid #eee;
  font-weight: 600;
  font-size: 13px;
}

.cal-cell {
  min-height: 100px;
  border-right: 1px solid #eee;
  border-bottom: 1px solid #eee;
  padding: 5px;
  position: relative;
  cursor: pointer;
}

.cal-cell:hover {
  background: #fcfcfc;
}

.cal-cell:nth-child(7n) {
  border-right: none;
}

.cal-cell.diff-month {
  background: #fcfcfc;
  color: #ddd;
}

.cal-cell.today {
  background: #f0f9ff;
}

.cal-cell.selected {
  background-color: #fff9db;
  border: 2px solid #ffd43b;
}

.day-num {
  font-size: 12px;
  font-weight: 600;
  display: block;
  margin-bottom: 5px;
}

.cal-events {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.cal-dot {
  font-size: 10px;
  padding: 2px 4px;
  border-radius: 4px;
  background: #eee;
  color: #555;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.cal-dot.active {
  background: #e3f2fd;
  color: #0171e9;
}

.cal-dot.done {
  background: #eee;
  color: #aaa;
  text-decoration: line-through;
}

.cal-dot.shift {
  background: #fff3bf;
  color: #e67700;
  font-weight: bold;
}

.cal-more {
  font-size: 10px;
  text-align: center;
  color: #999;
}

.selected-date-list {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 2px solid #333;
}

.selected-date-list h4 {
  font-size: 16px;
  font-weight: 700;
  color: #333;
}

.mt-10 {
  margin-top: 10px;
}

.py-20 {
  padding-top: 20px;
  padding-bottom: 20px;
}

.split-view {
  display: flex;
  gap: 20px;
}

.flex-1 {
  flex: 1;
}

.calendar-wrap.mini .cal-cell {
  min-height: 40px;
  text-align: center;
}

.todo-input-box {
  display: flex;
  gap: 5px;
  margin-bottom: 15px;
}

.todo-input-box input {
  flex: 1;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.todo-input-box button {
  background: #333;
  color: #fff;
  border: none;
  padding: 8px 15px;
  border-radius: 4px;
  cursor: pointer;
}

.todo-list {
  list-style: none;
  padding: 0;
}

.todo-list li {
  display: flex;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}

.todo-list li input {
  margin-right: 10px;
}

.todo-list li span.done {
  text-decoration: line-through;
  color: #aaa;
}

.btn-del-x {
  background: none;
  border: none;
  color: #ccc;
  cursor: pointer;
  font-size: 18px;
  margin-left: auto;
}

.btn-del-x:hover {
  color: #dc3545;
}

.red-alert {
  color: #dc3545;
  font-weight: 700;
  font-size: 13px;
}

.badge-gray {
  background: #eee;
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 12px;
  color: #666;
}

.btn-group {
  display: flex;
  gap: 4px;
  justify-content: center;
}

.btn-action {
  padding: 4px 8px;
  border: 1px solid #eee;
  border-radius: 4px;
  cursor: pointer;
  font-size: 11px;
  font-weight: 600;
  transition: 0.2s;
}

.btn-action.complete {
  background: #e3f2fd;
  color: #0171e9;
  border-color: #bad6f5;
}

.btn-action.complete:hover {
  background: #0171e9;
  color: #fff;
}

.btn-action.noshow {
  background: #fff9db;
  color: #f08c00;
  border-color: #ffec99;
}

.btn-action.noshow:hover {
  background: #f08c00;
  color: #fff;
}

.btn-action.cancel {
  background: #fff5f5;
  color: #e03131;
  border-color: #ffc9c9;
}

.btn-action.cancel:hover {
  background: #e03131;
  color: #fff;
}
</style>