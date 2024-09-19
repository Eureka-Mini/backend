
function putHeadersAccessToken() {
    const accessToken = localStorage.getItem("accessToken");

    if (!accessToken) {
        console.error("액세스 토큰이 존재하지 않습니다.");
        return {
            'Content-Type': 'application/json',
        };
    }

    return {
       "Authorization": `Bearer ${accessToken}`,
        "Content-Type": "application/json",
    };
}
// 사용 예시
/*
fetch("/some/protected/api", {
    method: "GET",
    headers: putHeadersAccessToken()
})
 */


