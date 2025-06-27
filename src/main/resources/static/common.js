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