const btnCancel = document.getElementById("cancel");

btnCancel.onclick = function() {
    const pathArray = location.pathname.split('/');
    window.location = pathArray[0] + "/" + pathArray[1] + "/shop";
}

