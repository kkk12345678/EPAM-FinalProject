const selectSort = document.getElementById("select-sort")
selectSort.onchange = function() {
    const sortType = this.value;
    const url = new URL(document.URL);
    switch (sortType) {
        case "1":
            url.searchParams.set("sort_by", "tag");
            url.searchParams.set("sort_type", "asc");
            break;
        case "2":
            url.searchParams.set("sort_by", "tag");
            url.searchParams.set("sort_type", "desc");
            break;
        case "3":
            url.searchParams.set("sort_by", "price");
            url.searchParams.set("sort_type", "asc");
            break;
        case "4":
            url.searchParams.set("sort_by", "price");
            url.searchParams.set("sort_type", "desc");
            break;
        case "5":
            url.searchParams.set("sort_by", "publishing_date");
            url.searchParams.set("sort_type", "asc");
            break;
        case "6":
            url.searchParams.set("sort_by", "publishing_date");
            url.searchParams.set("sort_type", "asc");
            break;
    }
    window.location = url;
}