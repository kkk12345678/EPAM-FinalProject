function send(action, id, message) {
    if (confirm(message)) {
        document.getElementById(action + id).submit();
    }
}