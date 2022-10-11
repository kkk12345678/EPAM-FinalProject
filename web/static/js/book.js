const btnFilter = document.getElementById("button-filter");
const selectCategory = document.getElementById("category");
const selectPublisher = document.getElementById("publisher");
const inputISBN = document.getElementById("isbn");
const inputTag = document.getElementById("tag");

btnFilter.onclick = function () {
    let queryString = "./books?page=1";
    if (!selectCategory.options[selectCategory.selectedIndex].disabled) {
        queryString += "&category=" + selectCategory.value;
    }
    if (!selectPublisher.options[selectPublisher.selectedIndex].disabled) {
        queryString += "&publisher=" + selectPublisher.value;
    }
    queryString += "&isbn=" + inputISBN.value;
    queryString += "&tag=" + inputTag.value;
    window.location.href = queryString;
}

window.onload = function () {
    const tag = (new URLSearchParams(window.location.search).get("tag"));
    if (tag != null) {
        inputTag.setAttribute("value", tag);
    }
    const isbn = (new URLSearchParams(window.location.search).get("isbn"));
    if (isbn != null) {
        inputISBN.setAttribute("value", isbn);
    }
    const categoryId = (new URLSearchParams(window.location.search).get("category"));
    if (categoryId !== "") {
        for (let i = 0; i < selectCategory.options.length; i++) {
            if (selectCategory.options[i].value === categoryId) {
                selectCategory.options[i].selected = true;
            }
        }
    }
    const publisherId = (new URLSearchParams(window.location.search).get("publisher"));
    if (publisherId !== "") {
        for (let i = 0; i < selectPublisher.options.length; i++) {
            if (selectPublisher.options[i].value === publisherId) {
                selectPublisher.options[i].selected = true;
            }
        }
    }

}
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
        const url = window.location.search;
        const index = url.indexOf("&");
        let params = "";
        if (index > -1) {
            params = url.substr(index);
        }
        window.location.href = "./books?page=" + page + params;
    }
}