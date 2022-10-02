const btnAdd = document.getElementById("add-book");
btnAdd.onclick = function () {
    window.location.href = "./edit-book?id=0";
}

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
        window.location.href = "./books?page=" + page;
    }
}