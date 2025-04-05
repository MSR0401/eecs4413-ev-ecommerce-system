const loginLink = document.getElementById('loginLink');
const registerLink = document.getElementById('registerLink');
const logoutLink = document.getElementById('logoutLink');
const userInfoDiv = document.getElementById('userInfo');
const cartLink = document.getElementById('cartLink');
const cartItemCountSpan = document.getElementById('cartItemCount');
const wishlistLink = document.getElementById('wishlistLink');

async function updateUI() {
    let isLoggedIn = false;
    loginLink.style.display = 'inline';
    registerLink.style.display = 'inline';
    logoutLink.style.display = 'none';
    wishlistLink.style.display = 'none'; 
    cartLink.style.display = 'none'; 
    userInfoDiv.style.display = 'none';
    userInfoDiv.textContent = '';
    cartItemCountSpan.textContent = '0';

    try {
        const response = await fetch('/api/users/me');

        if (response.ok) {
            const userData = await response.json();
            if (userData.authenticated) {
                isLoggedIn = true;
                loginLink.style.display = 'none';
                registerLink.style.display = 'none';
                logoutLink.style.display = 'inline';
                wishlistLink.style.display = 'inline'; 
                cartLink.style.display = 'inline';
                userInfoDiv.textContent = `Logged in as: ${userData.email}`;
                userInfoDiv.style.display = 'block';
            }
        } else {
            console.warn("Could not get user status, assuming logged out.");
        }
    } catch (error) {
        console.error('Error fetching auth status:', error);
    }

   
    if (isLoggedIn) {
        await updateCartCount();
    }
}

async function updateCartCount() {
    try {
        const cartResponse = await fetch('/api/cart');
        if (cartResponse.ok) {
            const cart = await cartResponse.json();
            let totalItems = 0;
            if (cart && cart.items) {
                totalItems = cart.items.reduce((sum, item) => sum + item.quantity, 0);
            }
            cartItemCountSpan.textContent = totalItems;
        } else if (cartResponse.status !== 401) {
            console.warn("Could not fetch cart count. Status:", cartResponse.status);
            cartItemCountSpan.textContent = '?';
        }
    } catch(error) {
        console.error("Error fetching cart count:", error);
        cartItemCountSpan.textContent = '?';
    }
}

async function handleLogout(event) {
    event.preventDefault();
    try {
        const response = await fetch('/api/users/logout', { method: 'POST' });
        if (response.ok || response.redirected) {
            window.location.href = '/login.html?logout=true';
        } else {
            console.error("Logout failed:", response.statusText);
            alert("Logout failed. Please try again.");
        }
    } catch (error) {
        console.error("Logout error:", error);
        alert("An error occurred during logout.");
    }
}

document.addEventListener('DOMContentLoaded', updateUI);
