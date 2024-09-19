document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('writeBoardForm');
    const token = 'eyJhbGciOiJIUzI1NiJ9.eyJjYXRlZ29yeSI6ImFjY2Vzc1Rva2VuIiwiZW1haWwiOiJzaWV1bkB0ZXN0LmNvbSIsImlhdCI6MTcyNjczMzM4MSwiZXhwIjoxNzI2NzM2OTgxfQ.26jvVU3RIZpB0rAvbqsjoPFbX3pLg5CvHOu9GITun1g';

    if (!token) {
        alert('로그인이 필요합니다.');
        return;
    }

    // 사용자 정보를 불러오기
    fetch('/members/my-info', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            console.log('Response status:', response.status);
            console.log('Response headers:', response.headers);
            return response.json();
        })
        .then(data => {
            console.log('Response data:', data);
            if (data.status === 'success') {
                setupFormSubmission(form, token, data.data);
            } else {
                throw new Error(data.message || '사용자 정보를 불러오는 데 실패했습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });

    //사용자가 폼을 제출할 때 페이지를 새로 고침하지 않고, 폼 데이터를 비동기적으로 서버로 전송. 이거 없으면 url뒤에 어쩌구 저쩌구 뜸
    form.addEventListener('submit', function(event) {
        event.preventDefault();
        submitBoard(token);
    });
});




function submitBoard(token) {
    const boardData = {
        title: document.getElementById('title').value,
        content: document.getElementById('content').value,
        price: parseInt(document.getElementById('price').value),
        boardStatus: '판매중'
    };

    console.log('board data:', boardData); //데이터 들어갔는지

    fetch('/boards', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(boardData),
        credentials: 'include'
    })
        .then(response => response.text().then(text => {
            if (!response.ok) {
                console.error('Error response body:', text);
                throw new Error(`서버 응답 오류 (${response.status}): ${text}`);
            }
            return text ? JSON.parse(text) : {};
        }))
        .then(data => {
            console.log('Success:', data);
            alert('게시글이 성공적으로 작성되었습니다.');
            window.location.href = '/boards';
        })
        .catch(error => {
            console.error('Error:', error);
            alert('게시글 작성에 실패했습니다. 오류: ' + error.message);
        });
}
