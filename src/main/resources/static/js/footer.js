document.addEventListener('DOMContentLoaded', async function () {
    try {
        await loadFooter();
    } catch (error) {
        console.error('Footer loading failed:', error);
        document.getElementById('footer-placeholder').innerHTML = '푸터 로드 실패';
    }
});

async function loadFooter(retries = 1) {
    for (let i = 0; i < retries; i++) {
        try {
            const response = await fetch('../html/footer.html', {
                cache: 'no-store'
            });
            if (!response.ok) throw new Error('Failed to fetch footer');
            const data = await response.text();
            document.getElementById('footer-placeholder').innerHTML = data;
            return; // 성공하면 종료
        } catch (error) {
            console.error(`Attempt ${i + 1} to load footer failed.`);
        }
    }
    throw new Error(`Failed to load footer after ${retries} attempts.`);
}
