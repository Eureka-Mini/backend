document.getElementById('searchButton').addEventListener('click', function() {
    const query = document.getElementById('searchInput').value;
    if (query) {
        alert('검색어: ' + query);
    } else {
        alert('검색어를 입력');
    }
});

document.addEventListener('DOMContentLoaded', () => {
    const loginButton = document.getElementById('login-btn');

    if (loginButton) {
        loginButton.addEventListener('click', () => {
            window.location.href = 'login.html';
        });
    }
});