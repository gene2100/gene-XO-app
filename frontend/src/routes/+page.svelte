<script lang="ts">

	import auth from '$lib/services/auth';
	import type { Auth0Client } from '@auth0/auth0-spa-js';
	import { onMount } from 'svelte';
	import { Button } from '@sveltestrap/sveltestrap';

	let auth0Client: Auth0Client;

    onMount(async () => {
        auth0Client = await auth.createClient()
    })

	function login() {
		auth.loginWithPopup(auth0Client);
	}
</script>

<div id="main_login">
	<h2 style="font-size: 72px;">Tic-Tac-Toe</h2>
	<div>
		<Button on:click={login}>Login</Button>
	</div>
</div>

<style>
	#main_login {
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
</style>
