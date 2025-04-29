document.addEventListener("DOMContentLoaded", function() {
    // Xử lý toggle form thêm tuyến
    var toggleButton = document.getElementById("toggleRouteFormButton");
    var cancelButton = document.getElementById("cancelRouteFormButton");
    var form = document.getElementById("addRouteForm");

    toggleButton.addEventListener("click", function() {
        if (form.style.display === "none" || form.style.display === "") {
            form.style.display = "block";
        } else {
            form.style.display = "none";
        }
    });

    cancelButton.addEventListener("click", function() {
        form.style.display = "none";
    });

    // Xử lý hiển thị button khi chọn trạm
    var selectStops = document.getElementById("stops");
    var stationButtonsContainer = document.getElementById("stationButtons");

    selectStops.addEventListener("change", function() {
        // Xóa các button hiện có
        stationButtonsContainer.innerHTML = "";

        // Lấy danh sách các option được chọn
        var selectedOptions = Array.from(selectStops.selectedOptions);

        // Tạo button cho mỗi trạm được chọn
        selectedOptions.forEach(function(option) {
            var button = document.createElement("button");
            button.type = "button";
            button.className = "btn btn-info btn-sm station-button";
            button.textContent = option.text; // Lấy stopName từ text của option
            stationButtonsContainer.appendChild(button);

            // Thêm sự kiện click để bỏ chọn trạm khi click vào button
            button.addEventListener("click", function() {
                option.selected = false; // Bỏ chọn option
                stationButtonsContainer.removeChild(button); // Xóa button
                // Kích hoạt sự kiện change để cập nhật lại danh sách button
                selectStops.dispatchEvent(new Event("change"));
            });
        });
    });
});
