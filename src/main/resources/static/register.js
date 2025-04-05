document.getElementById('registerForm').addEventListener('submit', async function(event) {
    event.preventDefault();

    const formData = new FormData(event.target);
    const data = Object.fromEntries(formData.entries());
    const messageDiv = document.getElementById('message');
    messageDiv.style.display = 'none'; 

    try {
        const response = await fetch('/api/users/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data)
        });

        const responseBody = await response.text();

        if (response.ok) {
            messageDiv.textContent = 'Registration successful! You can now login.';
            messageDiv.className = 'success-message';
            messageDiv.style.display = 'block';
            event.target.reset(); 
        } else {
            messageDiv.textContent = responseBody || 'Registration failed. Please try again.'; 
            messageDiv.className = 'error-message'; 
            messageDiv.style.display = 'block';
        }
    } catch (error) {
        console.error('Registration error:', error);
        messageDiv.textContent = 'An error occurred during registration.';
         messageDiv.className = 'error-message';
        messageDiv.style.display = 'block';
    }
});
