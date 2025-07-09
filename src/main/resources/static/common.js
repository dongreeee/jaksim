document.addEventListener('DOMContentLoaded', function () {
    window.toggleMenu = function () {
        const menu = document.getElementById('slideMenu');
        console.log('slideMenu:', menu); // 🔍 확인용
        if (menu) {
            menu.classList.remove('hidden');
            menu.classList.toggle('active');
            console.log('active toggled'); // 🔍 확인용
        }
    };
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
    window.location.href = `/calendarView/${calendarId}`;
  });
}




   const eventData = [
        {
          date: '2025-07-28',
          time: '9:00',
          title: '월세💸'
        },
        {
          date: '2025-08-02',
          time: '2:00',
          title: '남해여행🌊',
          period: '2025-08-02 14:00 ~ 2025-08-04'
        }
      ];

      const emojiMap = {
        '2025-07-28': '💸',
        '2025-08-02': '🌊'
      };

      function renderCalendar() {
        const today = new Date();
        const year = today.getFullYear();
        const month = today.getMonth();
        const date = today.getDate();

        const calendar = document.getElementById('calendar2');
        const monthName = `${month + 1}월`;

        const firstDay = new Date(year, month, 1);
        const lastDay = new Date(year, month + 1, 0);

        calendar.innerHTML = `<h3>${monthName}</h3>`;

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
      }

      function renderEvents() {
        const eventList = document.getElementById('event-list');
        const today = new Date();
        const todayStr = today.toISOString().slice(0, 10);

        let html = `<h2>${todayStr.replace(/-/g, '.')} (${['일','월','화','수','목','금','토'][today.getDay()]})</h2>`;
        const todaysEvents = eventData.filter(e => e.date === todayStr);

        if (todaysEvents.length === 0) {
          html += `<div class="no-event">더 이상 이벤트 없음</div>`;
        }

        eventData.forEach(event => {
          html += `
            <div class="event-item">
              <time>${event.time}</time>
              <div class="title">| ${event.title}</div>
              ${event.period ? `<div style="font-size: 0.8rem; color: #777">${event.period}</div>` : ''}
            </div>
          `;
        });

        eventList.innerHTML = html;
      }

      renderCalendar();
      renderEvents();