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
    const textarea = document.getElementById('newComment');

    textarea.addEventListener('input', function () {
        this.style.height = 'auto';
        this.style.height = this.scrollHeight + 'px';

        if (this.value.length > 200) {
            this.value = this.value.slice(0, 200);
        }

        document.getElementById('charCount').textContent = `${this.value.length}/200`;
    });

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

                const commentContainer = document.querySelector('.board-comment-container');
                boardData.comments.slice(0, 5).forEach(comment => {
                    const commentElement = document.createElement('div');
                    commentElement.classList.add('comment');

                    const isOwner = comment.writer === loginNickname;

                    const commentContentWithLineBreaks = comment.content.replace(/\n/g, '<br>');

                    commentElement.innerHTML = `
                        <div class="comment-profile">
                            <a style="text-align: left;">${comment.writer}</a>
                            <p class="created-time" style="text-align: right;">${timeSince(comment.createdAt)}</p>
                        </div>
                        <p>${commentContentWithLineBreaks}</p>
                        ${isOwner ? `
                        <div class="comment-actions">
                            <button class="edit-btn" data-id="${comment.id}">수정</button>
                            <button class="delete-btn" data-id="${comment.id}">삭제</button>
                        </div>` : ''}
                    `;

                    commentContainer.appendChild(commentElement);

                    if (isOwner) {
                        commentElement.querySelector('.edit-btn').addEventListener('click', () => editComment(boardId, comment.id));
                        commentElement.querySelector('.delete-btn').addEventListener('click', () => deleteComment(boardId, comment.id));
                    }
                });
            }).catch(error => console.error('Error fetching board details:', error));
    }

    document.getElementById('submitComment').addEventListener('click', function () {
        const newComment = document.getElementById('newComment').value;
        if (newComment) {
            fetch(`/boards/${boardId}/comments`, {
                method: 'POST',
                headers: putHeadersAccessToken(),
                body: JSON.stringify({content: newComment})
            })
                .then(() => location.reload())
                .catch(error => console.error('Error posting comment:', error));
        }
    });

    function refreshComments(boardId) {
        fetch(`/boards/${boardId}`, {
            method: 'GET',
            headers: putHeadersAccessToken()
        })
            .then(response => response.json())
            .then(data => {
                const commentContainer = document.querySelector('.board-comment-container');
                commentContainer.innerHTML = ''; // 기존 댓글 삭제
                data.data.comments.slice(0, 5).forEach(comment => {
                    const commentElement = document.createElement('div');
                    commentElement.classList.add('comment');
                    const isOwner = comment.writer === loginNickname;

                    commentElement.innerHTML = `
                <div class="comment-profile">
                    <a style="text-align: left;">${comment.writer}</a>
                    <p class="created-time" style="text-align: right;">${timeSince(comment.createdAt)}</p>
                </div>
                <p>${comment.content.replace(/\n/g, '<br>')}</p>
                ${isOwner ? `
                <div class="comment-actions">
                    <button class="edit-btn" data-id="${comment.id}">수정</button>
                    <button class="delete-btn" data-id="${comment.id}">삭제</button>
                </div>` : ''}
            `;

                    commentContainer.appendChild(commentElement);

                    if (isOwner) {
                        commentElement.querySelector('.edit-btn').addEventListener('click', () => editComment(boardId, comment.id));
                        commentElement.querySelector('.delete-btn').addEventListener('click', () => deleteComment(boardId, comment.id));
                    }
                });
            })
            .catch(error => console.error('Error fetching comments:', error));
    }

    function editComment(boardId, commentId) {
        const newContent = prompt('댓글을 수정하세요:');
        if (newContent) {
            fetch(`/boards/${boardId}/comments/${commentId}`, {
                method: 'PUT',
                headers: putHeadersAccessToken(),
                body: JSON.stringify({content: newContent})
            })
                .then(() => {
                    refreshComments(boardId);
                })
                .catch(error => console.error('Error editing comment:', error));
        }
    }

    function deleteComment(boardId, commentId) {
        if (confirm('정말로 삭제하시겠습니까?')) {
            fetch(`/boards/${boardId}/comments/${commentId}`, {
                method: 'DELETE',
                headers: putHeadersAccessToken()
            })
                .then(() => {
                    refreshComments(boardId);
                })
                .catch(error => console.error('Error deleting comment:', error));
        }
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

    function parseJwt(token) {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');

        // Base64 디코딩
        const decodedData = decodeURIComponent(
            Array.prototype.map.call(atob(base64), c =>
                '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
            ).join('')
        );

        // JSON 파싱 후 반환
        return JSON.parse(decodedData);
    }
});
