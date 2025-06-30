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

   if (bellIcon && modal) {
     bellIcon.addEventListener('click', (e) => {
       e.stopPropagation(); // 클릭 전파 방지
       modal.classList.toggle('show');
     });

     document.addEventListener('click', (e) => {
       if (!modal.contains(e.target)) {
         modal.classList.remove('show');
       }
     });
   }
 });




// webSocket
let stompClient = null; // ✅ 반드시 선언 필요

function connectWebSocket(username) {
  const socket = new SockJS('/ws-stomp');
  stompClient = Stomp.over(socket);

  stompClient.connect({}, function (frame) {
    console.log('✅ WebSocket 연결됨:', frame);

    stompClient.subscribe(`/topic/notify/useruser11`, function (message) {
      alert('🔔 알림: ' + message.body);
    });
  });
}