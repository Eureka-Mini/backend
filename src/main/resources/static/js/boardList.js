import {putHeadersAccessToken} from './jwt.js';

document.addEventListener('DOMContentLoaded', async function () {
    try {
        await loadMyBoards();
    } catch (error) {
        console.error('Error loading boards:', error);
    }
});

async function loadMyBoards() {
    try {
        // GET /boards/my-boards API 호출
        const response = await fetch('/boards/my-board', {
            method: 'GET',
            headers: putHeadersAccessToken()
        });

        if (!response.ok) {
            throw new Error('Failed to fetch boards');
        }

        // 응답 데이터를 JSON으로 파싱
        const boardData = await response.json();

        // 카드 요소 생성 및 추가
        const cardWrap = document.querySelector('.card-wrap');
        cardWrap.innerHTML = ''; // 기존 내용 제거

        boardData.data.content.forEach(board => {
            const card = createBoardCard(board);
            cardWrap.appendChild(card);
        });

    } catch (error) {
        console.error('Error fetching or displaying boards:', error);
    }
}

function createBoardCard(board) {
    const card = document.createElement('div');
    card.classList.add('card');

    const cardImage = document.createElement('img');
    cardImage.classList.add('card-image');

    if (board.image) {
        cardImage.src = board.image;
    } else {
        cardImage.src = '/images/defaultItem.png';
    }
    card.appendChild(cardImage);

    const cardTitle = document.createElement('h2');
    cardTitle.textContent = board.title;
    card.appendChild(cardTitle);

    const cardWriter = document.createElement('p');
    cardWriter.textContent = `작성자: ${board.writer}`;
    card.appendChild(cardWriter);

    const cardDate = document.createElement('p');
    cardDate.textContent = `작성일: ${new Date(board.createdAt).toLocaleDateString()}`;
    card.appendChild(cardDate);

    card.addEventListener('click', function () {
        const boardId = board.id;
        window.location.href = `/html/boardDetail.html?boardId=${boardId}`;
    });

    return card;
}
