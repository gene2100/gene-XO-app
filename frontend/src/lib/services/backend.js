
export async function userLogin(user, token) {
    const returnUser = await fetch("http://localhost:8080/tictactoe/api/v1/user",{
        method: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-type' : 'application/json',
            'Authorization' : 'Bearer ' + token
        },
        body: JSON.stringify(user)
    }).then(res => {
        return res.json();
    })
    return returnUser
}

export async function getAllScore(token) {
    const returnUser = await fetch("http://localhost:8080/tictactoe/api/v1/user",{
        headers: {
            'Authorization' : 'Bearer ' + token
        },
        method: "GET",
    }).then(res => {
        return res.json();
    })
    return returnUser
}

export async function getScore(userid, token) {
    const returnUser = await fetch("http://localhost:8080/tictactoe/api/v1/user/" + userid,{
        headers: {
            'Authorization' : 'Bearer ' + token
        },
        method: "GET",
    }).then(res => {
        return res.json();
    })
    return returnUser
}

export async function updateScore(user, token) {
    await fetch("http://localhost:8080/tictactoe/api/v1/user",{
        method: "PUT",
        headers: {
            'Accept': 'application/json',
            'Content-type' : 'application/json',
            'Authorization' : 'Bearer ' + token
        },
        body: JSON.stringify(user)
    })
}