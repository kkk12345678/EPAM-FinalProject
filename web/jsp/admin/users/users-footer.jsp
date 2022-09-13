<%@ page contentType="text/html;charset=UTF-8"%>
<script>
    const modalAddUser = document.getElementById("add-user-modal");
    const modalEditUser = document.getElementById("edit-user");

    const btnAdd = document.getElementById("add-user");
    const btnCancel = document.getElementById("btnCancel");

    btnAdd.onclick = function() {
        modalAddUser.style.display = "block";
    }

    btnCancel.onclick = function() {
        modalAddUser.style.display = "none";
    }

</script>