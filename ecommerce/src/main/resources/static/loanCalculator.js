document.getElementById('loanForm').addEventListener('submit', async function(event) {
    event.preventDefault(); 

    const price = parseFloat(document.getElementById('price').value);
    const downPayment = parseFloat(document.getElementById('downPayment').value);
    const interestRate = parseFloat(document.getElementById('interestRate').value);
    const duration = parseInt(document.getElementById('duration').value);
    const resultDiv = document.getElementById('result');

    if (isNaN(price) || isNaN(downPayment) || isNaN(interestRate) || isNaN(duration) || price <= 0 || downPayment < 0 || interestRate < 0 || duration <= 0 || downPayment >= price) {
        resultDiv.innerText = 'Please enter valid numbers for all fields. Price and duration must be positive, and down payment cannot exceed price.';
        resultDiv.style.color = 'red';
        return;
    }
    resultDiv.innerText = 'Calculating...';
    resultDiv.style.color = 'black';


    try {
        const response = await fetch(`/api/loans/calculate?price=${price}&downPayment=${downPayment}&interestRate=${interestRate}&duration=${duration}`);

        if (response.ok) {
            const data = await response.json();
            resultDiv.innerText = `Monthly Payment: $${data.monthlyPayment}`;
            resultDiv.style.color = 'green';
        } else {
            const errorText = await response.text();
            resultDiv.innerText = `Error: ${errorText || response.statusText}`;
            resultDiv.style.color = 'red';
        }
    } catch (error) {
        console.error("Loan calculation error:", error);
        resultDiv.innerText = 'Error calculating monthly payment. Please check network connection.';
        resultDiv.style.color = 'red';
    }
});
