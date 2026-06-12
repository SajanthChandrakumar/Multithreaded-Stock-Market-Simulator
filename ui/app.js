const API_URL = 'http://localhost:8080/api';

// State management
let previousStocks = {};
let previousTraders = {};

// DOM Elements
const stocksContainer = document.getElementById('stocks-container');
const tradersContainer = document.getElementById('traders-container');

// Formatting utilities
const formatCurrency = (value) => new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(value);
const formatNumber = (value) => new Intl.NumberFormat('en-US').format(value);

async function fetchStocks() {
    try {
        const response = await fetch(`${API_URL}/stocks`);
        if (!response.ok) throw new Error('Network response was not ok');
        const stocks = await response.json();
        renderStocks(stocks);
    } catch (error) {
        console.error('Error fetching stocks:', error);
    }
}

async function fetchTraders() {
    try {
        const response = await fetch(`${API_URL}/traders`);
        if (!response.ok) throw new Error('Network response was not ok');
        const traders = await response.json();
        // Sort traders by net profit descending
        traders.sort((a, b) => b.netProfit - a.netProfit);
        renderTraders(traders);
    } catch (error) {
        console.error('Error fetching traders:', error);
    }
}

function renderStocks(stocks) {
    if (!stocksContainer) return;
    
    // Clear initial loading state
    if (stocksContainer.innerHTML.includes('loading')) {
        stocksContainer.innerHTML = '';
    }

    stocks.forEach(stock => {
        const prevPrice = previousStocks[stock.symbol] || stock.price;
        const isUp = stock.price > prevPrice;
        const isDown = stock.price < prevPrice;
        const priceColorClass = isUp ? 'text-positive' : isDown ? 'text-negative' : '';
        const animationClass = isUp ? 'update-up' : isDown ? 'update-down' : '';

        let card = document.getElementById(`stock-${stock.symbol}`);
        
        if (!card) {
            // Create new card
            card = document.createElement('div');
            card.className = 'stock-card';
            card.id = `stock-${stock.symbol}`;
            
            card.innerHTML = `
                <div class="stock-header">
                    <span class="stock-symbol">${stock.symbol}</span>
                </div>
                <div class="stock-price-container">
                    <div class="stock-price" id="price-${stock.symbol}"></div>
                </div>
            `;
            stocksContainer.appendChild(card);
        }

        // Update price
        const priceElement = document.getElementById(`price-${stock.symbol}`);
        if (stock.price !== prevPrice || !priceElement.textContent) {
            priceElement.textContent = formatCurrency(stock.price);
            priceElement.className = `stock-price ${priceColorClass}`;
            
            // Trigger animation
            card.classList.remove('update-up', 'update-down');
            void card.offsetWidth; // trigger reflow
            if (animationClass) card.classList.add(animationClass);
        }

        previousStocks[stock.symbol] = stock.price;
    });
}

function renderTraders(traders) {
    if (!tradersContainer) return;

    tradersContainer.innerHTML = traders.map(trader => {
        const profitClass = trader.netProfit > 0 ? 'positive' : trader.netProfit < 0 ? 'negative' : 'neutral';
        const sign = trader.netProfit > 0 ? '+' : '';
        
        return `
            <tr>
                <td class="trader-name">${trader.name}</td>
                <td class="tabular-nums">${formatCurrency(trader.balance)}</td>
                <td>
                    <span class="profit-badge ${profitClass}">
                        ${sign}${formatCurrency(trader.netProfit)}
                    </span>
                </td>
                <td class="tabular-nums">${formatNumber(trader.totalTrades)}</td>
                <td class="tabular-nums text-positive">${formatNumber(trader.buyCount)}</td>
                <td class="tabular-nums text-negative">${formatNumber(trader.sellCount)}</td>
            </tr>
        `;
    }).join('');
}

// Initialization loop
function init() {
    // Initial fetch
    fetchStocks();
    fetchTraders();

    // Poll every second
    setInterval(() => {
        fetchStocks();
        fetchTraders();
    }, 1000);
}

// Start app
document.addEventListener('DOMContentLoaded', init);
