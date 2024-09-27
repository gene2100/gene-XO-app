<script  lang="ts">
    import { goto } from "$app/navigation";
    import auth from "$lib/services/auth";
    import { isAuthenticated, user } from "$lib/stores/auth";
    import type { Auth0Client } from "@auth0/auth0-spa-js";
    import { onMount } from "svelte";

    let auth0Client: Auth0Client;
    let isLoading = true

    onMount(async () => {
        auth0Client = await auth.createClient()
        isAuthenticated.set(await auth0Client.isAuthenticated())
        user.set(await auth0Client.getUser() || {})
        isAuthenticated.subscribe((value) => {
            if(value === false){
                goto("/")
            }
        })
        isLoading = false
    })
</script>


<main>
    {#if !isLoading}
        <slot/>
    {/if}
</main>