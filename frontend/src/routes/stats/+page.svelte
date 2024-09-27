<script lang="ts">
	import { getAllScore } from '$lib/services/backend';
	import { Table, Button } from '@sveltestrap/sveltestrap';
	import { onMount } from 'svelte';
	import { Pagination, PaginationItem, PaginationLink } from '@sveltestrap/sveltestrap';
	import { page } from '$app/stores';
	import dayjs from 'dayjs';
    import relativeTime from 'dayjs/plugin/relativeTime';

	interface userStat {
		name: String;
		point: number;
		conWin: number;
		lastLogin: Date;
		lastMatch: Date;
	}

    dayjs.extend(relativeTime)

	$: activeUrl =
		$page.url.searchParams.get('page') !== null &&
		parseInt($page.url.searchParams.get('page') as string) > 1
			? ($page.url.searchParams.get('page') as string)
			: '1';
	let scoreList: userStat[] = [];
	let currentScoreList: userStat[] = [];
	let lastPage = 0;
	$: {
		currentScoreList = scoreList.slice(
			(parseInt(activeUrl) - 1) * 12 - 1 < 0 ? 0 : (parseInt(activeUrl) - 1) * 12,
			parseInt(activeUrl) * 12
		);
	}

	onMount(async () => {
		scoreList = await getAllScore(localStorage.getItem('accessToken'));
		lastPage = Math.ceil(scoreList.length / 12);
	});

	function createPageArray(totalPages, currentPage) {
		const maxButtons = 5;
		const halfButtons = Math.floor(maxButtons / 2);
		const startPage = Math.max(currentPage - halfButtons, 1);
		const endPage = Math.min(startPage + maxButtons - 1, totalPages);

		const paginationArray = [];

		for (let i = startPage; i <= endPage; i++) {
			paginationArray.push(i);
		}

		return paginationArray;
	}
</script>

<div id="main_stats">
	<Button style="position : absolute; top : 1rem; left : 1rem" href="/tictactoe">Back</Button>
	<h2>Stats</h2>
	<div class="table_round">
		<Table bordered>
			<thead>
				<tr>
					<th>#</th>
					<th>Username</th>
					<th>Point</th>
					<th>Win</th>
					<th>Last Match</th>
					<th>Last Login</th>
				</tr>
			</thead>
			<tbody>
				{#each currentScoreList as score, index}
					<tr>
						<th scope="row">{index + 12 * (parseInt(activeUrl) - 1) + 1}</th>
						<td>{score.name}</td>
						<td>{score.point}</td>
						<td>{score.conWin}</td>
						<td>{dayjs(score.lastMatch).fromNow()}</td>
						<td>{dayjs(score.lastLogin).fromNow()}</td>
					</tr>
				{/each}
			</tbody>
		</Table>

		<Pagination size="lg">
			<PaginationItem>
				<PaginationLink first href="?page=1" />
			</PaginationItem>
			<PaginationItem>
				<PaginationLink previous href="?page={parseInt(activeUrl) === 1 ? 1 : parseInt(activeUrl) - 1}" />
			</PaginationItem>
            {#each createPageArray(lastPage, parseInt(activeUrl)) as i}
                <PaginationItem active={parseInt(activeUrl) === i}>
                    <PaginationLink href="?page={i}">{i}</PaginationLink>
                </PaginationItem>
            {/each}
			<PaginationItem>
				<PaginationLink next href="?page={parseInt(activeUrl) === lastPage ? lastPage : parseInt(activeUrl) + 1}" />
			</PaginationItem>
			<PaginationItem>
				<PaginationLink last href="?page={lastPage}" />
			</PaginationItem>
		</Pagination>
	</div>
</div>

<style>
	#main_stats {
		display: flex;
		text-align: center;
		justify-content: center;
		flex-direction: column;
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
		color: white;
	}

	.table_round {
		margin: 3rem;
	}
</style>
