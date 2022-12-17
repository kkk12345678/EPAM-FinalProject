const btnCancel = document.getElementById("cancel");
const pathArray = location.pathname.split('/');
btnCancel.onclick = function() {
    window.location = pathArray[0] + "/" + pathArray[1] + "/shop";
}




