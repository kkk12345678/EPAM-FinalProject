const table = document.getElementById("cart-table");
let total = 0;
if (table !== null) {
    const rows = table.rows;
    for (let i = 1, td; i < rows.length; i++) {
        td = document.createElement('td');
        td.appendChild(document.createTextNode(i.toString()));
        td.setAttribute("class", "number");
        rows[i].insertBefore(td, rows[i].firstChild);
    }

    const controlDeleteButtons = document.getElementsByClassName("delete");
    const controlIncreaseButtons = document.getElementsByClassName("plus");
    const controlDecreaseButtons = document.getElementsByClassName("minus");

    for (let i = 0; i < controlDeleteButtons.length; i++) {
        const id = controlDeleteButtons[i].getAttribute("id").split("-")[0];
        controlDeleteButtons[i].onclick = function () {
            window.location.href = "./cart?action=delete&id=" + id;
        }
        controlIncreaseButtons[i].onclick = function () {
            window.location.href = "./cart?action=increase&id=" + id;
        }
        controlDecreaseButtons[i].onclick = function () {
            window.location.href = "./cart?action=decrease&id=" + id;
        }
    }

    const prices = document.getElementsByClassName("price");
    const quantities = document.getElementsByClassName("quantity");
    const sums = document.getElementsByClassName("sum");

    for (let i = 0; i < sums.length; i++) {
        const lineSum = parseInt(prices[i].innerHTML) * parseInt(quantities[i].innerHTML);
        sums[i].innerHTML = parseFloat(lineSum.toString()).toFixed(1);
        total += lineSum;
    }
    const totalText = document.getElementById("total").innerText;
    document.getElementById("total").innerText = totalText + " " + parseFloat(total.toString()).toFixed(1);
}
const placeOrder = function () {
    document.getElementById("input").setAttribute("value", total);
    document.getElementById("form").submit();
}
