const cartItemsContainer = document.getElementById('cartItemsContainer');
const cartTotalSpan = document.getElementById('cartTotal');
const cartSummaryDiv = document.getElementById('cartSummary');
const errorMessageDiv = document.getElementById('errorMessage');
const emptyCartMessageDiv = document.getElementById('emptyCartMessage');
const checkoutButton = document.getElementById('checkoutButton');


async function fetchCart() {
    try {
        const response = await fetch('/api/cart');

        if (response.status === 401) { // Unauthorized
            errorMessageDiv.style.display = 'block';
            cartSummaryDiv.style.display = 'none';
            emptyCartMessageDiv.style.display = 'none';
            return;
        }

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const cart = await response.json();
        displayCart(cart);

    } catch (error) {
        console.error('Error fetching cart:', error);
        errorMessageDiv.textContent = `Error loading cart: ${error.message}. Please try again.`;
        errorMessageDiv.style.display = 'block';
        cartSummaryDiv.style.display = 'none';
        emptyCartMessageDiv.style.display = 'none';
    }
}

function displayCart(cart) {
    cartItemsContainer.innerHTML = '';
    errorMessageDiv.style.display = 'none'; 

    if (!cart || !cart.items || cart.items.length === 0) {
        emptyCartMessageDiv.style.display = 'block';
        cartSummaryDiv.style.display = 'none';
        return;
    }

    emptyCartMessageDiv.style.display = 'none';
    let total = 0;

    cart.items.forEach(item => {
        const vehicle = item.vehicle; 
        const itemElement = document.createElement('div');
        itemElement.classList.add('cart-item');

        const itemSubtotal = (vehicle.price * item.quantity);
        total += itemSubtotal;

        itemElement.innerHTML = `
             <img src="${vehicle.imageUrl || 'placeholder.jpg'}" alt="${vehicle.make} ${vehicle.model}">
             <div class="item-details">
                 <h5><a href="vehicle-detail.html?id=${vehicle.id}">${vehicle.year} ${vehicle.make} ${vehicle.model}</a></h5>
                 <p>Price: $${vehicle.price ? vehicle.price.toFixed(2) : 'N/A'}</p>
                 <p>Quantity: ${item.quantity}</p>
             </div>
             <div class="item-actions">
                 <p>Subtotal: $${itemSubtotal.toFixed(2)}</p>
                 <button class="btn btn-danger btn-sm" onclick="removeItem(${item.id})">Remove</button>
             </div>
         `;
        cartItemsContainer.appendChild(itemElement);
    });

    cartTotalSpan.textContent = total.toFixed(2);
    cartSummaryDiv.style.display = 'block'; // Show summary
}

async function removeItem(cartItemId) {
    if (!confirm('Are you sure you want to remove this item from your cart?')) {
        return;
    }

    try {
        const response = await fetch(`/api/cart/items/${cartItemId}`, {
            method: 'DELETE',
        });

        if (response.ok || response.status === 204) {
            fetchCart();
        } else if (response.status === 401) {
            alert("Please log in to modify your cart.");
            window.location.href = 'login.html';
        }
        else {
            throw new Error(`Failed to remove item. Status: ${response.status}`);
        }
    } catch (error) {
        console.error('Error removing item:', error);
        alert(`Error removing item: ${error.message}`);
    }
}


if (checkoutButton) {
    checkoutButton.addEventListener('click', () => {
        window.location.href = 'checkout.html';
    });
}


document.addEventListener('DOMContentLoaded', fetchCart);
