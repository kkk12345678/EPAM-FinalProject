let page = 1;

window.onload = function () {
    loadBooks();
}

loadBooks = function () {
    const xmlHttp = new XMLHttpRequest();
    const url = "./load-books?page=" + page;
    xmlHttp.open("GET", url, false);
    xmlHttp.send();
    document.getElementById("books").innerHTML += bookHtml(xmlHttp.responseText);
    page++;
}

const bookHtml = function (bookJson) {
    const languageId = getLanguageId();
    const books = JSON.parse(bookJson);
    let result = "";
    for (const i in books) {
        const src = imgSrc(books[i]["isbn"]);
        result += '<div class="book"><a href="./product/' + books[i]["tag"] + '"><img src="' + src + '" alt="" width="100px">';
        result += books[i]["titles"][languageId] + "</a></div>";
    }
    return result;
}

const getLanguageId = function() {
    const xmlHttp = new XMLHttpRequest();
    xmlHttp.open("GET", "./locale", false);
    xmlHttp.send();
    return xmlHttp.responseText;
}

const imgSrc = function (isbn) {
    const xmlHttp = new XMLHttpRequest();
    const url = "./static/img/product/" + isbn + ".jpg";
    xmlHttp.open("GET", url, false);
    xmlHttp.send();

    console.log(xmlHttp.status);
    if (xmlHttp.status !== 200) {
        return "./static/img/product/no-image.jpg"
    } else {
        return url;
    }
}