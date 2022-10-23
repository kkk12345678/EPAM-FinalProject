const bookImage = document.getElementsByClassName("book-img")[0];
const isbn = bookImage.id;
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
bookImage.setAttribute("src", imageSrc(isbn));

const addToCart = function (id, quantityOnHand, message) {
    const quantity = document.getElementById("quantity").getAttribute("value");
    if (quantity <= quantityOnHand) {
        const xmlHttp = new XMLHttpRequest();
        const url = "../cart" + "?action=add&id=" + id + "&quantity=" + quantity;
        xmlHttp.open("GET", url, false);
        xmlHttp.send();
        const badge = document.getElementById("badge");
        badge.innerText = xmlHttp.responseText;
    } else {
        alert(message);
    }
}