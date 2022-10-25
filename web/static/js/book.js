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

const imageSrc = function(isbn) {
    const xmlHttp = new XMLHttpRequest();
    const url = "../static/img/product/" + isbn + ".jpg";
    xmlHttp.open("GET", url, false);
    xmlHttp.send();
    if (xmlHttp.status === 200) {
        return "../static/img/product/" + isbn + ".jpg";
    }
    return "../static/img/product/no-image.jpg";
}

const bookImages = document.getElementsByClassName("book-img");
for (let i = 0; i < bookImages.length; i++) {
    if (bookImages[i].id) {
        bookImages[i].setAttribute("src", imageSrc(bookImages[i].id));
    }
}