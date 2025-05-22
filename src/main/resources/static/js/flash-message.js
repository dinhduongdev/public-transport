$(document).ready(function() {
    function hideAlert(selector, delay = 5000) {
        setTimeout(function() {
            $(selector).fadeOut('slow', function() {
                $(this).remove();
            });
        }, delay);
    }
    hideAlert('#successAlert');
});
