document.addEventListener('DOMContentLoaded', async function () {
    try {
        // 네브바 로드 함수 호출
        await loadNavbar();

        // 네브바 로드 후 이벤트 리스너 등록
        initializeNavbar();
    } catch (error) {
        console.error('Navbar loading failed:', error);
        document.getElementById('navbar-placeholder').innerHTML = '네브바 로드 실패';
    }
});

// 네브바를 로드하는 비동기 함수 (재시도 로직 포함)
async function loadNavbar(retries = 1) {
    for (let i = 0; i < retries; i++) {
        try {
            const response = await fetch('../html/navbar.html', {
                cache: 'no-store' // 캐시하지 않고 매번 서버에서 받아오도록 설정
            });
            if (!response.ok) throw new Error('Failed to fetch navbar');
            const data = await response.text();
            document.getElementById('navbar-placeholder').innerHTML = data;
            return; // 성공하면 종료
        } catch (error) {
            console.error(`Attempt ${i + 1} to load navbar failed.`);
        }
    }
    throw new Error(`Failed to load navbar after ${retries} attempts.`);
}

// 네브바 관련 이벤트 리스너 등록 함수
function initializeNavbar() {
    const loginButton = document.getElementById('login-button');
    const searchButton = document.getElementById('searchButton');
    const searchInput = document.getElementById('searchInput');

    // 로그인 상태에 따라 버튼 텍스트 설정
    const accessToken = localStorage.getItem('accessToken');
    if (accessToken) {
        loginButton.textContent = '로그아웃';
    } else {
        loginButton.textContent = '로그인';
    }

    // 로그인/로그아웃 버튼 이벤트
    loginButton.addEventListener('click', function () {
        const updatedAccessToken = localStorage.getItem('accessToken');
        if (updatedAccessToken) {
            alert('로그아웃');
            localStorage.removeItem('accessToken'); // 토큰 삭제
            clearNavbarCache(); // 캐시 삭제 (로그아웃 시)
            window.location.href = '../html/login.html'; // 로그아웃 후 로그인 페이지로 이동
        } else {
            window.location.href = '../html/login.html'; // 로그인 페이지로 이동
        }
    });

    // 검색 버튼 이벤트
    searchButton.addEventListener('click', function () {
        const query = searchInput.value;
        if (query) {
            alert('검색어: ' + query); // 검색어 출력 (실제 검색 기능 추가 가능)
        } else {
            alert('검색어를 입력하세요.');
        }
    });
}

// 로그아웃 시 캐시 삭제 함수
function clearNavbarCache() {
    if ('caches' in window) {
        caches.keys().then(function (names) {
            names.forEach(function (name) {
                caches.delete(name); // 모든 캐시 삭제
            });
        });
    }
}
