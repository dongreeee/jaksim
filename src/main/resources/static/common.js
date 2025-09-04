// ajax vs fetch() 차이
// 둘 다 비동기로 서버에 요청 , rest api요청에 적합

// fetch() - 표준 JS
// async/await 사용 가능
// 기본적으로는 GET요청을 보내지만 원하는 방식(POST, PUT, DELETE 등)으로 바꿀 수 있고,
//                      그 설정을 명시적으로 지정해야 한다.

// fetch('/api/goals', {
//   method: 'POST',
//   headers: {
//     'Content-Type': 'application/json'
//   },
//   body: JSON.stringify({
//     date: '2025-07-28',
//     emoji: '📌'
//   })
// });

//method: HTTP 메서드 지정 (POST, PUT, DELETE 등)
//headers: 요청 헤더 설정 (보통 JSON 전송 시 필수)
//body: 전송할 실제 데이터 (문자열이어야 함 → JSON.stringify 필요)

// 20250715
// div 동적으로 생성 시 순서에 맞게 생성되고 있는가에대한 체크 항시 필요 !!!!!! 짱 중요함


document.addEventListener('DOMContentLoaded', function () {
    const params = new URLSearchParams(window.location.search);
    const shouldActivateMenu = params.get('menu') === 'active';
    window.toggleMenu = function () {
        const menu = document.getElementById('slideMenu');
        console.log('slideMenu:', menu); // 🔍 확인용
        if (menu) {
            menu.classList.remove('hidden');
            menu.classList.toggle('active');
            console.log('active toggled'); // 🔍 확인용
        }
    };

      // 자동으로 메뉴 열기 (redirect로 왔을 때)
        if (shouldActivateMenu) {
            const menu = document.getElementById('slideMenu');
            if (menu) {
                menu.classList.remove('hidden');
                menu.classList.add('active');
            }

            // 👇 URL에서 ?menu=active 제거 (선택적)
            const url = new URL(window.location);
            url.searchParams.delete('menu');
            window.history.replaceState({}, '', url); // 주소 깔끔하게
        }
});

  function closeSlideMenu() {
    const menu = document.getElementById('slideMenu');
    menu.classList.remove('active');
    menu.classList.add('hidden');
  }

 document.addEventListener('DOMContentLoaded', () => {
   const bellIcon = document.getElementById('bellIcon');
   const modal = document.getElementById('notificationModal');
   const countEl = document.getElementById('msgCnt');
   const username = document.getElementById('loginUsername').value;

   connectWebSocket(username);

   // 🔔 모달 열고 닫기
   if (bellIcon && modal) {
     bellIcon.addEventListener('click', (e) => {
       e.stopPropagation();
       modal.classList.toggle('show');

         // ✅ 모달이 열릴 때만 알림 불러오기
           if (modal.classList.contains('show')) {
             loadNotifications(); // 여기에 들어감!
           }

     });

     document.addEventListener('click', (e) => {
       if (!modal.contains(e.target)) {
         modal.classList.remove('show');
       }
     });
   }

   // ✅ 로그인 후 알림 수 1회 조회
   fetch('/notications/count')
     .then(res => res.json())
     .then(count => {
       if (count > 0) {
         countEl.innerText = count;
         countEl.style.display = 'inline';
       } else {
         countEl.style.display = 'none';
       }
     });

   // ✅ WebSocket 연결 후 실시간 수신
   const socket = new SockJS('/ws-stomp');
   const stompClient = Stomp.over(socket);

   stompClient.connect({}, () => {
     stompClient.subscribe(`/topic/notify/`+username, (message) => {
       let current = parseInt(countEl.innerText || '0');
       current = isNaN(current) ? 0 : current;
       countEl.innerText = current + 1;
       countEl.style.display = 'inline';
     });
   });
 });




// webSocket
let stompClient = null; // ✅ 반드시 선언 필요

function connectWebSocket(username) {
const username1 = document.getElementById('loginUsername').value;
  const socket = new SockJS('/ws-stomp');
  stompClient = Stomp.over(socket);

  stompClient.connect({}, function (frame) {
    console.log('✅ WebSocket 연결됨:', frame);

    stompClient.subscribe(`/topic/notify/`+ username1, function (message) {
      alert('🔔 알림: ' + message.body);
    });
  });
}



async function loadNotifications() {
  const list = document.getElementById('notificationList');
  list.innerHTML = ''; // 초기화

  try {
    const res = await fetch('/notications/messages');
    const messages = await res.json();

    if (messages.length === 0) {
      list.innerHTML = '<li>📭 새 알림이 없습니다.</li>';
      return;
    }

    messages.forEach(msg => {
      const li = document.createElement('li');
      li.innerHTML = `
        <strong>📧${msg.senderDisplayName}님의 일정공유</strong><br>
        <small>${new Date(msg.sendReg).toLocaleString()}</small>
      `;

      if (msg.vc === '0') {
        li.style.color = 'black';
      }
      else {
        li.style.color = '#BDBDBD';
      }

      li.addEventListener('click', () => {
        onNotificationClick(msg.id, msg.calendarId);
      });

//      li.addEventListener('click', async () => {
//        await fetch(`/api/messages/${msg.id}/read`, {
//          method: 'PATCH'
//        });
//        li.style.fontWeight = 'normal';
//      });

      list.appendChild(li);
    });
  } catch (err) {
    list.innerHTML = '<li>❌ 알림을 불러올 수 없습니다.</li>';
    console.error(err);
  }
}

function onNotificationClick(messageId, calendarId) {
  // 1. 알림 상태 변경 요청 (vc=1)
  fetch('/messages/read-chk', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ messageId })
  }).then(() => {
    // 2. 해당 calendar 상세 페이지로 이동
    window.location.href = `/shareCalendarView/${messageId}/${calendarId}`;
  });
}






     let emojiMap = {};  // 날짜 -> emoji (항상 같은 이모지)

     function fetchGoalDatesAndRender() {
       fetch('/monthlyGoal/goalCount')  // 날짜만 주는 API
         .then(res => res.json())
//        날짜 배열을 받아서 각 날짜를 키로 하고 값은 항상 emoji인 객체(맵) 형태로 바꿔주는 것
         .then(data => {
         const goalDates = data.goalDates;
         console.log('goalcount옴');
         console.log('✅ 백엔드에서 받은 goalDates:', goalDates);
           // 모든 날짜에 동일 이모지 할당
//           dates : ["2025-07-28", "2025-08-02"] 같은 배열
//           acc : 객체를 누적할 객체 (초기값 : {})
//           date : 현재 순회 중인 날짜 문자열
           emojiMap = goalDates.reduce((acc, date) => {
             acc[date] = '🌟';  // 객체의 key : date / 값 : emoji
             return acc;
           }, {});

            console.log('✅ 생성된 emojiMap:', emojiMap); // 이걸 반드시 찍자!

           renderCalendar();
         })
         .catch(err => {
                 console.log(err.message);  // 목표 없어도 정상 처리
                 emojiMap = {};             // ⛔ 없으면 빈 객체라도 초기화
                 renderCalendar();          // ✅ 이모지 없어도 달력 그리기
               });
     }

      function renderCalendar() {
        const today = new Date();
        const year = today.getFullYear();
        const month = today.getMonth();
        const date = today.getDate();

        const calendar = document.getElementById('calendar2');
        const monthName = `${month + 1}`;

        const firstDay = new Date(year, month, 1);
        const lastDay = new Date(year, month + 1, 0);
        console.log("옴??");
        calendar.innerHTML = `<div class='slide-bar-monthly'><h3>${monthName}</h3>
                              <span id='goal_nav1'> 목표 지정하기 + </span>
                              <span id='goal_nav2' style="display:none;"></span>
                              </div>`;

        const days = document.createElement('div');
        days.className = 'days';

        const dayNames = ['월', '화', '수', '목', '금', '토', '일'];
        dayNames.forEach(name => {
          const d = document.createElement('div');
          d.className = 'day-header';
          d.textContent = name;
          days.appendChild(d);
        });

        const startDay = firstDay.getDay() === 0 ? 6 : firstDay.getDay() - 1;

        // 빈칸
        for (let i = 0; i < startDay; i++) {
          const empty = document.createElement('div');
          days.appendChild(empty);
        }

        for (let i = 1; i <= lastDay.getDate(); i++) {
          const fullDate = `${year}-${String(month + 1).padStart(2, '0')}-${String(i).padStart(2, '0')}`;


  console.log('📅 검사 중인 날짜:', fullDate, '✅ emoji:', emojiMap[fullDate]); // 👈 여기 찍어!


          const d = document.createElement('div');
          d.className = 'day';

          if (i === date) d.classList.add('today');
          if (emojiMap[fullDate]) {
            d.innerHTML = `<span class="emoji-day">${emojiMap[fullDate]}</span>`;
          } else {
            d.textContent = i;
          }

          days.appendChild(d);
        }

        calendar.appendChild(days);

        fetchMonthlyGoal();
      }

//    calendar nav
        function fetchTodayEvents() {
          fetch('/calendar/navInfo')  // 파라미터 없이 호출
            .then(res => res.json())
            .then(data => renderEvents(data))
            .catch(err => {
              console.error('이벤트 불러오기 실패:', err);
            });
        }

        function fetchMonthlyGoal() {
            const ym = new Date().toISOString().slice(0, 7); // '2025-08'

        fetch(`/monthlyGoal/info/${ym}`)
          .then(res => {
            if (res.status === 204) {
            console.log('1');
              throw new Error("이번 달 목표 없음");
            }
            if (!res.ok) {
           console.log('2');
              throw new Error("서버 오류");
            }
            return res.json();
          })
          .then(data => {
          console.log('3');
                  const title = data.title;
                  const goalNav1 = document.getElementById('goal_nav1');
                  const goalNav2 = document.getElementById('goal_nav2');
                  if (goalNav1 && goalNav2) {
                    goalNav2.textContent = `🌟${title}`;
                    goalNav2.style.display = 'inline';
                    goalNav1.style.display = 'none';
                  }
          })
          .catch(err => {
              console.log(err.message);
                  const goalNav1 = document.getElementById('goal_nav1');
                  const goalNav2 = document.getElementById('goal_nav2');
                  if (goalNav1 && goalNav2) {
                    goalNav1.style.display = 'inline';
                    goalNav2.style.display = 'none';
                  }
          });
          }

      function renderEvents(eventData) {
        const eventList = document.getElementById('event-list');
        const today = new Date();
        const todayStr = today.toISOString().slice(0, 10);
        const dayOfWeek = ['일','월','화','수','목','금','토'][today.getDay()];

        let html = `<h2>${todayStr.replace(/-/g, '.')} (${dayOfWeek})</h2>`;

        if (!eventData || eventData.length === 0) {
          html += `<div class="no-event">더 이상 이벤트 없음</div>`;
        } else {
          eventData.forEach(event => {
            html += `
              <div class="event-item">
                <div class="title">| ${event.title}</div>
              </div>
            `;
          });
        }

        eventList.innerHTML = html;
      }

      fetchGoalDatesAndRender();

      fetchTodayEvents();
      renderEvents();


//      todo List
  const dayOfWeekText = ['SUNDAY','MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY'];
  const monthNames = ['January','February','March','April','May','June','July','August','September','October','November','December'];

  let currentDate = new Date();

    function getDateKey(dateObj) {
      const year = dateObj.getFullYear();
      const month = String(dateObj.getMonth() + 1).padStart(2, '0');
      const day = String(dateObj.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    }

    function fetchTodos(dateKey) {
      fetch(`/todo/info/${dateKey}`)
        .then(res => res.json())
        .then(data => renderTasks(data, dateKey));
    }

    function renderTasks(tasks, dateKey) {
      // 날짜 텍스트
      const weekday = currentDate.getDay();
      document.getElementById('dayOfWeek').textContent = dayOfWeekText[weekday];
      document.getElementById('dateText').textContent = `, ${monthNames[currentDate.getMonth()]} ${currentDate.getDate()}`;

      // 목록 렌더링
      const list = document.querySelector('.task-list');
      list.innerHTML = '';
      if (tasks.length === 0) {
        list.innerHTML = '<div class="task"><p>오늘의 할일을 등록 해보세요!</p></div>';
      } else {
        tasks.forEach(todo => {
          const div = document.createElement('div');
          const isChecked = todo.isCompleted == '1' ? 'checked' : '';
          div.className = 'task';
          div.innerHTML = `
            <label><input type="checkbox"  class="todo-check"  name="checkbox" data-id="${todo.id}" ${isChecked}> ${todo.content}</label>
            <a href="#" data-id="${todo.id}" onclick="deleteTodo(event, this);"><img src="/images/delete-icon.png" alt="삭제" style="width:15px;" /></a>
          `;
          list.appendChild(div);
        });
      }
      // 푸터
      document.getElementById('todo_add_btn').href = '/todo/addView/' + dateKey;
    }

    // 초기 로딩
    fetchTodos(getDateKey(currentDate));

    // 버튼 제어
    document.getElementById('prevDayBtn').addEventListener('click', () => {
      currentDate.setDate(currentDate.getDate() - 1);
      fetchTodos(getDateKey(currentDate));
    });
    document.getElementById('nextDayBtn').addEventListener('click', () => {
      currentDate.setDate(currentDate.getDate() + 1);
      fetchTodos(getDateKey(currentDate));
    });


     function deleteTodo(e, button) {
        e.preventDefault(); // a태그 기본 이동 방지
        const id = button.getAttribute('data-id');
          fetch('/todo/delete', {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ id: id })
                    })
                    .then(response => {
                          if (response.ok) {
                              const todoDiv = button.closest('.task'); // ← div.task 삭제
                              const list = document.querySelector('.task-list');

                              if (list.children.length === 0) {
                                  list.innerHTML = '<div class="task"><p>오늘의 할일을 등록 해보세요!</p></div>';
                              }
                              if (todoDiv) {
                                  todoDiv.remove();
                              }
                          } else {
                              alert('삭제 실패');
                          }
                      })
                      .catch(err => {
                          console.error('삭제 중 오류:', err);
                      });
     }

     const todoList = document.getElementById('taskList');

    todoList.addEventListener('change', function(e) {
        if (e.target.matches('.todo-check')) {
            // 여기까지 오면 'todoList' 안의 체크박스만 처리됨
            const checkbox = e.target;
            const id = checkbox.getAttribute('data-id');
            const checked = checkbox.checked;

            fetch('/todo/check', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    id: id,
                    checked: checked
                })
            })
            .then(response => {
                if (!response.ok) throw new Error('서버 오류');
                console.log('업데이트 성공');
            })
            .catch(err => {
                console.error('업데이트 실패:', err);
                checkbox.checked = !checked; // 원상복구
            });
        }
    });

    function goToMonthly(){
         const today = new Date();
         const year = today.getFullYear();
         const month = String(today.getMonth() + 1).padStart(2, '0');
         const yyyyMM = `${year}-${month}`;

         window.location.href = `/monthlyGoal/addView`;
    }