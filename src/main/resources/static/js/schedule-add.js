$(document).ready(function() {
    // Khởi tạo Select2 cho dropdown tuyến đường
    $('#routeVariantId').select2({
        placeholder: "Chọn hoặc tìm kiếm tuyến đường",
        allowClear: true,
        width: '100%'
    });

    // Thêm chuyến mới
    $('#addTrip').click(function() {
        let template = $('#tripTemplate').clone().removeAttr('id').removeAttr('style');
        $('#tripContainer').append(template);
    });

    // Xóa chuyến
    $(document).on('click', '.remove-trip', function() {
        $(this).closest('.trip-row').remove();
    });

    // Xác thực form trước khi submit
    $('#scheduleForm').submit(function(e) {
        let tripRows = $('.trip-row:not(#tripTemplate)');
        let validTrips = false;

        // Xóa các input rỗng
        tripRows.each(function() {
            let startTime = $(this).find('.start-time').val();
            let endTime = $(this).find('.end-time').val();
            if (!startTime && !endTime) {
                $(this).remove(); // Xóa hàng nếu cả hai đều rỗng
            } else if (!startTime || !endTime) {
                alert('Thời gian bắt đầu và kết thúc của chuyến không được để trống.');
                e.preventDefault();
                return false;
            } else {
                validTrips = true;
            }
        });

        // Kiểm tra xem có ít nhất một chuyến hợp lệ
        if (!validTrips) {
            alert('Vui lòng thêm ít nhất một chuyến đi hợp lệ.');
            e.preventDefault();
            return false;
        }
    });
});