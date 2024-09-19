
document.getElementById("loginButton").addEventListener("click", handleLogin);

function handleLogin() {
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    fetch("/auth/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            email,
            password
        })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("올바른 이메일과 패스워드를 입력해주세요.");
            }
            return response.json();
        })
        .then(data => {
            const accessToken = data.accessToken;
            alert("로그인 성공!");

            localStorage.setItem("accessToken", accessToken);
            window.location.href = "/";
        })
        .catch((error) => {
            console.error('Error:', error);
            alert('올바른 이메일과 패스워드를 입력해주세요.');
        });
}