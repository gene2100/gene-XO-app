
<script lang="ts">
    let board = [["","",""]
                ,["","",""]
                ,["","",""]]

    // let remainBox = [0,1,2,3,4,5,6,7,8]

    function playerClick(r: number,c: number){
        if(board[r][c] !== ""){
            return
        }
        board[r][c] = "X";
        // remainBox.splice(remainBox.findIndex((ele) => ele === r*3+c), 1);
        if(board.some(row => row.includes(""))){
            botMove()
        }
    }

    function botMove(){
        let nextR = undefined
        let nextC = undefined

        //block win
        //check horizatal
        for(let r = 0; r < 3 ; r++){
            let Xcount = 0;
            let Ocount = 0;
            let spaceCount = 0;
            let lastSpaceIndex = 0;
            for(let c = 0; c < 3; c++){
                if(board[r][c] === "X"){
                    Xcount++
                }
                if(board[r][c] === "O"){
                    Ocount++
                }
                if(board[r][c] === ""){
                    spaceCount++
                    lastSpaceIndex = c
                }
                if(c === 2 && Ocount === 2 && spaceCount === 1){
                    board[r][lastSpaceIndex] = "O"
                    return
                }
                if(c === 2 && Xcount === 2 && spaceCount === 1){
                    nextR = r
                    nextC = lastSpaceIndex
                }
            }
        }

        //check vertical
        for(let c = 0; c < 3 ; c++){
            let Xcount = 0;
            let Ocount = 0;
            let spaceCount = 0;
            let lastSpaceIndex = 0;
            for(let r = 0; r < 3; r++){
                if(board[r][c] === "X"){
                    Xcount++
                }
                if(board[r][c] === "O"){
                    Ocount++
                }
                if(board[r][c] === ""){
                    spaceCount++
                    lastSpaceIndex = r
                }
                if(r === 2 && Ocount === 2 && spaceCount === 1){
                    board[lastSpaceIndex][c] = "O"
                    return
                }
                if(r === 2 && Xcount === 2 && spaceCount === 1){
                    nextR = lastSpaceIndex
                    nextC = c
                }
            }
        }

        //check diagonal
        if((board[0][0] === "" && board[1][1] === "X" && board[2][2] === "X") || (board[0][0] === "" && board[1][1] === "O" && board[2][2] === "O")){
            board[0][0] = "O"
            return
        }
        if((board[0][0] === "X" && board[1][1] === "" && board[2][2] === "X") || (board[0][0] === "O" && board[1][1] === "" && board[2][2] === "O")){
            board[1][1] = "O"
            return
        }
        if((board[0][0] === "X" && board[1][1] === "X" && board[2][2] === "") || (board[0][0] === "O" && board[1][1] === "O" && board[2][2] === "")){
            board[2][2] = "O"
            return
        }

        if((board[2][0] === "" && board[1][1] === "X" && board[0][2] === "X") || (board[2][0] === "" && board[1][1] === "O" && board[0][2] === "O")){
            board[2][0] = "O"
            return
        }
        if((board[2][0] === "X" && board[1][1] === "" && board[0][2] === "X") || (board[2][0] === "O" && board[1][1] === "" && board[0][2] === "O")){
            board[1][1] = "O"
            return
        }
        if((board[2][0] === "X" && board[1][1] === "X" && board[0][2] === "") || (board[2][0] === "O" && board[1][1] === "O" && board[0][2] === "")){
            board[0][2] = "O"
            return
        }

        //check if do not have win move and keep block move
        if(nextR !== undefined && nextC !== undefined){
            board[nextR][nextC] = "O"
            return
        }
        
        //random move
        let remainBox = new Array().concat(...board)
            .flatMap((ele, index) => {
                if(ele === ""){
                    return [index]
                }
                return []
            })
        let randomIndex = Math.floor(Math.random() * (remainBox.length - 1))
        let randomBox = remainBox[randomIndex]
        board[Math.floor(randomBox/3)][randomBox%3] = "O"
    }

</script>

<div id="main">
    <!-- <h1>Welcome to Tic-Tac-Toe</h1>
    <p>Visit <a href="https://kit.svelte.dev">kit.svelte.dev</a> to read the documentation</p> -->

    <div id="XO">
        <div class="row">
            <button class="box b1" on:click={() => playerClick(0,0)}>{board[0][0]}</button>
            <button class="box b2" on:click={() => playerClick(0,1)}>{board[0][1]}</button>
            <button class="box b3" on:click={() => playerClick(0,2)}>{board[0][2]}</button>
        </div>
        <div class="row">
            <button class="box b4" on:click={() => playerClick(1,0)}>{board[1][0]}</button>
            <button class="box b5" on:click={() => playerClick(1,1)}>{board[1][1]}</button>
            <button class="box b6" on:click={() => playerClick(1,2)}>{board[1][2]}</button>
        </div>
        <div class="row">
            <button class="box b7" on:click={() => playerClick(2,0)}>{board[2][0]}</button>
            <button class="box b8" on:click={() => playerClick(2,1)}>{board[2][1]}</button>
            <button class="box b9" on:click={() => playerClick(2,2)}>{board[2][2]}</button>
        </div>
    </div>
</div>


<style>
    #main{
        width: 100%;
        height: 100%;
        position: fixed;
        left: 0;
        top: 0;
        text-align: center;
        background-color: #263238;
    }

    #XO{ 
        display: inline-block;
        padding-top: 30vh;
    }

    .box{
        width: 8rem;
        height: 8rem;
        border: whitesmoke solid 3px;
        background-color: #263238;
        font-size: 72px;
        color: white;
    }

    .row{
        display: flex;
        flex-direction:row;
    }
</style>

