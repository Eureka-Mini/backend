import {putHeadersAccessToken} from './jwt.js';

document.addEventListener('DOMContentLoaded', function () {
    const token = localStorage.getItem('accessToken');

    if (!token) {
        document.body.innerHTML = '';
        alert('로그인이 필요합니다. 로그인 페이지로 이동합니다.');
        window.location.href = '/html/login.html';
        return;
    }

    const urlParams = new URLSearchParams(window.location.search);
    const boardId = urlParams.get('boardId');
    const loginNickname = parseJwt(token).nickname;

    if (boardId) {
        fetch(`/boards/${boardId}`, {
            method: 'GET',
            headers: putHeadersAccessToken()
        })
            .then(response => response.json())
            .then(data => {
                const boardData = data.data;
                
                document.getElementById('board-title').textContent = boardData.title;
                document.getElementById('board-created-time').textContent = timeSince(boardData.createdAt);
                document.getElementById('board-address').textContent = boardData.writerStreetAddress;
                document.getElementById('board-price').textContent = `${boardData.price}원` || "가격 없음";
                document.getElementById('board-status').textContent = boardData.boardStatus || "상태 없음";
                document.querySelector('.board-content p').innerHTML = boardData.content.replace(/\n/g, '<br>');
                document.getElementById('board-nickname').textContent = boardData.writer;

                if (boardData.writer === loginNickname) {
                    const boardButtonSection = document.getElementById('board-button');
                    boardButtonSection.innerHTML = `
                        <button class="edit-btn">글 수정</button>
                        <button class="delete-btn">글 삭제</button>
                    `;

                    document.querySelector('.edit-btn').addEventListener('click', function() {
                        window.location.href = `/html/updateboard.html?id=${boardId}`;
                    });

                    document.querySelector('.delete-btn').addEventListener('click', function() {
                        if (confirm('삭제하시겠습니까?')) {
                            fetch(`/boards/${boardId}`, {
                                method: 'DELETE',
                                headers: putHeadersAccessToken()
                            })
                                .then(() => {
                                    alert('게시물이 삭제되었습니다.');
                                    window.location.href = '/html/boardList.html';
                                })
                                .catch(error => {
                                    console.error('Error deleting board:', error);
                                    alert('삭제에 실패했습니다.');
                                });
                        }
                    });
                }
            })
            .catch(error => console.error('Error fetching board details:', error));
    }

    function parseJwt(token) {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));

        return JSON.parse(jsonPayload);
    }

    function timeSince(date) {
        const now = new Date();
        const seconds = Math.floor((now - new Date(date)) / 1000);

        let interval = Math.floor(seconds / 31536000);
        if (interval >= 1) return interval + "년 전";

        interval = Math.floor(seconds / 2592000);
        if (interval >= 1) return interval + "달 전";

        interval = Math.floor(seconds / 86400);
        if (interval >= 1) return interval + "일 전";

        interval = Math.floor(seconds / 3600);
        if (interval >= 1) return interval + "시간 전";

        interval = Math.floor(seconds / 60);
        if (interval >= 1) return interval + "분 전";

        return "방금 전";
    }
});
