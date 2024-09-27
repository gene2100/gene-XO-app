
import { goto } from '$app/navigation'
import {PUBLIC_AUTH0DOMAIN, PUBLIC_AUTH0CILENTID, PUBLIC_AUTH0CILENTIDBACKEND, PUBLIC_AUTH0CILENTSECRETBACKEND, PUBLIC_AUTH0AUDIENCEBACKEND} from '$env/static/public'
import { isAuthenticated, popupOpen, user } from '$lib/stores/auth'
import {createAuth0Client} from '@auth0/auth0-spa-js'
import { userLogin } from './backend'

async function createClient() {
  let auth0Client = await createAuth0Client({
    domain: PUBLIC_AUTH0DOMAIN,
    clientId: PUBLIC_AUTH0CILENTID
  })

  return auth0Client
} 

async function loginWithPopup(client, options) {
  popupOpen.set(true)
  try {
    await client.loginWithPopup(options)
    let userData = await client.getUser()
    console.log(userData)
    let tokenData = await fetchBackendToken()
    localStorage.setItem("accessToken", tokenData.access_token)
    await userLogin({
      id : userData.sub,
      name: userData.nickname
    }, localStorage.getItem("accessToken"))
    user.set(userData)
    isAuthenticated.set(true)
    goto("/tictactoe")
  } catch (e) {
    // eslint-disable-next-line
    console.error(e)
  } finally {
    popupOpen.set(false)
  }
}

function logout(client) {
  localStorage.removeItem("accessToken")
  return client.logout()
}

async function fetchBackendToken(){
  let res = await fetch("https://dev-5p3sgzjfn75fm318.us.auth0.com/oauth/token", {
    method: 'POST',
    headers: {'content-type': 'application/x-www-form-urlencoded'},
    body: new URLSearchParams({
      grant_type: 'client_credentials',
      client_id: PUBLIC_AUTH0CILENTIDBACKEND,
      client_secret: PUBLIC_AUTH0CILENTSECRETBACKEND,
      audience: PUBLIC_AUTH0AUDIENCEBACKEND
    })
  })

  let json = await res.json()
  return json
}

const auth = {
  createClient,
  loginWithPopup,
  logout,
}

export default auth
