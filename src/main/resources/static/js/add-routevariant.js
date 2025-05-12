let outboundStopIndex = 2; // Bắt đầu từ 2 vì đã có 2 trạm (thứ tự 1 và 2)
let inboundStopIndex = 2; // Bắt đầu từ 2 vì đã có 2 trạm (thứ tự 1 và 2)

// Hàm kiểm tra và hiển thị lỗi nếu bến đi và bến đến trùng nhau
function validateStops() {
    const submitButton = document.getElementById('submitButton');
    let hasError = false;

    // Kiểm tra Lượt đi
    const outboundStations = document.querySelectorAll('#outbound-stops-container .outbound-station');
    const outboundErrorDiv = document.getElementById('outbound-error');
    if (outboundStations.length >= 2) {
        const firstOutbound = outboundStations[0].value;
        const lastOutbound = outboundStations[outboundStations.length - 1].value;
        if (firstOutbound && lastOutbound && firstOutbound === lastOutbound) {
            outboundErrorDiv.textContent = "Bến đi và bến đến của Lượt đi không được trùng nhau.";
            hasError = true;
        } else {
            outboundErrorDiv.textContent = "";
        }
    }

    // Kiểm tra Lượt về
    const inboundStations = document.querySelectorAll('#inbound-stops-container .inbound-station');
    const inboundErrorDiv = document.getElementById('inbound-error');
    if (inboundStations.length >= 2) {
        const firstInbound = inboundStations[0].value;
        const lastInbound = inboundStations[inboundStations.length - 1].value;
        if (firstInbound && lastInbound && firstInbound === lastInbound) {
            inboundErrorDiv.textContent = "Bến đi và bến đến của Lượt về không được trùng nhau.";
            hasError = true;
        } else {
            inboundErrorDiv.textContent = "";
        }
    }

    // Vô hiệu hóa nút Lưu nếu có lỗi
    submitButton.disabled = hasError;
}

// Tự động điền trạm dừng cho Lượt về dựa trên Lượt đi
function updateInboundStops() {
    const outboundStations = document.querySelectorAll('#outbound-stops-container .outbound-station');
    const inboundStations = document.querySelectorAll('#inbound-stops-container .inbound-station');

    if (outboundStations.length > 0 && inboundStations.length > 0) {
        // Trạm đầu của Lượt về = Trạm cuối của Lượt đi
        const lastOutboundStation = outboundStations[outboundStations.length - 1].value;
        inboundStations[0].value = lastOutboundStation;

        // Trạm cuối của Lượt về = Trạm đầu của Lượt đi
        const firstOutboundStation = outboundStations[0].value;
        inboundStations[inboundStations.length - 1].value = firstOutboundStation;
    }

    validateStops(); // Kiểm tra sau khi cập nhật
}

// Gắn sự kiện onchange cho các trạm của Lượt đi và Lượt về
document.addEventListener('DOMContentLoaded', function() {
    const outboundStations = document.querySelectorAll('#outbound-stops-container .outbound-station');
    const inboundStations = document.querySelectorAll('#inbound-stops-container .inbound-station');

    outboundStations.forEach(station => {
        station.addEventListener('change', updateInboundStops);
    });

    inboundStations.forEach(station => {
        station.addEventListener('change', validateStops);
    });

    // Gắn sự kiện submit cho form
    const form = document.getElementById('routeVariantForm');
    form.addEventListener('submit', function(event) {
        validateStops();
        if (document.getElementById('submitButton').disabled) {
            event.preventDefault();
        }
    });

    // Kiểm tra ban đầu
    validateStops();
});

function addOutboundStop() {
    const container = document.getElementById('outbound-stops-container');
    const newStop = document.createElement('div');
    newStop.className = 'stop-item row';
    const newIndex = outboundStopIndex + 1; // Tăng thứ tự tự động
    newStop.innerHTML = `
                    <div class="col-md-6 mb-2">
                        <label class="form-label">Chọn trạm</label>
                        <select class="form-select outbound-station" name="outboundStops[${outboundStopIndex}].stationId" required>
                            <option value="">Chọn trạm</option>
                            ${[...document.querySelectorAll('#outbound-stops-container select option')]
        .filter(opt => opt.value)
        .map(opt => `<option value="${opt.value}">${opt.text}</option>`)
        .join('')}
                        </select>
                    </div>
                    <div class="col-md-4 mb-2">
                        <label class="form-label">Thứ tự</label>
                        <input type="number" class="form-control" name="outboundStops[${outboundStopIndex}].stopOrder" value="${newIndex}" readonly>
                    </div>
                    <div class="col-md-2 mb-2 d-flex align-items-end">
                        <button type="button" class="btn btn-danger btn-sm remove-stop" onclick="removeOutboundStop(${outboundStopIndex})">Xóa</button>
                    </div>
                `;
    container.appendChild(newStop);

    // Gắn sự kiện onchange cho trạm mới
    const newStation = newStop.querySelector('.outbound-station');
    newStation.addEventListener('change', updateInboundStops);

    outboundStopIndex++;
    updateInboundStops(); // Cập nhật Lượt về sau khi thêm trạm
}

function removeOutboundStop(index) {
    const stopDiv = document.querySelector(`input[name="outboundStops[${index}].stopOrder"]`).closest('.stop-item');
    if (document.querySelectorAll('#outbound-stops-container .stop-item').length > 2) { // Đảm bảo ít nhất 2 trạm dừng
        stopDiv.remove();
        outboundStopIndex--;

        // Cập nhật lại thứ tự của các trạm còn lại
        const remainingStops = document.querySelectorAll('#outbound-stops-container .stop-item');
        remainingStops.forEach((stop, i) => {
            const orderInput = stop.querySelector('input[name$=".stopOrder"]');
            orderInput.value = i + 1;
            const select = stop.querySelector('select');
            select.name = `outboundStops[${i}].stationId`;
            orderInput.name = `outboundStops[${i}].stopOrder`;
            const removeButton = stop.querySelector('button');
            removeButton.setAttribute('onclick', `removeOutboundStop(${i})`);
        });

        updateInboundStops(); // Cập nhật Lượt về sau khi xóa trạm
    } else {
        alert('Phải có ít nhất 2 trạm dừng cho Lượt đi!');
    }
}

function addInboundStop() {
    const container = document.getElementById('inbound-stops-container');
    const currentStops = document.querySelectorAll('#inbound-stops-container .stop-item');
    const insertPosition = Math.floor(currentStops.length / 2); // Vị trí giữa

    // Tăng thứ tự của các trạm từ vị trí chèn trở đi
    for (let i = insertPosition; i < currentStops.length; i++) {
        const stop = currentStops[i];
        const orderInput = stop.querySelector('input[name$=".stopOrder"]');
        const currentOrder = parseInt(orderInput.value);
        orderInput.value = currentOrder + 1;

        // Cập nhật tên của select và input để phản ánh chỉ số mới
        const select = stop.querySelector('select');
        const newIndex = i + 1;
        select.name = `inboundStops[${newIndex}].stationId`;
        orderInput.name = `inboundStops[${newIndex}].stopOrder`;
        const removeButton = stop.querySelector('button');
        removeButton.setAttribute('onclick', `removeInboundStop(${newIndex})`);
    }

    // Thêm trạm mới vào vị trí giữa
    const newStop = document.createElement('div');
    newStop.className = 'stop-item row';
    const newOrder = insertPosition + 1; // Thứ tự của trạm mới
    newStop.innerHTML = `
                    <div class="col-md-6 mb-2">
                        <label class="form-label">Chọn trạm</label>
                        <select class="form-select inbound-station" name="inboundStops[${insertPosition}].stationId" required>
                            <option value="">Chọn trạm</option>
                            ${[...document.querySelectorAll('#inbound-stops-container select option')]
        .filter(opt => opt.value)
        .map(opt => `<option value="${opt.value}">${opt.text}</option>`)
        .join('')}
                        </select>
                    </div>
                    <div class="col-md-4 mb-2">
                        <label class="form-label">Thứ tự</label>
                        <input type="number" class="form-control" name="inboundStops[${insertPosition}].stopOrder" value="${newOrder}" readonly>
                    </div>
                    <div class="col-md-2 mb-2 d-flex align-items-end">
                        <button type="button" class="btn btn-danger btn-sm remove-stop" onclick="removeInboundStop(${insertPosition})">Xóa</button>
                    </div>
                `;

    // Chèn trạm mới vào vị trí giữa
    if (currentStops.length === 0) {
        container.appendChild(newStop);
    } else {
        container.insertBefore(newStop, currentStops[insertPosition]);
    }

    // Gắn sự kiện onchange cho trạm mới
    const newStation = newStop.querySelector('.inbound-station');
    newStation.addEventListener('change', validateStops);

    inboundStopIndex++;
    updateInboundStops(); // Cập nhật Lượt về sau khi thêm trạm
}

function removeInboundStop(index) {
    const stopDiv = document.querySelector(`input[name="inboundStops[${index}].stopOrder"]`).closest('.stop-item');
    if (document.querySelectorAll('#inbound-stops-container .stop-item').length > 2) { // Đảm bảo ít nhất 2 trạm dừng
        stopDiv.remove();
        inboundStopIndex--;

        // Cập nhật lại thứ tự và chỉ số của các trạm còn lại
        const remainingStops = document.querySelectorAll('#inbound-stops-container .stop-item');
        remainingStops.forEach((stop, i) => {
            const orderInput = stop.querySelector('input[name$=".stopOrder"]');
            orderInput.value = i + 1;
            const select = stop.querySelector('select');
            select.name = `inboundStops[${i}].stationId`;
            orderInput.name = `inboundStops[${i}].stopOrder`;
            const removeButton = stop.querySelector('button');
            removeButton.setAttribute('onclick', `removeInboundStop(${i})`);
        });

        updateInboundStops(); // Cập nhật Lượt về sau khi xóa trạm
    } else {
        alert('Phải có ít nhất 2 trạm dừng cho Lượt về!');
    }
}