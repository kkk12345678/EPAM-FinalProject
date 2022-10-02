const tabs = Array.from(document.getElementById('input-tabs').children);
const languageTabs = Array.from(document.getElementById("language-tabs").children);
show = function (tab) {
    const currentTab = document.getElementById(tab);
    for (let i = 0; i < tabs.length; i += 1) {
        if (tabs[i].id === currentTab.id) {
            tabs[i].style.display = "block";
        } else {
            tabs[i].style.display = "none";
        }
        if (languageTabs[i].id === currentTab.id) {
            languageTabs[i].style.borderStyle = "solid";
        } else {
            languageTabs[i].style.borderStyle = "none";
        }
    }
};