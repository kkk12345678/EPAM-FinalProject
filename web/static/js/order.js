const btnsPage = document.getElementsByClassName("button-page");
const current = parseInt(new URLSearchParams(window.location.search).get("page"));
const total = parseInt(document.getElementById("total").textContent);
for (let i = 0; i < btnsPage.length; i++) {
    if (btnsPage[i].textContent === current.toString()) {
        btnsPage[i].setAttribute("style", "background: yellow; font-weight: bold")
    }

    btnsPage[i].onclick = function () {
        let page = btnsPage[i].textContent;
        if (page === ">") {
            if (current < total) {
                page = current + 1;
            } else {
                page = total;
            }
        }
        if (page === "<") {
            if (current === 1) {
                page = 1;
            } else {
                page = current - 1;
            }
        }
        const url = window.location.search;
        const index = url.indexOf("&");
        let params = "";
        if (index > -1) {
            params = url.substr(index);
        }
        const pathArray = location.pathname.split('/');
        window.location.href = "./" + pathArray[2].split("?")[0] + "?page=" + page + params;
    }
}

const orders = document.getElementsByClassName("order");

function makeVisible(id) {
    const ordersDetails = document.getElementsByClassName("details");
    for (let i = 0; i < ordersDetails.length; i++) {
        ordersDetails[i].style.display = "none";
    }
    document.getElementById("details-" + id).style.display = "contents";
}

