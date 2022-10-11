const bookImages = document.getElementsByClassName("book-img");
const isbn = bookImages[0].id;
const imageSrc = function(isbn) {
    const xmlHttp = new XMLHttpRequest();
    const url = "./static/img/product/" + isbn + ".jpg";
    xmlHttp.open("GET", url, false);
    xmlHttp.send();
    if (xmlHttp.status === 200) {
        return "../static/img/product/" + isbn + ".jpg";
    }
    return "../static/img/product/no-image.jpg";
}
bookImages[0].setAttribute("src", imageSrc(isbn));

const addToCart = function () {

}