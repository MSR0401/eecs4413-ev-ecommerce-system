const wishlistItemsContainer = document.getElementById('wishlistItemsContainer');
const errorMessageDiv = document.getElementById('errorMessage');
const emptyWishlistMessageDiv = document.getElementById('emptyWishlistMessage');

async function fetchWishlist() {
    try {
        const response = await fetch('/api/wishlist');

        if (response.status === 401) { 
            errorMessageDiv.style.display = 'block';
            emptyWishlistMessageDiv.style.display = 'none';
            wishlistItemsContainer.innerHTML = '';
            return;
        }

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const wishlist = await response.json();
        displayWishlist(wishlist);

    } catch (error) {
        console.error('Error fetching wishlist:', error);
        errorMessageDiv.textContent = `Error loading wishlist: ${error.message}. Please try again.`;
        errorMessageDiv.style.display = 'block';
        emptyWishlistMessageDiv.style.display = 'none';
        wishlistItemsContainer.innerHTML = '';
    }
}

function displayWishlist(wishlist) {
    wishlistItemsContainer.innerHTML = '';
    errorMessageDiv.style.display = 'none'; 

    if (!wishlist || !wishlist.items || wishlist.items.length === 0) {
        emptyWishlistMessageDiv.style.display = 'block';
        return;
    }

    emptyWishlistMessageDiv.style.display = 'none';

    wishlist.items.forEach(item => {
        const vehicle = item.vehicle;
        const itemElement = document.createElement('div');
        itemElement.classList.add('wishlist-item');
        itemElement.setAttribute('id', `wishlist-item-${item.id}`);

        itemElement.innerHTML = `
            <img src="${vehicle.imageUrl || 'placeholder.jpg'}" alt="${vehicle.make} ${vehicle.model}">
            <div class="item-details">
                <h5><a href="vehicle-detail.html?id=${vehicle.id}">${vehicle.year} ${vehicle.make} ${vehicle.model}</a></h5>
                <p>Price: $${vehicle.price ? vehicle.price.toFixed(2) : 'N/A'}</p>
                 <p><small>Added on: ${item.addedAt ? new Date(item.addedAt).toLocaleDateString() : 'N/A'}</small></p>
            </div>
            <div class="item-actions">
                 <button class="btn btn-danger btn-sm" onclick="removeItem(${item.id})">Remove</button>
            </div>
        `;
        wishlistItemsContainer.appendChild(itemElement);
    });
}

async function removeItem(wishlistItemId) {
    if (!confirm('Are you sure you want to remove this item from your wishlist?')) {
        return;
    }

    try {
        const response = await fetch(`/api/wishlist/items/${wishlistItemId}`, {
            method: 'DELETE',
            headers: {
            }
        });

        if (response.ok) {
            const itemElement = document.getElementById(`wishlist-item-${wishlistItemId}`);
            if (itemElement) {
                itemElement.remove();
            }
            if (wishlistItemsContainer.children.length === 0) {
                emptyWishlistMessageDiv.style.display = 'block';
            }
        } else if (response.status === 401) {
            alert("Please log in to modify your wishlist.");
            window.location.href = 'login.html';
        } else {
            const errorData = await response.json();
            throw new Error(errorData.error || `Failed to remove item. Status: ${response.status}`);
        }
    } catch (error) {
        console.error('Error removing item:', error);
        alert(`Error removing item: ${error.message}`);
    }
}

document.addEventListener('DOMContentLoaded', fetchWishlist);
