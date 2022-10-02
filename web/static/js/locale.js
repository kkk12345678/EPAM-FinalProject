const selectElement = document.getElementById("select-locale")
selectElement.onchange = function() {
    const locale = this.value;
    const pathArray = location.pathname.split('/');
    window.location = pathArray[0] + "/" + pathArray[1] + "/change-locale?locale=" + locale + "&page=" + window.location.href;

}