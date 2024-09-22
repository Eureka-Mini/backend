import {putHeadersAccessToken} from "./jwt.js";

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
    const searchInput = document.querySelector('.search-input');

    const accessToken = localStorage.getItem('accessToken');
    if (accessToken) {
        const nickname = parseNicknameFromToken(accessToken);
        if (nickname) {
            const nicknameElement = document.createElement('span');
            nicknameElement.textContent = "Welcome " + nickname + " !";
            nicknameElement.style.marginRight = '10px';
            nicknameElement.style.color = '#006400';
            loginButton.textContent = '로그아웃';
            loginButton.id = "logoutButton";
            loginButton.before(nicknameElement);
        }
    } else {
        loginButton.textContent = "로그인";
    }

    loginButton.addEventListener('click', function () {
        const updatedAccessToken = localStorage.getItem('accessToken');
        if (updatedAccessToken) {
            handleLogout();
        } else {
            window.location.href = '../html/login.html';
        }
    });

    searchInput.addEventListener('keydown', function (event) {
        if (event.key === 'Enter') {
            const query = searchInput.value.trim();
            if (query) {
                alert('검색어: ' + query);
            } else {
                alert('검색어를 입력하세요.');
            }
        }
    });
}

function parseNicknameFromToken(token) {
    try {
        const payloadBase = token.split('.')[1];
        const decodedPayload = decodeBase64Url(payloadBase);
        const payloadObject = JSON.parse(decodedPayload);
        return payloadObject.nickname;
    } catch (error) {
        console.error("토큰 내 닉네임 추출 실패 : ", error);
        return null;
    }
}


function handleLogout() {
    fetch("/auth/logout", {
        method: "POST",
        headers: putHeadersAccessToken(),
    }).then(response => {
        if (!response.ok) {
            throw Error("로그아웃 도중 문제가 발생 했습니다.")
        }
        alert("로그아웃 성공!");
        localStorage.removeItem("accessToken");
        window.location.href = "/";
        clearNavbarCache();
    }).catch(error => {
        console.error("Logout failed:", error);
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

function decodeBase64Url(base64Url) {
    let base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');

    const padLength = 4 - (base64.length % 4);
    if (padLength < 4) {
        base64 += '='.repeat(padLength);
    }

    const decoded = atob(base64);

    const utf8Array = new Uint8Array([...decoded].map(c => c.charCodeAt(0)));
    const decoder = new TextDecoder('utf-8');
    return decoder.decode(utf8Array);
}