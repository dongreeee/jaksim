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