document.addEventListener('DOMContentLoaded', async function () {
    try {
        await loadNavbar();

        initializeNavbar();
    } catch (error) {
        console.error('Navbar loading failed:', error);
        document.getElementById('navbar-placeholder').innerHTML = '네브바 로드 실패';
    }
});

async function loadNavbar(retries = 1) {
    for (let i = 0; i < retries; i++) {
        try {
            const response = await fetch('../html/navbar.html', {
                cache: 'no-store'
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

function initializeNavbar() {
    const loginButton = document.getElementById('login-button');
    const searchButton = document.getElementById('searchButton');
    const searchInput = document.getElementById('searchInput');

    const accessToken = localStorage.getItem('accessToken');
    if (accessToken) {
        loginButton.textContent = '로그아웃';
    } else {
        loginButton.textContent = '로그인';
    }

    loginButton.addEventListener('click', function () {
        const updatedAccessToken = localStorage.getItem('accessToken');
        if (updatedAccessToken) {
            alert('로그아웃');
            localStorage.removeItem('accessToken');
            clearNavbarCache();
            window.location.href = '../html/login.html';
        } else {
            window.location.href = '../html/login.html';
        }
    });

    searchButton.addEventListener('click', function () {
        const query = searchInput.value;
        if (query) {
            alert('검색어: ' + query);
        } else {
            alert('검색어를 입력하세요.');
        }
    });
}

function clearNavbarCache() {
    if ('caches' in window) {
        caches.keys().then(function (names) {
            names.forEach(function (name) {
                caches.delete(name);
            });
        });
    }
}
