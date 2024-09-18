document.addEventListener('DOMContentLoaded', () => {
    // 검색 버튼 클릭 처리
    const searchButton = document.getElementById('searchButton');
    if (searchButton) {
        searchButton.addEventListener('click', function() {
            const query = document.getElementById('searchInput').value;
            if (query) {
                alert('검색어: ' + query);
            } else {
                alert('검색어를 입력');
            }
        });
    }

    // 로그인 버튼 클릭 처리
    const loginButton = document.getElementById('login-btn');
    if (loginButton) {
        loginButton.addEventListener('click', () => {
            window.location.href = 'login.html';
        });
    }
});
