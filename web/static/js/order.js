const orders = document.getElementsByClassName("order");

function makeVisible(id) {
    const ordersDetails = document.getElementsByClassName("details");
    for (let i = 0; i < ordersDetails.length; i++) {
        ordersDetails[i].style.display = "none";
    }
    document.getElementById("details-" + id).style.display = "contents";
}

const btnFilter = document.getElementById("button-filter");

btnFilter.onclick = function () {
    let queryString = "./orders?page=1";
    const user = document.getElementById("user").value;
    if (user) {
        queryString += "&user=" + user;
    }
    const sum = document.getElementById("sum").value;
    if (sum) {
        queryString += "&sum=" + sum;
    }
    const dateValue = document.getElementById("date").value;
    if (dateValue) {
        const date = new Date(dateValue);
        const day = ("0" + date.getDate()).slice(-2);
        const month = ("0" + (date.getMonth() + 1)).slice(-2);
        queryString += "&date=" + [date.getFullYear(),month, day].join('-');
    }
    const status = document.getElementById("status").value;
    if (status) {
        queryString += "&status=" + status;
    }
    window.location.href = queryString;
}