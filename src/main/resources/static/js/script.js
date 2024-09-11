document.getElementById('searchButton').addEventListener('click', function() {
    const query = document.getElementById('searchInput').value;
    if (query) {
        alert('검색어: ' + query);
    } else {
        alert('검색어를 입력');
    }
});
