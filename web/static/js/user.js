const modalAddUser = document.getElementById("add-user-modal");
const btnAdd = document.getElementById("add-user");
const btnCancel = document.getElementById("btnCancel");

btnAdd.onclick = function() {
    modalAddUser.style.display = "block";
}

btnCancel.onclick = function() {
    modalAddUser.style.display = "none";
}