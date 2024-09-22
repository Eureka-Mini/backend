import { putHeadersAccessToken } from './jwt.js';

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
            document.getElementById('my-board-title').innerHTML = `[<span style="color: #EB9928;">${keyword}</span>]의 검색결과`;
        } else {
            await loadBoardsByKeyword('');
            document.getElementById('my-board-title').textContent = '중고거래 매물보기';
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
    cardImage.src = board.image || '/images/defaultItem.png';
    card.appendChild(cardImage);

    const cardTitle = document.createElement('h2');
    cardTitle.textContent = board.title;
    card.appendChild(cardTitle);

    const cardInfo = document.createElement('div');
    cardInfo.classList.add('card-info');

    const cardWriter = document.createElement('p');
    cardWriter.classList.add('card-writer');
    cardWriter.textContent = `작성자: ${board.writer}`;

    const cardDate = document.createElement('p');
    cardDate.classList.add('card-date');
    cardDate.textContent = new Date(board.createdAt).toLocaleDateString();  // 작성일자

    cardInfo.appendChild(cardWriter);
    cardInfo.appendChild(cardDate);
    card.appendChild(cardInfo);

    const cardPrice = document.createElement('p');
    cardPrice.classList.add('card-price');
    cardPrice.textContent = `${board.price}원`;  // 가격 표시
    card.appendChild(cardPrice);

    const cardStatus = document.createElement('p');
    cardStatus.classList.add('card-status');
    cardStatus.textContent = board.boardStatus;  // 판매상태
    cardStatus.classList.add(board.boardStatus === '판매중' ? 'selling' : 'sold');  // 상태에 따라 클래스 추가
    card.appendChild(cardStatus);

    card.addEventListener('click', function () {
        const boardId = board.id;
        window.location.href = `/html/boardDetail.html?boardId=${boardId}`;
    });

    return card;
}
