
document.getElementById("signup").addEventListener("click", handleSignup)

function handleSignup() {
    const email = document.getElementById("email").value;
    const nickname = document.getElementById("nickname").value;
    const password = document.getElementById("password").value;
    const street = document.getElementById("street").value;
    const detail = document.getElementById("detail").value;
    const zipcode = document.getElementById("zipcode").value;

    fetch("/auth/signup", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body : JSON.stringify({
            email,
            nickname,
            password,
            address: {
                street,
                detail,
                zipcode
            }
        })
    }).then(response => {
        if (!response.ok) {
            return response.json().then(error => {
                throw new Error(error.message);
            });
        }
        return response.json();
    }).then(data => {
        alert("회원 가입 성공!");
        window.location.href = "/html/login.html";
    }).catch(error => {
        switch (error.message) {
            case "유효하지 않은 이메일 형식입니다.":
                alert("이메일 형식이 잘못되었습니다. 올바른 이메일을 입력하세요.");
                break;
            case "닉네임 값을 입력해주세요.":
                alert("닉네임을 입력하세요.");
                break;
            case "비밀번호를 입력해주세요.":
                alert("비밀번호를 입력해주세요.");
                break;
            case "주소를 입력해주세요.":
                alert("주소를 입력해주세요.");
                break;
            case "상세주소를 입력해주세요.":
                alert("상세주소를 입력해주세요.");
                break;
            case "우편번호를 입력해주세요.":
                alert("우편번호를 입력해주세요.");
                break;
            case "이미 존재하는 이메일 입니다.":
                alert("이미 존재하는 이메일입니다.");
                break;
            case "이미 존재하는 닉네임 입니다.":
                alert("이미 존재하는 닉네임입니다.");
                break;
            default:
                alert("회원 가입에 실패했습니다. 다시 시도해 주세요.");
                break;
        }
    })
}