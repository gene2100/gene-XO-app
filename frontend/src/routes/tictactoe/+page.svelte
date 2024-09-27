<script lang="ts">
	import auth from '$lib/services/auth';
	import { onMount } from 'svelte';
	import type { Auth0Client } from '@auth0/auth0-spa-js';
	import { getScore, updateScore } from '$lib/services/backend';
	import { user } from '$lib/stores/auth';
	import { Button } from '@sveltestrap/sveltestrap';
	import { Spinner } from '@sveltestrap/sveltestrap';

	interface historyMove {
		board: Array<Array<string>>;
		recentBox: number;
		whoMove: string;
		turn: number;
	}

	interface userScore {
		id: string;
		name: string;
		point: number;
		conWin: number;
	}

	enum tictactoeResult {
		Win = 'X',
		Lose = 'O',
		Tie = 'T'
	}

	let board = [
		['', '', ''],
		['', '', ''],
		['', '', '']
	];

	let auth0Client: Auth0Client;
	let userScore: userScore = {
		id: '',
		name: '',
		point: 0,
		conWin: 0
	};

	onMount(async () => {
		auth0Client = await auth.createClient();
		let userData = await getScore($user.sub, localStorage.getItem('accessToken'));
		userScore = {
			id: userData.id,
			name: userData.name,
			point: userData.point,
			conWin: userData.conWin
		};
	});

	let isEnd = false;
	let endResult = '';
	let moveHistoryArray: historyMove[];
	$: moveHistoryArray = [];
	let turn = 0;

	function logout() {
		auth.logout(auth0Client);
	}

	function playerClick(r: number, c: number): void {
		turn++;
		if (board[r][c] !== '' || isEnd === true) {
			return;
		}
		board[r][c] = 'X';
		moveHistoryArray = [
			{
				board: JSON.parse(JSON.stringify(board)),
				recentBox: r * 3 + c,
				whoMove: 'Player',
				turn: turn
			},
			...moveHistoryArray
		];
		isEndCheck();
		if (board.some((row) => row.includes('')) && !isEnd) {
			turn++;
			let botBox = botMove();
			moveHistoryArray = [
				{
					board: JSON.parse(JSON.stringify(board)),
					recentBox: botBox,
					whoMove: 'Bot',
					turn: turn
				},
				...moveHistoryArray
			];
			isEndCheck();
		}
		console.log(moveHistoryArray);
	}

	function botMove(): number {
		let nextR = undefined;
		let nextC = undefined;

		//block win
		//check horizatal
		for (let r = 0; r < 3; r++) {
			let Xcount = 0;
			let Ocount = 0;
			let spaceCount = 0;
			let lastSpaceIndex = 0;
			for (let c = 0; c < 3; c++) {
				if (board[r][c] === 'X') {
					Xcount++;
				}
				if (board[r][c] === 'O') {
					Ocount++;
				}
				if (board[r][c] === '') {
					spaceCount++;
					lastSpaceIndex = c;
				}
				if (c === 2 && Ocount === 2 && spaceCount === 1) {
					board[r][lastSpaceIndex] = 'O';
					return r * 3 + lastSpaceIndex;
				}
				if (c === 2 && Xcount === 2 && spaceCount === 1) {
					nextR = r;
					nextC = lastSpaceIndex;
				}
			}
		}

		//check vertical
		for (let c = 0; c < 3; c++) {
			let Xcount = 0;
			let Ocount = 0;
			let spaceCount = 0;
			let lastSpaceIndex = 0;
			for (let r = 0; r < 3; r++) {
				if (board[r][c] === 'X') {
					Xcount++;
				}
				if (board[r][c] === 'O') {
					Ocount++;
				}
				if (board[r][c] === '') {
					spaceCount++;
					lastSpaceIndex = r;
				}
				if (r === 2 && Ocount === 2 && spaceCount === 1) {
					board[lastSpaceIndex][c] = 'O';
					return lastSpaceIndex * 3 + c;
				}
				if (r === 2 && Xcount === 2 && spaceCount === 1) {
					nextR = lastSpaceIndex;
					nextC = c;
				}
			}
		}

		//check diagonal
		if (
			(board[0][0] === '' && board[1][1] === 'X' && board[2][2] === 'X') ||
			(board[0][0] === '' && board[1][1] === 'O' && board[2][2] === 'O')
		) {
			board[0][0] = 'O';
			return 0;
		}
		if (
			(board[0][0] === 'X' && board[1][1] === '' && board[2][2] === 'X') ||
			(board[0][0] === 'O' && board[1][1] === '' && board[2][2] === 'O')
		) {
			board[1][1] = 'O';
			return 4;
		}
		if (
			(board[0][0] === 'X' && board[1][1] === 'X' && board[2][2] === '') ||
			(board[0][0] === 'O' && board[1][1] === 'O' && board[2][2] === '')
		) {
			board[2][2] = 'O';
			return 8;
		}

		if (
			(board[2][0] === '' && board[1][1] === 'X' && board[0][2] === 'X') ||
			(board[2][0] === '' && board[1][1] === 'O' && board[0][2] === 'O')
		) {
			board[2][0] = 'O';
			return 6;
		}
		if (
			(board[2][0] === 'X' && board[1][1] === '' && board[0][2] === 'X') ||
			(board[2][0] === 'O' && board[1][1] === '' && board[0][2] === 'O')
		) {
			board[1][1] = 'O';
			return 4;
		}
		if (
			(board[2][0] === 'X' && board[1][1] === 'X' && board[0][2] === '') ||
			(board[2][0] === 'O' && board[1][1] === 'O' && board[0][2] === '')
		) {
			board[0][2] = 'O';
			return 8;
		}

		//check if do not have win move and keep block move
		if (nextR !== undefined && nextC !== undefined) {
			board[nextR][nextC] = 'O';
			return nextR * 3 + nextC;
		}

		//random move
		let remainBox = new Array().concat(...board).flatMap((ele, index) => {
			if (ele === '') {
				return [index];
			}
			return [];
		});
		let randomIndex = Math.floor(Math.random() * (remainBox.length - 1));
		let randomBox = remainBox[randomIndex];
		board[Math.floor(randomBox / 3)][randomBox % 3] = 'O';
		return randomBox;
	}

	function isEndCheck(): void {
		//check horizontal
		if (board[0][0] === board[0][1] && board[0][1] === board[0][2] && board[0][0] !== '') {
			declareEnd(board[0][0]);
			return;
		}
		if (board[1][0] === board[1][1] && board[1][1] === board[1][2] && board[1][0] !== '') {
			declareEnd(board[1][0]);
			return;
		}
		if (board[2][0] === board[2][1] && board[2][1] === board[2][2] && board[2][0] !== '') {
			declareEnd(board[2][0]);
			return;
		}

		//check vetical
		if (board[0][0] === board[1][0] && board[1][0] === board[2][0] && board[0][0] !== '') {
			declareEnd(board[0][0]);
			return;
		}
		if (board[0][1] === board[1][1] && board[1][1] === board[2][1] && board[0][1] !== '') {
			declareEnd(board[0][1]);
			return;
		}
		if (board[0][2] === board[1][2] && board[1][2] === board[2][2] && board[0][2] !== '') {
			declareEnd(board[0][2]);
			return;
		}

		//check diagonal
		if (board[0][0] === board[1][1] && board[1][1] === board[2][2] && board[0][0] !== '') {
			declareEnd(board[0][0]);
			return;
		}
		if (board[2][0] === board[1][1] && board[1][1] === board[0][2] && board[2][0] !== '') {
			declareEnd(board[2][0]);
			return;
		}

		if (!board.some((row) => row.includes(''))) {
			declareEnd(tictactoeResult.Tie);
			return;
		}
	}

	async function declareEnd(result: string): Promise<void> {
		console.log(result);
		switch (result) {
			case tictactoeResult.Win:
				userScore.point++;
				userScore.conWin++;
				if (userScore.conWin === 3) {
					userScore.point++;
					userScore.conWin = 0;
				}
				break;
			case tictactoeResult.Lose:
				if (userScore.point > 0) {
					userScore.point--;
				}
				userScore.conWin = 0;
				break;
		}
		endResult = result;
		isEnd = true;
		await updateScore(userScore, localStorage.getItem('accessToken'));
	}

	function restartGame(): void {
		board = [
			['', '', ''],
			['', '', ''],
			['', '', '']
		];

		moveHistoryArray = [];
		turn = 0;

		isEnd = false;
		endResult = '';
	}
</script>

{#if userScore.id === ""}
	<div id="main_loading">
		<Spinner class="large" type="grow" color="light"/>
		<Spinner class="large" type="grow" color="light"/>
		<Spinner class="large" type="grow" color="light"/>
		<Spinner class="large" type="grow" color="light"/>
		<Spinner class="large" type="grow" color="light"/>
	</div>
{:else}
	<div id="main">
		<div id="profile">
			<div class="name">{userScore.name}</div>
			<div>
				<div class="point">Point : {userScore.point}</div>
				<div class="Win">Consecutive Win : {userScore.conWin}</div>
				<Button class="logout" href="/stats">Stats</Button>
				<Button class="logout" on:click={logout}>Logout</Button>
			</div>
		</div>
		<div id="XOcontainer">
			{#if isEnd && endResult === tictactoeResult.Win}
				<div class="result_text">You Win!</div>
			{:else if isEnd && endResult === tictactoeResult.Lose}
				<div class="result_text">You Lose</div>
			{:else if isEnd && endResult === tictactoeResult.Tie}
				<div class="result_text">Tie</div>
			{/if}

			<div class="XO">
				<div class="myRow">
					<button class="box b1" on:click={() => playerClick(0, 0)}>{board[0][0]}</button>
					<button class="box b2" on:click={() => playerClick(0, 1)}>{board[0][1]}</button>
					<button class="box b3" on:click={() => playerClick(0, 2)}>{board[0][2]}</button>
				</div>
				<div class="myRow">
					<button class="box b4" on:click={() => playerClick(1, 0)}>{board[1][0]}</button>
					<button class="box b5" on:click={() => playerClick(1, 1)}>{board[1][1]}</button>
					<button class="box b6" on:click={() => playerClick(1, 2)}>{board[1][2]}</button>
				</div>
				<div class="myRow">
					<button class="box b7" on:click={() => playerClick(2, 0)}>{board[2][0]}</button>
					<button class="box b8" on:click={() => playerClick(2, 1)}>{board[2][1]}</button>
					<button class="box b9" on:click={() => playerClick(2, 2)}>{board[2][2]}</button>
				</div>
			</div>

			{#if isEnd}
				<div>
					<button class="restart_button" on:click={restartGame}> Try Again? </button>
				</div>
			{/if}
		</div>
		<div id="history">
			<h2>Move History</h2>
			<div class="history_list">
				{#each moveHistoryArray as moveHistory}
					<div class="history_box">
						<div class="smallXO">
							<div class="myRow">
								<button
									class="smallbox b1"
									class:player_move={moveHistory.recentBox === 0 &&
										moveHistory.whoMove === 'Player'}
									class:bot_move={moveHistory.recentBox === 0 && moveHistory.whoMove === 'Bot'}
									>{moveHistory.board[0][0]}</button
								>
								<button
									class="smallbox b2"
									class:player_move={moveHistory.recentBox === 1 &&
										moveHistory.whoMove === 'Player'}
									class:bot_move={moveHistory.recentBox === 1 && moveHistory.whoMove === 'Bot'}
									>{moveHistory.board[0][1]}</button
								>
								<button
									class="smallbox b3"
									class:player_move={moveHistory.recentBox === 2 &&
										moveHistory.whoMove === 'Player'}
									class:bot_move={moveHistory.recentBox === 2 && moveHistory.whoMove === 'Bot'}
									>{moveHistory.board[0][2]}</button
								>
							</div>
							<div class="myRow">
								<button
									class="smallbox b4"
									class:player_move={moveHistory.recentBox === 3 &&
										moveHistory.whoMove === 'Player'}
									class:bot_move={moveHistory.recentBox === 3 && moveHistory.whoMove === 'Bot'}
									>{moveHistory.board[1][0]}</button
								>
								<button
									class="smallbox b5"
									class:player_move={moveHistory.recentBox === 4 &&
										moveHistory.whoMove === 'Player'}
									class:bot_move={moveHistory.recentBox === 4 && moveHistory.whoMove === 'Bot'}
									>{moveHistory.board[1][1]}</button
								>
								<button
									class="smallbox b6"
									class:player_move={moveHistory.recentBox === 5 &&
										moveHistory.whoMove === 'Player'}
									class:bot_move={moveHistory.recentBox === 5 && moveHistory.whoMove === 'Bot'}
									>{moveHistory.board[1][2]}</button
								>
							</div>
							<div class="myRow">
								<button
									class="smallbox b7"
									class:player_move={moveHistory.recentBox === 6 &&
										moveHistory.whoMove === 'Player'}
									class:bot_move={moveHistory.recentBox === 6 && moveHistory.whoMove === 'Bot'}
									>{moveHistory.board[2][0]}</button
								>
								<button
									class="smallbox b8"
									class:player_move={moveHistory.recentBox === 7 &&
										moveHistory.whoMove === 'Player'}
									class:bot_move={moveHistory.recentBox === 7 && moveHistory.whoMove === 'Bot'}
									>{moveHistory.board[2][1]}</button
								>
								<button
									class="smallbox b9"
									class:player_move={moveHistory.recentBox === 8 &&
										moveHistory.whoMove === 'Player'}
									class:bot_move={moveHistory.recentBox === 8 && moveHistory.whoMove === 'Bot'}
									>{moveHistory.board[2][2]}</button
								>
							</div>
						</div>
						<div style="display: flex; justify-content: center; align-items: center;">
							Turn {moveHistory.turn}
						</div>
					</div>
				{/each}
			</div>
		</div>
	</div>
{/if}

<style>
	#main {
		display: flex;
		flex-direction: row;
		position: fixed;
		width: 100%;
		height: 100%;
		left: 0;
		top: 0;
	}

	#main_loading{
		display: flex;
		flex-direction: row;
		align-items: center;
		justify-content: center;
		position: fixed;
		width: 100%;
		height: 100%;
		left: 0;
		top: 0;
		background: rgb(2, 0, 36);
		background: linear-gradient(
			164deg,
			rgba(2, 0, 36, 1) 0%,
			rgba(5, 24, 68, 1) 47%,
			rgba(38, 50, 56, 1) 100%
		);
	}

	#profile {
		display: flex;
		flex-direction: column;
		justify-content: space-between;
		width: 30vw;
		background: rgb(2, 0, 36);
		background: linear-gradient(
			164deg,
			rgba(2, 0, 36, 1) 0%,
			rgba(5, 24, 68, 1) 47%,
			rgba(38, 50, 56, 1) 100%
		);
		padding: 3rem;
		font-size: 32px;
		color: white;
	}

	:global(.large){
		width: 4rem;
		height: 4rem;
		margin-left: 0.5rem;
		margin-right: 0.5rem;
	}

	#XOcontainer {
		width: 40vw;
		text-align: center;
		background-color: #263238;
	}

	#history {
		width: 30vw;
		background: rgb(2, 0, 36);
		background: linear-gradient(
			164deg,
			rgba(2, 0, 36, 1) 0%,
			rgba(5, 24, 68, 1) 47%,
			rgba(38, 50, 56, 1) 100%
		);
		color: white;
		text-align: center;
		padding-top: 50px;
		font-size: 40px;
	}

	.XO {
		display: inline-block;
		padding-top: 30vh;
	}
	
	.name{
		font-size: 56px;
	}

	.box {
		width: 8rem;
		height: 8rem;
		border: whitesmoke solid 3px;
		background-color: #263238;
		font-size: 72px;
		color: white;
	}

	.smallXO {
		display: inline-block;
	}

	.history_box {
		height: 7rem;
		display: flex;
		flex-direction: row;
		justify-content: space-between;
		padding-left: 3rem;
		padding-right: 4rem;
	}

	.smallbox {
		width: 2rem;
		height: 2rem;
		border: whitesmoke solid 3px;
		background-color: #263238;
		font-size: 16px;
		color: white;
	}

	.player_move {
		color: green;
	}

	.bot_move {
		color: red;
	}

	.myRow {
		display: flex;
		flex-direction: row;
	}

	.result_text {
		padding-top: 18vh;
		position: absolute;
		margin-left: auto;
		margin-right: auto;
		left: 0;
		right: 0;
		text-align: center;
		font-size: 36px;
		color: white;
	}

	.restart_button {
		margin-top: 5rem;
		border: none;
		font-size: 36px;
		color: white;
		background: none;

		&:hover {
			color: rgb(197, 197, 197);
		}
	}

	.history_list {
		height: 70vh;
		overflow: hidden;
	}
</style>
