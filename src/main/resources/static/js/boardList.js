import {putHeadersAccessToken} from './jwt.js';

document.addEventListener('DOMContentLoaded', async function () {
    try {
        const viewMyPosts = sessionStorage.getItem('viewMyPosts');
        const urlParams = new URLSearchParams(window.location.search);
        const keyword = urlParams.get('keyword');

        if (viewMyPosts === 'true') {
            await loadMyBoards();
            document.getElementById('my-board-title').textContent = '내가 올린 매물';
            sessionStorage.removeItem('viewMyPosts');  // 상태 초기화
        } else if (keyword) {
            await loadBoardsByKeyword(keyword);
            document.getElementById('my-board-title').textContent = `${keyword} 중고매물`;
        } else {
            await loadBoardsByKeyword('');
            document.getElementById('my-board-title').textContent = '전체 중고매물';
        }
    } catch (error) {
        console.error('Error loading boards:', error);
    }
});

async function loadMyBoards(page = 0, size = 9, sort = 'createdAt,desc') {
    try {
        const response = await fetch(`/boards/my-board?page=${page}&size=${size}&sort=${sort}`, {
            method: 'GET',
            headers: putHeadersAccessToken()
        });

        if (!response.ok) {
            throw new Error('Failed to fetch my boards');
        }

        const boardData = await response.json();
        const cardWrap = document.querySelector('.card-wrap');
        cardWrap.innerHTML = '';

        boardData.data.content.forEach(board => {
            const card = createBoardCard(board);
            cardWrap.appendChild(card);
        });

        const totalPages = boardData.data.page.totalPages;
        setupPaginationForMyBoards(page, totalPages);

    } catch (error) {
        console.error('Error fetching or displaying my boards:', error);
    }
}

function setupPaginationForMyBoards(currentPage, totalPages) {
    const paginationWrap = document.querySelector('.pagination-wrap');
    paginationWrap.innerHTML = '';

    for (let i = 0; i < totalPages; i++) {
        const pageButton = document.createElement('button');
        pageButton.textContent = i + 1;
        pageButton.classList.add('page-button');
        if (i === currentPage) {
            pageButton.classList.add('active');
        }
        pageButton.addEventListener('click', function () {
            loadMyBoards(i);
        });
        paginationWrap.appendChild(pageButton);
    }
}

async function loadBoardsByKeyword(keyword, page = 0, size = 9, sort = 'createdAt,desc') {
    try {
        const response = await fetch(`/boards?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}&sort=${sort}`, {
            method: 'GET',
            headers: putHeadersAccessToken()
        });

        if (!response.ok) {
            throw new Error('Failed to fetch boards');
        }

        const boardData = await response.json();
        const cardWrap = document.querySelector('.card-wrap');
        cardWrap.innerHTML = '';

        boardData.data.content.forEach(board => {
            const card = createBoardCard(board);
            cardWrap.appendChild(card);
        });

        const totalPages = boardData.data.page.totalPages;
        setupPaginationForKeywordOrAllBoards(page, totalPages, keyword);

    } catch (error) {
        console.error('Error fetching or displaying boards:', error);
    }
}

function setupPaginationForKeywordOrAllBoards(currentPage, totalPages, keyword) {
    const paginationWrap = document.querySelector('.pagination-wrap');
    paginationWrap.innerHTML = '';

    for (let i = 0; i < totalPages; i++) {
        const pageButton = document.createElement('button');
        pageButton.textContent = i + 1;
        pageButton.classList.add('page-button');
        if (i === currentPage) {
            pageButton.classList.add('active');
        }
        pageButton.addEventListener('click', function () {
            loadBoardsByKeyword(keyword, i);
        });
        paginationWrap.appendChild(pageButton);
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