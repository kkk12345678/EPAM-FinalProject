const imageSrc = function(isbn) {
    const xmlHttp = new XMLHttpRequest();
    const url = "./static/img/product/" + isbn + ".jpg";
    xmlHttp.open("GET", url, false);
    xmlHttp.send();
    if (xmlHttp.status === 200) {
        return "./static/img/product/" + isbn + ".jpg";
    }
    return "./static/img/product/no-image.jpg";
}

const bookImages = document.getElementsByClassName("book-img");
for (let i = 0; i < bookImages.length; i++) {
    if (bookImages[i].id) {
        bookImages[i].setAttribute("src", imageSrc(bookImages[i].id));
    }
}

const filter = function () {
    const priceMin = document.getElementById("fromInput").value;
    const priceMax = document.getElementById("toInput").value;
    let url = "./shop?page=1&price_min=" + priceMin + "&price_max=" + priceMax;
    const categoryId = document.getElementById("category-filter").value;
    if (categoryId > 0) {
        url += "&category=" + categoryId;
    }
    const publisherId = document.getElementById("publisher-filter").value;
    if (publisherId > 0) {
        url += "&publisher=" + publisherId;
    }
    window.location = url;
}

const cancelFilter = function () {
    window.location = "./shop?page=1";
}

let page = 1;
const loadBooks = function (labelPrice, labelQuantity, languageId) {
    page++;
    const url = new URL(document.URL);
    url.searchParams.set("page", page.toString());

    const urlParts = url.href.split("?");
    const xmlHttp = new XMLHttpRequest();
    xmlHttp.open("GET", "./load-books?" + urlParts[1], false);
    xmlHttp.send();
    document.getElementById("books").innerHTML += bookHtml(xmlHttp.responseText, labelPrice, labelQuantity, languageId);
}

const bookHtml = function (bookJson, labelPrice, labelQuantity, languageId) {
    const books = JSON.parse(bookJson);
    let result = "";
    for (const i in books) {
        const src = imageSrc(books[i]["isbn"]);
        result +=
            '<div class="book"><div class="book-details"><div class="book-img">' +
            '<img class="book-img" id="' + books[i]["isbn"] + '" alt="" width="100px" src="' + src + '"></div>' +
            '<div class="book-text-details"><p>' + labelQuantity + ': ' + books[i]["quantity"] + '</p><p>ISBN: ' + books[i]["isbn"] + '</p>' +
            '<p>' + labelPrice + ': ' + Number(books[i]["price"]).toFixed(1) + '</p></div></div>' +
            '<div class="book-link"><a href="./product/' + books[i]["tag"] + '">' + books[i]["names"][languageId] + '</a></div></div></div>';
    }
    return result;
}


